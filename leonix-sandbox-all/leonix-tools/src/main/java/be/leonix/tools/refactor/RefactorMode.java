package be.leonix.tools.refactor;

/**
 * Enumeration of the possible refactoring modes.
 * 
 * @author Ken Audenaert
 */
public enum RefactorMode {
	/** Do not change, only log the changes. */
	LOG_CHANGE,
	/** Perform the changes in the files. */
	UPDATE_FILE,
	/** Commit the changes in the files. */
	COMMIT_REPO;
}
