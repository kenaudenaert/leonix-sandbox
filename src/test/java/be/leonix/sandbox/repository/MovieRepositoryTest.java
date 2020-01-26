package be.leonix.sandbox.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import be.leonix.sandbox.model.Movie;
import be.leonix.sandbox.mongo.EmbeddedMongoConfig;
import be.leonix.sandbox.mongo.MongoConfig;

@SpringJUnitConfig
@ContextConfiguration(classes={MongoConfig.class})
@TestPropertySource("classpath:config/sandbox.properties")
public class MovieRepositoryTest {
	
	@Autowired
	private MovieRepository movieRepository;
	
	@Test
	void checkEmptyRepository() {
	//	Assertions.assertTrue(movieRepository.findAll().isEmpty());
		Movie movie = new Movie();
		movie.setTitle("My first movie");
		movieRepository.insert(movie);
		Assertions.assertFalse(movieRepository.findAll().isEmpty());
	}
}
