package com.learnings.forum.utils;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

/**
 * Created with IntelliJ IDEA.
 * Description: 邮箱发送验证码
 * User: 12569
 * Date: 2023-12-02
 * Time: 14:25
 */
public class SendEmailUtil {

    /**
     * 发送邮箱验证码的实现
     * @return 邮箱验证码
     * @throws EmailException
     */
    public static String mail(String Email) throws EmailException {
        CodeUtil codeUtil = new CodeUtil();
        // 1-创建一个HtmlEmail实例对象
        HtmlEmail email = new HtmlEmail();
        // 2-设置邮箱的SMTP服务器
        email.setHostName("smtp.163.com");
        // 3-设置发送的字符类型
        email.setCharset("utf-8");
        // 4-设置收件人
        email.addTo(Email);
        // 5-发送人的邮箱为自己的，用户名可以随便填
        email.setFrom("yidan8080@163.com","FoxForum");
        // 6-设置发送人的邮箱和用户名和授权码(授权码是自己设置的)
        email.setAuthentication("yidan8080@163.com","DQBBNQUDJTEFGAAH");
        // 7-设置发送主题
        email.setSubject("找回密码验证");
        String Captcha = codeUtil.createCode();
        // 8-设置发送内容
        email.setMsg("[FoxForum-技术论坛] 您正在找回密码，验证码为："+String.valueOf(Captcha)+"\n" +
                "验证码有效期为5分钟，请及时填写！如非本人操作，请忽略。");
        // 9-进行发送
//        email.send();

        return Captcha;
    }
}
