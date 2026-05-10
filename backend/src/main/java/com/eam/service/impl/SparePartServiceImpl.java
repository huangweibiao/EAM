package com.eam.service.impl;

import com.eam.common.BusinessException;
import com.eam.entity.SparePart;
import com.eam.repository.SparePartRepository;
import com.eam.service.ISparePartService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 备件 Service 实现类
 */
@Service
public class SparePartServiceImpl implements ISparePartService {

    private final SparePartRepository sparePartRepository;

    public SparePartServiceImpl(SparePartRepository sparePartRepository) {
        this.sparePartRepository = sparePartRepository;
    }

    @Override
    public Page<SparePart> page(Long pageNum, Long pageSize, String keyword, Long categoryId, String status) {
        // JPA 分页从 0 开始，MyBatis-Plus 从 1 开始，需要转换
        Pageable pageable = PageRequest.of(pageNum.intValue() - 1, pageSize.intValue(),
                Sort.by(Sort.Direction.DESC, "createTime"));

        Specification<SparePart> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                predicates.add(cb.or(
                        cb.like(root.get("partCode"), "%" + keyword + "%"),
                        cb.like(root.get("partName"), "%" + keyword + "%")
                ));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("categoryId"), categoryId));
            }
            if (StringUtils.hasText(status)) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return sparePartRepository.findAll(spec, pageable);
    }

    @Override
    public SparePart add(SparePart sparePart) {
        Specification<SparePart> spec = (root, query, cb) ->
                cb.equal(root.get("partCode"), sparePart.getPartCode());
        long count = sparePartRepository.count(spec);
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
        return sparePartRepository.save(sparePart);
    }

    @Override
    public SparePart update(SparePart sparePart) {
        if (sparePart.getId() == null) {
            throw new BusinessException("备件ID不能为空");
        }
        return sparePartRepository.save(sparePart);
    }

    @Override
    public boolean delete(Long id) {
        sparePartRepository.deleteById(id);
        return true;
    }

    @Override
    public List<SparePart> listAll() {
        return sparePartRepository.findAll();
    }

    @Override
    public SparePart getById(Long id) {
        return sparePartRepository.findById(id).orElse(null);
    }

    @Override
    public List<SparePart> listWarning() {
        List<SparePart> allParts = sparePartRepository.findAll();
        return allParts.stream()
                .filter(part -> part.getQuantity() != null && part.getMinQuantity() != null
                        && part.getQuantity().compareTo(part.getMinQuantity()) < 0)
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateQuantity(Long id, BigDecimal quantity) {
        SparePart sparePart = sparePartRepository.findById(id).orElse(null);
        if (sparePart == null) {
            throw new BusinessException("备件不存在");
        }
        BigDecimal newQuantity = sparePart.getQuantity().add(quantity);
        if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("库存不足");
        }
        sparePart.setQuantity(newQuantity);
        sparePartRepository.save(sparePart);
        return true;
    }
}