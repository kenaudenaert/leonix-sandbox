package be.leonix.tools.refactor.operation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import be.leonix.tools.refactor.FileRefactor;
import be.leonix.tools.refactor.RefactorContext;
import be.leonix.tools.refactor.RefactorMode;
import be.leonix.tools.refactor.model.repo.SourceFile;
import be.leonix.tools.refactor.model.repo.SourceLine;

/**
 * A {@link FileRefactor} that refactors code to use Java Collections API. 
 * 
 * @author Ken Audenaert
 */
public final class CollectionRefactor implements FileRefactor {
	
	private static final Pattern GUAVA_LIST = Pattern.compile(
			"=\\s+Lists\\s*\\.\\s*newArrayList\\s*\\(\\s*\\);");
	
	@Override
	public String getDescription() {
		return "CollectionRefactor (use standard collection constructors)";
	}
	
	@Override
	public void refactorFile(SourceFile sourceFile, RefactorContext context) {
		if (acceptFile(sourceFile)) {
			long changeCount = 0;
			
			for (SourceLine sourceLine : sourceFile.getSourceLines()) {
				String oldLine = sourceLine.getLineContent();
				if (StringUtils.isNotEmpty(oldLine)) {
					
					String newLine = refactorLine(oldLine, GUAVA_LIST, "new ArrayList<>()");
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
