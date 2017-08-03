package com.kunal.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import com.kunal.vo.StocksVO;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import gui.ava.html.image.generator.HtmlImageGenerator;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

@Service
public class OutputService {

    @Value("${twitter.consumer-key}")
    private String consumerKey;

    @Value("${twitter.consumer-secret}")
    private String consumerSecret;

    @Value("${twitter.access-token}")
    private String accessTokenKey;

    @Value("${twitter.access-token-secret}")
    private String accessTokenSecret;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public String sendMail(List<StocksVO> stocksVOList, LocalDate date) {
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
            String html = formatVOsToEmail(stocksVOList, date);
            message.setText(html);
            message.setHeader("Content-type", "text/html");
            Transport.send(message);
            return html;
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

    public void tweetImage(LocalDate date, String html) {

        try {
            HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
            imageGenerator.loadHtml(html);
            imageGenerator.saveAsImage(date + ".png");
            Twitter twitter = new TwitterFactory().getInstance();
            twitter.setOAuthConsumer(consumerKey, consumerSecret);
            AccessToken accessToken = new AccessToken(accessTokenKey, accessTokenSecret);
            twitter.setOAuthAccessToken(accessToken);
            StatusUpdate status = new StatusUpdate("Earnings call for: " + date);
            status.setMedia("calls", new FileInputStream(date + ".png"));
            twitter.updateStatus(status);
        } catch (TwitterException te) {
            te.printStackTrace();
        } catch (FileNotFoundException fe) {
            logger.error("File not found.", fe);
        }
    }
}
