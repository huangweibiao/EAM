package com.eam.repository;

import com.eam.entity.Supplier;
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
 * 供应商 Repository
 */
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long>, JpaSpecificationExecutor<Supplier> {

    /**
     * 根据供应商编码查询
     */
    Optional<Supplier> findBySupplierCode(String supplierCode);

    /**
     * 检查供应商编码是否存在
     */
    boolean existsBySupplierCode(String supplierCode);

    /**
     * 根据合作状态查询
     */
    List<Supplier> findByCooperationStatus(String cooperationStatus);

    /**
     * 模糊查询供应商名称
     */
    List<Supplier> findBySupplierNameContaining(String keyword);

    /**
     * 模糊查询联系人
     */
    List<Supplier> findByContactPersonContaining(String keyword);

    /**
     * 根据联系电话查询
     */
    Optional<Supplier> findByPhone(String phone);

    /**
     * 根据邮箱查询
     */
    Optional<Supplier> findByEmail(String email);

    /**
     * 多条件查询
     */
    @Query("SELECT s FROM Supplier s WHERE " +
           "(:supplierCode IS NULL OR s.supplierCode LIKE %:supplierCode%) AND " +
           "(:supplierName IS NULL OR s.supplierName LIKE %:supplierName%) AND " +
           "(:contactPerson IS NULL OR s.contactPerson LIKE %:contactPerson%) AND " +
           "(:phone IS NULL OR s.phone = :phone) AND " +
           "(:cooperationStatus IS NULL OR s.cooperationStatus = :cooperationStatus)")
    Page<Supplier> search(@Param("supplierCode") String supplierCode,
                         @Param("supplierName") String supplierName,
                         @Param("contactPerson") String contactPerson,
                         @Param("phone") String phone,
                         @Param("cooperationStatus") String cooperationStatus,
                         Pageable pageable);
}