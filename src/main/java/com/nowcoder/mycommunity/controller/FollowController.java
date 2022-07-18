package com.nowcoder.mycommunity.controller;

import com.nowcoder.mycommunity.entity.Event;
import com.nowcoder.mycommunity.entity.Page;
import com.nowcoder.mycommunity.entity.User;
import com.nowcoder.mycommunity.event.EventProducer;
import com.nowcoder.mycommunity.service.FollowService;
import com.nowcoder.mycommunity.util.HostHolder;
import com.nowcoder.mycommunity.util.MyCommunityConstant;
import com.nowcoder.mycommunity.util.MyCommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.PipedReader;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-13-16:24
 */
@Controller
public class FollowController implements MyCommunityConstant {

    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        if (user == null) {
            throw new RuntimeException("用户未登录，无法关注！");
        }
        followService.follow(user.getId(), entityType, entityId);

        // 触发关注事件
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);

        return MyCommunityUtil.getJSONString(0, "已关注");
    }

    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unFollow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        if (user == null) {
            throw new RuntimeException("用户未登录，系统异常！");
        }
        followService.unFollow(user.getId(), entityType, entityId);
        return MyCommunityUtil.getJSONString(0, "已取消关注");
    }
}
