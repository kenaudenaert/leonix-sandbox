package be.leonix.sandbox.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
