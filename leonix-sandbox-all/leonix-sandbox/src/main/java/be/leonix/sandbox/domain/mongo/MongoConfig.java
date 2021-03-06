package be.leonix.sandbox.domain.mongo;

import java.util.Objects;

import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {
	
	private final String uri;
	private final String databaseName;
	
	public MongoConfig(@Value("${sandbox.mongo.uri}") String uri) {
		this.uri = uri;
		
		databaseName = new MongoClientURI(uri).getDatabase();
		if (Objects.isNull(databaseName)) {
			throw new IllegalArgumentException("no database name found in db connection uri: " + uri);
		}
	}
	
	@Bean
	@Override
	public MongoClient mongoClient() {
		return MongoClients.create(uri);
	}
	
	@Override
	protected String getDatabaseName() {
		return databaseName;
	}
	
	@Bean
	public MongoDatabase mongoDatabase(MongoClient mongoClient) {
		CodecProvider codecProvider = new JacksonCodecProvider(new MongoObjectMapper());
		
		CodecRegistry defaultCodecRegistry = MongoClientSettings.getDefaultCodecRegistry();
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				defaultCodecRegistry, CodecRegistries.fromProviders(codecProvider));
		
		return mongoClient.getDatabase(getDatabaseName()).withCodecRegistry(codecRegistry);
	}
}
