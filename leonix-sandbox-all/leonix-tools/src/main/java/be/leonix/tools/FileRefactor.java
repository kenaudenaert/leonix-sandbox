package be.leonix.tools;

import be.leonix.tools.model.SourceFile;

/**
 * A refactor operation that transforms at a {@link SourceFile}.
 * 
 * @author Ken Audenaert
 */
public interface FileRefactor {
	
	/**
	 * Refactors the specified source-file using the specified context.
	 * 
	 * @return Whether the source-file has been updated.
	 */
	public boolean refactorFile(SourceFile sourceFile, RefactorContext context);
}
