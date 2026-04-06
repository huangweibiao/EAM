package com.eam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eam.common.BusinessException;
import com.eam.entity.Asset;
import com.eam.entity.AssetInventory;
import com.eam.entity.AssetInventoryDetail;
import com.eam.mapper.AssetInventoryDetailMapper;
import com.eam.mapper.AssetInventoryMapper;
import com.eam.service.IAssetInventoryService;
import com.eam.service.IAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 资产盘点 Service 实现类
 */
@Service
public class AssetInventoryServiceImpl extends ServiceImpl<AssetInventoryMapper, AssetInventory> implements IAssetInventoryService {

    @Autowired
    private AssetInventoryDetailMapper detailMapper;

    @Autowired
    private IAssetService assetService;

    @Override
    public IPage<AssetInventory> page(Long pageNum, Long pageSize, String status) {
        Page<AssetInventory> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AssetInventory> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) {
            wrapper.eq(AssetInventory::getStatus, status);
        }
        wrapper.orderByDesc(AssetInventory::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public AssetInventory create(AssetInventory inventory) {
        // 生成盘点单号
        String inventoryNo = "INV" + System.currentTimeMillis();
        inventory.setInventoryNo(inventoryNo);
        if (inventory.getStatus() == null) {
            inventory.setStatus("IN_PROGRESS");
        }
        if (inventory.getStartTime() == null) {
            inventory.setStartTime(LocalDateTime.now());
        }

        // 统计应盘点资产数量
        Long assetCount = assetService.count();
        inventory.setTotalAssetCount(assetCount.intValue());
        inventory.setActualCount(0);
        inventory.setMismatchCount(0);

        this.save(inventory);
        return inventory;
    }

    @Override
    public AssetInventory complete(Long id) {
        AssetInventory inventory = this.getById(id);
        if (inventory == null) {
            throw new BusinessException("盘点单不存在");
        }
        if (!"IN_PROGRESS".equals(inventory.getStatus())) {
            throw new BusinessException("只有进行中的盘点单可以完成");
        }

        // 统计实际盘点数量和差异
        List<AssetInventoryDetail> details = detailMapper.selectList(
                new LambdaQueryWrapper<AssetInventoryDetail>()
                        .eq(AssetInventoryDetail::getInventoryId, id));

        int actualCount = 0;
        int mismatchCount = 0;
        for (AssetInventoryDetail detail : details) {
            if (detail.getInventoryTime() != null) {
                actualCount++;
                if (detail.getIsMatch() != null && detail.getIsMatch() == 0) {
                    mismatchCount++;
                }
            }
        }

        inventory.setActualCount(actualCount);
        inventory.setMismatchCount(mismatchCount);
        inventory.setEndTime(LocalDateTime.now());
        inventory.setStatus("COMPLETED");
        this.updateById(inventory);

        return inventory;
    }

    @Override
    public List<AssetInventoryDetail> getDetails(Long inventoryId) {
        return detailMapper.selectList(
                new LambdaQueryWrapper<AssetInventoryDetail>()
                        .eq(AssetInventoryDetail::getInventoryId, inventoryId));
    }

    @Override
    public boolean addDetail(AssetInventoryDetail detail) {
        if (detail.getInventoryTime() == null) {
            detail.setInventoryTime(LocalDateTime.now());
        }
        return detailMapper.insert(detail) > 0;
    }

    @Override
    public boolean updateDetail(AssetInventoryDetail detail) {
        return detailMapper.updateById(detail) > 0;
    }
}