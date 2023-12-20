package com.blog.payloads;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentDto {

	private int id;
	private String content;
	private UserDto user;
}
