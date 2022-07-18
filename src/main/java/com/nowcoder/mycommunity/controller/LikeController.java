package com.nowcoder.mycommunity.controller;

import com.nowcoder.mycommunity.entity.Event;
import com.nowcoder.mycommunity.entity.User;
import com.nowcoder.mycommunity.event.EventProducer;
import com.nowcoder.mycommunity.service.LikeService;
import com.nowcoder.mycommunity.service.UserService;
import com.nowcoder.mycommunity.util.HostHolder;
import com.nowcoder.mycommunity.util.MyCommunityConstant;
import com.nowcoder.mycommunity.util.MyCommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-12-22:23
 */
@Controller
public class LikeController implements MyCommunityConstant {

    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId) {
        User user = hostHolder.getUser();
        if (user == null) {
            return MyCommunityUtil.getJSONString(1, "您没有登录，无法点赞");
        }
        int userId = user.getId();
        // 点赞
        likeService.like(userId, entityType, entityId, entityUserId);
        // 点赞数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        // 点赞状态（点赞还是取消赞）
        int likeStatus = likeService.findEntityLikeStatus(userId, entityType, entityId);
        // 封装
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        // 触发点赞事件
        if (likeStatus == 1) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);
            eventProducer.fireEvent(event);
        }

        return MyCommunityUtil.getJSONString(0, null, map);
    }
}
