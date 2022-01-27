package boot.spring.util.mail;

import boot.spring.mapper.MailInfoMapper;
import boot.spring.po.MailInfo;
import boot.spring.util.AppException;
import com.alibaba.fastjson.JSON;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static boot.spring.util.Constants.ERRORCODE_MAILEERROR;

/**
 * @Author duxiangdong
 * @Date 2022/1/25 09:16
 * @Version 1.0
 */
@Component
public class MailHelper {
    private static final Logger logger = LoggerFactory.getLogger(MailHelper.class);

    public static void main(String[] args) {
//
//        String[] emailNames = {"18896588320@163.com"};
//        //抄送人
//        String[] cc = {"18896588320@163.com"};
//
//        ByteArrayOutputStream baops = new ByteArrayOutputStream();
//
//        MailHelper.syncSendMailAddFile(emailNames, cc, "测试邮件", null, "ceshi.xlsx", "", baops);

    }

    @Autowired
    MailInfoMapper mailInfoMapper;

    /**
     * 添加附件
     * 异步调用
     *
     * @param toAdder
     * @param cc
     * @param subject
     * @param text
     * @param attachName
     * @param outputStream
     * @throws AppException
     */
    @Async
    public void syncSendMailAddFile(String[] toAdder, String[] cc, String subject, String text, String attachName, OutputStream outputStream) throws AppException {

        try {
            sendMimeMailOutPut(toAdder, cc, subject, text, attachName, outputStream);
        } catch (Exception e) {
            logger.error(text + "邮件发送失败{[]}", e);
            throw new AppException(ERRORCODE_MAILEERROR, e);
        }
    }

    /**
     * 邮箱参数配置
     *
     * @param toAdder
     * @param cc
     * @param subject
     * @param text
     * @param attachName
     * @param attachOutput
     * @throws AppException
     */
    public void sendMimeMailOutPut(String[] toAdder, String[] cc, String subject, String text, String attachName, OutputStream attachOutput) throws AppException {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        MailInfo mailInfo = mailInfoMapper.getMailInfo();
        if (mailInfo == null) {
            logger.warn("参数配置表没有配置163邮箱!");
            throw new AppException(ERRORCODE_MAILEERROR, "参数配置表没有配置163邮箱!");
        }
        mailSender.setHost(mailInfo.getHost());
        mailSender.setPort(mailInfo.getPort());
        mailSender.setUsername(mailInfo.getUsername());
        mailSender.setPassword(mailInfo.getPassword());//163邮箱授权码 UFPPUHFOQCPZVEZA
        mailSender.setDefaultEncoding("UTF-8");
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");//true表示需要身份验证，false表示不需要身份验证！
        properties.put("mail.smtp.timeout", mailInfo.getTimeout());
        mailSender.setJavaMailProperties(properties);
        logger.info("邮件发送收件人：" + JSON.toJSONString(toAdder));
        logger.info("邮件抄送人：" + JSON.toJSONString(cc));
        logger.info("邮件主题：" + subject);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            // 设置邮件消息的发送者
            helper.setFrom(mailInfo.getUsername());
            helper.setTo(toAdder);
            if (cc != null && cc.length > 0) {
                List<String> collect = Arrays.stream(cc).filter(x -> {
                    for (String addr : toAdder) {
                        if (StringUtils.equalsIgnoreCase(x, addr)) return false;
                    }
                    return true;
                }).collect(Collectors.toList());
                if (collect.size() > 0)
                    helper.setCc(collect.toArray(new String[collect.size()]));
            }
            helper.setSubject(subject);
            helper.setText(text, true);
            if (attachOutput != null) {
                InputStream inputStream = new ByteArrayInputStream(((ByteArrayOutputStream) attachOutput).toByteArray());
                helper.addAttachment(attachName, new ByteArrayResource(IOUtils.toByteArray(inputStream)));
            }
            mailSender.send(message);
            logger.info("邮件发送成功！");
        } catch (MailException | javax.mail.MessagingException e) {
            logger.warn("发送邮件失败", e);
            throw new AppException(ERRORCODE_MAILEERROR, e);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}