package be.leonix.sandbox.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.leonix.sandbox.model.Movie;
import be.leonix.sandbox.repository.MovieRepository;

/**
 * @author leonix
 */
@Service("movieService")
public class MovieServiceImpl implements MovieService {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(MovieServiceImpl.class);
	
	@SuppressWarnings("unused")
	private final MovieRepository movieRepository;
	
	@Autowired
	public MovieServiceImpl(MovieRepository movieRepository) {
		this.movieRepository = movieRepository;
	}
	
	@Override
	public List<Movie> findAllMovies() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Optional<Movie> findByTitle(String title) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void save(Movie movie) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void removeByTitle(String title) {
		// TODO Auto-generated method stub
		
	}
}
