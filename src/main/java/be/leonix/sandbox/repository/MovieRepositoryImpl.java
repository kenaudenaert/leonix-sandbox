package be.leonix.sandbox.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import be.leonix.sandbox.model.Movie;

/**
 * @author leonix
 */
@Repository
public class MovieRepositoryImpl implements MovieRepository {

	@Override
	public List<Movie> findAllMovies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Movie> findMovieById(String movieId) {
		// TODO Auto-generated method stub
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
