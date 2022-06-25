package com.pttbackend.pttclone.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;

import javax.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

import com.pttbackend.pttclone.model.NotificationMail;
import com.pttbackend.pttclone.model.User;

/**
 * <p> Send Mail to user for </p>
 * <p> activating the account and reseting the password </p>
 * <p> Java Mail API </p>
 * <p> {@code com.pttbackend.pttclone.model.NotificationMail}</p>
 * <p> {@code javax.mail.internet.MimeMessage}</p>
 * <p> {@code org.springframework.mail.MailException}</p>
 * <p> {@code org.springframework.mail.javamail.JavaMailSender}</p>
 * @see <a href="https://stackoverflow.com/questions/52321988/best-practice-for-value-fields-lombok-and-constructor-injection"> 
 *   If we use AllargsConstructor instad of RequiredArgsConstructor then error occurs
 *   Parameter 0 of constructor in com.pttbackend.pttclone.service.SendMailService required a bean of type 'java.lang.String' that could not be found. </a>
 * @see <a href="https://www.baeldung.com/spring-async">annotation @async </a>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SendMailService {

   @Value("${tokenurl.sendby}")
   private String email;
   
   private final JavaMailSender sender; 
   private final MailBodyBuilder mailBodyBuilder;

   

   /** 
    * <p> Via {@link MimeMessagePreparator}, {@link MimeMessageHelper} 
    *     and {@code mailBodyBuilder#BuilderHTMLContent} to set up mail </p>
    * <p> Via {@link JavaMailSender} SEND mail to user </p>
    * @param notificationMail {@link User}'s mail
    * @throws MailException Base class for all mail exceptions.
    * @see <a href="https://medium.com/@hyWang/%E9%9D%9E%E5%90%8C%E6%AD%A5-asynchronous-%E8%88%87%E5%90%8C%E6%AD%A5-synchronous-%E7%9A%84%E5%B7%AE%E7%95%B0-c7f99b9a298a">
    *       What IS Async </a>
    */
   @Async
   public void sendTokenMail(NotificationMail notificationMail){
      // set up mail protocol (from, sneder, subject, description ...)
      MimeMessagePreparator preparator = new MimeMessagePreparator() {
         @Override
         public void prepare(MimeMessage mimeMessage) throws Exception {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setFrom(email);
            message.setTo(notificationMail.getRecipient());
            message.setSubject(notificationMail.getSubject());
            message.setText(mailBodyBuilder.BuilderHTMLContent(notificationMail.getBody()));
         }
      };
      try {
         // JavaMailSender#send(MimeMessagePreparator)
         sender.send(preparator);
         log.info("The Mail Successfully Sent");
      }
      catch(MailException e){
         log.warn("Failed To Send The Mail To Recipient " + notificationMail.getRecipient());
      }
   }

   
}
