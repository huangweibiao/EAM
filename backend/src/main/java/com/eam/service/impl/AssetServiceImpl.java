package com.eam.service.impl;

import com.eam.common.BusinessException;
import com.eam.entity.Asset;
import com.eam.entity.AssetChangeLog;
import com.eam.repository.AssetChangeLogRepository;
import com.eam.repository.AssetRepository;
import com.eam.service.IAssetService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 资产 Service 实现类
 */
@Service
public class AssetServiceImpl implements IAssetService {

    private final AssetRepository assetRepository;
    private final AssetChangeLogRepository changeLogRepository;

    public AssetServiceImpl(AssetRepository assetRepository, AssetChangeLogRepository changeLogRepository) {
        this.assetRepository = assetRepository;
        this.changeLogRepository = changeLogRepository;
    }

    @Override
    public Page<Asset> page(Long pageNum, Long pageSize, String keyword, Long categoryId, Long deptId, String status) {
        // JPA 分页从 0 开始，MyBatis-Plus 从 1 开始，需要转换
        Pageable pageable = PageRequest.of(pageNum.intValue() - 1, pageSize.intValue(),
                Sort.by(Sort.Direction.DESC, "createTime"));

        Specification<Asset> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                predicates.add(cb.or(
                        cb.like(root.get("assetCode"), "%" + keyword + "%"),
                        cb.like(root.get("assetName"), "%" + keyword + "%")
                ));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("categoryId"), categoryId));
            }
            if (deptId != null) {
                predicates.add(cb.equal(root.get("deptId"), deptId));
            }
            if (StringUtils.hasText(status)) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return assetRepository.findAll(spec, pageable);
    }

    @Override
    public Asset add(Asset asset) {
        // 检查资产编码是否存在
        if (assetRepository.existsByAssetCode(asset.getAssetCode())) {
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
        return assetRepository.save(asset);
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
        return assetRepository.save(asset);
    }

    @Override
    public boolean delete(Long id) {
        Asset asset = assetRepository.findById(id).orElse(null);
        if (asset != null && "IN_USE".equals(asset.getStatus())) {
            throw new BusinessException("使用中的资产不能删除");
        }
        assetRepository.deleteById(id);
        return true;
    }

    @Override
    public Asset getById(Long id) {
        return assetRepository.findById(id).orElse(null);
    }

    @Override
    public List<Asset> listAll() {
        return assetRepository.findAll();
    }

    @Override
    public boolean change(Long assetId, String changeType, String newValue, String reason, String operator) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new BusinessException("资产不存在"));

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
        assetRepository.save(asset);

        // 记录变动日志
        AssetChangeLog log = new AssetChangeLog();
        log.setAssetId(assetId);
        log.setChangeType(changeType);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        log.setReason(reason);
        log.setChangeTime(LocalDateTime.now());
        log.setOperator(operator);
        changeLogRepository.save(log);

        return true;
    }

    @Override
    public boolean transfer(Long assetId, Long toDeptId, Long toUserId, String reason, String operator) {
        // 资产调拨需要审批，这里简化处理直接变更
        return change(assetId, "DEPT", String.valueOf(toDeptId), reason, operator);
    }

    @Override
    public boolean updateMaintenanceDates(Long assetId, java.time.LocalDate lastMaintenanceDate, java.time.LocalDate nextMaintenanceDate) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new BusinessException("资产不存在"));
        asset.setLastMaintenanceDate(lastMaintenanceDate);
        asset.setNextMaintenanceDate(nextMaintenanceDate);
        assetRepository.save(asset);
        return true;
    }
}