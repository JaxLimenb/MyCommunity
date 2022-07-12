package com.nowcoder.mycommunity.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-06-21:01
 * 创建用于发送验证码邮箱的客户端
 */
@Component
public class MailClient {

    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    /**
     * 发送邮件的方法
     * @param targetMail 目标邮箱
     * @param subject 邮件标题
     * @param content 邮件内容
     */
    public void sendMail(String targetMail, String subject, String content) {
        try {
            // 创建邮箱类模板
            MimeMessage message = mailSender.createMimeMessage();
            // 通过Helper导入参数
            MimeMessageHelper helper = new MimeMessageHelper(message);
            // 设置发送的邮箱
            helper.setFrom(fromMail);
            // 设置目标邮箱
            helper.setTo(targetMail);
            // 设置邮箱标题
            helper.setSubject(subject);
            // 设置邮箱内容，允许支持html文本
            helper.setText(content, true);
            // 发送邮件
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("发送邮件失败：" + e.getMessage());
        }
    }
}
