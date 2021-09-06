package be.leonix.tools.refactor.operation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.leonix.tools.refactor.FileRefactor;
import be.leonix.tools.refactor.RefactorContext;
import be.leonix.tools.refactor.model.SourceFile;

/**
 * A {@link FileRefactor} that refactors code to use the MetaInfo constants. 
 * 
 * @author Ken Audenaert
 */
public final class MetaInfoRefactor implements FileRefactor {
	
	/**
	 * @author leonix
	 */
	public static final class MetaInfoType {
		
		private static final Logger logger = LoggerFactory.getLogger(MetaInfoType.class);
		
		private static final Pattern META_INFO_DEF = Pattern.compile(
				"public static final String ([\\w_]+) = \"([\\w_]+)\";");
		
		private String packageID;
		private String infoClass;
		private final Map<String, String> metaConstants = new LinkedHashMap<>();
		
		public MetaInfoType(File metaInfoFile) {
			
			// Get the class-name of the meta-info type.
			String fileName = metaInfoFile.getName();
			if (fileName.endsWith("Meta.java")) {
				infoClass = StringUtils.removeEnd(fileName, "Meta.java");
				logger.info("Found meta-info class: {}", infoClass);
			} else {
				logger.warn("Skipped non-meta-info: {}", fileName);
				return;
			}
			
			try (FileReader fileReader = new FileReader(metaInfoFile, StandardCharsets.UTF_8)) {
				try (BufferedReader reader = new BufferedReader(fileReader)) {
					String line = reader.readLine();
					
					// Get the pacage-name of the meta-info type.
					if (line != null && line.startsWith("package ") && line.endsWith(";")) {
						packageID = line.substring("package ".length(), line.length() - 1);
						logger.info("Found meta-info package: {}", packageID);
					} else {
						logger.warn("Skipped no package: {}", fileName);
						return;
					}
					
					line = reader.readLine();
					while (line != null) {
						Matcher matcher = META_INFO_DEF.matcher(line);
						if (matcher.find()) {
							String constant = matcher.group(1);
							String literal  = matcher.group(2);
							metaConstants.put(literal, constant);
						}
						line = reader.readLine();
					}
				}
			} catch (RuntimeException | IOException ex) {
				throw new RuntimeException("Could not parse meta-info-file: " + metaInfoFile, ex);
			}
		}
		
		public String getInfoClass() {
			return infoClass;
		}
		
		public String getPackageID() {
			return packageID;
		}
		
		public Map<String, String> getMetaConstants() {
			return Collections.unmodifiableMap(metaConstants);
		}
	}
	
	private static final Logger logger = LoggerFactory.getLogger(MetaInfoRefactor.class);
	
	private static final Pattern META_INFO_REF = Pattern.compile("\"([\\w_])\"" );
	
	private final Map<String, MetaInfoType> metaInfos = new LinkedHashMap<>();
	
	public MetaInfoRefactor(File metaInfoDir) {
		File[] metaInfoFiles = metaInfoDir.listFiles();
		if (metaInfoFiles != null) {
			for (File metaInfoFile : metaInfoFiles) {
				MetaInfoType metaInfo = new MetaInfoType(metaInfoFile);
				for (String literal : metaInfo.getMetaConstants().keySet()) {
					if (metaInfos.putIfAbsent(literal, metaInfo) != null) {
						logger.error("Found literal override: {}", literal);
					}
				}
			}
		}
	}
	
	@Override
	public void refactorFile(SourceFile sourceFile, RefactorContext context) {
		
	}
	
	public static void main(String[] args) {
		new MetaInfoRefactor(new File("/Users/leonix/Desktop/meta"));
	}
}
