package be.leonix.sandbox.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import be.leonix.sandbox.data.MovieData;
import be.leonix.sandbox.service.MovieService;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author leonix
 */
@Controller
@Path("movies")
public class MovieResource {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(MovieResource.class);
	
	@SuppressWarnings("unused")
	private final MovieService movieService;
	
	@Autowired
	public MovieResource(MovieService movieService) {
		this.movieService = movieService;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response findAllSets() {
		return Response.ok().build();
	}
	
	@GET
	@Path("{movieId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response findById(@PathParam("movieId") String movieId) {
		return Response.ok().build();
	}
	
	@PUT
	@Path("{movieId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateOrderSet(@PathParam("movieId") String movieId, MovieData data) {
		return Response.ok().build();
	}
	
	@POST
	@Path("remove")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response removeMoviesById(List<String> movieIds) {
		return Response.ok().build();
	}
}
