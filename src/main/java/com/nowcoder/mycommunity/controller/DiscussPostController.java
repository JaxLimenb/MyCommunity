package com.nowcoder.mycommunity.controller;

import com.nowcoder.mycommunity.entity.*;
import com.nowcoder.mycommunity.event.EventProducer;
import com.nowcoder.mycommunity.service.CommentService;
import com.nowcoder.mycommunity.service.DiscussPostService;
import com.nowcoder.mycommunity.service.LikeService;
import com.nowcoder.mycommunity.service.UserService;
import com.nowcoder.mycommunity.util.HostHolder;
import com.nowcoder.mycommunity.util.MyCommunityConstant;
import com.nowcoder.mycommunity.util.MyCommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-10-14:25
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements MyCommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private LikeService likeService;
    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return MyCommunityUtil.getJSONString(403, "你还没有登录！");
        }
        // 发布帖子，存入数据库
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        // 过滤敏感词并存入数据库
        discussPostService.addDiscussPost(post);
        // 发布成功后，触发发帖事件，将新帖子存入ES里
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        eventProducer.fireEvent(event);
        // 报错的情况，将来统一处理
        return MyCommunityUtil.getJSONString(0, "发布成功！");
    }

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        // 帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        // 作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("post", post);
        model.addAttribute("user", user);
        // 获取帖子的点赞数量
        long postLikeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
        model.addAttribute("postLikeCount", postLikeCount);
        // 获取帖子点赞状态
        User holderUser = hostHolder.getUser();
        int postLikeStatus;
        if (holderUser == null) {
            postLikeStatus = 0;
        }else {
            postLikeStatus = likeService.findEntityLikeStatus(holderUser.getId(), ENTITY_TYPE_POST, discussPostId);
        }
        model.addAttribute("postLikeStatus", postLikeStatus);
        // 获取评论信息
        page.setLimit(5);
        page.setRows(post.getCommentCount());
        page.setPath("/discuss/detail/" + discussPostId);
        // 获取所有评论集合
        List<Comment> commentList = commentService.findCommentsByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        // 评论：给帖子的评论；回复：给评论的回复
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                Map<String, Object> commentVo = new HashMap<>();
                // 评论
                commentVo.put("comment", comment);
                // 评论者
                commentVo.put("user", userService.findUserById(comment.getUserId()));
                // 点赞数量
                long commentLikeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("commentLikeCount", commentLikeCount);
                // 点赞状态
                int commentLikeStatus;
                if (holderUser == null) {
                    commentLikeStatus = 0;
                }else {
                    commentLikeStatus = likeService.findEntityLikeStatus(holderUser.getId(), ENTITY_TYPE_COMMENT, comment.getId());
                }
                commentVo.put("commentLikeStatus", commentLikeStatus);
                // 回复数量
                int replyCount = commentService.findCommentsCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);
                // 回复列表
                List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        // 回复
                        replyVo.put("reply", reply);
                        // 回复者
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        // 回复的目标评论作者
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);
                        // 回复的点赞数量
                        long replyLikeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("replyLikeCount", replyLikeCount);
                        // 回复的点赞状态
                        // 点赞状态
                        int replyLikeStatus;
                        if (holderUser == null) {
                            replyLikeStatus = 0;
                        }else {
                            replyLikeStatus = likeService.findEntityLikeStatus(holderUser.getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        }
                        replyVo.put("replyLikeStatus", replyLikeStatus);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replyVoList", replyVoList);

                commentVoList.add(commentVo);
            }
        }
        // 回复
        model.addAttribute("commentVoList", commentVoList);
        return "site/discuss-detail";
    }

}
