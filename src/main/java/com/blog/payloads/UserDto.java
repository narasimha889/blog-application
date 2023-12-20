package com.blog.payloads;

import java.util.HashSet;
import java.util.Set;

import com.blog.entities.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Data
public class UserDto {

	private int id;
	@NotBlank
	@Size(min = 4,message = "User Name must be minimum of 4 characters !!")
	private String name;
	@Email
	@NotBlank(message = "Please enter email address")
	@Column(name = "email", unique=true)
	private String email;
	@NotBlank
	@Size(min = 4,max = 12,message="Password must be min of 4 chars and max of 12 chars !!")
	private String password;
	@NotBlank
	private String about;
	private Set<RoleDto> roles = new HashSet<>();

}
