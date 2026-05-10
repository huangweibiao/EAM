package com.eam.service;

import com.eam.entity.AssetTransfer;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 资产调拨 Service 接口
 */
public interface IAssetTransferService {

    Page<AssetTransfer> page(Long pageNum, Long pageSize, String status);

    AssetTransfer create(AssetTransfer transfer);

    AssetTransfer approve(Long id, String approver, boolean approved);

    AssetTransfer complete(Long id);

    List<AssetTransfer> list();

    AssetTransfer getById(Long id);

    List<AssetTransfer> listPending();
}