package be.leonix.sandbox.data;

import static be.leonix.sandbox.data.UserDataMapping.EMAIL_ADDRESS;
import static be.leonix.sandbox.data.UserDataMapping.FULL_NAME;
import static be.leonix.sandbox.data.UserDataMapping.ROLES;
import static be.leonix.sandbox.data.UserDataMapping.USERNAME;

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
	
	public static UserData map(User user) {
		UserData data = new UserData();
		data.setUserName(user.getUserName());
		data.setRoles(user.getRoles());
		data.setFullName(user.getFullName());
		data.setEmailAddress(user.getEmailAddress());
		return data;
	}
}
