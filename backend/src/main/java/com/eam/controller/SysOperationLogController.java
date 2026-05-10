package com.eam.controller;

import org.springframework.data.domain.Page;
import com.eam.common.PageResult;
import com.eam.common.Result;
import com.eam.entity.SparePart;
import com.eam.entity.SysOperationLog;
import com.eam.service.ISysOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志 Controller
 */
@RestController
@RequestMapping("/api/system/log")
public class SysOperationLogController {

    @Autowired
    private ISysOperationLogService operationLogService;

    /**
     * 分页查询操作日志
     */
    @GetMapping("/page")
    public Result<PageResult<SysOperationLog>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String operation) {
        Page<SysOperationLog> page = operationLogService.page(pageNum, pageSize, username, operation, module);
        PageResult<SysOperationLog> result = PageResult.of(
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                page.getContent()
        );
        return Result.success(result);
    }

    /**
     * 获取日志详情
     */
    @GetMapping("/{id}")
    public Result<SysOperationLog> getById(@PathVariable Long id) {
        SysOperationLog log = operationLogService.getById(id);
        return Result.success(log);
    }

    /**
     * 删除日志
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        operationLogService.removeById(id);
        return Result.success();
    }

    /**
     * 批量删除日志
     */
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody java.util.List<Long> ids) {
        operationLogService.removeByIds(ids);
        return Result.success();
    }
}
