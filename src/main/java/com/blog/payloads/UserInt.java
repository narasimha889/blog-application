package com.blog.payloads;

import lombok.Data;

@Data
public class UserInt {

	private Integer id;
	private String email;
	private String password;
	private String newPassword;
}
