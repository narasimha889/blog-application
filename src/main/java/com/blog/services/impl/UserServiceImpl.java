package com.blog.services.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.blog.config.AppConstants;
import com.blog.entities.PasswordResetToken;
import com.blog.entities.Role;
import com.blog.entities.User;
import com.blog.entities.VerificationToken;
import com.blog.exceptions.ResourceNotFoundException;
import com.blog.payloads.UserDto;
import com.blog.repositories.PasswordResetTokenRepo;
import com.blog.repositories.RolesRepo;
import com.blog.repositories.UserRepo;
import com.blog.repositories.VerificationTokenRepo;
import com.blog.services.UserService;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepo userRepo;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private RolesRepo rolesRepo;
	@Autowired
	private VerificationTokenRepo verificationTokenRepo;
	@Autowired
	private PasswordResetTokenRepo passwordResetTokenRepo;
	@Override
	public UserDto createUser(UserDto userDto) {
		User user = this.dtoToUser(userDto);
		User savedUser = this.userRepo.save(user);
		return this.userToDto(savedUser);
	}

	@Override
	public UserDto updateUser(Map<String, String> userDetails, Integer userId) {
		User user = this.userRepo.findById(userId).orElseThrow(()-> new ResourceNotFoundException("user","id",userId));
		user.setName(userDetails.get("name"));
		user.setAbout(userDetails.get("about"));
		this.userRepo.save(user);
		User updatedUser = this.userRepo.findById(userId).orElseThrow(()-> new ResourceNotFoundException("user","id",userId));
		return this.modelMapper.map(updatedUser, UserDto.class);
	}

	@Override
	public UserDto getUserById(Integer userId) {
		User user = this.userRepo.findById(userId).orElseThrow(()-> new ResourceNotFoundException("user","id",userId));
		return this.userToDto(user);
	}

	@Override
	public List<UserDto> getAllUsers() {
		List <User> users = this.userRepo.findAll();
		List<UserDto> userDtos = users.stream().map(user->this.userToDto(user)).collect(Collectors.toList());
		return userDtos;
	}

	@Override
	public void deleteUser(Integer userId) {
		User user = this.userRepo.findById(userId).orElseThrow(()-> new ResourceNotFoundException("user","id",userId));
		this.userRepo.delete(user);
	}
	
	public User dtoToUser(UserDto userDto) {
		User user = this.modelMapper.map(userDto, User.class);
//		user.setId(userDto.getId());
//		user.setName(userDto.getName());
//		user.setPassword(userDto.getPassword());
//		user.setEmail(userDto.getEmail());
//		user.setAbout(userDto.getAbout());
		return user;
	}
	
	public UserDto userToDto(User user) {
		UserDto userDto = this.modelMapper.map(user, UserDto.class);
//		userDto.setId(user.getId());
//		userDto.setName(user.getName());
//		userDto.setEmail(user.getEmail());
//		userDto.setAbout(user.getAbout());
//		userDto.setPassword(user.getPassword());
		return userDto;
	}

	@Override
	public UserDto registerNewUser(UserDto userDto) {
		User user = this.modelMapper.map(userDto, User.class);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		Role role =this.rolesRepo.findById(AppConstants.ROLE_NORMAL).get();
		user.getRoles().add(role);
		User newUser = this.userRepo.save(user);
		return this.modelMapper.map(newUser, UserDto.class);
	}
	//generate verification token
	@Override
	public void saveVerificationTokenForUser(String token, User user) {
		VerificationToken verificationToken = new VerificationToken(user,token);
		verificationTokenRepo.save(verificationToken);
	}

	@Override
	public String getTokenVerification(String email) {
		Optional<User> user = this.userRepo.findByEmail(email);
		
		if(user.get()==null) {
			return "invalid";
		}
		VerificationToken verificationToken = verificationTokenRepo.findByUserId(user.get().getId());
		if(verificationToken==null) {
			return "invalidtoken";
		}
		Calendar cal = Calendar.getInstance();
		if((verificationToken.getExpirationTime().getTime()-cal.getTime().getTime())<=0){
			return "expired";
		}
		return verificationToken.getToken();
	}

	@Override
	public String generateNewVerificationToken(String email){
		Optional<User> user = this.userRepo.findByEmail(email);
		if(user.get()==null) {
			return "invalid";
		}
		VerificationToken verificationToken = verificationTokenRepo.findByUserId(user.get().getId());
		String token ="";
		if(verificationToken==null) {
			token = "invalid";
			return token;
		}
		verificationToken.setToken(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6));
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(new Date().getTime());
		calendar.add(calendar.MINUTE, AppConstants.EXPIRATION_TIME);
		verificationToken.setExpirationTime(new Date(calendar.getTime().getTime()));
		verificationTokenRepo.save(verificationToken);
		token = verificationToken.getToken();
		return token;
	}

	@Override
	public void createPasswordResetTokenForUser(User user, String token) {
		PasswordResetToken passwordResetToken = new PasswordResetToken(user,token);
		passwordResetTokenRepo.save(passwordResetToken);
	}

	@Override
	public UserDto getUserByEmail(String email) {
		Optional<User> user = userRepo.findByEmail(email);
		return this.modelMapper.map(user, UserDto.class) ;
	}

	@Override
	public String findOtpByEmail(String email) {
		Optional<User> user = this.userRepo.findByEmail(email);
		PasswordResetToken passwordResetToken = passwordResetTokenRepo.findByUserId(user.get().getId());
		if(passwordResetToken==null) {
			return "invalidtoken";
		}
		Calendar cal = Calendar.getInstance();
		if((passwordResetToken.getExpirationTime().getTime()-cal.getTime().getTime())<=0){
			return "expired";
		}
		return passwordResetToken.getToken();
	}

	@Override
	public void changePassword(User user, String password) {
		user.setPassword(passwordEncoder.encode(password));
		userRepo.save(user);
	}

	@Override
	public boolean checkIfValidOldPassword(User user, String password) {
		return passwordEncoder.matches(password, user.getPassword());
	}

	@Override
	public String generateNewPasswordVerificationToken(String email) {
		Optional<User> user = this.userRepo.findByEmail(email);
		PasswordResetToken passwordResetToken = passwordResetTokenRepo.findByUserId(user.get().getId());
		String token ="";
		if(passwordResetToken==null) {
			token="invalid";
			return token;
		}
		passwordResetToken.setToken(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6));
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(new Date().getTime());
		calendar.add(calendar.MINUTE, AppConstants.EXPIRATION_TIME);
		passwordResetToken.setExpirationTime(new Date(calendar.getTime().getTime()));
		passwordResetTokenRepo.save(passwordResetToken);
		token = passwordResetToken.getToken();
		return token;
	}





}
