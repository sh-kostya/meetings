package testgroup.meeting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private JavaMailSender mailSender;

    private SimpleMailMessage mailMessage;

    public JavaMailSender getMailSender() {
        return mailSender;
    }

    @Autowired
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public SimpleMailMessage getMailMessage() {
        return mailMessage;
    }

    @Autowired
    public void setMailMessage(SimpleMailMessage mailMessage) {
        this.mailMessage = mailMessage;
    }

    public void setTo(String email) {
        mailMessage.setTo(email);
    }

    public void setText(String text) {
        mailMessage.setText(text);
    }

    public void send(SimpleMailMessage message) {
        mailSender.send(message);
    }

}
