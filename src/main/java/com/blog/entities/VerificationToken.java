package com.blog.entities;

import java.util.Calendar;
import java.util.Date;

import com.blog.config.AppConstants;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class VerificationToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String token;
	private Date expirationTime;
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="user_id",
				nullable=false,
				foreignKey = @ForeignKey(name="FK_USER_VERIFY_TOKEN"))
	private User user;
	public VerificationToken(User user, String token) {
		super();
		this.token= token;
		this.user=user;
		this.expirationTime = calculateExpirationDate(AppConstants.EXPIRATION_TIME);
	}
	public VerificationToken(String token) {
		super();
		this.token = token;
		this.expirationTime = calculateExpirationDate(AppConstants.EXPIRATION_TIME);
	}
	private Date calculateExpirationDate(int expirationTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(new Date().getTime());
		calendar.add(calendar.MINUTE, expirationTime);
		return new Date(calendar.getTime().getTime());
	}
}
