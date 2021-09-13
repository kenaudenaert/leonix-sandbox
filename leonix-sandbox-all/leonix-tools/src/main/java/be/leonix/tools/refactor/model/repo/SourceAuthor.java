package be.leonix.tools.refactor.model.repo;

import java.util.Objects;

/**
 * This class defines an (immutable) source-author.
 * 
 * @author Ken Audenaert
 */
public final class SourceAuthor {
	
	private final String name;
	private final String email;
	
	public SourceAuthor(String name, String email) {
		this.name  = Objects.requireNonNull(name);
		this.email = Objects.requireNonNull(email);
	}
	
	public String getName() {
		return name;
	}
	
	public String getEmail() {
		return email;
	}
}
