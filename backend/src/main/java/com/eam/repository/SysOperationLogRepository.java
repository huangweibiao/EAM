package com.eam.repository;

import com.eam.entity.SysOperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 系统操作日志 Repository
 */
@Repository
public interface SysOperationLogRepository extends JpaRepository<SysOperationLog, Long>, JpaSpecificationExecutor<SysOperationLog> {

    /**
     * 根据用户ID查询操作日志
     */
    List<SysOperationLog> findByUserId(Long userId);

    /**
     * 根据用户名查询操作日志
     */
    List<SysOperationLog> findByUsername(String username);

    /**
     * 根据操作类型查询
     */
    List<SysOperationLog> findByOperation(String operation);

    /**
     * 根据操作模块查询
     */
    List<SysOperationLog> findByModule(String module);

    /**
     * 根据IP地址查询
     */
    List<SysOperationLog> findByIp(String ip);

    /**
     * 根据操作结果查询
     */
    List<SysOperationLog> findByResult(String result);

    /**
     * 多条件查询
     */
    @Query("SELECT l FROM SysOperationLog l WHERE " +
           "(:userId IS NULL OR l.userId = :userId) AND " +
           "(:username IS NULL OR l.username LIKE %:username%) AND " +
           "(:operation IS NULL OR l.operation = :operation) AND " +
           "(:module IS NULL OR l.module = :module) AND " +
           "(:result IS NULL OR l.result = :result) AND " +
           "(:ip IS NULL OR l.ip = :ip)")
    Page<SysOperationLog> search(@Param("userId") Long userId,
                                 @Param("username") String username,
                                 @Param("operation") String operation,
                                 @Param("module") String module,
                                 @Param("result") String result,
                                 @Param("ip") String ip,
                                 Pageable pageable);
}