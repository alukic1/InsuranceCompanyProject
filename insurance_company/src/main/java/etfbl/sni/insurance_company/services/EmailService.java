package etfbl.sni.insurance_company.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailService {

      @Autowired
      private JavaMailSender mailSender;

      public void sendEmail(String to, String subject, String body) {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
      }

      public void sendEmailWithPdfAttachment(String to, String subject, String text, File pdfFile) {
            try {
                  MimeMessage message = mailSender.createMimeMessage();
                  MimeMessageHelper helper = new MimeMessageHelper(message, true);
                  helper.setTo(to);
                  helper.setSubject(subject);
                  helper.setText(text);
                  helper.addAttachment(pdfFile.getName(), pdfFile);
                  mailSender.send(message);

            } catch (MessagingException e) {
                  e.printStackTrace();
            }
      }
}
