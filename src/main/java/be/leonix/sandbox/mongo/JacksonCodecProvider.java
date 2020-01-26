package be.leonix.sandbox.mongo;

import java.util.Objects;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A {@link CodecProvider} that creates a {@link JacksonCodec} for each type.
 *
 * @author leonix
 */
public class JacksonCodecProvider implements CodecProvider {
	
	private final ObjectMapper objectMapper;
	
	public JacksonCodecProvider(ObjectMapper objectMapper) {
		this.objectMapper = Objects.requireNonNull(objectMapper);
	}
	
	@Override
	public <T> Codec<T> get(Class<T> type, CodecRegistry registry) {
		return new JacksonCodec<T>(objectMapper, registry, type);
	}
}
