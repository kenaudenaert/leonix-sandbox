package be.leonix.tools.refactor.operation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class encapsulates meta-info (constant/formula) for a model datatype.
 * 
 * @author leonix
 */
public final class MetaInfo {
	
	private static final Logger logger = LoggerFactory.getLogger(MetaInfo.class);
	
	private static final Pattern META_INFO_DEF = Pattern.compile(
			"public static final String ([\\w]+) = \"([\\w]+)\";");
	
	private final String infoClass;
	private final String packageID;
	private final String keyPrefix;
	
	private final Map<String, String> formulas  = new LinkedHashMap<>();
	private final Map<String, String> constants = new LinkedHashMap<>();
	
	public MetaInfo(File metaInfoFile) {
		logger.info("Loading metaInfoFile: {}", metaInfoFile);
		
		// Check the class-name of the meta-info.
		String fileName = metaInfoFile.getName();
		if (! fileName.endsWith("Meta.java")) {
			throw new RuntimeException("Invalid (type-name) MetaInfo file: " + fileName);
		}
		infoClass = StringUtils.removeEnd(fileName, ".java");
		
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
				
				int blockScope = 0;
				line = reader.readLine();
				while (line != null) {
					if (line.contains("{")) {
						blockScope++;
					} else if (line.contains("}")) {
						blockScope--;
					} else {
						Matcher matcher = META_INFO_DEF.matcher(line);
						if (matcher.find()) {
							String constant = matcher.group(1);
							String literal  = matcher.group(2);
							
							if (blockScope == 1) {
								constants.put(literal, constant);
							} else if (blockScope == 2) {
								formulas.put(literal, constant);
							}
						}
					}
					line = reader.readLine();
				}
			}
		} catch (RuntimeException | IOException ex) {
			throw new RuntimeException("Could not parse meta-info-file: " + metaInfoFile, ex);
		}
		
		// Get the prefix and check constants.
		List<String> keys = new ArrayList<>(constants.keySet());
		Collections.reverse(keys);
		String lastKey = keys.iterator().next();
		if (lastKey == null) {
			throw new RuntimeException("Invalid (no prefix) MetaInfo file: " + fileName);
		} else {
			keyPrefix = lastKey.substring(0, lastKey.indexOf('_'));
			if (keyPrefix.length() != 4) {
				throw new RuntimeException("Invalid (bad prefix) MetaInfo file: " + keyPrefix);
			}
			
			Iterator<String> iterator = constants.keySet().iterator();
			while (iterator.hasNext()) {
				String value = iterator.next();
				if (! value.startsWith(keyPrefix + "_")) {
					logger.error("Invalid (bad prefix) Constant: " + value);
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
	
	public Map<String, String> getConstants() {
		return Collections.unmodifiableMap(constants);
	}
	
	public Map<String, String> getFormulas() {
		return Collections.unmodifiableMap(formulas);
	}
	
	public static Map<String, MetaInfo> getMetaInfo(File metaInfoDir) {
		Map<String, MetaInfo> metaInfos = new LinkedHashMap<>();
		getMetaInfo(metaInfoDir, metaInfos);
		
		int totalFound = metaInfos.keySet().size();
		logger.info("MetaInfo Found: {}", totalFound);
		
		// Only consider the constants that do not have an ambiguous formula.
		Set<MetaInfo> infoSet = new HashSet<MetaInfo>(metaInfos.values());
		for (MetaInfo metaInfo : infoSet) {
			for (String formula : metaInfo.getFormulas().keySet()) {
				if (metaInfos.containsKey(formula)) {
					logger.info("Disabled: {}", formula);
					metaInfos.remove(formula);
				}
			}
		}
		
		int totalUsable = metaInfos.keySet().size();
		logger.info("MetaInfo Actual: {}", totalUsable);
		logger.info("MetaInfo Ignore: {}", (totalFound - totalUsable));
		return Collections.unmodifiableMap(metaInfos);
	}
	
	public static void getMetaInfo(File metaInfoDir, Map<String, MetaInfo> metaInfos) {
		File[] metaInfoFiles = metaInfoDir.listFiles();
		if (metaInfoFiles != null) {
			for (File metaInfoFile : metaInfoFiles) {
				if (metaInfoFile.isDirectory()) {
					getMetaInfo(metaInfoFile, metaInfos);
					
				} else if (metaInfoFile.isFile()) {
					String fileName = metaInfoFile.getName();
					if (fileName.equals(".DS_Store")) {
						continue;
					
					} else if (fileName.endsWith("Meta.java")) {
						MetaInfo metaInfo = new MetaInfo(metaInfoFile);
						if (metaInfo.getKeyPrefix().equals("prnt")) {
							continue; // Skipping since too ambiguous !!
						}
						// Only consider the constants; not the formulas !!
						for (String literal : metaInfo.getConstants().keySet()) {
							if (literal.equals("tbfl_fk_datatype")) {
								continue; // Skipping since too ambiguous !!
							}
							if (metaInfos.putIfAbsent(literal, metaInfo) != null) {
								// Fail hard => we can filter for ambiguity !!
								throw new RuntimeException("Found override: {}" + literal);
							}
						}
					} else { // Fail hard => we can verify input dir-tree !!
						throw new RuntimeException("Found non-meta-info: {}" + fileName);
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		getMetaInfo(new File("/Users/leonix/Desktop/meta"));
	}
}
