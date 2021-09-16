package be.leonix.tools.refactor.operation;

import java.util.Objects;

/**
 * This class defines a meta-type identifier (constant or formula).
 * 
 * @author leonix
 */
public final class MetaTypeID {
	
	private final String identifier;
	private final String literal;
	
	public MetaTypeID(String identifier, String literal) {
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
		if (!(obj instanceof MetaTypeID)) {
			return false;
		}
		MetaTypeID other = (MetaTypeID) obj;
		return (Objects.equals(identifier, other.identifier) &&
				Objects.equals(literal, other.literal));
	}
	
	@Override
	public String toString() {
		return "MetaTypeID [identifier=" + identifier + ", literal=" + literal + "]";
	}
}
