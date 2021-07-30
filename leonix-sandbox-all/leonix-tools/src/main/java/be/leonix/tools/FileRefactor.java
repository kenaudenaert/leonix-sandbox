package be.leonix.tools;

import java.io.File;

/**
 * A refactor operation that transforms at a source-file.
 * 
 * @author Ken Audenaert
 */
public interface FileRefactor {
	
	/**
	 * Refactors the specified source-file using the specified mode.
	 */
	public void refactorFile(File sourceFile, RefactorMode mode);
}
