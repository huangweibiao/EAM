package com.eam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eam.common.BusinessException;
import com.eam.entity.AssetCategory;
import com.eam.mapper.AssetCategoryMapper;
import com.eam.service.IAssetCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 资产分类 Service 实现类
 */
@Service
public class AssetCategoryServiceImpl extends ServiceImpl<AssetCategoryMapper, AssetCategory> implements IAssetCategoryService {

    @Override
    public List<AssetCategory> tree() {
        List<AssetCategory> allCategories = this.list();
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
            Long count = this.count(new LambdaQueryWrapper<AssetCategory>()
                    .eq(AssetCategory::getCategoryCode, category.getCategoryCode()));
            if (count > 0) {
                throw new BusinessException("分类编码已存在");
            }
        }
        this.save(category);
        return category;
    }

    @Override
    public AssetCategory update(AssetCategory category) {
        if (category.getId() == null) {
            throw new BusinessException("分类ID不能为空");
        }
        this.updateById(category);
        return category;
    }

    @Override
    public boolean delete(Long id) {
        // 检查是否有子分类
        Long count = this.count(new LambdaQueryWrapper<AssetCategory>()
                .eq(AssetCategory::getParentId, id));
        if (count > 0) {
            throw new BusinessException("请先删除子分类");
        }
        return this.removeById(id);
    }
}