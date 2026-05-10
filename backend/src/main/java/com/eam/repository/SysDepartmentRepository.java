package com.eam.repository;

import com.eam.entity.SysDepartment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 系统部门 Repository
 */
@Repository
public interface SysDepartmentRepository extends JpaRepository<SysDepartment, Long>, JpaSpecificationExecutor<SysDepartment> {

    /**
     * 根据部门编码查询
     */
    Optional<SysDepartment> findByDeptCode(String deptCode);

    /**
     * 检查部门编码是否存在
     */
    boolean existsByDeptCode(String deptCode);

    /**
     * 根据父ID查询子部门
     */
    List<SysDepartment> findByParentId(Long parentId);

    /**
     * 根据父ID查询子部门并按排序字段升序
     */
    List<SysDepartment> findByParentIdOrderBySortOrderAsc(Long parentId);

    /**
     * 根据状态查询
     */
    List<SysDepartment> findByStatus(Integer status);

    /**
     * 模糊查询部门名称
     */
    List<SysDepartment> findByDeptNameContaining(String keyword);

    /**
     * 根据负责人查询
     */
    List<SysDepartment> findByManager(String manager);

    /**
     * 多条件查询
     */
    @Query("SELECT d FROM SysDepartment d WHERE " +
           "(:deptCode IS NULL OR d.deptCode LIKE %:deptCode%) AND " +
           "(:deptName IS NULL OR d.deptName LIKE %:deptName%) AND " +
           "(:parentId IS NULL OR d.parentId = :parentId) AND " +
           "(:status IS NULL OR d.status = :status) AND " +
           "(:manager IS NULL OR d.manager = :manager)")
    Page<SysDepartment> search(@Param("deptCode") String deptCode,
                              @Param("deptName") String deptName,
                              @Param("parentId") Long parentId,
                              @Param("status") Integer status,
                              @Param("manager") String manager,
                              Pageable pageable);
}