package be.leonix.tools.refactor.model.repo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * This class defines a source-change that occurrs at several locations.
 * 
 * @author Ken Audenaert
 */
public final class SourceChange {
	
	private final String oldText;
	private final String newText;
	private final List<SourceLocation> locations;
	private int changeCount;
	
	public SourceChange(String oldText, String newText) {
		this.oldText = Objects.requireNonNull(oldText);
		this.newText = Objects.requireNonNull(newText);
		this.locations = new ArrayList<>();
		this.changeCount = 0;
	}
	
	public String getOldText() {
		return oldText;
	}
	
	public String getNewText() {
		return newText;
	}
	
	public List<SourceLocation> getLocations() {
		return Collections.unmodifiableList(locations);
	}
	
	public void addLocation(SourceLocation location) {
		locations.add(Objects.requireNonNull(location));
	}
	
	public int getChangeCount() {
		return changeCount;
	}
	
	public void addChange() {
		changeCount++;
	}
}
