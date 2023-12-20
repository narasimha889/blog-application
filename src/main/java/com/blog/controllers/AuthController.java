package com.blog.controllers;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.entities.User;
import com.blog.exceptions.ApiException;
import com.blog.payloads.JwtAuthRequest;
import com.blog.payloads.JwtAuthResponse;
import com.blog.payloads.UserDto;
import com.blog.payloads.UserInt;
import com.blog.security.JwtTokenHelper;
import com.blog.services.UserService;
import com.blog.utils.EmailSenderService;
import com.blog.utils.RegistrationCompleteEvent;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
	
	@Autowired
	private JwtTokenHelper jwtTokenHelper;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserService userService;
	@Autowired
	private ApplicationEventPublisher publisher;
	@Autowired
	private EmailSenderService emailSenderService;
	@PostMapping("/login")
	public ResponseEntity<JwtAuthResponse> createToken(@Valid
			@RequestBody JwtAuthRequest request) throws Exception{
		String status=this.authenticate(request.getUsername(),request.getPassword());
		if(status.equalsIgnoreCase("invalid")) {
			throw new ApiException("Invalid Username or Password!1");
		}
		UserDetails userDetails = this.userDetailsService.loadUserByUsername(request.getUsername());
		String token =this.jwtTokenHelper.generateToken(userDetails);
		JwtAuthResponse response = new JwtAuthResponse();
		response.setToken(token);
		response.setUser(this.modelMapper.map((User) userDetails, UserDto.class));
		return new ResponseEntity<JwtAuthResponse>(response,HttpStatus.OK);
	}

	@PostMapping("/checkcredentials")
	public String checkCredentials(
			@RequestBody JwtAuthRequest request) throws Exception {
		String status=this.authenticate(request.getUsername(),request.getPassword());
		return status;
	}
	private String authenticate(String username, String password) throws Exception {
		UsernamePasswordAuthenticationToken usernameAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
		String status = "valid";
		try{
			this.authenticationManager.authenticate(usernameAuthenticationToken);
		}
		catch(BadCredentialsException e) {
			System.out.println("Invalid Creds!!");
			status="invalid";
			return status;
//			throw new ApiException("Invalid Username or Password!1");
		}
		return status;
	}
	@PostMapping("/register")
	public String registerUser(@Valid @RequestBody UserDto userDto,final HttpServletRequest request){
		UserDto oldUser = this.userService.getUserByEmail(userDto.getEmail());
		if(oldUser==null) {
			UserDto registeredUser = this.userService.registerNewUser(userDto);
			return "valid";
		}
		else {
			return "invalid";
		}
		
	}
//	@PostMapping("/generatetoken")
//	public String generateTokenForUserLogin(@RequestBody UserInt userInt) {
////		publisher.publishEvent(new RegistrationCompleteEvent(userInt.getEmail()));
//		return "valid";
//	}
	@PostMapping("/generatedOtp")
	public String getGeneratedToken(@RequestBody UserInt userInt) {
		String token = this.userService.getTokenVerification(userInt.getEmail());
		return token;
	}
	@PostMapping("/generatetoken")
	public String resendVerificationToken(@RequestBody UserInt userInt) {
		String oldtoken=this.userService.getTokenVerification(userInt.getEmail());
		if(oldtoken.equalsIgnoreCase("invalidtoken")) {
			UserDto userDto = userService.getUserByEmail(userInt.getEmail());
			User user = this.modelMapper.map(userDto, User.class);
			String newtoken = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
			this.userService.saveVerificationTokenForUser(newtoken, user);
			return "valid";
		}
		String token = userService.generateNewVerificationToken(userInt.getEmail());
		if(token=="invalid") {
			return "invalid";
		}
		emailSenderService.sendEmail(userInt.getEmail(),token,"Otp for Verification of Blog Application");
		return "valid";
	}
	//reset password
//	@PostMapping("/resetPassword")
//	public String resetPassword(@RequestBody UserInt userInt) {
//		UserDto userDto = userService.getUserByEmail(userInt.getEmail());
//		User user = this.modelMapper.map(userDto, User.class);
//		String token = "";
//		if(user==null) {
//			return "invalid";
//		}
//		if(user!=null) {
//			token = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
//			userService.createPasswordResetTokenForUser(user,token);
//			emailSenderService.sendEmail(user.getEmail(),token,"Otp to Reset Password");
//		}
//		return "valid";
//		}
	@PostMapping("/getPasswordOtp")
	public String getPasswordOtp(@RequestBody UserInt userInt) {
		String otpDetails = userService.findOtpByEmail(userInt.getEmail());
		return otpDetails;
	}
	@PostMapping("/resetPassword")
	public String resendPasswordVerifyToken(@RequestBody UserInt userInt) {
		UserDto oldUserDto=this.userService.getUserByEmail(userInt.getEmail());
		if(oldUserDto==null) {
			return "invaliduser";
		}
		String oldtoken=this.userService.findOtpByEmail(oldUserDto.getEmail());
		if(oldtoken.equalsIgnoreCase("invalidtoken")) {
			UserDto userDto = userService.getUserByEmail(oldUserDto.getEmail());
			User user = this.modelMapper.map(userDto, User.class);
			String newtoken = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
			this.userService.createPasswordResetTokenForUser(user, newtoken);
			return "valid";
		}
		String token = userService.generateNewPasswordVerificationToken(oldUserDto.getEmail());
		emailSenderService.sendEmail(userInt.getEmail(),token,"Otp to Reset Password");
		return "valid";
	}
	@PostMapping("/savePassword")
	public String savePassword(@RequestBody UserInt userInt) {
		UserDto userDto = userService.getUserByEmail(userInt.getEmail());
		User user = this.modelMapper.map(userDto, User.class);
		if(user!=null) {
			userService.changePassword(user,userInt.getPassword());
			return "valid";
		}else {
			return "invalid";
		}
	}
	@PostMapping("/changePassword")
	public String changePassword(@RequestBody UserInt userInt) { 
		UserDto userDto = userService.getUserById(userInt.getId());
		User user = this.modelMapper.map(userDto, User.class);
		if(!userService.checkIfValidOldPassword(user,userInt.getPassword())) {
			return "invalid";
		}
		userService.changePassword(user, userInt.getNewPassword());
		return "valid";
	}
}

