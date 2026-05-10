package com.eam.common;

import java.util.List;

/**
 * 分页结果类
 */
public class PageResult<T> {

    private long total;
    private int pageNum;
    private int pageSize;
    private List<T> records;

    public PageResult() {
    }

    public PageResult(long total, int pageNum, int pageSize, List<T> records) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.records = records;
    }

    public static <T> PageResult<T> of(long total,int pageNum,int pageSize,List<T> records){
        return new PageResult<>(total,pageNum,pageSize,records);
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }
}