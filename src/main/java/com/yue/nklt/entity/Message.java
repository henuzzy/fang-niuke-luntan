package com.yue.nklt.entity;

import lombok.Data;
import java.util.Date;

/**
 * 私信实体类
 */
@Data
public class Message {
    private Integer id;
    private Integer fromId;
    private Integer toId;
    private String conversationId;
    private String content;
    /**
     * 0-未读; 1-已读; 2-删除;
     */
    private Integer status;
    private Date createTime;
}

