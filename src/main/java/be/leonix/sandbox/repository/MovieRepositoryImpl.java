package be.leonix.sandbox.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import be.leonix.sandbox.model.Movie;
import be.leonix.sandbox.model.MovieMongoMapping;

/**
 * @author leonix
 */
@Repository("mongoMovies")
public class MovieRepositoryImpl implements MovieRepository {
	
	private final MongoCollection<Movie> movies;
	
	@Autowired
	public MovieRepositoryImpl(MongoDatabase mongoDatabase) {
		movies = mongoDatabase.getCollection(
				MovieMongoMapping.COLLECTION_NAME, Movie.class);
	}
	
	@Override
	public List<Movie> findAll() {
		return movies.find().into(new ArrayList<>());
	}
	
	@Override
	public Optional<Movie> findById(String movieId) {
		Movie movie = movies.find(
				Filters.eq(MovieMongoMapping.ID, movieId)).limit(1).first();
		return Optional.ofNullable(movie);
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
