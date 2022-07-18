package com.nowcoder.mycommunity.service;

import com.nowcoder.mycommunity.util.MyCommunityUtil;
import com.nowcoder.mycommunity.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-12-22:10
 */
@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 某个用户的点赞或者取消赞功能
     * @param userId 点赞的用户id
     * @param entityType 实体类型（帖子或者回复的类型）
     * @param entityId 实体id（帖子或者回复的id）
     * @param entityUserId 实体的用户id，即被点赞的帖子的拥有者
     */
    public void like(int userId, int entityType, int entityId, int entityUserId) {
        // 代码重构，扩展功能，当用户给一个帖子点赞时，备点赞的用户个人主要需要显示所有的点赞数量，同时该数量加1
        // 这里涉及事务操作，整个过程需要构成一个事务，改动较大，因此需要重构
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                // 查询必须放在事务之外
                Boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);
                // 开启事务
                operations.multi();
                if (isMember) {
                    // 如果点过赞，取消赞，并且赞的数量减1
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey);
                }else {
                    // 如果没点过赞，增加赞，并且赞的数量加1
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                }
                // 执行事务（结束事务）
                return operations.exec();
            }
        });
    }

    /**
     * 查询某实体点赞的数量
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return 返回点赞数量
     */
    public long findEntityLikeCount(int entityType, int entityId) {
        // 获取数据库中的key
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    /**
     * 查询某人对某实体的点赞状态，便于在页面上显示是否已点赞
     * @param userId 用户id
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return 返回点赞状态，0表示未点赞，1表示点赞，（扩展2表示踩）
     */
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        // 获取数据库中的key
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    /**
     * 根据用户id获取当前用户获得的所有赞
     * @param userId 用户id
     * @return 返回赞的总数
     */
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count;
    }
}
