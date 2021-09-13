package be.leonix.tools.refactor;

import be.leonix.tools.refactor.model.repo.SourceFile;

/**
 * A refactor operation that transforms a {@link SourceFile}.
 * 
 * @author Ken Audenaert
 */
public interface FileRefactor {
	
	/**
	 * Returns a non-blank description for this refactor operation.
	 */
	default String getDescription() { return getClass().getSimpleName(); }
	
	/**
	 * Refactors the specified source-file using the specified refactor-context.
	 */
	public void refactorFile(SourceFile sourceFile, RefactorContext context);
}
