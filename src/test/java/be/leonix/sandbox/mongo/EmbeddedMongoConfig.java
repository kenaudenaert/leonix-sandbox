package be.leonix.sandbox.mongo;

import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

@Configuration
@ComponentScan("be.leonix.sandbox.repository")
public class EmbeddedMongoConfig extends AbstractMongoClientConfiguration {
	
	private final EmbeddedMongo embeddedMongo;
	
	public EmbeddedMongoConfig() {
		embeddedMongo = new EmbeddedMongo("localhost", 12345);
	}
	
	@Bean(destroyMethod = "stop")
	public EmbeddedMongo mongoDb() {
		return embeddedMongo;
	}
	
	@Override
	public MongoClient mongoClient() {
		return MongoClients.create("mongodb://" + embeddedMongo.getMongodHost() + ":" + embeddedMongo.getMongodPort());
	}
	
	@Override
	protected String getDatabaseName() {
		return "sandboxtest";
	}
	
	@Bean
	public MongoDatabase mongoDatabase() {
		CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
		
		CodecRegistry defaultCodecRegistry = MongoClientSettings.getDefaultCodecRegistry();
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				defaultCodecRegistry, CodecRegistries.fromProviders(pojoCodecProvider));
		
		return mongoClient().getDatabase(getDatabaseName()).withCodecRegistry(codecRegistry);
	}
}
