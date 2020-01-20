package be.leonix.sandbox.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import be.leonix.sandbox.model.Movie;
import be.leonix.sandbox.model.MovieMongoMapping;

/**
 * @author leonix
 */
@Repository
public class MovieRepositoryImpl implements MovieRepository {
	
	private final MongoCollection movies;
	
	@Autowired
	public MovieRepositoryImpl(Jongo jongo) {
		movies = jongo.getCollection(MovieMongoMapping.COLLECTION_NAME);
	}
	
	@Override
	public List<Movie> findAll() {
		List<Movie> all = new ArrayList<>();
		MongoCursor<Movie> cursor = movies.find().as(Movie.class);
		cursor.forEach(movie -> all.add(movie));
		return all;
	}
	
	@Override
	public Optional<Movie> findById(String movieId) {
		return null;
	}
	
	@Override
	public void insert(Movie movie) {
		
	}
	
	@Override
	public void update(Movie movie) {
		
	}
	
	@Override
	public void removeById(List<String> movieIds) {
		
	}
}
