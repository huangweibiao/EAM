package com.eam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eam.entity.Asset;

import java.util.List;

/**
 * 资产 Service 接口
 */
public interface IAssetService extends IService<Asset> {

    IPage<Asset> page(Long pageNum, Long pageSize, String keyword, Long categoryId, Long deptId, String status);

    Asset add(Asset asset);

    Asset update(Asset asset);

    boolean delete(Long id);

    List<Asset> listAll();

    /**
     * 资产变动
     */
    boolean change(Long assetId, String changeType, String newValue, String reason, String operator);

    /**
     * 资产调拨
     */
    boolean transfer(Long assetId, Long toDeptId, Long toUserId, String reason, String operator);
}