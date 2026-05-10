package com.eam.service.impl;

import com.eam.common.BusinessException;
import com.eam.entity.PurchaseRequest;
import com.eam.repository.PurchaseRequestRepository;
import com.eam.service.IPurchaseRequestService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 采购申请 Service 实现类
 */
@Service
public class PurchaseRequestServiceImpl implements IPurchaseRequestService {

    private final PurchaseRequestRepository purchaseRequestRepository;

    public PurchaseRequestServiceImpl(PurchaseRequestRepository purchaseRequestRepository) {
        this.purchaseRequestRepository = purchaseRequestRepository;
    }

    @Override
    public Page<PurchaseRequest> page(Long pageNum, Long pageSize, String status) {
        // JPA 分页从 0 开始，MyBatis-Plus 从 1 开始，需要转换
        Pageable pageable = PageRequest.of(pageNum.intValue() - 1, pageSize.intValue(),
                Sort.by(Sort.Direction.DESC, "createTime"));

        Specification<PurchaseRequest> spec = (root, query, cb) -> {
            ArrayList<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(status)) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return purchaseRequestRepository.findAll(spec, pageable);
    }

    @Override
    public PurchaseRequest add(PurchaseRequest request) {
        String requestNo = "PR" + System.currentTimeMillis();
        request.setRequestNo(requestNo);
        if (request.getRequestTime() == null) {
            request.setRequestTime(LocalDateTime.now());
        }
        if (request.getStatus() == null) {
            request.setStatus("ACTIVE");
        }
        if (request.getApproveStatus() == null) {
            request.setApproveStatus("PENDING");
        }
        if (request.getUrgency() == null) {
            request.setUrgency("NORMAL");
        }
        return purchaseRequestRepository.save(request);
    }

    @Override
    public PurchaseRequest approve(Long id, String approver, boolean approved, String remark) {
        PurchaseRequest request = purchaseRequestRepository.findById(id).orElse(null);
        if (request == null) {
            throw new BusinessException("采购申请不存在");
        }
        request.setApprover(approver);
        request.setApproveTime(LocalDateTime.now());
        request.setApproveStatus(approved ? "APPROVED" : "REJECTED");
        request.setApproveRemark(remark);
        if (approved) {
            request.setStatus("APPROVED");
        }
        return purchaseRequestRepository.save(request);
    }

    @Override
    public boolean delete(Long id) {
        purchaseRequestRepository.deleteById(id);
        return true;
    }

    @Override
    public PurchaseRequest getById(Long id) {
        return purchaseRequestRepository.findById(id).orElse(null);
    }

    @Override
    public List<PurchaseRequest> list() {
        return purchaseRequestRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"));
    }
}