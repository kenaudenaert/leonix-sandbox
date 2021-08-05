package be.leonix.tools;

import be.leonix.tools.model.SourceFile;

/**
 * A refactor operation that transforms a {@link SourceFile}.
 * 
 * @author Ken Audenaert
 */
public interface FileRefactor {
	
	/**
	 * Refactors the specified source-file using the specified refactor-context.
	 */
	public void refactorFile(SourceFile sourceFile, RefactorContext context);
}
