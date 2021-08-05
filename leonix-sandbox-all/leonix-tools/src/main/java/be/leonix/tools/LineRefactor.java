package be.leonix.tools;

import be.leonix.tools.model.SourceLine;

/**
 * A refactor-operation that transforms a {@link SourceLine}.
 * 
 * @author Ken Audenaert
 */
public interface LineRefactor {
	
	/**
	 * Refactors the specified source-line using the specified context.
	 * 
	 * @return Whether the source-line has been updated.
	 */
	public boolean refactorLine(SourceLine sourceLine, RefactorContext context);
}
