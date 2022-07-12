package com.nowcoder.mycommunity;

import com.nowcoder.mycommunity.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-06-21:16
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = MycommunityApplication.class)
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail() {
        mailClient.sendMail("2581402649@qq.com", "测试邮件发送", "这是一个测试邮件发送功能的邮件，不用回复");
    }

    @Test
    public void testHtmlMail() {
        Context context = new Context();
        context.setVariable("username", "sunday");
        // 利用模板引擎将html文件转换成文本文件
        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);
        mailClient.sendMail("2581402649@qq.com", "Html文本发送", content);
    }
}
