package com.eam.service.impl;

import com.eam.entity.SysOperationLog;
import com.eam.repository.SysOperationLogRepository;
import com.eam.service.ISysOperationLogService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 操作日志 Service实现
 */
@Service
public class SysOperationLogServiceImpl implements ISysOperationLogService {

    private final SysOperationLogRepository operationLogRepository;

    public SysOperationLogServiceImpl(SysOperationLogRepository operationLogRepository) {
        this.operationLogRepository = operationLogRepository;
    }

    @Override
    public void saveLog(SysOperationLog log) {
        operationLogRepository.save(log);
    }

    @Override
    public Page<SysOperationLog> page(Integer pageNum, Integer pageSize, String username, String operation, String module) {
        // JPA 分页从 0 开始，MyBatis-Plus 从 1 开始，需要转换
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize,
                Sort.by(Sort.Direction.DESC, "createTime"));

        Specification<SysOperationLog> spec = (root, query, cb) -> {
            ArrayList<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(username)) {
                predicates.add(cb.like(root.get("username"), "%" + username + "%"));
            }
            if (StringUtils.hasText(operation)) {
                predicates.add(cb.equal(root.get("operation"), operation));
            }
            if (StringUtils.hasText(module)) {
                predicates.add(cb.like(root.get("module"), "%" + module + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return operationLogRepository.findAll(spec, pageable);
    }

    @Override
    public SysOperationLog getById(Long id) {
        return operationLogRepository.findById(id).orElse(null);
    }

    @Override
    public List<SysOperationLog> getByUserId(Long userId) {
        return operationLogRepository.findByUserId(userId);
    }

    @Override
    public boolean removeById(Long id) {
        operationLogRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean removeByIds(List<Long> ids) {
        operationLogRepository.deleteAllById(ids);
        return true;
    }
}
