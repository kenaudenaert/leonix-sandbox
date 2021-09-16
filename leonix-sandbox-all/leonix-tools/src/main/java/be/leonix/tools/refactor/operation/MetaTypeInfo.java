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
 * This class encapsulates the info about a data-model meta-type.
 * 
 * @author leonix
 */
public final class MetaTypeInfo {
	
	private static final Logger logger = LoggerFactory.getLogger(MetaTypeInfo.class);
	
	// The identifier for the 'unique-id' data-model attribute.
	private static final String UNIQUE_ID = "UNIQUE_IDENTIFIER";
	
	// The set of ignored alternative names for constants.
	private static final Set<String> IGNORED_OVERRIDES = Set.of(UNIQUE_ID);
	
	// The set of ignored invalid (non-prefix) constants.
	private static final Set<String> IGNORED_NO_PREFIX = Set.of("roleField");
	
	// The pattern for a meta-type identifier (formula or constant).
	private static final Pattern IDENTIFIER_DEF = Pattern.compile(
			"public static final String ([\\w]+) = \"([\\w]+)\";");
	
	private final File sourceFile;
	private final String className;
	private final String packageID;
	private final String keyPrefix;
	
	private final List<MetaTypeID> formulaIDs  = new ArrayList<>();
	private final List<MetaTypeID> constantIDs = new ArrayList<>();
	
	// The formulas (identifiers) by string value (literal).
	private final Map<String, String> formulas = new LinkedHashMap<>();
	
	// The constants (identifiers) by string value (literal).
	private final Map<String, String> constants = new LinkedHashMap<>();
	
	/**
	 * Creates meta-type-info from the specified meta-type source-file.
	 * 
	 * @param sourceFile The required (non-null) source-file.
	 */
	public MetaTypeInfo(File sourceFile) {
		this.sourceFile = Objects.requireNonNull(sourceFile);
		
		// Check the class-name of the meta-type.
		String fileName = sourceFile.getName();
		if (! fileName.endsWith("Meta.java")) {
			throw new RuntimeException("Invalid (filename) meta-type file: " + fileName);
		}
		className = StringUtils.removeEnd(fileName, ".java");
		
		logger.info("Loading MetaType from: {}", sourceFile);
		try (FileReader fileReader = new FileReader(sourceFile, StandardCharsets.UTF_8)) {
			try (BufferedReader reader = new BufferedReader(fileReader)) {
				String line = reader.readLine().trim();
				
				// Get the package-id for the meta-type.
				if (line != null && line.startsWith("package ") && line.endsWith(";")) {
					packageID = line.substring("package ".length(), line.length() - 1).trim();
				} else {
					throw new RuntimeException("Invalid (no package) meta-type file: " + fileName);
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
							
							MetaTypeID metaID = new MetaTypeID(identifier, literal);
							if (blockScope == 1) {
								constantIDs.add(metaID);
								
								// Ensure unique identifier for a constant.
								String oldIdentifier = constants.put(literal, identifier);
								if (oldIdentifier != null) {
									if (IGNORED_OVERRIDES.contains(identifier)) {
										constants.put(literal, oldIdentifier);
									} else {
										throw new RuntimeException("Found constant override in " + className + ": " + identifier);
									}
								}
							} else if (blockScope == 2) {
								formulaIDs.add(metaID);
								
								// Ensure unique identifier for a formula.
								String oldIdentifier = formulas.put(literal, identifier);
								if (oldIdentifier != null) {
									throw new RuntimeException("Found formula override in " + className + ": " + identifier);
								}
							}
						}
					}
					line = reader.readLine();
				}
			}
		} catch (IOException | RuntimeException ex) {
			throw new RuntimeException("Could not parse meta-type file: " + sourceFile, ex);
		}
		
		// Get the prefix and check literals for constants.
		List<String> keys = new ArrayList<>(constants.keySet());
		Collections.reverse(keys);
		String lastKey = keys.iterator().next();
		if (lastKey == null) {
			throw new RuntimeException("Invalid (no prefix) meta-type file: " + fileName);
		} else {
			keyPrefix = lastKey.substring(0, lastKey.indexOf('_'));
			if (keyPrefix.length() != 4) {
				throw new RuntimeException("Invalid (bad prefix) meta-type file: " + keyPrefix);
			}
			
			Iterator<String> iterator = constants.keySet().iterator();
			while (iterator.hasNext()) {
				String value = iterator.next();
				if (! value.startsWith(keyPrefix + "_")) {
					if (! IGNORED_NO_PREFIX.contains(value)) {
						throw new RuntimeException("Invalid (bad prefix) meta-type constant: " + value);
					}
					iterator.remove();
				}
			}
		}
	}
	
	public File getSourceFile() {
		return sourceFile;
	}
	
	public String getClassName() {
		return className;
	}
	
	public String getPackageID() {
		return packageID;
	}
	
	public String getKeyPrefix() {
		return keyPrefix;
	}
	
	public List<MetaTypeID> getFormulaIDs() {
		return Collections.unmodifiableList(formulaIDs);
	}
	
	public List<MetaTypeID> getConstantIDs() {
		return Collections.unmodifiableList(constantIDs);
	}
	
	public MetaTypeID getConstantID(String name) {
		for (MetaTypeID constantID : constantIDs) {
			if (constantID.getIdentifier().equals(name)) {
				return constantID;
			}
		}
		return null;
	}
	
	public MetaTypeID getUniqueID() {
		MetaTypeID standardUID = getConstantID(UNIQUE_ID);
		if (standardUID != null) {
			String alternative = constants.get(standardUID.getLiteral());
			if (alternative != null) {
				return getConstantID(alternative);
			}
		}
		return standardUID;
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
