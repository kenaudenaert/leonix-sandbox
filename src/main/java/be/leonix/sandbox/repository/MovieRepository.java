package be.leonix.sandbox.repository;

import java.util.List;
import java.util.Optional;

import be.leonix.sandbox.model.Movie;

/**
 * @author leonix
 */
public interface MovieRepository {
	
	public List<Movie> findAll();
	
	public Optional<Movie> findById(String movieId);
	
	public void insert(Movie movie);
	
	public void update(Movie movie);
	
	public void removeById(List<String> movieIds);
}
