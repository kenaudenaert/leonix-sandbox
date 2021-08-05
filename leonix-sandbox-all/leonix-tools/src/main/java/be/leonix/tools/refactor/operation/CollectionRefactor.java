package be.leonix.tools.refactor.operation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import be.leonix.tools.refactor.FileRefactor;
import be.leonix.tools.refactor.RefactorContext;
import be.leonix.tools.refactor.RefactorMode;
import be.leonix.tools.refactor.model.SourceFile;
import be.leonix.tools.refactor.model.SourceLine;

/**
 * A {@link FileRefactor} that refactors code to use Java Collections API. 
 * 
 * @author Ken Audenaert
 */
public final class CollectionRefactor implements FileRefactor {
	
	private static final Pattern GUAVA_LIST = Pattern.compile(
			"=\\s+Lists\\s*\\.\\s*newArrayList\\s*\\(\\s*\\);");
	
	@Override
	public void refactorFile(SourceFile sourceFile, RefactorContext context) {
		SourceLine importLists = sourceFile.getImportLine("com.google.common.collect.Lists");
		if (importLists != null) {
			long changeCount = 0;
			
			for (SourceLine sourceLine : sourceFile.getSourceLines()) {
				String oldLine = sourceLine.getLineContent();
				
				String newLine = refactorLine(oldLine, GUAVA_LIST);
				if (! StringUtils.equals(oldLine, newLine)) {
					sourceLine.setLineContent(newLine);
					changeCount++;
				}
			}
			if (context.getMode() == RefactorMode.UPDATE_FILE && changeCount > 0) {
				sourceFile.addImportLine("java.util.ArrayList");
				sourceFile.saveContents();
			}
		}
	}
	
	private String refactorLine(String sourceLine, Pattern pattern) {
		StringBuilder builder = new StringBuilder();
		if (StringUtils.isNotEmpty(sourceLine)) {
			
			int offset = 0;
			Matcher matcher = pattern.matcher(sourceLine);
			while (matcher.find(offset)) {
				// Copy unmatched leading section.
				if (matcher.start() > offset) {
					builder.append(sourceLine.substring(offset, matcher.start()));
				}
				// Execute refactor for pattern.
				// String reference = matcher.group();
				for (int group = 1; group <= matcher.groupCount(); group++) {
					builder.append(matcher.group(group));
				}
				offset = matcher.end();
			}
			// Copy unmatched trailing section.
			if (offset < sourceLine.length()) {
				builder.append(sourceLine.substring(offset));
			}
		}
		return builder.toString();
	}
}
