package be.leonix.sandbox.server.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.leonix.sandbox.domain.model.User;
import be.leonix.sandbox.domain.repository.UserRepository;
import be.leonix.sandbox.server.data.UserData;

/**
 * @author leonix
 */
@Service("userService")
public class UserServiceImpl implements UserService {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	private final UserRepository userRepository;
	
	@Autowired
	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}
	
	@Override
	public Optional<User> findByName(String userName) {
		return userRepository.findByName(userName);
	}
	
	@Override
	public User create(UserData userData) {
		User user = new User();
		userRepository.insert(user);
		return user;
	}
	
	@Override
	public Optional<User> update(UserData userData) {
		Optional<User> user = userRepository.findByName(userData.getUserName());
		if (user.isPresent()) {
			userRepository.update(user.get());
			return user;
		} else {
			return Optional.empty();
		}
	}
}
