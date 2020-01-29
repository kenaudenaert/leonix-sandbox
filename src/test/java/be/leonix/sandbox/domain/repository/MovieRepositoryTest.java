package be.leonix.sandbox.domain.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import be.leonix.sandbox.domain.model.Movie;
import be.leonix.sandbox.domain.mongo.EmbeddedMongoConfig;
import be.leonix.sandbox.domain.mongo.MongoConfig;
import be.leonix.sandbox.domain.repository.MovieRepository;

@SpringJUnitConfig
@ContextConfiguration(classes={MongoConfig.class})
@ComponentScan({"be.leonix.sandbox.repository", "be.leonix.sandbox.mongo"})
@TestPropertySource("classpath:config/sandbox.properties")
public class MovieRepositoryTest {
	
	@Autowired
	private MovieRepository movieRepository;
	
	@Test
	void checkEmptyRepository() {
		Assertions.assertFalse(movieRepository.findAll().isEmpty());
		Movie movie = new Movie();
		movie.setTitle("My first movie");
		movieRepository.insert(movie);
		// Assertions.assertFalse(movieRepository.findAll().isEmpty());
	}
}
