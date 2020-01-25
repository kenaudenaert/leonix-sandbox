package be.leonix.sandbox.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import be.leonix.sandbox.mongo.EmbeddedMongoConfig;

@SpringJUnitConfig
@ContextConfiguration(classes={EmbeddedMongoConfig.class})
public class UserRepositoryTest {
	
	@Autowired
	private UserRepository userRepository;
	
	@Test
	void checkEmptyRepository() {
		Assertions.assertTrue(userRepository.findAll().isEmpty());
	}
}
