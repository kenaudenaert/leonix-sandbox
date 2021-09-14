package be.leonix.tools.refactor.operation;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
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
public final class MetaInfoRefactor implements FileRefactor {
	
	private static final Logger logger = LoggerFactory.getLogger(MetaInfoRefactor.class);
	
	private static final Pattern META_INFO_REF = Pattern.compile("\"([\\w]+)\"" );
	
	// The filter for packages (meta-info and its source package). 
	private static final Set<String> FILTER_PACKAGES = Set.of("com.genohm.slims.common.model");
	
	private final Map<String, MetaInfo> infoByLiteral;
	
	public MetaInfoRefactor(String metaInfoDir) {
		this(new MetaInfoDirectory(new File(metaInfoDir)));
	}
	
	public MetaInfoRefactor(MetaInfoDirectory metaInfoDir) {
		infoByLiteral = new LinkedHashMap<>();
		infoByLiteral.putAll(metaInfoDir.getInfoByConstant()); // unique constants
	//	infoByLiteral.putAll(metaInfoDir.getInfoByFormula());  // unique formulas
	}
	
	@Override
	public String getDescription() {
		return "MetaInfoRefactor (use constants and formulas)";
	}
	
	@Override
	public void refactorFile(SourceFile sourceFile, RefactorContext context) {
		try {
			String packageName = sourceFile.getPackageName();
			for (String filterPackage : FILTER_PACKAGES) {
				if (packageName.equals(filterPackage) ||
					packageName.startsWith(filterPackage + '.')) {
					return;
				}
			}
		} catch (Exception ex) {
			// Commented out file or empty file ??
			logger.error(ex.getMessage());
			return;
		}
		long changeCount = 0;
		
		Set<MetaInfo> addedMetaInfo = new HashSet<>();
		for (SourceLine sourceLine : sourceFile.getSourceLines()) {
			String oldLine = sourceLine.getLineContent();
			if (StringUtils.isNotEmpty(oldLine)) {
				StringBuilder builder = new StringBuilder();
				
				int offset = 0;
				Matcher matcher = META_INFO_REF.matcher(oldLine);
				while (matcher.find(offset)) {
					// Copy unmatched leading section.
					if (matcher.start() > offset) {
						builder.append(oldLine, offset, matcher.start());
					}
					// Execute refactor for pattern.
					String keyReference = matcher.group(1);
					
					MetaInfo metaInfo = infoByLiteral.get(keyReference);
					if (metaInfo != null) {
						String keyConstant = metaInfo.getConstants().get(keyReference);
						if (keyConstant != null) {
							builder.append(metaInfo.getInfoClass());
							builder.append(".");
							builder.append(keyConstant);
						} else {
							String keyFormula = metaInfo.getFormulas().get(keyReference);
							builder.append(metaInfo.getInfoClass());
							builder.append(".Formulas.");
							builder.append(keyFormula);
						}
						changeCount++;
						addedMetaInfo.add(metaInfo);
					} else {
						builder.append(matcher.group());
					}
					
					offset = matcher.end();
				}
				// Copy unmatched trailing section.
				if (offset < oldLine.length()) {
					builder.append(oldLine.substring(offset));
				}
				
				String newLine = builder.toString();
				if (!StringUtils.equals(oldLine, newLine)) {
					sourceLine.setLineContent(newLine);
					changeCount++;
				}
			}
		}
		if (context.getMode() == RefactorMode.UPDATE_FILE && changeCount > 0) {
			for (MetaInfo metaInfo : addedMetaInfo) {
				sourceFile.addImportLine(metaInfo.getPackageID() + "." + metaInfo.getInfoClass());
			}
			sourceFile.saveContents();
		}
	}
}
