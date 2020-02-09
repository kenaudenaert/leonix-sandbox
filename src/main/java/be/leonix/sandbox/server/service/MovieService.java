package be.leonix.sandbox.server.service;

import java.util.List;
import java.util.Optional;

import be.leonix.sandbox.domain.model.Movie;
import be.leonix.sandbox.server.data.MovieData;

/**
 * This service provides the business logic for movies.
 * 
 * @author leonix
 */
public interface MovieService {
	
	public List<Movie> findAll();
	
	public Optional<Movie> findById(String movieId);
	
	public Movie create(MovieData movieData);
	
	public Optional<Movie> update(MovieData movieData);
	
	public void remove(List<String> movieIds);
}
