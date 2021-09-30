package be.leonix.tools.refactor.operation;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.leonix.tools.refactor.LineRefactor;
import be.leonix.tools.refactor.RefactorContext;
import be.leonix.tools.refactor.model.SourceChange;
import be.leonix.tools.refactor.model.SourceLine;

/**
 * A {@link LineRefactor} that refactors code to use the diamond syntax. 
 * 
 * @author Ken Audenaert
 */
public final class DiamondRefactor implements LineRefactor {
	
	private static final Logger logger = LoggerFactory.getLogger(DiamondRefactor.class);
	
	private static final Pattern NON_DIAMOND_CTOR = Pattern.compile(
			"=\\s*new\\s+([\\w\\.]+)\\s*<" +	// = new package.Generic<
			"[\\w\\.\\s,]+" +					// package.Foo, Bar
			">\\s*\\(\\s*\\)\\s*;");			// >();
	
	private final Set<String> typeRefFilter = new TreeSet<>();
	private final Map<String, SourceChange> changes = new TreeMap<>();
	
	// The number of matches found.
	private long matchedCount = 0;
	// The number of changes done.
	private long changedCount = 0;
	
	/**
	 * Returns the type-reference filter (no filtering when empty).
	 */
	public Set<String> getTypeRefFilter() {
		return typeRefFilter;
	}
	
	@Override
	public String getDescription() {
		return "DiamondRefactor (filter=" + Arrays.toString(typeRefFilter.toArray()) + ")";
	}
	
	@Override
	public void refactorStarted() {
		matchedCount = 0;
		changedCount = 0;
		changes.clear();
	}
	
	@Override
	public void refactorStopped() {
		logger.info("Statistic: matchedCount : {}", matchedCount);
		logger.info("Statistic: changedCount : {}", changedCount);
		
		for (SourceChange change : changes.values()) {
			String oldText = change.getOldText();
			String newText = change.getNewText();
			int changeCount = change.getChangeCount();
			logger.info("Changes: {}x : '{}' -> '{}'", changeCount, oldText, newText);
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
			// Execute refactor for pattern: normalize expression.
			String typeRef = matcher.group(1);
			matchedCount++;
			
			if (typeRefFilter.isEmpty() || typeRefFilter.contains(typeRef)) {
				String oldText = matcher.group();
				String newText = "= new " + typeRef + "<>();";
				
				// Update change statistics for source-change.
				SourceChange change = changes.get(oldText);
				if (change == null) {
					change = new SourceChange(oldText, newText);
					changes.put(oldText, change);
				}
				change.addChange();
				
				changedCount++;
				builder.append(newText);
			} else {
				builder.append(matcher.group());
			}
			offset = matcher.end();
		}
		// Copy unmatched trailing section.
		if (offset < sourceLine.length()) {
			builder.append(sourceLine.substring(offset));
		}
		return builder.toString();
	}
}
