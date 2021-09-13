package be.leonix.tools.refactor.model.repo;

import java.util.Objects;

/**
 * This class encapsulates an editable source-line.
 * 
 * @author Ken Audenaert
 */
public final class SourceLine {
	
	private final long lineNumber;
	private String lineContent;
	private String lineEnding;
	
	public SourceLine(long lineNumber, String lineContent, String lineEnding) {
		if (lineNumber < 0) {
			throw new IllegalArgumentException("Invalid (negative) line-number.");
		}
		if (lineContent == null) {
			throw new IllegalArgumentException("Invalid (missing) line-content.");
		}
		this.lineNumber  = lineNumber;
		this.lineContent = lineContent;
		this.lineEnding  = lineEnding;
	}
	
	/**
	 * Returns the (1-based) line-number.
	 */
	public long getLineNumber() {
		return lineNumber;
	}
	
	/**
	 * Returns the (non-null) line-content.
	 */
	public String getLineContent() {
		return lineContent;
	}
	
	/**
	 * Changes the (non-null) line-content.
	 */
	public void setLineContent(String lineContent) {
		this.lineContent = Objects.requireNonNull(lineContent);
	}
	
	/**
	 * Returns the (optional) line-ending.
	 */
	public String getLineEnding() {
		return lineEnding;
	}
	
	/**
	 * Changes the (optional) line-ending.
	 */
	public void setLineEnding(String lineEnding) {
		this.lineEnding = lineEnding;
	}
}
