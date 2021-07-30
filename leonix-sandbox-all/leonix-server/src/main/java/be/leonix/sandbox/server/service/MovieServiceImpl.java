package be.leonix.sandbox.server.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.leonix.sandbox.domain.model.Movie;
import be.leonix.sandbox.domain.repository.MovieRepository;
import be.leonix.sandbox.server.data.MovieData;

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
	public List<Movie> findAll() {
		return movieRepository.findAll();
	}
	
	@Override
	public Optional<Movie> findById(String movieId) {
		return movieRepository.findById(movieId);
	}
	
	@Override
	public Movie create(MovieData movieData) {
		Movie movie = new Movie();
		movieRepository.insert(movie);
		return movie;
	}
	
	@Override
	public Optional<Movie> update(MovieData movieData) {
		Optional<Movie> movie = movieRepository.findById(movieData.getTitle());
		if (movie.isPresent()) {
			movieRepository.update(movie.get());
			return movie;
		} else {
			return Optional.empty();
		}
	}
	
	@Override
	public void remove(List<String> movieIds) {
		movieRepository.removeById(movieIds);
	}
}
