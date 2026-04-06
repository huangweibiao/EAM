package com.eam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eam.entity.AssetCategory;

import java.util.List;

/**
 * 资产分类 Service 接口
 */
public interface IAssetCategoryService extends IService<AssetCategory> {

    List<AssetCategory> tree();

    AssetCategory add(AssetCategory category);

    AssetCategory update(AssetCategory category);

    boolean delete(Long id);
}