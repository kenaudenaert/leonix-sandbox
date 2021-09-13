package be.leonix.tools.refactor;

import be.leonix.tools.refactor.model.repo.SourceLine;

/**
 * A refactor-operation that transforms a {@link SourceLine}.
 * 
 * @author Ken Audenaert
 */
public interface LineRefactor {
	
	/**
	 * Returns a non-blank description for this refactor operation.
	 */
	default String getDescription() { return getClass().getSimpleName(); }
	
	/**
	 * Refactors the specified source-line using the specified refactor-context.
	 */
	public void refactorLine(SourceLine sourceLine, RefactorContext context);
}
