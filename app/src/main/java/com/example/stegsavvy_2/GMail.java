package com.example.stegsavvy_2;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class GMail {


    //Setting needed for Gmail to work with the app
    final String ePort = "587";
    final String smtpAuth = "true";
    final String starttls = "true";
    final String eHost = "smtp.gmail.com";

    String fEmail;
    String fPwd;
    String mailSub;
    String mailBody;
    List<String> emailList;
    Properties mailProperties;
    Session gMailSession;
    MimeMessage mailMessage;

    public GMail(String fEmail, String fPwd, List<String> emailList, String mailSub, String mailBody){
        this.fEmail = fEmail;
        this.fPwd = fPwd;
        this.emailList = emailList;
        this.mailSub = mailSub;
        this.mailBody = mailBody;

        //server Setting to get Gmail working here
        mailProperties = System.getProperties();
        mailProperties.put("mail.smtp.port", ePort);
        mailProperties.put("mail.smtp.auth", smtpAuth);
        mailProperties.put("mail.smtp.starttls.enable", starttls);
        Log.i("Gmail", "The Gmail Server Properties are now set up here...");
    }

    public MimeMessage createEmailMessage() throws AddressException,
        MessagingException, UnsupportedEncodingException {
            gMailSession = Session.getDefaultInstance(mailProperties,null);
            mailMessage = new MimeMessage(gMailSession);

            mailMessage.setFrom(new InternetAddress(fEmail, fEmail));
            for(String tEmail: emailList){
                Log.i("Gmail", "tEmail" + tEmail);
                mailMessage.addRecipient(Message.RecipientType.TO,
                        new InternetAddress(tEmail));
            }

            mailMessage.setSubject(mailSub);
            mailMessage.setContent(mailBody, "text/html");
            Log.i("GMail", "Your email has been created");
            return mailMessage;
    }

    public void SendMail() throws AddressException, MessagingException
    {
        Transport transport = gMailSession.getTransport("smtp");
        transport.connect(eHost, fEmail, fPwd);
        Log.i("Gmail", "All Receivers" + mailMessage.getAllRecipients());
        transport.sendMessage(mailMessage, mailMessage.getAllRecipients());
        transport.close();
        Log.i("Gmail", "Your Email has been sent spectacularly... ;)");
    }


}
