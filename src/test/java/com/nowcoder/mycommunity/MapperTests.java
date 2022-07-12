package com.nowcoder.mycommunity;

import com.nowcoder.mycommunity.dao.*;
import com.nowcoder.mycommunity.entity.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-05-19:49
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = MycommunityApplication.class)
public class MapperTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelectUser() {
        User user1 = userMapper.selectById(101);
        System.out.println(user1);
        User user2 = userMapper.selectByName("liubei");
        System.out.println(user2);
        User user3 = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user3);
    }

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("https://www.nowcoder.com");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void updateUser() {
        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150, "https://www.nowcoder.com/101.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150, "password123");
        System.out.println(rows);
    }

    @Test
    public void testSelectDiscussPosts() {
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 0, 10);
        discussPosts.forEach(System.out::println);
        int rows = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(rows);
    }

    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("123124");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        int rows = loginTicketMapper.insertLoginTicket(loginTicket);
        System.out.println(rows);
        System.out.println(loginTicket.getId());
    }

    @Test
    public void testSelectLoginTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("123124");
        System.out.println(loginTicket);

        System.out.println(loginTicket.getStatus());
        loginTicketMapper.updateStatus("123124", 1);
        loginTicket = loginTicketMapper.selectByTicket("123124");
        System.out.println(loginTicket.getStatus());
    }

    @Test
    public void testSelectComment() {
        List<Comment> comments = commentMapper.selectCommentsByEntity(1, 228, 0, 15);
        for (Comment comment : comments) {
            System.out.println(comment);
        }
        int rows = commentMapper.selectCountByEntity(1, 228);
        System.out.println(rows);
    }

    @Test
    public void testSelectMessage() {
        List<Message> conversations = messageMapper.selectConversations(111, 0, 20);
        for (Message m : conversations) {
            System.out.println(m);
        }
        int rows = messageMapper.selectConversationCount(111);
        System.out.println(rows);

        List<Message> letters = messageMapper.selectLetters("111_112", 0, 10);
        for (Message m : letters) {
            System.out.println(m);
        }
        int count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);

        int unreadCount = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(unreadCount);
    }
}
