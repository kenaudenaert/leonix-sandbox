package be.leonix.sandbox;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

@Configuration
public class MongoDBConfig extends AbstractMongoClientConfiguration {
	
	private String uri;
	private String databaseName;
	
	public MongoDBConfig(@Value("${sandbox.mongo.uri}") String uri) {
		this.uri = uri;
		
		databaseName = new MongoClientURI(uri).getDatabase();
		if (Objects.isNull(databaseName)) {
			throw new IllegalArgumentException("no database name found in db connection uri: " + uri);
		}
	}
	
	@Override
	public MongoClient mongoClient() {
		return MongoClients.create(uri);
	}
	
	@Override
	protected String getDatabaseName() {
		return databaseName;
	}
	
	@Bean
	public MongoDatabase mongoDatabase() {
		return mongoClient().getDatabase(getDatabaseName());
	}
}
