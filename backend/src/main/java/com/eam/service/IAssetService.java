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

    /**
     * 验证资产状态流转是否合法
     * @param currentStatus 当前状态
     * @param targetStatus 目标状态
     * @return 是否合法
     */
    boolean isValidStatusTransition(String currentStatus, String targetStatus);

    /**
     * 生成资产二维码
     * @param assetId 资产ID
     * @return 二维码Base64字符串
     */
    String generateAssetQrCode(Long assetId);

    /**
     * 生成资产二维码图片
     * @param assetId 资产ID
     * @param width 图片宽度
     * @param height 图片高度
     * @return 二维码图片的Base64字符串
     */
    String generateAssetQrCodeImage(Long assetId, int width, int height);

    /**
     * 更新资产的二维码字段
     * @param assetId 资产ID
     * @param qrCode 二维码内容
     * @return 是否成功
     */
    boolean updateAssetQrCode(Long assetId, String qrCode);

    /**
     * 根据资产分类获取资产列表
     * @param categoryId 资产分类ID
     * @return 资产列表
     */
    List<Asset> listByCategory(Long categoryId);
}