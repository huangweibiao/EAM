package com.eam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eam.entity.SparePart;

import java.util.List;

/**
 * 备件 Service 接口
 */
public interface ISparePartService extends IService<SparePart> {

    IPage<SparePart> page(Long pageNum, Long pageSize, String keyword, Long categoryId, String status);

    SparePart add(SparePart sparePart);

    SparePart update(SparePart sparePart);

    boolean delete(Long id);

    List<SparePart> listAll();

    List<SparePart> listWarning();

    boolean updateQuantity(Long id, java.math.BigDecimal quantity);
}