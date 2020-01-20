package be.leonix.sandbox.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.leonix.sandbox.data.MovieData;
import be.leonix.sandbox.model.Movie;
import be.leonix.sandbox.repository.MovieRepository;

/**
 * @author leonix
 */
@Service("movieService")
public class MovieServiceImpl implements MovieService {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(MovieServiceImpl.class);
	
	private final MovieRepository movieRepository;
	
	@Autowired
	public MovieServiceImpl(MovieRepository movieRepository) {
		this.movieRepository = movieRepository;
	}
	
	@Override
	public List<Movie> findAllMovies() {
		return movieRepository.findAllMovies();
	}
	
	@Override
	public Optional<Movie> findMovieById(String movieId) {
		return movieRepository.findMovieById(movieId);
	}
	
	@Override
	public Movie addMovie(MovieData movieData) {
		Movie movie = new Movie();
		movieRepository.addMovie(movie);
		return movie;
	}
	
	@Override
	public Optional<Movie> updateMovie(MovieData movieData) {
		Optional<Movie> movie = movieRepository.findMovieById(movieData.getTitle());
		if (movie.isPresent()) {
			movieRepository.updateMovie(movie.get());
			return movie;
		} else {
			return Optional.empty();
		}
	}
}
