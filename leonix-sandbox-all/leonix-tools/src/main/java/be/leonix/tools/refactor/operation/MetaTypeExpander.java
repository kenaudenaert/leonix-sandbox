package be.leonix.tools.refactor.operation;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.leonix.tools.refactor.FileRefactor;
import be.leonix.tools.refactor.RefactorContext;
import be.leonix.tools.refactor.RefactorMode;
import be.leonix.tools.refactor.model.repo.SourceFile;
import be.leonix.tools.refactor.model.repo.SourceLine;

/**
 * A {@link FileRefactor} that refactors code to expand the MetaInfo constants. 
 * 
 * @author Ken Audenaert
 */
public final class MetaTypeExpander implements FileRefactor {
	
	private static final Logger logger = LoggerFactory.getLogger(MetaTypeExpander.class);
	
	private static final Pattern META_TYPE_ID_REF = Pattern.compile(
			"[^\\w]([\\w]+)\\.([\\w]+)[^\\w]");
	
	public MetaTypeExpander(String metaTypeDir) {
		this(new MetaTypeDirectory(new File(metaTypeDir)));
	}
	
	public MetaTypeExpander(MetaTypeDirectory metaTypeDir) {
		for (MetaTypeInfo metaTypeInfo : metaTypeDir.getInfoByName().values()) {
			MetaTypeID metaID = metaTypeInfo.getUniqueID();
			if (metaID != null && !metaID.getIdentifier().equals("UNIQUE_IDENTIFIER")) {
				logger.info("Alternative Unique ID: {} ({})",  metaTypeInfo.getClassName(), metaID.getIdentifier());
			}
		}
	}
	
	@Override
	public String getDescription() {
		return "MetaTypeExpander (expands meta-type identifiers)";
	}
	
	@Override
	public void refactorFile(SourceFile sourceFile, RefactorContext context) {
		long changeCount = 0;
		
		for (SourceLine sourceLine : sourceFile.getSourceLines()) {
			String oldLine = sourceLine.getLineContent();
			if (StringUtils.isNotEmpty(oldLine)) {
				
				String newLine = refactorLine(oldLine);
				if (! StringUtils.equals(oldLine, newLine)) {
					sourceLine.setLineContent(newLine);
					changeCount++;
				}
			}
		}
		if (changeCount > 0 && context.getMode() != RefactorMode.LOG_CHANGE) {
			sourceFile.saveContents();
		}
	}
	
	/**
	 * Refactors the specified source-line and returns the result.
	 */
	private String refactorLine(String sourceLine) {
		StringBuilder builder = new StringBuilder();
		int offset = 0;
		Matcher matcher = META_TYPE_ID_REF.matcher(sourceLine);
		while (matcher.find(offset)) {
			// Copy unmatched leading section.
			if (matcher.start() > offset) {
				builder.append(sourceLine, offset, matcher.start());
			}
			// Execute refactor for pattern: replace match with ???.
			builder.append(matcher.group());
			offset = matcher.end();
		}
		// Copy unmatched trailing section.
		if (offset < sourceLine.length()) {
			builder.append(sourceLine.substring(offset));
		}
		return builder.toString();
	}
}
