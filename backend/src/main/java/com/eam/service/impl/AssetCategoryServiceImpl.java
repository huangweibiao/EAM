package com.eam.service.impl;

import com.eam.common.BusinessException;
import com.eam.entity.AssetCategory;
import com.eam.repository.AssetCategoryRepository;
import com.eam.service.IAssetCategoryService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 资产分类 Service 实现类
 */
@Service
public class AssetCategoryServiceImpl implements IAssetCategoryService {

    private final AssetCategoryRepository assetCategoryRepository;

    public AssetCategoryServiceImpl(AssetCategoryRepository assetCategoryRepository) {
        this.assetCategoryRepository = assetCategoryRepository;
    }

    @Override
    public List<AssetCategory> tree() {
        List<AssetCategory> allCategories = assetCategoryRepository.findAll();
        return buildTree(allCategories, 0L);
    }

    private List<AssetCategory> buildTree(List<AssetCategory> allCategories, Long parentId) {
        return allCategories.stream()
                .filter(cat -> cat.getParentId().equals(parentId))
                .peek(cat -> cat.setChildren(buildTree(allCategories, cat.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public AssetCategory add(AssetCategory category) {
        if (StringUtils.hasText(category.getCategoryCode())) {
            Specification<AssetCategory> spec = (root, query, cb) ->
                    cb.equal(root.get("categoryCode"), category.getCategoryCode());
            long count = assetCategoryRepository.count(spec);
            if (count > 0) {
                throw new BusinessException("分类编码已存在");
            }
        }
        return assetCategoryRepository.save(category);
    }

    @Override
    public AssetCategory update(AssetCategory category) {
        if (category.getId() == null) {
            throw new BusinessException("分类ID不能为空");
        }
        return assetCategoryRepository.save(category);
    }

    @Override
    public boolean delete(Long id) {
        // 检查是否有子分类
        Specification<AssetCategory> spec = (root, query, cb) ->
                cb.equal(root.get("parentId"), id);
        long count = assetCategoryRepository.count(spec);
        if (count > 0) {
            throw new BusinessException("请先删除子分类");
        }
        assetCategoryRepository.deleteById(id);
        return true;
    }

    @Override
    public List<AssetCategory> list() {
        return assetCategoryRepository.findAll();
    }

    @Override
    public AssetCategory getById(Long id) {
        return assetCategoryRepository.findById(id).orElse(null);
    }
}
