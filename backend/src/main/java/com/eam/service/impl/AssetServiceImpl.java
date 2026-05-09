package com.eam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eam.common.BusinessException;
import com.eam.entity.Asset;
import com.eam.entity.AssetChangeLog;
import com.eam.mapper.AssetChangeLogMapper;
import com.eam.mapper.AssetMapper;
import com.eam.service.IAssetService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 资产 Service 实现类
 */
@Service
public class AssetServiceImpl extends ServiceImpl<AssetMapper, Asset> implements IAssetService {

    private final AssetChangeLogMapper changeLogMapper;

    public AssetServiceImpl(AssetChangeLogMapper changeLogMapper) {
        this.changeLogMapper = changeLogMapper;
    }

    @Override
    public IPage<Asset> page(Long pageNum, Long pageSize, String keyword, Long categoryId, Long deptId, String status) {
        Page<Asset> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Asset> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Asset::getAssetCode, keyword)
                    .or().like(Asset::getAssetName, keyword));
        }
        if (categoryId != null) {
            wrapper.eq(Asset::getCategoryId, categoryId);
        }
        if (deptId != null) {
            wrapper.eq(Asset::getDeptId, deptId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Asset::getStatus, status);
        }
        wrapper.orderByDesc(Asset::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public Asset add(Asset asset) {
        // 检查资产编码是否存在
        Long count = this.count(new LambdaQueryWrapper<Asset>()
                .eq(Asset::getAssetCode, asset.getAssetCode()));
        if (count > 0) {
            throw new BusinessException("资产编码已存在");
        }
        // 校验资产净值不能大于购买原值
        if (asset.getCurrentValue() != null && asset.getPurchasePrice() != null
                && asset.getCurrentValue().compareTo(asset.getPurchasePrice()) > 0) {
            throw new BusinessException("资产净值不能大于购买原值");
        }
        if (asset.getStatus() == null) {
            asset.setStatus("NEW");
        }
        this.save(asset);
        return asset;
    }

    @Override
    public Asset update(Asset asset) {
        if (asset.getId() == null) {
            throw new BusinessException("资产ID不能为空");
        }
        // 校验资产净值不能大于购买原值
        if (asset.getCurrentValue() != null && asset.getPurchasePrice() != null
                && asset.getCurrentValue().compareTo(asset.getPurchasePrice()) > 0) {
            throw new BusinessException("资产净值不能大于购买原值");
        }
        this.updateById(asset);
        return asset;
    }

    @Override
    public boolean delete(Long id) {
        Asset asset = this.getById(id);
        if (asset != null && "IN_USE".equals(asset.getStatus())) {
            throw new BusinessException("使用中的资产不能删除");
        }
        return this.removeById(id);
    }

    @Override
    public List<Asset> listAll() {
        return this.list();
    }

    @Override
    public boolean change(Long assetId, String changeType, String newValue, String reason, String operator) {
        Asset asset = this.getById(assetId);
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }

        // 记录旧值
        String oldValue = "";
        switch (changeType) {
            case "DEPT":
                oldValue = String.valueOf(asset.getDeptId());
                asset.setDeptId(Long.parseLong(newValue));
                break;
            case "USER":
                oldValue = String.valueOf(asset.getUserId());
                asset.setUserId(Long.parseLong(newValue));
                break;
            case "STATUS":
                oldValue = asset.getStatus();
                asset.setStatus(newValue);
                break;
            case "LOCATION":
                oldValue = asset.getLocation();
                asset.setLocation(newValue);
                break;
            default:
                throw new BusinessException("未知的变动类型");
        }

        // 更新资产
        this.updateById(asset);

        // 记录变动日志
        AssetChangeLog log = new AssetChangeLog();
        log.setAssetId(assetId);
        log.setChangeType(changeType);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        log.setReason(reason);
        log.setChangeTime(LocalDateTime.now());
        log.setOperator(operator);
        changeLogMapper.insert(log);

        return true;
    }

    @Override
    public boolean transfer(Long assetId, Long toDeptId, Long toUserId, String reason, String operator) {
        // 资产调拨需要审批，这里简化处理直接变更
        return change(assetId, "DEPT", String.valueOf(toDeptId), reason, operator);
    }

    @Override
    public boolean updateMaintenanceDates(Long assetId, java.time.LocalDate lastMaintenanceDate, java.time.LocalDate nextMaintenanceDate) {
        Asset asset = this.getById(assetId);
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }
        asset.setLastMaintenanceDate(lastMaintenanceDate);
        asset.setNextMaintenanceDate(nextMaintenanceDate);
        return this.updateById(asset);
    }
}