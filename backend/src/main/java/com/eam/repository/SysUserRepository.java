package com.eam.repository;

import com.eam.entity.SysUser;
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
 * 系统用户 Repository
 */
@Repository
public interface SysUserRepository extends JpaRepository<SysUser, Long>, JpaSpecificationExecutor<SysUser> {

    /**
     * 根据用户名查询
     */
    Optional<SysUser> findByUsername(String username);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 根据部门查询
     */
    List<SysUser> findByDeptId(Long deptId);

    /**
     * 根据角色查询
     */
    List<SysUser> findByRoleId(Long roleId);

    /**
     * 根据状态查询
     */
    List<SysUser> findByStatus(Integer status);

    /**
     * 根据OAuth2提供者和用户ID查询
     */
    Optional<SysUser> findByOauth2ProviderAndOauth2UserId(String provider, String userId);

    /**
     * 多条件查询
     */
    @Query("SELECT u FROM SysUser u WHERE " +
           "(:username IS NULL OR u.username LIKE %:username%) AND " +
           "(:status IS NULL OR u.status = :status) AND " +
           "(:deptId IS NULL OR u.deptId = :deptId)")
    Page<SysUser> search(@Param("username") String username,
                        @Param("status") Integer status,
                        @Param("deptId") Long deptId,
                        Pageable pageable);
}