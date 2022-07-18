package com.nowcoder.mycommunity.util;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-12-21:54
 */
public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";

    /**
     * 某个实体的赞：存入的redis数据的格式：like:entity:entityType:entityId -> set(userId)
     * 这里不单纯存int数量，而是存入集合，集合包含有userId，便于后面拓展获取点赞的用户
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return 返回在Redis用于存放数据的key
     */
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 根据用户id获取Redis的key
     * @param userId 用户id
     * @return key
     */
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    /**
     * 根据用户id和关注的实体类型返回key（关注）
     * @param userId 用户id
     * @param entityType 实体类型
     * @return 返回followee:userId:entityType -> ZSET(entityId, now)
     */
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    /**
     * 根据实体类和实体id返回key（粉丝）
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return 返回follower:entityType:entityId -> ZSET(userId, now);
     */
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 生成验证码key
     * @param owner 登录用户验证码的拥有者(生成了一个随机字符串发送出去作为凭证)
     * @return
     */
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    /**
     * 生成用户登录凭证的key
     * @param ticket 凭证
     * @return
     */
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    /**
     * 获取用户的key
     * @param userId 用户id
     * @return
     */
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }
}
