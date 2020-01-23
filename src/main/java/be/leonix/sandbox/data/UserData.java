package be.leonix.sandbox.data;

import static be.leonix.sandbox.data.UserDataMapping.EMAIL_ADDRESS;
import static be.leonix.sandbox.data.UserDataMapping.FULL_NAME;
import static be.leonix.sandbox.data.UserDataMapping.ROLES;
import static be.leonix.sandbox.data.UserDataMapping.USERNAME;
import static be.leonix.sandbox.data.UserDataMapping.LOCALE;
import static be.leonix.sandbox.data.UserDataMapping.PHONE_NUMBER;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import be.leonix.sandbox.model.User;

/**
 * This class defines a user as presented to the user.
 * 
 * @author leonix
 */
public class UserData {
	
	@JsonProperty(USERNAME)
	private String userName;
	@JsonProperty(ROLES)
	private List<String> roles = new ArrayList<>();
	@JsonProperty(FULL_NAME)
	private String fullName;
	@JsonProperty(EMAIL_ADDRESS)
	private String emailAddress;
	@JsonProperty(PHONE_NUMBER)
	private String phoneNumber;
	@JsonProperty(LOCALE)
	private String locale;
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public List<String> getRoles() {
		return roles;
	}
	
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	
	public String getFullName() {
		return fullName;
	}
	
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	public String getEmailAddress() {
		return emailAddress;
	}
	
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getLocale() {
		return locale;
	}
	
	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	public static UserData map(User user) {
		UserData data = new UserData();
		data.setUserName    (user.getUserName());
		data.setRoles       (user.getRoles());
		data.setFullName    (user.getFullName());
		data.setEmailAddress(user.getEmailAddress());
		data.setPhoneNumber (user.getPhoneNumber());
		data.setLocale      (user.getLocale());
		return data;
	}
}
