package be.leonix.sandbox.model;

/**
 * This class provides the Mongo mapping for {@link Movie}.
 * 
 * @author leonix
 */
public final class MovieMongoMapping {
	private MovieMongoMapping() {}
	
	public static final String MOVIE_TYPE_ALIAS = "movie";
	public static final String TITLE = "title";
	public static final String DESCRIPTION = "description";
	public static final String TAGS = "tags";
}
