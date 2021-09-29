package be.leonix.tools.refactor.operation;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
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
 * A {@link FileRefactor} that refactors code to select a MetaInfo constant. 
 * 
 * @author Ken Audenaert
 */
public final class MetaTypeReplacer implements FileRefactor {
	
	private static final Logger logger = LoggerFactory.getLogger(MetaTypeReplacer.class);
	
	// The identifier for the 'unique-id' data-model attribute.
	private static final String UNIQUE_ID = "UNIQUE_IDENTIFIER";
	
	private static final Pattern META_TYPE_ID_REF = Pattern.compile(
			"[^\\w]([\\w]+)\\.([\\w]+)[^\\w]");
	
	private final MetaTypeDirectory metaTypeDir;
	
	// The unique-ID (identifier) by meta-type (class-name).
	private final Map<String, String> uniqueIDs = new LinkedHashMap<>();
	
	// The number of changed constant references.
	private final Map<String, SourceChange> changes = new TreeMap<>();
	
	public MetaTypeReplacer(String metaTypeDir) {
		this(new MetaTypeDirectory(new File(metaTypeDir)));
	}
	
	public MetaTypeReplacer(MetaTypeDirectory metaTypeDir) {
		this.metaTypeDir = Objects.requireNonNull(metaTypeDir);
		
		for (MetaTypeInfo metaTypeInfo : metaTypeDir.getInfoByName().values()) {
			MetaTypeID metaID = metaTypeInfo.getUniqueID();
			if (metaID != null && !metaID.getIdentifier().equals(UNIQUE_ID)) {
				String metaType = metaTypeInfo.getClassName();
				String uniqueID = metaID.getIdentifier();
				
				logger.info("Unique ID: {} : {}.", metaType, uniqueID);
				uniqueIDs.put(metaType, uniqueID);
			}
		}
	}
	
	@Override
	public String getDescription() {
		return "MetaTypeReplacer (replaces meta-type identifiers)";
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
	public void refactorFile(SourceFile sourceFile, RefactorContext context) {
		long changeCount = 0;
		
		for (SourceLine sourceLine : sourceFile.getSourceLines()) {
			String oldLine = sourceLine.getLineContent();
			if (StringUtils.isNotEmpty(oldLine)) {
				
				String newLine = refactorLine(oldLine, context);
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
	 * Returns whether the specfified source-line is a comment-line.
	 */
	private static boolean isCommentLine(String sourceLine) {
		String text = sourceLine.trim();
		return (text.startsWith("/*") || 
				text.endsWith("*/")   ||
				text.startsWith("//") ||
				text.startsWith("*"));
	}
	
	/**
	 * Refactors the specified source-line and returns the result.
	 */
	private String refactorLine(String sourceLine, RefactorContext context) {
		StringBuilder builder = new StringBuilder();
		int offset = 0;
		Matcher matcher = META_TYPE_ID_REF.matcher(sourceLine);
		while (matcher.find(offset)) {
			// Copy unmatched leading section.
			if (matcher.start() > offset) {
				builder.append(sourceLine, offset, matcher.start());
			}
			// Execute refactor for pattern: replace the unique-id.
			String metaType = matcher.group(1);
			if (metaType.endsWith("Meta")) {
				String constant = matcher.group(2);
				if (constant.equals(UNIQUE_ID)) {
					String uniqueID = uniqueIDs.get(metaType);
					if (uniqueID != null) {
						String oldText = matcher.group();
						String newText = oldText.replace(UNIQUE_ID, uniqueID);
						
						// Update change statistics for source-change.
						SourceChange change = changes.get(oldText);
						if (change == null) {
							change = new SourceChange(oldText, newText);
							changes.put(oldText, change);
						}
						change.addChange();
						
						builder.append(newText);
					} else {
						builder.append(matcher.group());
					}
				} else if (isCommentLine(sourceLine)) {
					MetaTypeID metaTypeID = metaTypeDir.getMetaTypeID(metaType, constant);
					if (metaTypeID != null) {
						String metaTypeLiteral = '"' + metaTypeID.getLiteral() + '"';
						String oldText = matcher.group();
						String newText = oldText.replace(metaType + '.' + constant, metaTypeLiteral);
						builder.append(newText);
					} else {
						builder.append(matcher.group());
					}
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
