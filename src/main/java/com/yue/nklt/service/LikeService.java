package com.yue.nklt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

//

/**
 * 点赞相关业务
 */
@Service
public class LikeService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private String getEntityLikeKey(int entityType, int entityId) {
        return "like:entity:" + entityType + ":" + entityId;
    }

    private String getUserLikeCountKey(int userId) {
        return "like:user:" + userId + ":count";
    }

    /**
     * 点赞或取消点赞
     */
    public void like(int userId, int entityType, int entityId, int entityOwnerId) {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            @SuppressWarnings("unchecked")
            public Object execute(RedisOperations operations) {
                final String entityLikeKey = getEntityLikeKey(entityType, entityId);
                final String userLikeCountKey = getUserLikeCountKey(entityOwnerId);

                operations.watch(entityLikeKey);
                Boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);
                operations.multi();
                if (Boolean.TRUE.equals(isMember)) {
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeCountKey);
                } else {
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeCountKey);
                }
                return operations.exec();
            }
        });
    }

    /**
     * 查询某实体点赞数量
     */
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = getEntityLikeKey(entityType, entityId);
        Long size = redisTemplate.opsForSet().size(entityLikeKey);
        return size == null ? 0 : size;
    }

    /**
     * 查询某人对某实体的点赞状态（1 已赞，0 未赞）
     */
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = getEntityLikeKey(entityType, entityId);
        Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        return Boolean.TRUE.equals(isMember) ? 1 : 0;
    }

    /**
     * 查询某用户获得的赞数量
     */
    public int findUserLikeCount(int userId) {
        String key = getUserLikeCountKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(key);
        return count == null ? 0 : count;
    }
}


