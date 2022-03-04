package cn.ycc.api.admin.commons.utils;


import cn.ycc.api.admin.commons.support.CountableThreadPool;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2020.11.29 22:34
 *  需要依赖 javax.mail 包
 */
public class MailHelper {

    private MailConfig config;
    private Session session;

    private MailTaskAsyncExecutor mailTaskAsyncExecutor;

    public MailHelper(MailConfig config) {
        checkConfigRule(config);
        this.config = config;
        this.session = Session.getInstance(config);
    }

    private void checkConfigRule(MailConfig config) {
        if (config.getProtocol() == null ||
                config.getProtocol().trim().length() == 0) {
            throw new RuntimeException("Protocol不能为空");
        }else if (config.getHost() == null ||
                config.getHost().trim().length() == 0) {
            throw new RuntimeException("Host不能为空");
        }else if (config.getPort() == null ) {
            throw new RuntimeException("Port不能为空");
        }
    }

    private Transport getConnecTransport() {
        try {
            Transport transport = session.getTransport(config.getProtocol());
            transport.connect(config.host, config.getPort(), config.getUsername(), config.getPassword());
            return transport;
        } catch (Exception e) {
            throw new RuntimeException("获取Transport发生异常", e);
        }
    }

    public void sendSimplMail(String from, String to, String subject, String content,boolean isHtml) {
        MimeMessage mimeMessage = newMimeMessage();
        try {
            if(isHtml){
                mimeMessage.setContent(content, "text/html;charset=utf-8");
            }else{
                mimeMessage.setText(content);
            }
            mimeMessage.setFrom(new InternetAddress(config.getUsername(), from, config.getEncode()));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            mimeMessage.setSubject(subject);
            sendMail(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("发送邮件失败", e);
        }
    }

    public void sendAttachmentMail(String from, String to, String subject, String content, boolean isHtml, Set<File> attachmentFiles) {
        MimeMessage mimeMessage = newMimeMessage();
        try {
            final MimeMultipart mimeMultipart = new MimeMultipart();

            if (attachmentFiles != null
                    && !attachmentFiles.isEmpty()) {
                for (File attachmentFile : attachmentFiles) {
                    MimeBodyPart fileBodyPart = new MimeBodyPart();
                    DataHandler dataHandler = new DataHandler(new FileDataSource(attachmentFile));
                    fileBodyPart.setDataHandler(dataHandler);
                    fileBodyPart.setFileName(dataHandler.getName());

                    mimeMultipart.addBodyPart(fileBodyPart);
                }
            }

            MimeBodyPart contentPart = new MimeBodyPart();
            contentPart.setContent(content, (isHtml ? "text/html" : "text/plain") + ";charset=utf-8");
            mimeMultipart.addBodyPart(contentPart);

            mimeMessage.setContent(mimeMultipart);
            mimeMessage.setFrom(new InternetAddress(config.getUsername(), from, config.getEncode()));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            mimeMessage.setSubject(subject);
            sendMail(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("发送邮件失败", e);
        }
    }

    public MimeMessage newMimeMessage() {
        return new MimeMessage(session);
    }

    public void sendMail(MimeMessage... messages) {
        if (messages == null || messages.length == 0) {
            throw new RuntimeException("没有可以发送的对象");
        }
        Transport transport = null;
        try {
            for (MimeMessage message : messages) {
                if (transport == null) {
                    transport = getConnecTransport();
                }
                try {
                    message.setSentDate(new Date());
                    message.saveChanges();
                    Address[] addresses = message.getAllRecipients();
                    if (addresses != null && addresses.length > 0) {
                        transport.sendMessage(message, addresses);
                    }
                } catch (MessagingException e) {
                    throw new RuntimeException("发送失败", e);
                }
            }

        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                }
            }
        }
    }

    public void sendSimplMailAsync(String from, String to, String subject, String content, Boolean isHtml) {
        MailTaskAsyncExecutor asyncTaskExecutor = getAsyncTaskExecutor();
        asyncTaskExecutor.execute(()->{
            sendSimplMail(from,to,subject,content,isHtml);
        });
    }

    public void sendAttachmentMailAsync(String from, String to, String subject, String content, boolean isHtml, Set<File> attachmentFiles){
        MailTaskAsyncExecutor asyncTaskExecutor = getAsyncTaskExecutor();
        asyncTaskExecutor.execute(()->{
            sendAttachmentMail(from,to,subject,content,isHtml,attachmentFiles);
        });
    }

    private MailTaskAsyncExecutor getAsyncTaskExecutor() {
        if(mailTaskAsyncExecutor == null){
            synchronized (this){
                if(mailTaskAsyncExecutor == null) {
                    mailTaskAsyncExecutor = new MailTaskAsyncExecutor();
                    mailTaskAsyncExecutor.start();
                }
            }
        }
        return mailTaskAsyncExecutor;
    }

    public class MailTaskAsyncExecutor extends Thread{

        private final BlockingQueue<Runnable> runnables = new LinkedBlockingQueue<>(500);

        private CountableThreadPool countableThreadPool = new CountableThreadPool(5);

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()){
                try {
                    Runnable runnable = runnables.take();
                    countableThreadPool.execute(runnable);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        public void execute(Runnable runnable) {
            try {
                runnables.put(runnable);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static class MailConfig extends Properties {

        private String protocol;
        private String host;
        private Integer port;
        private String username;
        private String password;
        private String encode = "UTF-8";

        public MailConfig() {
            this.protocol = "smtp";
        }

        public MailConfig(String protocol, String host, Integer port) {
            this.protocol = protocol;
            this.host = host;
            this.port = port;
        }

        public MailConfig(String protocol, String host, Integer port, String username, String password) {
            this.protocol = protocol;
            this.host = host;
            this.port = port;
            this.username = username;
            this.password = password;
        }

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void useSSL(boolean flag) {
            setProperty("mail.smtp.ssl.enable", String.valueOf(flag));
        }

        public String getEncode() {
            return encode;
        }

        public void setEncode(String encode) {
            this.encode = encode;
        }
    }


    public static void main(String[] args) {
        MailConfig mailConfig = new MailConfig();
        mailConfig.setProtocol("smtp");
        mailConfig.setHost("smtp.qq.com");
        mailConfig.setPort(465);
        mailConfig.setUsername("1162280694@qq.com");
        mailConfig.setPassword("nxbtexihtgqfbaga");
        mailConfig.useSSL(true);

        Set<File> files = new HashSet<File>(
                Arrays.asList(new File("C:\\Users\\chaoq\\Desktop\\sonar检查修复对照表.xlsx"),
                        new File("C:\\Users\\chaoq\\Desktop\\重做基本信息表.jpg"))
        );

        MailHelper mailHelper = new MailHelper(mailConfig);
        mailHelper.sendAttachmentMail("来自星星的仰望", "chaoqunyu9311@163.com", "悻悻地仰望", "<h1 style='color:red'>你好啊年轻人</h1>", false, files);

    }
}
