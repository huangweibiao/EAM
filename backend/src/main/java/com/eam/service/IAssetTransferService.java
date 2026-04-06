package com.eam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eam.entity.AssetTransfer;

import java.util.List;

/**
 * 资产调拨 Service 接口
 */
public interface IAssetTransferService extends IService<AssetTransfer> {

    IPage<AssetTransfer> page(Long pageNum, Long pageSize, String status);

    AssetTransfer create(AssetTransfer transfer);

    AssetTransfer approve(Long id, String approver, boolean approved);

    AssetTransfer complete(Long id);

    List<AssetTransfer> listPending();
}