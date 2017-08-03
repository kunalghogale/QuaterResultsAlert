package com.kunal.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.kunal.vo.StocksVO;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

@Service
public class SendMailService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void sendMail(List<StocksVO> stocksVOList, LocalDate date) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
            "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("earningscallalert", readKey());
                }
            });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("earningscallalert@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse("kunal.ghogale@gmail.com"));
            message.setSubject("Earning Calls For : " + date);
            message.setText(formatVOsToEmail(stocksVOList, date));
            message.setHeader("Content-type", "text/html");
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private String formatVOsToEmail(List<StocksVO> stocksVOS, LocalDate date) {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_25);
            cfg.setDirectoryForTemplateLoading(new File("src/main/templates/"));
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            Template temp = cfg.getTemplate("email.ftl");
            cfg.setLogTemplateExceptions(false);
            Writer out = new StringWriter(10000);
            Map<String, Object> input = new HashMap<>();
            input.put("title", "Earning Calls For : " + date);
            input.put("stocks", stocksVOS);
            temp.process(input, out);
            return out.toString();
        } catch (IOException e) {
            logger.error("Error loading templates");
        } catch (TemplateException te) {
            logger.error("Error writing templates");
        }
        return "";
    }

    private String readKey() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword("SUNRISETOMORROW");
        Properties props = new EncryptableProperties(encryptor);
        try {
            props.load(new FileInputStream("src/main/resources/application.properties"));
        } catch (IOException e) {
            logger.warn("Error in reading password");
        }
        return props.getProperty("key");
    }
}
