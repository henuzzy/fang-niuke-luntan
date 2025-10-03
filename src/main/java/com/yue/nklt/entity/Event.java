package com.yue.nklt.entity;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

/**
 * 站内通知事件模型（用于Kafka传输）
 */
@Data
public class Event {
    private String topic;           // comment / like / follow
    private Integer userId;         // 触发者
    private Integer entityType;     // 实体类型：1-帖子 2-评论 3-用户
    private Integer entityId;       // 实体ID
    private Integer entityOwnerId;  // 实体作者/被关注者
    private Map<String, Object> data = new HashMap<>();

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}


