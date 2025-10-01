package com.yue.nklt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页结果DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageDTO<T> {
    /**
     * 总记录数
     */
    private Long total;
    /**
     * 总页数
     */
    private Integer pages;
    /**
     * 当前页码
     */
    private Integer page;
    /**
     * 数据列表
     */
    private List<T> rows;
}

