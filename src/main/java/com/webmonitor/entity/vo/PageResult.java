package com.webmonitor.entity.vo;

import lombok.Data;
import java.util.List;

/**
 * 分页结果封装类
 */
@Data
public class PageResult<T> {
    /**
     * 总记录数
     */
    private long total;
    /**
     * 每页记录数
     */
    private long pageSize;
    /**
     * 总页数
     */
    private long totalPages;
    /**
     * 当前页码
     */
    private long pageNum;
    /**
     * 列表数据
     */
    private List<T> list;

    /**
     * 分页结果构造函数
     * @param list 列表数据
     * @param total 总记录数
     * @param pageNum 当前页码
     * @param pageSize 每页记录数
     */
    public PageResult(List<T> list, long total, long pageNum, long pageSize) {
        this.list = list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalPages = (total + pageSize - 1) / pageSize;
    }
}