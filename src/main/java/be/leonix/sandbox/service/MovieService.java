package be.leonix.sandbox.service;

import java.util.List;
import java.util.Optional;

import be.leonix.sandbox.data.MovieData;
import be.leonix.sandbox.model.Movie;

/**
 * This service provides the business logic for movies.
 * 
 * @author leonix
 */
public interface MovieService {
	
	public List<Movie> findAllMovies();
	
	public Optional<Movie> findMovieById(String movieId);
	
	public Movie addMovie(MovieData movieData);
	
	public Optional<Movie> updateMovie(MovieData movieData);
}
