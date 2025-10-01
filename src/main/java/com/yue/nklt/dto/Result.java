package com.yue.nklt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 统一响应结果类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    /**
     * 响应码：1表示成功，0表示失败
     */
    private Integer code;
    /**
     * 提示信息
     */
    private String msg;
    /**
     * 返回的数据
     */
    private Object data;
    /**
     * 总数（用于分页）
     */
    private Long total;

    public static Result ok() {
        return new Result(1, "success", null, null);
    }

    public static Result ok(Object data) {
        return new Result(1, "success", data, null);
    }

    public static Result ok(String msg, Object data) {
        return new Result(1, msg, data, null);
    }

    public static Result ok(List<?> data, Long total) {
        return new Result(1, "success", data, total);
    }

    public static Result fail(String errorMsg) {
        return new Result(0, errorMsg, null, null);
    }
}

