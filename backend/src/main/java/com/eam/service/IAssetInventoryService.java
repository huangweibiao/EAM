package com.eam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eam.entity.AssetInventory;
import com.eam.entity.AssetInventoryDetail;

import java.util.List;

/**
 * 资产盘点 Service 接口
 */
public interface IAssetInventoryService extends IService<AssetInventory> {

    IPage<AssetInventory> page(Long pageNum, Long pageSize, String status);

    AssetInventory create(AssetInventory inventory);

    AssetInventory complete(Long id);

    List<AssetInventoryDetail> getDetails(Long inventoryId);

    boolean addDetail(AssetInventoryDetail detail);

    boolean updateDetail(AssetInventoryDetail detail);
}