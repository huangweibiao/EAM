package com.eam.service.impl;

import com.eam.entity.AssetCategory;
import com.eam.repository.AssetCategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 带缓存的资产分类服务
 * 为常用静态数据添加Redis缓存
 */
@Service
@CacheConfig(cacheNames = "asset:category")
public class CachedAssetCategoryServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(CachedAssetCategoryServiceImpl.class);

    private final AssetCategoryRepository categoryRepository;

    @Autowired
    public CachedAssetCategoryServiceImpl(AssetCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * 获取所有资产分类（带缓存）
     * 缓存有效期：120分钟
     */
    @Cacheable(key = "'list:all'")
    public List<AssetCategory> listAll() {
        log.debug("从数据库获取所有资产分类列表");
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "sortOrder"));
    }

    /**
     * 获取分类树（带缓存）
     */
    @Cacheable(key = "'tree'")
    public List<AssetCategory> getTree() {
        log.debug("从数据库获取资产分类树");
        List<AssetCategory> allCategories = categoryRepository.findAll(
                Sort.by(Sort.Direction.ASC, "sortOrder"));
        // 构建树结构
        return buildTree(allCategories);
    }

    /**
     * 根据ID获取分类（带缓存）
     */
    @Cacheable(key = "'id:' + #id")
    public AssetCategory getById(Long id) {
        log.debug("从数据库获取资产分类: {}", id);
        return categoryRepository.findById(id).orElse(null);
    }

    /**
     * 清除分类缓存
     */
    @CacheEvict(allEntries = true)
    public void clearCache() {
        log.debug("清除资产分类缓存");
    }

    /**
     * 构建树结构
     */
    private List<AssetCategory> buildTree(List<AssetCategory> categories) {
        // 找到所有顶级节点
        List<AssetCategory> rootNodes = categories.stream()
                .filter(c -> c.getParentId() == null || c.getParentId() == 0)
                .toList();

        // 递归设置子节点
        for (AssetCategory root : rootNodes) {
            setChildren(root, categories);
        }

        return rootNodes;
    }

    /**
     * 递归设置子节点
     */
    private void setChildren(AssetCategory parent, List<AssetCategory> allCategories) {
        List<AssetCategory> children = allCategories.stream()
                .filter(c -> parent.getId().equals(c.getParentId()))
                .toList();

        for (AssetCategory child : children) {
            setChildren(child, allCategories);
        }
    }
}