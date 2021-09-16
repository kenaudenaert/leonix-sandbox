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
	
	private static final Pattern NON_DIAMOND_CTOR = Pattern.compile(
			"(=\\s*new\\s+[\\w\\.]+\\s*<)" +	// = new package.Generic<
			"[\\w\\.\\s,]+" +					// package.Foo, Bar
			"(>\\s*\\(\\s*\\)\\s*;)");			// >();
	
	@Override
	public String getDescription() {
		return "DiamondRefactor (use diamond syntax ctor)";
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
			builder.append(matcher.group(1));
			builder.append(matcher.group(2));
			offset = matcher.end();
		}
		// Copy unmatched trailing section.
		if (offset < sourceLine.length()) {
			builder.append(sourceLine.substring(offset));
		}
		return builder.toString();
	}
}
