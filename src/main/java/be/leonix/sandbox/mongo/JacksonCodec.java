package be.leonix.sandbox.mongo;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A {@link Codec} that uses a Jackson {@link ObjectMapper}.
 * 
 * @author leonix
 */
public class JacksonCodec<T> implements Codec<T> {
	
	private final ObjectMapper objectMapper;
	private final Codec<RawBsonDocument> rawBsonDocumentCodec;
	private final Class<T> objectType;
	
	public JacksonCodec(ObjectMapper objectMapper, CodecRegistry codecRegistry, Class<T> type) {
		this.objectMapper = Objects.requireNonNull(objectMapper);
		this.rawBsonDocumentCodec = codecRegistry.get(RawBsonDocument.class);
		this.objectType = Objects.requireNonNull(type);
	}
	
	@Override
	public T decode(BsonReader reader, DecoderContext decoderContext) {
		try {
			RawBsonDocument document = rawBsonDocumentCodec.decode(reader, decoderContext);
			String json = document.toJson();
			return objectMapper.readValue(json, objectType);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	@Override
	public void encode(BsonWriter writer, Object value, EncoderContext encoderContext) {
		try {
			String json = objectMapper.writeValueAsString(value);
			RawBsonDocument document = RawBsonDocument.parse(json);
			rawBsonDocumentCodec.encode(writer, document, encoderContext);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	@Override
	public Class<T> getEncoderClass() {
		return objectType;
	}
}
