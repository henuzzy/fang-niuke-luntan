package com.yue.nklt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * 关注/取关相关业务
 */
@Service
public class FollowService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private String getFolloweeKey(int userId, int entityType) {
        return "followee:" + userId + ":" + entityType; // 我关注的实体
    }

    private String getFollowerKey(int entityType, int entityId) {
        return "follower:" + entityType + ":" + entityId; // 关注我的人
    }

    /**
     * 关注
     */
    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            @SuppressWarnings("unchecked")
            public Object execute(RedisOperations operations) {
                String followeeKey = getFolloweeKey(userId, entityType);
                String followerKey = getFollowerKey(entityType, entityId);
                long now = System.currentTimeMillis();
                operations.multi();
                operations.opsForZSet().add(followeeKey, entityId, (double) now);
                operations.opsForZSet().add(followerKey, userId, (double) now);
                return operations.exec();
            }
        });
    }

    /**
     * 取关
     */
    public void unfollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            @SuppressWarnings("unchecked")
            public Object execute(RedisOperations operations) {
                String followeeKey = getFolloweeKey(userId, entityType);
                String followerKey = getFollowerKey(entityType, entityId);
                operations.multi();
                operations.opsForZSet().remove(followeeKey, entityId);
                operations.opsForZSet().remove(followerKey, userId);
                return operations.exec();
            }
        });
    }

    /**
     * 某用户关注的实体数量
     */
    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = getFolloweeKey(userId, entityType);
        Long count = redisTemplate.opsForZSet().zCard(followeeKey);
        return count == null ? 0 : count;
    }

    /**
     * 某实体的粉丝数量
     */
    public long findFollowerCount(int entityType, int entityId) {
        String followerKey = getFollowerKey(entityType, entityId);
        Long count = redisTemplate.opsForZSet().zCard(followerKey);
        return count == null ? 0 : count;
    }

    /**
     * 当前用户是否已关注该实体
     */
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        String followeeKey = getFolloweeKey(userId, entityType);
        Double score = redisTemplate.opsForZSet().score(followeeKey, entityId);
        return score != null;
    }
}


