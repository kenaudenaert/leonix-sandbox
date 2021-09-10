package be.leonix.tools.refactor.operation;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import be.leonix.tools.refactor.FileRefactor;
import be.leonix.tools.refactor.RefactorContext;
import be.leonix.tools.refactor.RefactorMode;
import be.leonix.tools.refactor.model.SourceFile;
import be.leonix.tools.refactor.model.SourceLine;

/**
 * A {@link FileRefactor} that refactors code to use the MetaInfo constants. 
 * 
 * @author Ken Audenaert
 */
public final class MetaInfoRefactor implements FileRefactor {
	
	private static final Pattern META_INFO_REF = Pattern.compile("\"([\\w]+)\"" );
	
	private final Map<String, MetaInfo> infoByConstant;
	
	public MetaInfoRefactor(File metaInfoDir) {
		infoByConstant = new LinkedHashMap<>();
		infoByConstant.putAll(MetaInfo.getInfoByConstant(metaInfoDir)); // unique constants
		infoByConstant.putAll(MetaInfo.getInfoByFormula(metaInfoDir));  // unique formulas
	}
	
	@Override
	public void refactorFile(SourceFile sourceFile, RefactorContext context) {
		if (sourceFile.getSourceFile().getParentFile().getAbsolutePath().endsWith(
				"com/genohm/slims/common/model")) {
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
						builder.append(oldLine.substring(offset, matcher.start()));
					}
					// Execute refactor for pattern.
					String keyReference = matcher.group(1);
					
					MetaInfo metaInfo = infoByConstant.get(keyReference);
					if (metaInfo != null) {
						String keyConstant = metaInfo.getConstants().get(keyReference);
						if (keyConstant != null) {
							builder.append(metaInfo.getInfoClass());
							builder.append(".");
							builder.append(keyConstant);
						} else {
							String keyFormula = metaInfo.getFormulas().get(keyReference);
							builder.append(metaInfo.getInfoClass());
							builder.append(".Formula.");
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
