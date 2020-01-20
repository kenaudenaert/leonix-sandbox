package be.leonix.sandbox.service;

import java.util.List;
import java.util.Optional;

import be.leonix.sandbox.data.UserData;
import be.leonix.sandbox.model.User;

/**
 * This service provides the business logic for users.
 * 
 * @author leonix
 */
public interface UserService {
	
	public List<User> findAll();
	
	public Optional<User> findByName(String userName);
	
	public User create(UserData userData);
	
	public Optional<User> update(UserData userData);
}
