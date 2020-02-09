package be.leonix.sandbox.server.service;

import java.util.List;
import java.util.Optional;

import be.leonix.sandbox.domain.model.User;
import be.leonix.sandbox.server.data.UserData;

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
