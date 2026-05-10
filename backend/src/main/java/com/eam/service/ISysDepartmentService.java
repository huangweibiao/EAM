package com.eam.service;

import com.eam.entity.SysDepartment;

import java.util.List;

/**
 * 系统部门 Service 接口
 */
public interface ISysDepartmentService {

    List<SysDepartment> tree();

    SysDepartment add(SysDepartment dept);

    SysDepartment update(SysDepartment dept);

    boolean delete(Long id);

    SysDepartment getById(Long id);

    List<SysDepartment> list();

    List<SysDepartment> listChildren(Long parentId);
}