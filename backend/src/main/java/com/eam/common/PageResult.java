package com.eam.common;

import java.util.List;

/**
 * 分页结果类
 */
public class PageResult<T> {

    private Long total;
    private Long pageNum;
    private Long pageSize;
    private List<T> records;

    public PageResult() {
    }

    public PageResult(Long total, Long pageNum, Long pageSize, List<T> records) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.records = records;
    }

    public static <T> PageResult<T> of(Long total, Long pageNum, Long pageSize, List<T> records) {
        return new PageResult<>(total, pageNum, pageSize, records);
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getPageNum() {
        return pageNum;
    }

    public void setPageNum(Long pageNum) {
        this.pageNum = pageNum;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }
}