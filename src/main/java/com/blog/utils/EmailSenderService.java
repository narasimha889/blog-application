package com.blog.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

	@Autowired
	private JavaMailSender mailSender;
	
	public void sendEmail(String toMail,String body, String subject) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("diarydailystore@gmail.com");
		message.setTo(toMail);
		message.setText(body);
		message.setSubject(subject);
		mailSender.send(message);
	}
}
