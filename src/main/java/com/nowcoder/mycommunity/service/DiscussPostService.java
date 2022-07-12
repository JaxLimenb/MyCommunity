package com.nowcoder.mycommunity.service;

import com.nowcoder.mycommunity.dao.DiscussPostMapper;
import com.nowcoder.mycommunity.entity.DiscussPost;
import com.nowcoder.mycommunity.util.MyCommunityUtil;
import com.nowcoder.mycommunity.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;
import java.util.List;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-05-21:13
 */
@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    public int findDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }
    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }
    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCountById(id, commentCount);
    }

    // 需要对post里面的title和content做敏感词处理
    // 需要对标签进行过滤，如<script>abc</script>，这种加载到页面中会影响页面的功能，需要去掉小于号大于号这种

    /**
     * 定义了私有的对敏感词进行处理的方法
     * @param post
     * @return
     */
    private int addDiscussPost(DiscussPost post) {
        if (post == null) {
            throw new IllegalArgumentException("帖子参数不能为空，发表帖子失败！");
        }
        // 转义html标记（标签过滤）
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        // 过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    /**
     * 发布帖子
     * @param title 标题
     * @param content 正文
     * @param userId 用户id
     * @return 返回修改行数
     */
    public int publish(String title, String content, int userId) {
        DiscussPost post = new DiscussPost();
        post.setUserId(userId);
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        // 过滤敏感词并存入数据库
        return addDiscussPost(post);
    }


}
