package be.leonix.tools.model;

/**
 * This class encapsulates a source-line.
 * 
 * @author leonix
 */
public final class SourceLine {
	
	private final long   lineNumber;
	private final String lineContent;
	private final String lineEnding;
	
	public SourceLine(long lineNumber, String lineContent, String lineEnding) {
		if (lineNumber <= 0) {
			throw new IllegalArgumentException("Invalid line-number.");
		}
		if (lineContent == null) {
			throw new IllegalArgumentException("Invalid line-content.");
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
	 * Returns the (optional) line-ending.
	 */
	public String getLineEnding() {
		return lineEnding;
	}
}
