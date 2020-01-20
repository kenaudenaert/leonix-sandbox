package be.leonix.sandbox.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import be.leonix.sandbox.data.MovieData;
import be.leonix.sandbox.service.MovieService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * @author leonix
 */
@Controller
@Path("movies")
public class MovieResource {
	
	private static final Logger logger = LoggerFactory.getLogger(MovieResource.class);
	
	private final MovieService movieService;
	
	@Autowired
	public MovieResource(MovieService movieService) {
		this.movieService = movieService;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response findAllMovies() {
		logger.info("findAllMovies()");
		
		List<MovieData> movieDatas = movieService.findAllMovies().stream()
				.map(MovieData::map).collect(Collectors.toList());
		
		return Response.ok(movieDatas).build();
	}
	
	@GET
	@Path("{movieId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response findMovieById(@PathParam("movieId") String movieId) {
		logger.info("findMovieById({})", movieId);
		
		Optional<MovieData> movieData = movieService.findMovieById(movieId)
				.map(MovieData::map);
		
		if (movieData.isPresent()) {
			return Response.ok(movieData).build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addMovie(MovieData movieData) {
		logger.info("addMovie({})", movieData.getTitle());
		
		MovieData addedData = MovieData.map(movieService.addMovie(movieData));
		
		return Response.ok(addedData).build();
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateMovie(MovieData movieData) {
		logger.info("updateMovie({})", movieData.getTitle());
		
		Optional<MovieData> updatedData = movieService.updateMovie(movieData)
				.map(MovieData::map);
		
		if (updatedData.isPresent()) {
			return Response.ok(movieData).build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}
	
	@POST
	@Path("remove")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response removeMoviesById(List<String> movieIds) {
		return Response.ok().build();
	}
}
