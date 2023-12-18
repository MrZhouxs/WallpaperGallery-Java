package com.kerwin.gallery.service;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class MailService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String host = "smtp.163.com";
    private final String port = "25";
    private final String username = "Mrzhouxs@163.com";
    private final String password = "zhouxs997464";

    private Session getMailSession() {
        Session session;
        // 从数据库中获取邮件的配置信息

        // 文件名经过base64编码后很容易超长，会导致文件名出错
        System.setProperty("mail.mime.splitlongparameters", "false");
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", port);
        properties.setProperty("mail.transport.protocol", "smtp");
        // SMTP服务器连接超时时间
        properties.setProperty("mail.smtp.connectiontimeout", "15000");
        // 发送邮件超时时间
        properties.setProperty("mail.smtp.timeout","60000");
        if (StrUtil.isNotEmpty(username)) {
            properties.setProperty("mail.smtp.auth", "true");
            session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        } else {
            properties.setProperty("mail.smtp.auth", "false");
            session = Session.getInstance(properties);
        }
        return session;
    }

    public Boolean sendSimpleMail(String content) {
        boolean flag = false;
        try {
            Session session = getMailSession();
            MimeMessage message = new MimeMessage(session);
            // 邮件发送人
            String clientName = "Kerwin壁纸";
            InternetAddress address;
            if (StrUtil.isNotEmpty(clientName)) {
                address = new InternetAddress(username, clientName, "UTF-8");
            } else {
                address = new InternetAddress(username);
            }
            message.setFrom(address);
            // 设置邮件主题
            message.setSubject("Kerwin壁纸——消息提醒");
            // 设置邮件内容
            message.setText(content);

            String receives = "863913681@qq.com";
            // 设置收件人（通用的收件人 + 临时收件人）；可以有多个接收者，中间用逗号隔开，以下类似
            if (StrUtil.isNotEmpty(receives)) {
                message.setRecipients(Message.RecipientType.TO, receives);
            }
            // 设置邮件抄送人，可以有多个抄送人
            String copy = null;
            if (StrUtil.isNotEmpty(copy)) {
                message.setRecipients(Message.RecipientType.CC, copy);
            }
            // 发送邮件
            Transport.send(message);
            flag = true;
            logger.debug("邮件发送成功");
        } catch (Exception e) {
            logger.debug("邮件发送失败");
        }
        return flag;
    }
}
