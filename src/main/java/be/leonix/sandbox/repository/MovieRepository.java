package be.leonix.sandbox.repository;

import java.util.List;
import java.util.Optional;

import be.leonix.sandbox.model.Movie;

/**
 * @author leonix
 */
public interface MovieRepository {

	public List<Movie> findAllMovies();
	
	public Optional<Movie> findMovieById(String movieId);
	
	public void addMovie(Movie movie);
	
	public void updateMovie(Movie movie);
}
