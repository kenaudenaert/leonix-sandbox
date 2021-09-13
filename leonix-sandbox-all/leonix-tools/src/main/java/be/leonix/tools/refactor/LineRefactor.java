package be.leonix.tools.refactor;

import be.leonix.tools.refactor.model.repo.SourceLine;

/**
 * A refactor-operation that transforms a {@link SourceLine}.
 * 
 * @author Ken Audenaert
 */
public interface LineRefactor {
	
	/**
	 * Refactors the specified source-line using the specified refactor-context.
	 */
	public void refactorLine(SourceLine sourceLine, RefactorContext context);
}
