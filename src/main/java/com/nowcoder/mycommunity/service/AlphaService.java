package com.nowcoder.mycommunity.service;

import com.nowcoder.mycommunity.dao.AlphaDao;
import com.nowcoder.mycommunity.dao.DiscussPostMapper;
import com.nowcoder.mycommunity.dao.UserMapper;
import com.nowcoder.mycommunity.entity.DiscussPost;
import com.nowcoder.mycommunity.entity.User;
import com.nowcoder.mycommunity.util.MyCommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.thymeleaf.TemplateEngine;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-03-22:28
 * 在实际开发中，一般
 * 使用Controller控制Service，
 * 使用Service调用Dao
 *
 */
@Service
@Scope("prototype")
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private TransactionTemplate transactionTemplate;

    public AlphaService() {
//        System.out.println("实例化AlphaService过程");
    }

    @PostConstruct
    public void init() {
//        System.out.println("初始化AlphaService");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("销毁AlphaService");
    }

    // 模拟实现查询的业务
    public String findDao() {
        return "findDao result: " + alphaDao.select();
    }

    /**
     * 模拟事务：声明式事务，通过注解实现。要求：新增一个用户以及该用户发表一个"新人报道"帖子，这两个操作构成一个事务
     * 1.isolation设置隔离级别：
     *      （1）READ_UNCOMMITTED：读取未提交（什么都不做，效率最高，隔离性最低）
     *      （2）READ_COMMITTED：读取已提交（只隔离脏读，效率较高，隔离性一般）
     *      （3）REPEATABLE_READ：可重复读（隔离脏读和可重复读，效率较低，隔离性较好）
     *      （4）SERIALIZABLE：序列化（全部隔离，包括脏读、可重复读、幻读，效率最低，隔离性最好）
     * 2.propagation设置传播机制（业务方法1可能会调用业务方法2，两个业务方法都可能定义了事务）
     *      传播机制就是解决事务交叉在一起的问题
     *      常见的有：
     *      （1）REQUIRED：支持当前事务(外部事务)，以当前事务为主，即1调用2以1为主
     *      （2）REQUIRES_NEW：创建一个新的事务，并且暂停外部事务，1调用2，2无视1的事务
     *      （3）NESTED：如果当前存在外部事务，则嵌套在该事务中执行（有独立的提交和回滚），否则和REQUIRED一样
     *
     * @return 返回结果
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1() {
        // 新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(MyCommunityUtil.getRandomString().substring(0, 5));
        user.setPassword(MyCommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 新增帖子
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("Hello!");
        post.setContent("新人报道！");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        Integer.valueOf("abc"); // 手动报错

        return "ok";
    }

    /**
     * 模拟事务：编程式事务，通过注入Bean实现（Spring自动创建、自动实现）
     * @return
     */
    public Object save2() {
        // 设置隔离级别
        transactionTemplate.setIsolationLevel(TransactionTemplate.ISOLATION_READ_COMMITTED);
        // 设置传播机制
        transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
        // 通过内部方法执行数据库操作
        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                // 新增用户
                User user = new User();
                user.setUsername("alpha");
                user.setSalt(MyCommunityUtil.getRandomString().substring(0, 5));
                user.setPassword(MyCommunityUtil.md5("123" + user.getSalt()));
                user.setEmail("alpha@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                // 新增帖子
                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("Hello!");
                post.setContent("新人报道！");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);

                Integer.valueOf("abc");

                return "ok";
            }
        });
    }
}

