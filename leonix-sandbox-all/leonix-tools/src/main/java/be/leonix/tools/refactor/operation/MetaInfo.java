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
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class encapsulates info (constants/formulas) for a model meta-class.
 * 
 * @author leonix
 */
public final class MetaInfo {
	
	private static final Logger logger = LoggerFactory.getLogger(MetaInfo.class);
	
	// The set of ignored alternative names for constants.
	private static final Set<String> IGNORED_OVERRIDES = Set.of("UNIQUE_IDENTIFIER");
	
	// The set of ignored invalid (non-prefix) constants.
	private static final Set<String> IGNORED_NO_PREFIX = Set.of("roleField");
	
	// The pattern for a meta-class identifier (formula or constant).
	private static final Pattern IDENTIFIER_DEF = Pattern.compile(
			"public static final String ([\\w]+) = \"([\\w]+)\";");
	
	private final File sourceFile;
	private final String metaClass;
	private final String packageID;
	private final String keyPrefix;
	
	// The formulas (identifiers) by string value (literal).
	private final Map<String, String> formulas  = new LinkedHashMap<>();
	
	// The constants (identifiers) by string value (literal).
	private final Map<String, String> constants = new LinkedHashMap<>();
	
	/**
	 * Creates meta-info from the specified meta-class source-file.
	 * 
	 * @param sourceFile The required (non-null) source-file.
	 */
	public MetaInfo(File sourceFile) {
		this.sourceFile = Objects.requireNonNull(sourceFile);
		
		// Check the class-name of the meta-class.
		String fileName = sourceFile.getName();
		if (! fileName.endsWith("Meta.java")) {
			throw new RuntimeException("Invalid (filename) meta-class file: " + fileName);
		}
		metaClass = StringUtils.removeEnd(fileName, ".java");
		
		logger.info("Loading metaInfo from: {}", sourceFile);
		try (FileReader fileReader = new FileReader(sourceFile, StandardCharsets.UTF_8)) {
			try (BufferedReader reader = new BufferedReader(fileReader)) {
				String line = reader.readLine().trim();
				
				// Get the package-id for the meta-class.
				if (line != null && line.startsWith("package ") && line.endsWith(";")) {
					packageID = line.substring("package ".length(), line.length() - 1).trim();
				} else {
					throw new RuntimeException("Invalid (no package) meta-class file: " + fileName);
				}
				
				int blockScope = 0;
				line = reader.readLine();
				while (line != null) {
					if (line.contains("{")) {
						blockScope++;
					} else if (line.contains("}")) {
						blockScope--;
					} else {
						Matcher matcher = IDENTIFIER_DEF.matcher(line);
						if (matcher.find()) {
							String identifier = matcher.group(1);
							String literal    = matcher.group(2);
							
							if (blockScope == 1) {
								String oldIdentifier = constants.put(literal, identifier);
								if (oldIdentifier != null) {
									if (IGNORED_OVERRIDES.contains(identifier)) {
										constants.put(literal, oldIdentifier);
									} else {
										throw new RuntimeException("Found constant override in " + metaClass + ": " + identifier);
									}
								}
							} else if (blockScope == 2) {
								String oldIdentifier = formulas.put(literal, identifier);
								if (oldIdentifier != null) {
									throw new RuntimeException("Found formula override in " + metaClass + ": " + identifier);
								}
							}
						}
					}
					line = reader.readLine();
				}
			}
		} catch (IOException | RuntimeException ex) {
			throw new RuntimeException("Could not parse meta-class file: " + sourceFile, ex);
		}
		
		// Get the prefix and check literals for constants.
		List<String> keys = new ArrayList<>(constants.keySet());
		Collections.reverse(keys);
		String lastKey = keys.iterator().next();
		if (lastKey == null) {
			throw new RuntimeException("Invalid (no prefix) meta-class file: " + fileName);
		} else {
			keyPrefix = lastKey.substring(0, lastKey.indexOf('_'));
			if (keyPrefix.length() != 4) {
				throw new RuntimeException("Invalid (bad prefix) meta-class file: " + keyPrefix);
			}
			
			Iterator<String> iterator = constants.keySet().iterator();
			while (iterator.hasNext()) {
				String value = iterator.next();
				if (! value.startsWith(keyPrefix + "_")) {
					if (! IGNORED_NO_PREFIX.contains(value)) {
						throw new RuntimeException("Invalid (bad prefix) meta-class constant: " + value);
					}
					iterator.remove();
				}
			}
		}
	}
	
	public File getSourceFile() {
		return sourceFile;
	}
	
	public String getMetaClass() {
		return metaClass;
	}
	
	public String getPackageID() {
		return packageID;
	}
	
	public String getKeyPrefix() {
		return keyPrefix;
	}
	
	/**
	 * Returns the (read-only) formulas (identifiers) by their literal.
	 */
	public Map<String, String> getFormulas() {
		return Collections.unmodifiableMap(formulas);
	}
	
	/**
	 * Returns the (read-only) constants (identifiers) by their literal.
	 */
	public Map<String, String> getConstants() {
		return Collections.unmodifiableMap(constants);
	}
}
