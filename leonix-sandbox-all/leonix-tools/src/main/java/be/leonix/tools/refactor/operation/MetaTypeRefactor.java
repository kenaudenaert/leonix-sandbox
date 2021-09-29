package be.leonix.tools.refactor.operation;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
 * A {@link FileRefactor} that refactors code to use the MetaInfo constants. 
 * 
 * @author Ken Audenaert
 */
public final class MetaTypeRefactor implements FileRefactor {
	
	private static final Logger logger = LoggerFactory.getLogger(MetaTypeRefactor.class);
	
	private static final Pattern META_TYPE_LITERAL = Pattern.compile("\"([\\w]+)\"" );
	
	// The filter for packages (meta-type and data-model package). 
	private static final Set<String> FILTER_PACKAGES = Set.of("com.genohm.slims.common.model");
	
	private final MetaTypeDirectory metaTypeDir;
	private final Map<String, MetaTypeInfo> infoByLiteral;
	private final Set<String> infoPrefixes;
	
	// The number of literals found.
	private long literalCount = 0;
	// The number of literals with a prefix found.
	private long prefixedCount = 0;
	// The number of replaced constant references.
	private long constantCount = 0;
	// The number of replaced formula references.
	private long formulaCount = 0;
	
	public MetaTypeRefactor(String metaTypeDir) {
		this(new MetaTypeDirectory(new File(metaTypeDir)));
	}
	
	public MetaTypeRefactor(MetaTypeDirectory metaTypeDir) {
		this.metaTypeDir = Objects.requireNonNull(metaTypeDir);
		
		infoByLiteral = new LinkedHashMap<>();
		infoByLiteral.putAll(this.metaTypeDir.getInfoByConstant()); // unique constants
		infoByLiteral.putAll(this.metaTypeDir.getInfoByFormula());  // unique formulas
		infoPrefixes = this.metaTypeDir.getInfoPrefixes();
	}
	
	@Override
	public String getDescription() {
		return "MetaTypeRefactor (use constants and formulas)";
	}
	
	@Override
	public void refactorStarted() {
		literalCount  = 0;
		prefixedCount = 0;
		constantCount = 0;
		formulaCount  = 0;
	}
	
	@Override
	public void refactorStopped() {
		logger.info("Statistic: literalCount : {}", literalCount);
		logger.info("Statistic: prefixedCount: {}", prefixedCount);
		logger.info("Statistic: constantCount: {}", constantCount);
		logger.info("Statistic: formulaCount : {}", formulaCount);
	}
	
	@Override
	public void refactorFile(SourceFile sourceFile, RefactorContext context) {
		if (acceptFile(sourceFile)) {
			long changeCount = 0;
			
			Set<MetaTypeInfo> newMetaTypes = new HashSet<>();
			for (SourceLine sourceLine : sourceFile.getSourceLines()) {
				String oldLine = sourceLine.getLineContent();
				if (StringUtils.isNotEmpty(oldLine)) {
					
					String newLine = refactorLine(oldLine, newMetaTypes);
					if (! StringUtils.equals(oldLine, newLine)) {
						sourceLine.setLineContent(newLine);
						changeCount++;
					}
				}
			}
			if (changeCount > 0 && context.getMode() != RefactorMode.LOG_CHANGE) {
				for (MetaTypeInfo metaTypeInfo : newMetaTypes) {
					sourceFile.addImportLine(metaTypeInfo.getPackageID() + "." + metaTypeInfo.getClassName());
				}
				sourceFile.saveContents();
			}
		}
	}
	
	/**
	 * Returns whether the specified source-file should be refactored.
	 */
	private boolean acceptFile(SourceFile sourceFile) {
		try {
			String packageName = sourceFile.getPackageName();
			for (String filterPackage : FILTER_PACKAGES) {
				if (packageName.equals(filterPackage) ||
					packageName.startsWith(filterPackage + '.')) {
					return false;
				}
			}
		} catch (RuntimeException ex) {
			// Commented out file or empty file ??
			logger.error(ex.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Refactors the specified source-line and returns the result.
	 */
	private String refactorLine(String sourceLine, Set<MetaTypeInfo> newMetaTypes) {
		StringBuilder builder = new StringBuilder();
		int offset = 0;
		Matcher matcher = META_TYPE_LITERAL.matcher(sourceLine);
		while (matcher.find(offset)) {
			// Copy unmatched leading section.
			if (matcher.start() > offset) {
				builder.append(sourceLine, offset, matcher.start());
			}
			// Execute refactor for pattern: replace literal by identifier.
			String keyReference = matcher.group(1);
			literalCount++;
			
			MetaTypeInfo metaTypeInfo = infoByLiteral.get(keyReference);
			if (metaTypeInfo != null) {
				String keyConstant = metaTypeInfo.getConstants().get(keyReference);
				if (keyConstant != null) {
					constantCount++;
					builder.append(metaTypeInfo.getClassName());
					builder.append(".");
					builder.append(keyConstant);
				} else {
					formulaCount++;
					String keyFormula = metaTypeInfo.getFormulas().get(keyReference);
					builder.append(metaTypeInfo.getClassName());
					builder.append(".Formulas.");
					builder.append(keyFormula);
				}
				newMetaTypes.add(metaTypeInfo);
			} else if (keyReference.indexOf('_') == 4 && infoPrefixes.contains(keyReference.substring(0,4))) {
				prefixedCount++;
				builder.append(matcher.group());
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
