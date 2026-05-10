package com.eam.service.impl;

import com.eam.common.BusinessException;
import com.eam.entity.Asset;
import com.eam.entity.AssetInventory;
import com.eam.entity.AssetInventoryDetail;
import com.eam.repository.AssetInventoryDetailRepository;
import com.eam.repository.AssetInventoryRepository;
import com.eam.service.IAssetInventoryService;
import com.eam.service.IAssetService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
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
 * 资产盘点 Service 实现类
 */
@Service
public class AssetInventoryServiceImpl implements IAssetInventoryService {

    private final AssetInventoryRepository inventoryRepository;
    private final AssetInventoryDetailRepository detailRepository;
    private final IAssetService assetService;

    @Autowired
    public AssetInventoryServiceImpl(AssetInventoryRepository inventoryRepository,
                                      AssetInventoryDetailRepository detailRepository,
                                      IAssetService assetService) {
        this.inventoryRepository = inventoryRepository;
        this.detailRepository = detailRepository;
        this.assetService = assetService;
    }

    @Override
    public Page<AssetInventory> page(Long pageNum, Long pageSize, String status) {
        // JPA 分页从 0 开始，MyBatis-Plus 从 1 开始，需要转换
        Pageable pageable = PageRequest.of(pageNum.intValue() - 1, pageSize.intValue(),
                Sort.by(Sort.Direction.DESC, "createTime"));

        Specification<AssetInventory> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(status)) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return inventoryRepository.findAll(spec, pageable);
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
        List<Asset> allAssets = assetService.listAll();
        Long assetCount = (long) allAssets.size();
        inventory.setTotalAssetCount(assetCount.intValue());
        inventory.setActualCount(0);
        inventory.setMismatchCount(0);

        return inventoryRepository.save(inventory);
    }

    @Override
    public AssetInventory complete(Long id) {
        AssetInventory inventory = inventoryRepository.findById(id).orElse(null);
        if (inventory == null) {
            throw new BusinessException("盘点单不存在");
        }
        if (!"IN_PROGRESS".equals(inventory.getStatus())) {
            throw new BusinessException("只有进行中的盘点单可以完成");
        }

        // 统计实际盘点数量和差异
        List<AssetInventoryDetail> details = detailRepository.findByInventoryId(id);

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
        return inventoryRepository.save(inventory);
    }

    @Override
    public List<AssetInventoryDetail> getDetails(Long inventoryId) {
        return detailRepository.findByInventoryId(inventoryId);
    }

    @Override
    public boolean addDetail(AssetInventoryDetail detail) {
        if (detail.getInventoryTime() == null) {
            detail.setInventoryTime(LocalDateTime.now());
        }
        // 校验盘点差异需填写差异说明
        if (detail.getIsMatch() != null && detail.getIsMatch() == 0) {
            if (!StringUtils.hasText(detail.getRemark())) {
                throw new BusinessException("盘点存在差异时，必须填写差异说明");
            }
        }
        detailRepository.save(detail);
        return true;
    }

    @Override
    public List<AssetInventory> list() {
        return inventoryRepository.findAll();
    }

    @Override
    public AssetInventory getById(Long id) {
        return inventoryRepository.findById(id).orElse(null);
    }

    @Override
    public boolean updateDetail(AssetInventoryDetail detail) {
        detailRepository.save(detail);
        return true;
    }
}