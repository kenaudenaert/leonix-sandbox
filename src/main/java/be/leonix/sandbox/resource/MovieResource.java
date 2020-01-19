package be.leonix.sandbox.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import be.leonix.sandbox.service.MovieService;

/**
 * @author leonix
 */
@Controller
public class MovieResource {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(MovieResource.class);
	
	@SuppressWarnings("unused")
	private final MovieService movieService;
	
	@Autowired
	public MovieResource(MovieService movieService) {
		this.movieService = movieService;
	}
}
