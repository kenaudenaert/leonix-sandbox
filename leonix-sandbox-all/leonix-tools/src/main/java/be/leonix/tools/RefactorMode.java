package be.leonix.tools;

/**
 * Enumeration of the possible refactoring modes.
 * 
 * @author Ken Audenaert
 */
public enum RefactorMode {
	/** Do not change, just add a comment. */
	ADD_COMMENT,
	/** Do not change, only log the changes. */
	LOG_CHANGE,
	/** Perform the changes in the files. */
	UPDATE_FILE;
}
