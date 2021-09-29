package be.leonix.tools.refactor.operation;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.leonix.tools.refactor.FileRefactor;
import be.leonix.tools.refactor.RefactorContext;
import be.leonix.tools.refactor.RefactorMode;
import be.leonix.tools.refactor.model.SourceChange;
import be.leonix.tools.refactor.model.SourceFile;
import be.leonix.tools.refactor.model.SourceLine;

/**
 * A {@link FileRefactor} that refactors code to use Java Collections API. 
 * 
 * @author Ken Audenaert
 */
public final class CollectionRefactor implements FileRefactor {
	
	private static final Logger logger = LoggerFactory.getLogger(CollectionRefactor.class);
	
	private static final Pattern GUAVA_LIST_CTOR = Pattern.compile(
			"=\\s+Lists\\s*\\.\\s*newArrayList\\s*\\(\\s*\\);");
	
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
		return "CollectionRefactor (filter=" + typeRefFilter.toArray() + ")";
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
	public void refactorFile(SourceFile sourceFile, RefactorContext context) {
		if (acceptFile(sourceFile)) {
			long changeCount = 0;
			
			for (SourceLine sourceLine : sourceFile.getSourceLines()) {
				String oldLine = sourceLine.getLineContent();
				if (StringUtils.isNotEmpty(oldLine)) {
					
					String newLine = refactorLine(oldLine, GUAVA_LIST_CTOR, "new ArrayList<>()");
					if (! StringUtils.equals(oldLine, newLine)) {
						sourceLine.setLineContent(newLine);
						changeCount++;
					}
				}
			}
			if (changeCount > 0 && context.getMode() != RefactorMode.LOG_CHANGE) {
				sourceFile.addImportLine("java.util.ArrayList");
				sourceFile.saveContents();
			}
		}
	}
	
	/**
	 * Returns whether the specified source-file should be refactored.
	 */
	private boolean acceptFile(SourceFile sourceFile) {
		SourceLine importLists = sourceFile.getImportLine("com.google.common.collect.Lists");
		return (importLists != null);
	}
	
	/**
	 * Refactors the specified source-line and returns the result.
	 */
	private String refactorLine(String sourceLine, Pattern pattern, String replacement) {
		StringBuilder builder = new StringBuilder();
		int offset = 0;
		Matcher matcher = pattern.matcher(sourceLine);
		while (matcher.find(offset)) {
			// Copy unmatched leading section.
			if (matcher.start() > offset) {
				builder.append(sourceLine, offset, matcher.start());
			}
			// Execute refactor for pattern: replace match with replacement.
			builder.append(replacement);
			offset = matcher.end();
		}
		// Copy unmatched trailing section.
		if (offset < sourceLine.length()) {
			builder.append(sourceLine.substring(offset));
		}
		return builder.toString();
	}
}
