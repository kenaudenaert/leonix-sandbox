package be.leonix.tools.refactor;

import be.leonix.tools.refactor.model.SourceLine;

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
	 * Called when the refactor operation has been started.
	 */
	default void refactorStarted() {}
	
	/**
	 * Called when the refactor operation has been stopped.
	 */
	default void refactorStopped() {}
	
	/**
	 * Refactors the specified source-line using the specified refactor-context.
	 */
	void refactorLine(SourceLine sourceLine, RefactorContext context);
}
