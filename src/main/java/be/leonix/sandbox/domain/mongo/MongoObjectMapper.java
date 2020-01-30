package be.leonix.sandbox.domain.mongo;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The {@link ObjectMapper} for Mongo document conversion.
 * 
 * @author leonix
 */
public class MongoObjectMapper extends ObjectMapper {
	
	private static final long serialVersionUID = 1L;
	
	public MongoObjectMapper() {
		this(null);
	}
	
	public MongoObjectMapper(JsonFactory jsonFactory) {
		super(jsonFactory);
	}
}
