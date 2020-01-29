package be.leonix.sandbox.security;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import be.leonix.sandbox.model.User;
import be.leonix.sandbox.repository.UserRepository;

// @Service
public class SandboxUserDetailsService implements UserDetailsService {
	
	private static final String DEFAULT_USER = "sandbox";
	
	private UserRepository userRepository;
	
	@Autowired
	public SandboxUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) {
		if (username.equals(DEFAULT_USER)) {
			User user = new User();
			user.setUserName(DEFAULT_USER);
			user.setEncodedPassword(DEFAULT_USER);
			user.setRoles(Collections.singletonList(DEFAULT_USER));
			return new SandboxUserPrincipal(user);
		} else {
			Optional<User> user = userRepository.findByName(username);
			if (user.isEmpty()) {
				throw new UsernameNotFoundException(username);
			}
			return new SandboxUserPrincipal(user.get());
		}
	}
}
