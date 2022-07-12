package com.nowcoder.mycommunity.controller;

import com.nowcoder.mycommunity.MycommunityApplication;
import com.nowcoder.mycommunity.entity.Message;
import com.nowcoder.mycommunity.entity.Page;
import com.nowcoder.mycommunity.entity.User;
import com.nowcoder.mycommunity.service.MessageService;
import com.nowcoder.mycommunity.service.UserService;
import com.nowcoder.mycommunity.util.HostHolder;
import com.nowcoder.mycommunity.util.MyCommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.lang.model.type.IntersectionType;
import java.util.*;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-11-14:59
 */
@Controller
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page) {
        User user = hostHolder.getUser();
        // 设置分页逻辑
        page.setLimit(5);
        page.setPath("/message/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        // 会话列表
        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>();
                // 获取一条私信中的会话数量
                int letterCount = messageService.findLetterCount(message.getConversationId());
                // 获取这条私信中未读的会话数量
                int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), message.getConversationId());
                // 获取该私信的目标用户，便于显示目标头像等信息
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                User targetUser = userService.findUserById(targetId);
                map.put("conversation", message);
                map.put("letterCount", letterCount);
                map.put("unreadCount", letterUnreadCount);
                map.put("user", targetUser);
                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);
        // 查询所有私信的未读消息数量
        int conversationUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("conversationUnreadCount", conversationUnreadCount);
        return "site/letter";
    }

    @RequestMapping(path = "/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Model model, Page page) {
        // 分页信息
        page.setLimit(5);
        page.setPath("/message/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));
        // 获取私信里的所有内容
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message letter : letterList) {
                Map<String, Object> map = new HashMap<>();
                // 通过fromId查找私信的发送人，便于显示头像
                User fromUser = userService.findUserById(letter.getFromId());
                map.put("letter", letter);
                map.put("fromUser", fromUser);
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);
        // 私信目标
        User target = getLetterTarget(conversationId);
        model.addAttribute("target", target);
        // 将未读私信改为已读状态
        List<Integer> ids = getUnreadLetterIds(letterList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }
        return "site/letter-detail";
    }

    /**
     * 将conversationId中的双方分离，查找某次私信的用户
     * @param conversationId 会话id
     * @return 返回查找的用户
     */
    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if (hostHolder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        }else {
            return userService.findUserById(id0);
        }
    }

    /**
     * 获取私信列表中未读私信的id值
     * @param letterList 私信列表
     * @return 返回待修改私信状态的id集合
     */
    private List<Integer> getUnreadLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();
        if (letterList != null) {
            for (Message letter : letterList) {
                // 如果当前用户为私信的目标用户，且该私信为未读状态，将其加入集合中
                if (hostHolder.getUser().getId() == letter.getToId() && letter.getStatus() == 0) {
                    ids.add(letter.getId());
                }
            }
        }
        return ids;
    }

    /**
     * 发送私信功能
     * @param toName 发送的目标用户名
     * @param content 发送私信的内容
     * @return 返回异步请求的信息
     */
    @RequestMapping(path = "/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content) {
        User toUser = userService.findUserByName(toName);
        if (toUser == null) {
            return MyCommunityUtil.getJSONString(1, "目标用户不存在！");
        }

        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(toUser.getId());
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        }else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);

        return MyCommunityUtil.getJSONString(0);
    }
}
