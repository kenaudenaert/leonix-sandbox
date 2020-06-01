package be.leonix.sandbox.web.resource;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import be.leonix.sandbox.server.data.UserData;
import be.leonix.sandbox.server.service.UserService;

/**
 * @author leonix
 */
@Controller
@Path("users")
public class UserResource {

	private static final Logger logger = LoggerFactory.getLogger(UserResource.class);
	
	private final UserService userService;
	
	@Autowired
	public UserResource(UserService userService) {
		this.userService = userService;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response findUsers() {
		logger.info("findUsers()");
		
		List<UserData> userDatas = userService.findAll().stream()
				.map(UserData::map).collect(Collectors.toList());
		
		return Response.ok(userDatas).build();
	}
	
	@GET
	@Path("{userName}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response findUserById(@PathParam("userName") String userName) {
		logger.info("findUserByName({})", userName);
		
		Optional<UserData> userData = userService.findByName(userName)
				.map(UserData::map);
		
		if (userData.isPresent()) {
			return Response.ok(userData).build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addUser(UserData userData) {
		logger.info("addUser({})", userData.getUserName());
		
		UserData resultData = UserData.map(userService.create(userData));
		
		return Response.ok(resultData).build();
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateUser(UserData userData) {
		logger.info("updateUser({})", userData.getUserName());
		
		Optional<UserData> resultData = userService.update(userData)
				.map(UserData::map);
		
		if (resultData.isPresent()) {
			return Response.ok(userData).build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}
}
