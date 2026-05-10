package com.eam.service;

import com.eam.entity.Asset;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 资产 Service 接口
 */
public interface IAssetService {

    Page<Asset> page(Long pageNum, Long pageSize, String keyword, Long categoryId, Long deptId, String status);

    Asset add(Asset asset);

    Asset update(Asset asset);

    boolean delete(Long id);

    List<Asset> listAll();

    Asset getById(Long id);

    /**
     * 资产变动
     */
    boolean change(Long assetId, String changeType, String newValue, String reason, String operator);

    /**
     * 资产调拨
     */
    boolean transfer(Long assetId, Long toDeptId, Long toUserId, String reason, String operator);

    /**
     * 更新资产维护日期
     * @param assetId 资产ID
     * @param lastMaintenanceDate 最后维护日期
     * @param nextMaintenanceDate 下次维护日期
     * @return 是否成功
     */
    boolean updateMaintenanceDates(Long assetId, java.time.LocalDate lastMaintenanceDate, java.time.LocalDate nextMaintenanceDate);
}