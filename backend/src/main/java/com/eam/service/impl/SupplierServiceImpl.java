package com.eam.service.impl;

import com.eam.common.BusinessException;
import com.eam.entity.Supplier;
import com.eam.repository.SupplierRepository;
import com.eam.service.ISupplierService;
import com.eam.util.AesEncryptUtil;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 供应商 Service 实现类
 */
@Service
public class SupplierServiceImpl implements ISupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public List<Supplier> listAll() {
        return supplierRepository.findAll();
    }

    @Override
    public Supplier add(Supplier supplier) {
        Specification<Supplier> spec = (root, query, cb) ->
                cb.equal(root.get("supplierCode"), supplier.getSupplierCode());
        long count = supplierRepository.count(spec);
        if (count > 0) {
            throw new BusinessException("供应商编码已存在");
        }
        if (supplier.getCooperationStatus() == null) {
            supplier.setCooperationStatus("ACTIVE");
        }
        // 加密敏感字段
        encryptSensitiveFields(supplier);
        return supplierRepository.save(supplier);
    }

    @Override
    public Supplier update(Supplier supplier) {
        if (supplier.getId() == null) {
            throw new BusinessException("供应商ID不能为空");
        }
        // 加密敏感字段
        encryptSensitiveFields(supplier);
        return supplierRepository.save(supplier);
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
        supplierRepository.deleteById(id);
        return true;
    }

    @Override
    public Supplier getById(Long id) {
        return supplierRepository.findById(id).orElse(null);
    }
}