package be.leonix.tools.refactor.operation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import be.leonix.tools.refactor.LineRefactor;
import be.leonix.tools.refactor.RefactorContext;
import be.leonix.tools.refactor.model.repo.SourceLine;

/**
 * A {@link LineRefactor} that refactors code to use the diamond syntax. 
 * 
 * @author Ken Audenaert
 */
public final class DiamondRefactor implements LineRefactor {
	
	private static final Pattern DIAMOND = Pattern.compile(
			"(=\\s+new\\s+[\\w\\.]+\\s*<)" +	// new package.Generic<
			"[\\w\\.\\s,]+" +					// package.Foo, Bar
			"(>\\s*\\(\\s*\\)\\s*;)");			// >();
	
	@Override
	public String getDescription() {
		return "DiamondRefactor (use diamond syntax)";
	}
	
	@Override
	public void refactorLine(SourceLine sourceLine, RefactorContext context) {
		String oldLine = sourceLine.getLineContent();
		String newLine = refactorLine(oldLine, DIAMOND);
		if (! StringUtils.equals(oldLine, newLine)) {
			sourceLine.setLineContent(newLine);
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
