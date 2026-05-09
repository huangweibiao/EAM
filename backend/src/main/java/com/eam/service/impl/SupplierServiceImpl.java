package com.eam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eam.common.BusinessException;
import com.eam.entity.Supplier;
import com.eam.mapper.SupplierMapper;
import com.eam.service.ISupplierService;
import com.eam.util.AesEncryptUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 供应商 Service 实现类
 */
@Service
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements ISupplierService {

    @Override
    public List<Supplier> listAll() {
        return this.list();
    }

    @Override
    public Supplier add(Supplier supplier) {
        Long count = this.count(new LambdaQueryWrapper<Supplier>()
                .eq(Supplier::getSupplierCode, supplier.getSupplierCode()));
        if (count > 0) {
            throw new BusinessException("供应商编码已存在");
        }
        if (supplier.getCooperationStatus() == null) {
            supplier.setCooperationStatus("ACTIVE");
        }
        // 加密敏感字段
        encryptSensitiveFields(supplier);
        this.save(supplier);
        return supplier;
    }

    @Override
    public Supplier update(Supplier supplier) {
        if (supplier.getId() == null) {
            throw new BusinessException("供应商ID不能为空");
        }
        // 加密敏感字段
        encryptSensitiveFields(supplier);
        this.updateById(supplier);
        return supplier;
    }

    /**
     * 加密敏感字段
     */
    private void encryptSensitiveFields(Supplier supplier) {
        // 加密银行账号
        if (StringUtils.hasText(supplier.getBankAccount())
                && !AesEncryptUtil.isEncrypted(supplier.getBankAccount())) {
            supplier.setBankAccount(AesEncryptUtil.encrypt(supplier.getBankAccount()));
        }
        // 加密税号
        if (StringUtils.hasText(supplier.getTaxNo())
                && !AesEncryptUtil.isEncrypted(supplier.getTaxNo())) {
            supplier.setTaxNo(AesEncryptUtil.encrypt(supplier.getTaxNo()));
        }
    }

    @Override
    public boolean delete(Long id) {
        return this.removeById(id);
    }
}
