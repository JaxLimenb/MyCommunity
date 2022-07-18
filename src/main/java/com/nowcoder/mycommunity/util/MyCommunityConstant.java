package com.nowcoder.mycommunity.util;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-07-10:58
 * 账号激活常量
 */
public interface MyCommunityConstant {
    // 激活成功
    int ACTIVATION_SUCCESS = 0;

    // 重复激活
    int ACTIVATION_REPEAT = 1;

    // 激活失败
    int ACTIVATION_FAILURE = 2;

    // 默认状态登录凭证的超时时间
    long DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    // 勾选“记住我”状态下的登录凭证的超时时间
    long REMEMBER_ME_EXPIRED_SECONDS = 3600 * 24 * 10;

    // 实体类型
    int ENTITY_TYPE_POST = 1; // 帖子
    int ENTITY_TYPE_COMMENT = 2; // 评论
    int ENTITY_TYPE_USER = 3; // 用户

    // 事件主题
    String TOPIC_COMMENT = "comment";
    String TOPIC_LIKE = "like";
    String TOPIC_FOLLOW = "follow";
    String TOPIC_PUBLISH = "publish";

    // 系统用户id
    int SYSTEM_USER_ID = 1;
}
