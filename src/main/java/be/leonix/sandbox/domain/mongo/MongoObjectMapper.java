package be.leonix.sandbox.domain.mongo;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


/**
 * The {@link ObjectMapper} for Mongo document conversion.
 * 
 * @author leonix
 */
public class MongoObjectMapper extends ObjectMapper {
	
	private static final long serialVersionUID = 1L;
	
	public MongoObjectMapper() {
		// Support for Java Time API types.
		registerModule(new JavaTimeModule());
		
		// Support for the Optional<T> type.
		registerModule(new Jdk8Module());
		
		// Support the mongo ObjectId type.
		SimpleModule objectIdmodule = new SimpleModule();
		objectIdmodule.addSerializer(ObjectId.class, new ObjectIdSerializer());
		objectIdmodule.addDeserializer(ObjectId.class, new ObjectIdDeserializer());
		registerModule(objectIdmodule);
		
		configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		// Only support serialization using annotated fields.
		setVisibility(getSerializationConfig().getDefaultVisibilityChecker()
				.withFieldVisibility(JsonAutoDetect.Visibility.ANY)
				.withGetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withSetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
		
		// Only support deserialization using annotated fields.
		setVisibility(getSerializationConfig().getDefaultVisibilityChecker()
				.withFieldVisibility(JsonAutoDetect.Visibility.ANY)
				.withGetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withSetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
	}
}
