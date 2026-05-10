package com.eam.service;

import com.eam.entity.AssetTransfer;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 资产调拨 Service 接口
 */
public interface IAssetTransferService {

    Page<AssetTransfer> page(Long pageNum, Long pageSize, String status);

    AssetTransfer add(AssetTransfer transfer);

    AssetTransfer update(AssetTransfer transfer);

    boolean delete(Long id);

    List<AssetTransfer> list();

    AssetTransfer getById(Long id);

    List<AssetTransfer> listPending();

    List<AssetTransfer> listByAssetId(Long assetId);

    List<AssetTransfer> listByStatus(String status);

    Page<AssetTransfer> pageByAssetId(Long assetId, Long pageNum, Long pageSize);

    AssetTransfer approve(Long id, String approver, boolean approved, String remark);

    AssetTransfer complete(Long id, String operator, String completeRemark);
}