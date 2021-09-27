package be.leonix.tools.refactor.operation;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.leonix.tools.refactor.LineRefactor;
import be.leonix.tools.refactor.RefactorContext;
import be.leonix.tools.refactor.model.repo.SourceChange;
import be.leonix.tools.refactor.model.repo.SourceLine;

/**
 * A {@link LineRefactor} that refactors code to use the diamond syntax. 
 * 
 * @author Ken Audenaert
 */
public final class DiamondRefactor implements LineRefactor {
	
	private static final Logger logger = LoggerFactory.getLogger(DiamondRefactor.class);
	
	private static final Pattern NON_DIAMOND_CTOR = Pattern.compile(
			"(=\\s*new\\s+[\\w\\.]+\\s*<)" +	// = new package.Generic<
			"[\\w\\.\\s,]+" +					// package.Foo, Bar
			"(>\\s*\\(\\s*\\)\\s*;)");			// >();
	
	private final Map<String, SourceChange> changes = new TreeMap<>();
	
	@Override
	public String getDescription() {
		return "DiamondRefactor (use diamond syntax ctor)";
	}
	
	@Override
	public void refactorStarted() {
		changes.clear();
	}
	
	@Override
	public void refactorStopped() {
		for (SourceChange change : changes.values()) {
			String oldText = change.getOldText();
			String newText = change.getNewText();
			int changeCount = change.getChangeCount();
			logger.info("Changed: {}x : '{}' -> '{}'", changeCount, oldText, newText);
		}
	}
	
	@Override
	public void refactorLine(SourceLine sourceLine, RefactorContext context) {
		String oldLine = sourceLine.getLineContent();
		if (StringUtils.isNotEmpty(oldLine)) {
			String newLine = refactorLine(oldLine);
			if (! StringUtils.equals(oldLine, newLine)) {
				sourceLine.setLineContent(newLine);
			}
		}
	}
	
	/**
	 * Refactors the specified source-line and returns the result.
	 */
	private String refactorLine(String sourceLine) {
		StringBuilder builder = new StringBuilder();
		int offset = 0;
		Matcher matcher = NON_DIAMOND_CTOR.matcher(sourceLine);
		while (matcher.find(offset)) {
			// Copy unmatched leading section.
			if (matcher.start() > offset) {
				builder.append(sourceLine, offset, matcher.start());
			}
			// Execute refactor for pattern: keep only the sub-groups.
			String oldText = matcher.group();
			String newText = matcher.group(1) + matcher.group(2);
			
			// Update change statistics for source-change.
			SourceChange change = changes.get(oldText);
			if (change == null) {
				change = new SourceChange(oldText, newText);
				changes.put(oldText, change);
			}
			change.addChange();
			
			builder.append(newText);
			offset = matcher.end();
		}
		// Copy unmatched trailing section.
		if (offset < sourceLine.length()) {
			builder.append(sourceLine.substring(offset));
		}
		return builder.toString();
	}
}
