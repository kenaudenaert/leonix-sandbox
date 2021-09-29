package be.leonix.tools.refactor.operation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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
	
	private static final Pattern COLLECTION_FACTORY_METHOD = Pattern.compile(
			"=\\s*([\\w]+)\\s*\\.\\s*new([\\w]+)\\s*\\(\\s*\\)\\s*;");
	
	private final Set<String> typeRefFilter = new TreeSet<>();
	private final Map<String, SourceChange> changes = new TreeMap<>();
	
	private final Map<String, String> factoryTypeByRef = new HashMap<>();
	private final Map<String, String> createdTypeByRef = new HashMap<>();
	
	// The number of matches found.
	private long matchedCount = 0;
	// The number of changes done.
	private long changedCount = 0;
	
	public CollectionRefactor() {
		Stream.of(CollectionMapping.values()).forEach(m -> {
			factoryTypeByRef.put(m.getFactoryTypeRef(), m.getFactoryType());
			createdTypeByRef.put(m.getCreatedTypeRef(), m.getCreatedType());
		});
	}
	
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
			
			Set<String> importTypes = new HashSet<>();
			for (SourceLine sourceLine : sourceFile.getSourceLines()) {
				String oldLine = sourceLine.getLineContent();
				if (StringUtils.isNotEmpty(oldLine)) {
					
					String newLine = refactorLine(oldLine, importTypes);
					if (! StringUtils.equals(oldLine, newLine)) {
						sourceLine.setLineContent(newLine);
						changeCount++;
					}
				}
			}
			if (changeCount > 0 && context.getMode() != RefactorMode.LOG_CHANGE) {
				for (String importType : importTypes) {
					sourceFile.addImportLine(importType);
				}
				sourceFile.saveContents();
			}
		}
	}
	
	/**
	 * Returns whether the specified source-file should be refactored.
	 */
	private boolean acceptFile(SourceFile sourceFile) {
		for (String factoryType : factoryTypeByRef.values()) {
			SourceLine factoryImport = sourceFile.getImportLine(factoryType);
			if (factoryImport != null) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Refactors the specified source-line and returns the result.
	 */
	private String refactorLine(String sourceLine, Set<String> importTypes) {
		StringBuilder builder = new StringBuilder();
		int offset = 0;
		Matcher matcher = COLLECTION_FACTORY_METHOD.matcher(sourceLine);
		while (matcher.find(offset)) {
			// Copy unmatched leading section.
			if (matcher.start() > offset) {
				builder.append(sourceLine, offset, matcher.start());
			}
			// Execute refactor for pattern: use the constructor.
			String factoryRef = matcher.group(1);
			String createdRef = matcher.group(2);
			matchedCount++;
			
			if (typeRefFilter.isEmpty() || typeRefFilter.contains(createdRef)) {
				String factoryType = factoryTypeByRef.get(factoryRef);
				String createdType = createdTypeByRef.get(createdRef);
				
				if (factoryType != null && createdType != null) {
					String oldText = matcher.group();
					String newText = "= new " + createdRef + "<>();";
					
					// Update change statistics for source-change.
					SourceChange change = changes.get(oldText);
					if (change == null) {
						change = new SourceChange(oldText, newText);
						changes.put(oldText, change);
					}
					change.addChange();
					
					changedCount++;
					builder.append(newText);
					
					// Add the import for the created type.
					importTypes.add(createdType);
				} else {
					builder.append(matcher.group());
				}
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
