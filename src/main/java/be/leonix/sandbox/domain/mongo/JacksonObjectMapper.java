package be.leonix.sandbox.domain.mongo;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The (configured) {@link ObjectMapper} for Jackson (de)serialization.
 * 
 * @author leonix
 */
public class JacksonObjectMapper extends ObjectMapper {
	
	private static final long serialVersionUID = 1L;
	
	public JacksonObjectMapper() {
		this(null);
	}
	
	public JacksonObjectMapper(JsonFactory jsonFactory) {
		super(jsonFactory);
	}
}
