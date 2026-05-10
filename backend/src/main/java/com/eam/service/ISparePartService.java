package com.eam.service;

import com.eam.entity.SparePart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * 备件 Service 接口
 */
public interface ISparePartService {

    Page<SparePart> page(Long pageNum, Long pageSize, String keyword, Long categoryId, String status);

    SparePart add(SparePart sparePart);

    SparePart update(SparePart sparePart);

    boolean delete(Long id);

    List<SparePart> listAll();

    SparePart getById(Long id);

    List<SparePart> listWarning();

    boolean updateQuantity(Long id, BigDecimal quantity);

    List<SparePart> listPending();
}