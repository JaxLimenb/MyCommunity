package com.nowcoder.mycommunity.service;

import com.nowcoder.mycommunity.dao.LoginTicketMapper;
import com.nowcoder.mycommunity.dao.UserMapper;
import com.nowcoder.mycommunity.entity.LoginTicket;
import com.nowcoder.mycommunity.entity.User;
import com.nowcoder.mycommunity.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-05-21:18
 */
@Service
public class UserService implements MyCommunityConstant {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private TemplateEngine templateEngine;

    @Value("${mycommunity.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 通过Mybatis利用用户id查找数据库信息
     * @param id id
     * @return 返回查找结果
     */
    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    /**
     * 通过Mybatis利用凭证ticket查找数据块信息
     * @param ticket 凭证
     * @return 返回查找的LoginTicket对象
     */
    public LoginTicket findLoginTicketByTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    /**
     * 通过姓名查找用户
     * @param username 姓名
     * @return 放回查找的用户
     */
    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }

    /**
     * 注册账户
     * @param user 注册界面输入信息传入user里面
     * @return 如果注册成功，返回null，注册失败返回对应失败信息
     */
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        // 控制判断
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        if (user.getSalt().length() < 8) {
            map.put("passwordMsg", "密码长度不能小于8位!");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }

        // 验证账号
        if (userMapper.selectByName(user.getUsername()) != null) {
            map.put("usernameMsg", "该账号已存在!");
            return map;
        }
        // 验证邮箱
        if (userMapper.selectByEmail(user.getEmail()) != null) {
            map.put("emailMsg", "该邮箱已被注册!");
            return map;
        }

        // 注册用户（存入数据库）
        user.setSalt(MyCommunityUtil.getRandomString().substring(0, 5));
        user.setPassword(MyCommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        // 设置随机激活码
        user.setActivationCode(MyCommunityUtil.getRandomString());
        // 设置随机头像
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }

    /**
     * 根据用户邮箱点击的传入的userId和激活码actCode判断是否激活成功，并返回
     * @param userId 用户id
     * @param actCode 用户激活码
     * @return 如果用户已经激活过，返回重复激活；如果用户激活码正确，返回激活成功；其他返回激活失败
     */
    public int activation(int userId, String actCode) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        }else if (user.getActivationCode().equals(actCode)) {
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAILURE;
        }
    }

    /**
     * 登录账户
     * @param username 用户名
     * @param password 密码
     * @param expiredSeconds 登录持续时间
     * @return 返回各种信息
     */
    public Map<String, Object> login(String username, String password, long expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // 判断账号密码是否为空
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }else if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }

        // 判断账号是否存在
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "账号不存在！");
            return map;
        }
        // 判断账号是否已激活
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "当前账号未激活！");
            return map;
        }
        // 判断密码是否正确
        password = MyCommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码错误！");
            return map;
        }

        // 创建新的用户凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(MyCommunityUtil.getRandomString());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        // 删去旧凭证
        loginTicketMapper.deleteLoginTicketByUserId(loginTicket.getTicket(), loginTicket.getUserId());

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    /**
     * 退出登录
     * @param ticket 根据外部传入的ticket将用户对应loginTicket的状态改为失效(1)
     */
    public void logout(String ticket) {
        if (StringUtils.isBlank(ticket)) {
            return;
        }
        loginTicketMapper.updateStatus(ticket, 1);
    }

    /**
     * 上传头像后更新头像路劲
     * @param userId 用户userId
     * @param headerUrl 头像路径存放位置
     * @return 返回修改行数
     */
    public int updateHeaderPicture(int userId, String headerUrl) {
        return userMapper.updateHeader(userId, headerUrl);
    }

    /**
     * 更新user里面的用户密码
     * @param user 待更新密码的用户
     * @param oldPassword 原始密码
     * @param newPassword 新密码
     * @return 返回更新信息
     */
    public Map<String, Object> updatePassword(User user, String oldPassword, String newPassword, String confirmPassword) {
        Map<String, Object> map = new HashMap<>();
        if (!confirmPassword.equals(newPassword)) {
            map.put("confirmPasswordMsg", "两次输入的密码不一致！");
            return map;
        }
        if (oldPassword == null) {
            map.put("oldPasswordMsg", "输入密码不能为空！");
            return map;
        }
        // 获取加密密码
        oldPassword = MyCommunityUtil.md5(oldPassword + user.getSalt());
        // 如果数据库存的密码与输入的密码不一致，返回信息
        if (!user.getPassword().equals(oldPassword)) {
            map.put("oldPasswordMsg", "输入密码错误！");
            return map;
        }
        // 新密码为空
        if (StringUtils.isBlank(newPassword)) {
            map.put("newPasswordMsg", "新密码不能为空！");
            return map;
        }
        // 新密码长度小于8
        if (newPassword.length() < 8) {
            map.put("newPasswordMsg", "密码长度不能小于8位！");
            return map;
        }
        // 将新密码加密
        newPassword = MyCommunityUtil.md5(newPassword + user.getSalt());
        // 新密码与原密码一致
        if (oldPassword.equals(newPassword)) {
            map.put("newPasswordMsg", "新密码与原来密码一致！");
            return map;
        }
        // 更新密码
        userMapper.updatePassword(user.getId(), newPassword);
        return map;
    }
}
