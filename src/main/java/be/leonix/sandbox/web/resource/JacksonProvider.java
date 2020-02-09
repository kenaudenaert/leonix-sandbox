package be.leonix.sandbox.web.resource;

import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;
import org.springframework.stereotype.Component;

import be.leonix.sandbox.domain.mongo.MongoObjectMapper;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

@Component
@Provider
@Consumes({ MediaType.APPLICATION_JSON, "application/*+json", "text/json" })
@Produces({ MediaType.APPLICATION_JSON, "application/*+json", "text/json" })
public class JacksonProvider extends JacksonJsonProvider {
	public JacksonProvider() {
		setMapper(new MongoObjectMapper());
	}
}
