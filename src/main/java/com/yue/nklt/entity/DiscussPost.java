package com.yue.nklt.entity;

import lombok.Data;
import java.util.Date;

/**
 * 帖子实体类
 */
@Data
public class DiscussPost {
    private Integer id;
    private String userId;  // 数据库中是varchar类型
    private String title;
    private String content;
    /**
     * 0-普通; 1-置顶;
     */
    private Integer type;
    /**
     * 0-正常; 1-精华; 2-拉黑;
     */
    private Integer status;
    private Date createTime;
    private Integer commentCount;
    private Double score;
}

