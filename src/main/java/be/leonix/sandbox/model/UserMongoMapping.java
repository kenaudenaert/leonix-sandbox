package be.leonix.sandbox.model;

/**
 * This class provides the Mongo mapping for {@link User}.
 * 
 * @author leonix
 */
public final class UserMongoMapping {
	private UserMongoMapping() {}
	
	public static final String COLLECTION_NAME = "users";
	public static final String ID = "_id";
	
	public static final String USERNAME = "user";
	public static final String SALT = "salt";
	public static final String ENCODED_PASSWORD = "encoded_pw";
	public static final String ROLES = "roles";
	public static final String FULL_NAME = "full_name";
	public static final String EMAIL_ADDRESS = "email";
}
