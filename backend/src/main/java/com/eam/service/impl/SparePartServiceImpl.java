package com.eam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eam.common.BusinessException;
import com.eam.entity.SparePart;
import com.eam.mapper.SparePartMapper;
import com.eam.service.ISparePartService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 备件 Service 实现类
 */
@Service
public class SparePartServiceImpl extends ServiceImpl<SparePartMapper, SparePart> implements ISparePartService {

    @Override
    public IPage<SparePart> page(Long pageNum, Long pageSize, String keyword, Long categoryId, String status) {
        Page<SparePart> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SparePart> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SparePart::getPartCode, keyword)
                    .or().like(SparePart::getPartName, keyword));
        }
        if (categoryId != null) {
            wrapper.eq(SparePart::getCategoryId, categoryId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(SparePart::getStatus, status);
        }
        wrapper.orderByDesc(SparePart::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public SparePart add(SparePart sparePart) {
        Long count = this.count(new LambdaQueryWrapper<SparePart>()
                .eq(SparePart::getPartCode, sparePart.getPartCode()));
        if (count > 0) {
            throw new BusinessException("备件编码已存在");
        }
        if (sparePart.getQuantity() == null) {
            sparePart.setQuantity(BigDecimal.ZERO);
        }
        if (sparePart.getMinQuantity() == null) {
            sparePart.setMinQuantity(BigDecimal.ZERO);
        }
        if (sparePart.getMaxQuantity() == null) {
            sparePart.setMaxQuantity(BigDecimal.ZERO);
        }
        if (sparePart.getStatus() == null) {
            sparePart.setStatus("ACTIVE");
        }
        this.save(sparePart);
        return sparePart;
    }

    @Override
    public SparePart update(SparePart sparePart) {
        if (sparePart.getId() == null) {
            throw new BusinessException("备件ID不能为空");
        }
        this.updateById(sparePart);
        return sparePart;
    }

    @Override
    public boolean delete(Long id) {
        return this.removeById(id);
    }

    @Override
    public List<SparePart> listAll() {
        return this.list();
    }

    @Override
    public List<SparePart> listWarning() {
        List<SparePart> allParts = this.list();
        return allParts.stream()
                .filter(part -> part.getQuantity() != null && part.getMinQuantity() != null
                        && part.getQuantity().compareTo(part.getMinQuantity()) < 0)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public boolean updateQuantity(Long id, BigDecimal quantity) {
        SparePart sparePart = this.getById(id);
        if (sparePart == null) {
            throw new BusinessException("备件不存在");
        }
        BigDecimal newQuantity = sparePart.getQuantity().add(quantity);
        if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("库存不足");
        }
        sparePart.setQuantity(newQuantity);
        return this.updateById(sparePart);
    }
}