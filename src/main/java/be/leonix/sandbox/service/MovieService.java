package be.leonix.sandbox.service;

import java.util.List;
import java.util.Optional;

import be.leonix.sandbox.model.Movie;

/**
 * @author leonix
 */
public interface MovieService {
	
	public List<Movie> findAllMovies();
	
	public Optional<Movie> findByTitle(String title);
	
	public void save(Movie movie);
	
	public void removeByTitle(String title);
}
