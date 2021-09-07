package be.leonix.tools.refactor.operation;

import java.io.File;
import java.util.HashSet;
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
import be.leonix.tools.refactor.model.SourceFile;
import be.leonix.tools.refactor.model.SourceLine;

/**
 * A {@link FileRefactor} that refactors code to use the MetaInfo constants. 
 * 
 * @author Ken Audenaert
 */
public final class MetaInfoRefactor implements FileRefactor {
	
	private static final Logger logger = LoggerFactory.getLogger(MetaInfoRefactor.class);
	
	private static final Pattern META_INFO_REF = Pattern.compile("\"([\\w_])\"" );
	
	private final Map<String, MetaInfo> metaInfos;
	
	public MetaInfoRefactor(File metaInfoDir) {
		metaInfos = MetaInfo.getMetaInfo(metaInfoDir);
		logger.info("Found {} meta-infos.", metaInfos.size());
	}
	
	@Override
	public void refactorFile(SourceFile sourceFile, RefactorContext context) {
		long changeCount = 0;
		
		Set<MetaInfo> addedMetaInfo = new HashSet<>();
		for (SourceLine sourceLine : sourceFile.getSourceLines()) {
			String oldLine = sourceLine.getLineContent();
			
			StringBuilder builder = new StringBuilder();
			if (StringUtils.isNotEmpty(oldLine)) {
				
				int offset = 0;
				Matcher matcher = META_INFO_REF.matcher(oldLine);
				while (matcher.find(offset)) {
					// Copy unmatched leading section.
					if (matcher.start() > offset) {
						builder.append(oldLine.substring(offset, matcher.start()));
					}
					// Execute refactor for pattern.
					// String reference = matcher.group();
					for (int group = 1; group <= matcher.groupCount(); group++) {
						String reference = matcher.group(1);
						
						MetaInfo metaInfo = metaInfos.get(reference);
						if (metaInfo != null) {
							builder.append(metaInfo.getInfoClass());
							builder.append(".");
							builder.append(metaInfo.getMetaConstants().get(reference));
							
							changeCount++;
							addedMetaInfo.add(metaInfo);
						} else {
							builder.append(matcher.group());
						}
					}
					offset = matcher.end();
				}
				// Copy unmatched trailing section.
				if (offset < oldLine.length()) {
					builder.append(oldLine.substring(offset));
				}
			}
			String newLine = builder.toString();
			if (! StringUtils.equals(oldLine, newLine)) {
				sourceLine.setLineContent(newLine);
				changeCount++;
			}
		}
		if (context.getMode() == RefactorMode.UPDATE_FILE && changeCount > 0) {
			for (MetaInfo metaInfo : addedMetaInfo) {
				sourceFile.addImportLine(metaInfo.getPackageID() + "." + metaInfo.getInfoClass());
			}
			sourceFile.saveContents();
		}
	}
	
	public static void main(String[] args) {
		new MetaInfoRefactor(new File("/Users/leonix/Desktop/meta"));
	}
}