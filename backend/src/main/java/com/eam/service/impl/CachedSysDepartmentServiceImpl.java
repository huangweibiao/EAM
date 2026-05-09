package com.eam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eam.entity.SysDepartment;
import com.eam.mapper.SysDepartmentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 带缓存的部门服务
 * 为常用静态数据添加Redis缓存
 */
@Slf4j
@Service
@CacheConfig(cacheNames = "sys:dept")
public class CachedSysDepartmentServiceImpl {

    @Autowired
    private SysDepartmentMapper departmentMapper;

    /**
     * 获取所有部门（带缓存）
     * 缓存有效期：60分钟
     */
    @Cacheable(key = "'list:all'")
    public List<SysDepartment> listAll() {
        log.debug("从数据库获取所有部门列表");
        return departmentMapper.selectList(new LambdaQueryWrapper<SysDepartment>()
                .orderByAsc(SysDepartment::getSortOrder));
    }

    /**
     * 根据ID获取部门（带缓存）
     */
    @Cacheable(key = "'id:' + #id")
    public SysDepartment getById(Long id) {
        log.debug("从数据库获取部门: {}", id);
        return departmentMapper.selectById(id);
    }

    /**
     * 清除部门缓存
     */
    @CacheEvict(allEntries = true)
    public void clearCache() {
        log.debug("清除部门缓存");
    }
}
