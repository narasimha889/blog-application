package com.blog.utils;

import org.springframework.context.ApplicationEvent;

import com.blog.entities.User;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RegistrationCompleteEvent extends ApplicationEvent {
	private String email;
	public RegistrationCompleteEvent(String email) {
		super(email);
		this.email = email;
	}

}
