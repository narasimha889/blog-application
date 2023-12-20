package com.blog.services;

import java.util.List;
import java.util.Map;

import com.blog.entities.User;
import com.blog.payloads.UserDto;

public interface UserService {

	UserDto registerNewUser(UserDto userDto);
	UserDto createUser(UserDto user);
	UserDto updateUser(Map<String, String> userDetails,Integer userId);
	UserDto getUserById(Integer userId);
	List<UserDto> getAllUsers();
	void deleteUser(Integer userId);
	void saveVerificationTokenForUser(String token, User user);
	String getTokenVerification(String string);
	String generateNewVerificationToken(String string);
	void createPasswordResetTokenForUser(User user, String token);
	UserDto getUserByEmail(String email);
	String findOtpByEmail(String email);
	void changePassword(User user, String password);
	boolean checkIfValidOldPassword(User user, String password);
	String generateNewPasswordVerificationToken(String email);

}
