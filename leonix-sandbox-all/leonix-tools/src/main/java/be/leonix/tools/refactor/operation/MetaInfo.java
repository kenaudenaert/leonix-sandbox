package be.leonix.tools.refactor.operation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * @author leonix
 */
public final class MetaInfo {
	
	private static final Pattern META_INFO_DEF = Pattern.compile(
			"public static final String ([\\w_]+) = \"([\\w_]+)\";");
	
	private final String infoClass;
	private final String packageID;
	private final String keyPrefix;
	
	private final Map<String, String> metaConstants = new LinkedHashMap<>();
	
	public MetaInfo(File metaInfoFile) {
		
		// Check the class-name of the meta-info.
		String fileName = metaInfoFile.getName();
		if (! fileName.endsWith("Meta.java")) {
			throw new RuntimeException("Invalid (type-name) MetaInfo file: " + fileName);
		}
		infoClass = StringUtils.removeEnd(fileName, "Meta.java");
		
		// Get the package and constants.
		try (FileReader fileReader = new FileReader(metaInfoFile, StandardCharsets.UTF_8)) {
			try (BufferedReader reader = new BufferedReader(fileReader)) {
				String line = reader.readLine();
				
				// Get the package-name of the meta-info.
				if (line != null && line.startsWith("package ") && line.endsWith(";")) {
					packageID = line.substring("package ".length(), line.length() - 1);
				} else {
					throw new RuntimeException("Invalid (no package) MetaInfo file: " + fileName);
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
		
		// Get the prefix and filter on it.
		List<String> keys = new ArrayList<>(metaConstants.keySet());
		Collections.reverse(keys);
		String lastKey = keys.iterator().next();
		if (lastKey == null) {
			throw new RuntimeException("Invalid (no prefi) MetaInfo file: " + fileName);
		} else {
			keyPrefix = lastKey.substring(0, lastKey.indexOf('_'));
			
			Iterator<String> iterator = metaConstants.keySet().iterator();
			while (iterator.hasNext()) {
				String value = iterator.next();
				if (! value.startsWith(keyPrefix + "_")) {
					iterator.remove();
				}
			}
		}
	}
	
	public String getInfoClass() {
		return infoClass;
	}
	
	public String getPackageID() {
		return packageID;
	}
	
	public String getKeyPrefix() {
		return keyPrefix;
	}
	
	public Map<String, String> getMetaConstants() {
		return Collections.unmodifiableMap(metaConstants);
	}
	
	public static Map<String, MetaInfo> getMetaInfo(File metaInfoDir) {
		Map<String, MetaInfo> metaInfos = new LinkedHashMap<>();
		
		File[] metaInfoFiles = metaInfoDir.listFiles();
		if (metaInfoFiles != null) {
			for (File metaInfoFile : metaInfoFiles) {
				if (metaInfoFile.getName().endsWith("Meta.java")) {
					
					MetaInfo metaInfo = new MetaInfo(metaInfoFile);
					for (String literal : metaInfo.getMetaConstants().keySet()) {
						if (literal.equals("tbfl_fk_datatype")) {
							// DataSourceField 
							continue; // Skip this one -> do it manual !!
						}
						if (metaInfos.putIfAbsent(literal, metaInfo) != null) {
							throw new RuntimeException("Found override: {}" + literal);
						}
					}
				}
			}
		}
		
		return Collections.unmodifiableMap(metaInfos);
	}
}
