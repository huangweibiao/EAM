package com.eam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eam.entity.SysDepartment;

import java.util.List;

/**
 * 系统部门 Service 接口
 */
public interface ISysDepartmentService extends IService<SysDepartment> {

    List<SysDepartment> tree();

    SysDepartment add(SysDepartment dept);

    SysDepartment update(SysDepartment dept);

    boolean delete(Long id);

    List<SysDepartment> listChildren(Long parentId);
}