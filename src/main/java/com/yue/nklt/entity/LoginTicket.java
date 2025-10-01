package com.yue.nklt.entity;

import lombok.Data;
import java.util.Date;

/**
 * 登录凭证实体类
 */
@Data
public class LoginTicket {
    private Integer id;
    private Integer userId;
    private String ticket;
    /**
     * 0-有效; 1-无效;
     */
    private Integer status;
    private Date expired;
}

