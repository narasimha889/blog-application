package com.blog.utils.listener;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.blog.entities.User;
import com.blog.payloads.UserDto;
import com.blog.services.UserService;
import com.blog.utils.EmailSenderService;
import com.blog.utils.RegistrationCompleteEvent;
@Component
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent>{

	@Autowired
	private UserService userService;
	@Autowired
	private EmailSenderService emailSenderService;
	@Autowired
	private ModelMapper modelMapper;
	@Override
	public void onApplicationEvent(RegistrationCompleteEvent event) {
		String email = event.getEmail();
		UserDto userDto = this.userService.getUserByEmail(email);
		User user = this.modelMapper.map(userDto, User.class);
		String token = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
		userService.saveVerificationTokenForUser(token,user);
		//send email to user
		emailSenderService.sendEmail(user.getEmail(),token,"User verification for Blog Application");
		}

}
