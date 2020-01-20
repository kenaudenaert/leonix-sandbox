package be.leonix.sandbox.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import be.leonix.sandbox.model.Movie;

/**
 * @author leonix
 */
@Repository
public class MovieRepositoryImpl implements MovieRepository {
	
	private final MongoCollection movies;
	
	@Autowired
	public MovieRepositoryImpl(Jongo jongo) {
		movies = jongo.getCollection("movies");
	}
	
	@Override
	public List<Movie> findAllMovies() {
		List<Movie> allMovies = new ArrayList<>();
		MongoCursor<Movie> cursor = movies.find().as(Movie.class);
		cursor.forEach(movie -> allMovies.add(movie));
		return allMovies;
	}
	
	@Override
	public Optional<Movie> findMovieById(String movieId) {
		return null;
	}
	
	@Override
	public void addMovie(Movie movie) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void updateMovie(Movie movie) {
		// TODO Auto-generated method stub
		
	}
}
