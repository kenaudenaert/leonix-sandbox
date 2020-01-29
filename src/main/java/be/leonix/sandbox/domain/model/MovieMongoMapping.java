package be.leonix.sandbox.domain.model;

/**
 * This class provides the Mongo mapping for {@link Movie}.
 * 
 * @author leonix
 */
public final class MovieMongoMapping {
	private MovieMongoMapping() {}
	
	public static final String COLLECTION_NAME = "movies";
	public static final String ID = "_id";
	
	public static final String TITLE = "title";
	public static final String DESCRIPTION = "description";
	public static final String TAGS = "tags";
	public static final String EXTERNAL_ID = "external-id";
}
