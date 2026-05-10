package com.eam.service;

import com.eam.entity.AssetInventory;
import com.eam.entity.AssetInventoryDetail;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 资产盘点 Service 接口
 */
public interface IAssetInventoryService {

    Page<AssetInventory> page(Long pageNum, Long pageSize, String status);

    AssetInventory create(AssetInventory inventory);

    AssetInventory complete(Long id);

    List<AssetInventoryDetail> getDetails(Long inventoryId);

    boolean addDetail(AssetInventoryDetail detail);

    boolean updateDetail(AssetInventoryDetail detail);

    List<AssetInventory> list();

    AssetInventory getById(Long id);
}