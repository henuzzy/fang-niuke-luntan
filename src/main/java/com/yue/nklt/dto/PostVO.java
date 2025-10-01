package com.yue.nklt.dto;

import lombok.Data;
import java.util.Date;

/**
 * 帖子视图对象（包含用户信息）
 */
@Data
public class PostVO {
    private Integer id;
    private String userId;  // 数据库中是varchar类型
    private String username;
    private String title;
    private String content;
    private Integer type;
    private Integer status;
    private Date createTime;
    private Integer commentCount;
    private Integer likeCount;
    private Double score;
}

