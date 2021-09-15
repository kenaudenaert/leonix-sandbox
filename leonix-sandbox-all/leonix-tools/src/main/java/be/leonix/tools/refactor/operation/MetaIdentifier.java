package be.leonix.tools.refactor.operation;

import java.util.Objects;

/**
 * This class defines a meta identifier (constant/formula).
 * 
 * @author leonix
 */
public final class MetaIdentifier {
	
	private final String identifier;
	private final String literal;
	
	public MetaIdentifier(String identifier, String literal) {
		this.identifier = Objects.requireNonNull(identifier);
		this.literal    = Objects.requireNonNull(literal);
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public String getLiteral() {
		return literal;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(identifier, literal);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof MetaIdentifier)) {
			return false;
		}
		MetaIdentifier other = (MetaIdentifier) obj;
		return (Objects.equals(identifier, other.identifier) &&
				Objects.equals(literal, other.literal));
	}
	
	@Override
	public String toString() {
		return "MetaIdentifier [identifier=" + identifier + ", literal=" + literal + "]";
	}
}
