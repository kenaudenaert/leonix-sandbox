package be.leonix.tools.refactor.model;

/**
 * This class defines an immutable location in a {@link SourceFile}.
 * 
 * @author Ken Audenaert
 */
public final class SourceLocation {
	
	private final SourceFile sourceFile;
	private final SourceLine sourceLine;
	private final long linePosition;
	
	public SourceLocation(SourceFile sourceFile, SourceLine sourceLine, long linePosition) {
		if (sourceFile == null) {
			throw new IllegalArgumentException("Invalid (missing) source-file.");
		}
		if (sourceLine == null) {
			throw new IllegalArgumentException("Invalid (missing) source-line.");
		}
		if (linePosition < 0) {
			throw new IllegalArgumentException("Invalid (negative) line-position.");
		}
		this.sourceFile = sourceFile;
		this.sourceLine = sourceLine;
		this.linePosition = linePosition;
	}
	
	/**
	 * Returns the (non-null) source-file.
	 */
	public SourceFile getSourceFile() {
		return sourceFile;
	}
	
	/**
	 * Returns the (non-null) source-line.
	 */
	public SourceLine getSourceLine() {
		return sourceLine;
	}
	
	/**
	 * Returns the (0-based) line-position.
	 */
	public long getLinePosition() {
		return linePosition;
	}
}
