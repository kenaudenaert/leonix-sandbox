package be.leonix.sandbox.model;

import static be.leonix.sandbox.model.UserMongoMapping.EMAIL_ADDRESS;
import static be.leonix.sandbox.model.UserMongoMapping.ENCODED_PASSWORD;
import static be.leonix.sandbox.model.UserMongoMapping.FULL_NAME;
import static be.leonix.sandbox.model.UserMongoMapping.ID;
import static be.leonix.sandbox.model.UserMongoMapping.ROLES;
import static be.leonix.sandbox.model.UserMongoMapping.SALT;
import static be.leonix.sandbox.model.UserMongoMapping.USERNAME;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class defines a user as persisted in Mongo.
 * 
 * @author leonix
 */
public class User {
	
	@JsonProperty(ID)
	private ObjectId id;
	@JsonProperty(USERNAME)
	private String userName;
	@JsonProperty(SALT)
	private String salt;
	@JsonProperty(ENCODED_PASSWORD)
	private String encodedPassword;
	@JsonProperty(ROLES)
	private List<String> roles = new ArrayList<>();
	@JsonProperty(FULL_NAME)
	private String fullName;
	@JsonProperty(EMAIL_ADDRESS)
	private String emailAddress;
	
	public ObjectId getId() {
		return id;
	}
	
	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getSalt() {
		return salt;
	}
	
	public void setSalt(String salt) {
		this.salt = salt;
	}
	
	public String getEncodedPassword() {
		return encodedPassword;
	}
	
	public void setEncodedPassword(String encodedPassword) {
		this.encodedPassword = encodedPassword;
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
}
