package be.leonix.tools;

/**
 * A refactor-operation that transforms a source-line.
 * 
 * @author Ken Audenaert
 */
public interface LineRefactor {
	
	/**
	 * Refactors the specified source-line.
	 */
	public String refactorLine(String sourceLine);
}
