package com.eam.service.impl;

import com.eam.annotation.OperationLog;
import com.eam.common.BusinessException;
import com.eam.entity.Asset;
import com.eam.repository.AssetRepository;
import com.eam.service.IAssetService;
import com.eam.util.QrCodeUtil;
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

    @Override
    public boolean isValidStatusTransition(String currentStatus, String targetStatus) {
        if (currentStatus == null || targetStatus == null) {
            return false;
        }

        switch (currentStatus) {
            case "NEW":
                // NEW -> IN_USE 或 NEW -> MAINTENANCE
                return "IN_USE".equals(targetStatus) || "MAINTENANCE".equals(targetStatus);
            case "IN_USE":
                // IN_USE -> MAINTENANCE 或 IN_USE -> SCRAP 或 IN_USE -> LOST
                return "MAINTENANCE".equals(targetStatus) || "SCRAP".equals(targetStatus) || "LOST".equals(targetStatus);
            case "MAINTENANCE":
                // MAINTENANCE -> IN_USE
                return "IN_USE".equals(targetStatus);
            case "SCRAP":
            case "LOST":
                // SCRAP和LOST是最终状态，不能再转换
                return false;
            default:
                return false;
        }
    }

    /**
     * 生成资产二维码
     * Task 10.2.1: 实现生成资产二维码方法
     * Task 10.2.2: 二维码包含资产基本信息
     */
    @Override
    @OperationLog(value = "生成资产二维码", description = "生成资产二维码", operationType = "READ", recordParams = true, recordResult = false)
    public String generateAssetQrCode(Long assetId) {
        try {
            Asset asset = getById(assetId);
            if (asset == null) {
                throw new BusinessException("资产不存在");
            }

            // 生成二维码内容：EAM_ASSET:资产ID:资产编码:资产名称
            String qrContent = QrCodeUtil.generateAssetQrCodeContent(
                    asset.getId(),
                    asset.getAssetCode(),
                    asset.getAssetName()
            );

            // 生成二维码Base64字符串
            String base64QrCode = QrCodeUtil.generateQrCodeToBase64(qrContent);
            
            // 更新资产的qr_code字段
            updateAssetQrCode(assetId, qrContent);
            
            return base64QrCode;
        } catch (Exception e) {
            logger.error("生成资产二维码失败: {}", e.getMessage(), e);
            throw new BusinessException("生成资产二维码失败: " + e.getMessage());
        }
    }

    /**
     * 生成资产二维码图片
     */
    @Override
    @OperationLog(value = "生成资产二维码图片", description = "生成资产二维码图片", operationType = "READ", recordParams = true, recordResult = false)
    public String generateAssetQrCodeImage(Long assetId, int width, int height) {
        try {
            Asset asset = getById(assetId);
            if (asset == null) {
                throw new BusinessException("资产不存在");
            }

            // 生成二维码内容
            String qrContent = QrCodeUtil.generateAssetQrCodeContent(
                    asset.getId(),
                    asset.getAssetCode(),
                    asset.getAssetName()
            );

            // 生成二维码图片Base64字符串
            String base64QrCode = QrCodeUtil.generateQrCodeToBase64(qrContent, width, height);
            
            // 更新资产的qr_code字段
            updateAssetQrCode(assetId, qrContent);
            
            return base64QrCode;
        } catch (Exception e) {
            logger.error("生成资产二维码图片失败: {}", e.getMessage(), e);
            throw new BusinessException("生成资产二维码图片失败: " + e.getMessage());
        }
    }

    /**
     * 更新资产的二维码字段
     * Task 10.2.3: 二维码文件存储管理
     * Task 10.2.4: 更新资产qr_code字段
     */
    @Override
    @Transactional
    public boolean updateAssetQrCode(Long assetId, String qrCode) {
        try {
            Asset asset = getById(assetId);
            if (asset == null) {
                return false;
            }

            // 验证二维码内容是否为资产二维码
            if (!QrCodeUtil.isAssetQrCode(qrCode)) {
                throw new BusinessException("二维码内容格式错误");
            }

            // 更新资产的qr_code字段
            asset.setQrCode(qrCode);
            assetRepository.save(asset);
            
            logger.info("资产二维码已更新: 资产ID={}, 二维码内容={}", assetId, qrCode);
            return true;
        } catch (Exception e) {
            logger.error("更新资产二维码失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<Asset> listByCategory(Long categoryId) {
        return assetRepository.findByCategoryId(categoryId);
    }
}