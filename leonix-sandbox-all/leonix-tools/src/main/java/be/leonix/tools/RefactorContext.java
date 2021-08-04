package be.leonix.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;

/**
 * This class encapsulates the context for a refactor-operation.
 * 
 * @author Ken Audenaert
 */
public final class RefactorContext {
	
	private final RefactorMode mode;
	private final List<String> infoLines;
	
	public RefactorContext(RefactorMode mode) {
		this.mode = Objects.requireNonNull(mode);
		infoLines = new ArrayList<>();
	}
	
	public RefactorMode getMode() {
		return mode;
	}
	
	public List<String> getInfo() {
		return Collections.unmodifiableList(infoLines);
	}
	
	public void addInfo(String infoLine) {
		infoLines.add(infoLine);
	}
	
	/**
	 * Logs the collected info for the refactor.
	 */
	public void logInfo() {
		for (String infoLine : infoLines) {
			System.out.println(infoLine);
		}
	}
	
	/**
	 * Logs the collected info for the refactor.
	 */
	public void logInfo(Logger logger) {
		for (String infoLine : infoLines) {
			logger.info(infoLine);
		}
	}
}
