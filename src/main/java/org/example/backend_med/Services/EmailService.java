package org.example.backend_med.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
   private  JavaMailSender mailSender;
   public  void send(String to ,String subject,String content){
       SimpleMailMessage mailMessage = new SimpleMailMessage();
       mailMessage.setTo(to);
       mailMessage.setSubject(subject);
       mailMessage.setText(content);
       mailSender.send(mailMessage);
   }


}
