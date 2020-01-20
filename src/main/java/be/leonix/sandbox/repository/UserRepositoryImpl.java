package be.leonix.sandbox.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import be.leonix.sandbox.model.User;

/**
 * @author leonix
 */
@Repository
public class UserRepositoryImpl implements UserRepository {
	
	private final MongoCollection users;
	
	@Autowired
	public UserRepositoryImpl(Jongo jongo) {
		users = jongo.getCollection("users");
	}
	
	@Override
	public List<User> findAll() {
		List<User> all = new ArrayList<>();
		MongoCursor<User> cursor = users.find().as(User.class);
		cursor.forEach(movie -> all.add(movie));
		return all;
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
