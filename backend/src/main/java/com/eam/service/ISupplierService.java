package com.eam.service;

import com.eam.entity.Supplier;

import java.util.List;

/**
 * 供应商 Service 接口
 */
public interface ISupplierService {

    List<Supplier> listAll();

    Supplier add(Supplier supplier);

    Supplier update(Supplier supplier);

    boolean delete(Long id);
}
