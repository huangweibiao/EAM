package com.eam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eam.common.BusinessException;
import com.eam.entity.Asset;
import com.eam.entity.AssetTransfer;
import com.eam.mapper.AssetTransferMapper;
import com.eam.service.IAssetTransferService;
import com.eam.service.IAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 资产调拨 Service 实现类
 */
@Service
public class AssetTransferServiceImpl extends ServiceImpl<AssetTransferMapper, AssetTransfer> implements IAssetTransferService {

    @Autowired
    private IAssetService assetService;

    @Override
    public IPage<AssetTransfer> page(Long pageNum, Long pageSize, String status) {
        Page<AssetTransfer> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AssetTransfer> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) {
            wrapper.eq(AssetTransfer::getStatus, status);
        }
        wrapper.orderByDesc(AssetTransfer::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public AssetTransfer create(AssetTransfer transfer) {
        // 生成调拨单号
        String transferNo = "TRF" + System.currentTimeMillis();
        transfer.setTransferNo(transferNo);
        if (transfer.getStatus() == null) {
            transfer.setStatus("PENDING");
        }
        if (transfer.getTransferTime() == null) {
            transfer.setTransferTime(LocalDateTime.now());
        }

        // 检查资产是否存在
        Asset asset = assetService.getById(transfer.getAssetId());
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }

        this.save(transfer);
        return transfer;
    }

    @Override
    public AssetTransfer approve(Long id, String approver, boolean approved) {
        AssetTransfer transfer = this.getById(id);
        if (transfer == null) {
            throw new BusinessException("调拨单不存在");
        }
        if (!"PENDING".equals(transfer.getStatus())) {
            throw new BusinessException("只有待审批的调拨单可以审批");
        }

        transfer.setApprover(approver);
        transfer.setApproveTime(LocalDateTime.now());
        transfer.setStatus(approved ? "APPROVED" : "REJECTED");
        this.updateById(transfer);

        // 如果审批通过，自动完成调拨（更新资产信息）
        if (approved) {
            complete(id);
        }

        return transfer;
    }

    @Override
    public AssetTransfer complete(Long id) {
        AssetTransfer transfer = this.getById(id);
        if (transfer == null) {
            throw new BusinessException("调拨单不存在");
        }
        if (!"APPROVED".equals(transfer.getStatus())) {
            throw new BusinessException("只有已审批的调拨单可以完成");
        }

        // 更新资产信息
        assetService.change(transfer.getAssetId(), "DEPT", String.valueOf(transfer.getToDeptId()),
                "调拨完成", transfer.getOperator());

        transfer.setStatus("COMPLETED");
        this.updateById(transfer);
        return transfer;
    }

    @Override
    public List<AssetTransfer> listPending() {
        return this.list(new LambdaQueryWrapper<AssetTransfer>()
                .eq(AssetTransfer::getStatus, "PENDING")
                .orderByDesc(AssetTransfer::getCreateTime));
    }
}