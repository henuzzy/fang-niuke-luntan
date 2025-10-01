package com.yue.nklt.entity;

import lombok.Data;
import java.util.Date;

/**
 * 评论实体类
 */
@Data
public class Comment {
    private Integer id;
    private Integer userId;
    /**
     * 实体类型：1-帖子; 2-评论;
     */
    private Integer entityType;
    private Integer entityId;
    /**
     * 指向的用户id（评论的目标用户）
     */
    private Integer targetId;
    private String content;
    /**
     * 0-正常; 1-禁用;
     */
    private Integer status;
    private Date createTime;
}

