package be.leonix.sandbox.domain.repository;

import java.util.List;
import java.util.Optional;

import be.leonix.sandbox.domain.model.User;

/**
 * @author leonix
 */
public interface UserRepository {
	
	public List<User> findAll();
	
	public Optional<User> findByName(String userName);
	
	public void insert(User user);
	
	public void update(User user);
}
