package com.blog.payloads;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostDto {

	private Integer postId;
	@NotBlank
	@Size(min = 4,message = "Title must be minimum of 4 characters !!")
	private String title;
	@NotBlank
	@Size(min = 10,message = "content must be minimum of 10 characters !!")
	private String content;
	private String imageName;
	private Date addedDate;
	private CategoryDto category;
	private UserDto user;
	private Set<CommentDto> comments = new HashSet<>();
}
