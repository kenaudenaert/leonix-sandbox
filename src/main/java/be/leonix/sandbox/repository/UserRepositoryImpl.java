package be.leonix.sandbox.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import be.leonix.sandbox.model.User;
import be.leonix.sandbox.model.UserMongoMapping;

/**
 * @author leonix
 */
@Repository("mongoUsers")
public class UserRepositoryImpl implements UserRepository {
	
	private final MongoCollection<User> users;
	
	@Autowired
	public UserRepositoryImpl(MongoDatabase mongoDatabase) {
		users = mongoDatabase.getCollection(
				UserMongoMapping.COLLECTION_NAME, User.class);
	}
	
	@Override
	public List<User> findAll() {
		return users.find(User.class).into(new ArrayList<>());
	}
	
	@Override
	public Optional<User> findByName(String userName) {
		return null;
	}
	
	@Override
	public void insert(User user) {
	
	}
	
	@Override
	public void update(User user) {
		
	}
}
