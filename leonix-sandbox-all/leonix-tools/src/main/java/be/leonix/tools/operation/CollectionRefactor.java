package be.leonix.tools.operation;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import be.leonix.tools.FileRefactor;
import be.leonix.tools.RefactorMode;
import be.leonix.tools.model.SourceFile;
import be.leonix.tools.model.SourceLine;

/**
 * A {@link FileRefactor} that refactors code to use Java collections. 
 * 
 * @author Ken Audenaert
 */
public class CollectionRefactor implements FileRefactor {
	
	private static final Pattern GUAVA_LIST = Pattern.compile(
			"=\\s+Lists\\s*\\.\\s*newArrayList\\s*\\(\\s*\\);");
	
	@Override
	public void refactorFile(File sourceFile, RefactorMode mode) {
		SourceFile source = new SourceFile(sourceFile);
		
		SourceLine importLists = source.getImportLine("com.google.common.collect.Lists");
		if (importLists != null) {
			long changeCount = 0;
			
			for (SourceLine sourceLine : source.getSourceLines()) {
				String oldLine = sourceLine.getLineContent();
				String newLine = refactorLine(oldLine, GUAVA_LIST);
				if (! StringUtils.equals(oldLine, newLine)) {
					changeCount++;
				}
			}
			if (changeCount > 0) {
				source.addImportLine("java.util.ArrayList");
				source.saveContents();
			}
		}
	}
	
	protected String refactorLine(String sourceLine, Pattern pattern) {
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
