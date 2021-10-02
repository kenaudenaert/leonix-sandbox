package be.leonix.tools.refactor;

import be.leonix.tools.refactor.model.SourceFile;

/**
 * A refactor-operation that transforms a {@link SourceFile}.
 * 
 * @author Ken Audenaert
 */
public interface FileRefactor {
	
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
	 * Refactors the specified source-file using the specified refactor-context.
	 */
	void refactorFile(SourceFile sourceFile, RefactorContext context);
}
