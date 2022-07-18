package com.nowcoder.mycommunity.service;

import com.nowcoder.mycommunity.entity.User;
import com.nowcoder.mycommunity.util.MyCommunityConstant;
import com.nowcoder.mycommunity.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.util.*;


/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-13-16:05
 */
@Service
public class FollowService implements MyCommunityConstant {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserService userService;

    /**
     * 关注功能
     * @param userId 用户id
     * @param entityType 实体类型
     * @param entityId 实体id
     */
    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 获取关注和粉丝的key
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                // 开启事务
                operations.multi();
                // 存入关注
                operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                // 存入粉丝
                operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());
                // 启用事务
                return operations.exec();
            }
        });
    }

    /**
     * 取消关注
     * @param userId 用户id
     * @param entityType 实体类型
     * @param entityId 实体id
     */
    public void unFollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 获取取消关注和粉丝的key
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                // 开启事务
                operations.multi();
                // 删除关注
                operations.opsForZSet().remove(followeeKey, entityId);
                // 删除粉丝
                operations.opsForZSet().remove(followerKey, userId);
                // 启用事务
                return operations.exec();
            }
        });
    }

    public long getFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    public long getFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    public boolean isFollowed(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Double score = redisTemplate.opsForZSet().score(followeeKey, entityId);
        return score != null;
    }

    /**
     * 查询某用户关注的人
     * @param userId 用户id
     * @param offset 显示关注的人的起始位置
     * @param limit 每页显示多少
     * @return 返回集合，包含关注的人以及关注时间
     */
    public List<Map<String, Object>> getFollowees(int userId, int offset, int limit) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, ENTITY_TYPE_USER);
        // 因为支持分页，所以查找从起始位置offset到终止位置offset+limit-1的所有key
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);
        if (targetIds == null) {
            return null;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer targetId : targetIds) {
            Map<String, Object> map = new HashMap<>();
            // 获取关注列表的用户
            User followee = userService.findUserById(targetId);
            map.put("followee", followee);
            // 获取关注的时间
            Double score = redisTemplate.opsForZSet().score(followeeKey, targetId);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }

    /**
     * 查询某用户的粉丝
     * @param userId 用户id
     * @param offset 显示粉丝的起始位置
     * @param limit 每页显示多少
     * @return 返回集合，包含粉丝用户以及关注的时间
     */
    public List<Map<String, Object>> getFollowers(int userId, int offset, int limit) {
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, userId);
        // 因为支持分页，所以查找从起始位置offset到终止位置offset+limit-1的所有key
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);
        if (targetIds == null) {
            return null;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer targetId : targetIds) {
            Map<String, Object> map = new HashMap<>();
            // 获取粉丝列表的用户
            User follower = userService.findUserById(targetId);
            map.put("follower", follower);
            // 获取粉丝关注的时间
            Double score = redisTemplate.opsForZSet().score(followerKey, targetId);
            map.put("followTime", new Date(score.longValue()));

            list.add(map);
        }
        return list;
    }
}
