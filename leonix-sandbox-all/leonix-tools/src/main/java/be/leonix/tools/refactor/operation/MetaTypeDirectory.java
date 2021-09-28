package be.leonix.tools.refactor.operation;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class encapsulates a directory containing meta-types.
 * 
 * @author leonix
 */
public final class MetaTypeDirectory {
	
	private static final Logger logger = LoggerFactory.getLogger(MetaTypeDirectory.class);
	
	// The directory names that are ignored during a search.
	private static final Set<String> IGNORED_DIRECTORIES = Set.of("olss");
	
	// The file names that are ignored during a search.
	private static final Set<String> IGNORED_FILES = Set.of(".DS_Store");
	
	// The ambiguous prefixes (see printer sub-package). 
	private static final Set<String> AMBIGUOUS_PREFIXES = Set.of("prnt");
	
	// The ambiguous literals (see DataSourceMetaInfo).
	private static final Set<String> AMBIGUOUS_LITERALS = Set.of("tbfl_fk_datatype");
	
	private final File metaTypeDir;
	
	// The info for the meta-types by their 'fully qualified' name.
	private final Map<String, MetaTypeInfo> infoByName = new LinkedHashMap<>();
	
	// The set of all the known (found in the directory) prefixes.
	private final Set<String> infoPrefixes = new LinkedHashSet<>();
	
	/**
	 * Creates meta-type-directory from the specified directory.
	 * 
	 * @param metaTypeDir The required (non-null) directory.
	 */
	public MetaTypeDirectory(File metaTypeDir) {
		this.metaTypeDir = Objects.requireNonNull(metaTypeDir);
		if (! metaTypeDir.isDirectory()) {
			throw new IllegalArgumentException("Invalid meta-type-dir: " + metaTypeDir);
		}
		
		logger.info("Searching MetaTypes in: {}", metaTypeDir);
		collectMetaTypeInfo(metaTypeDir);
		
		logger.info("Found MetaType classes: {}", infoByName.size());
		for (MetaTypeInfo metaTypeInfo : infoByName.values()) {
			infoPrefixes.add(metaTypeInfo.getKeyPrefix());
		}
		logger.info("Found MetaType prefixes: {}", infoPrefixes.size());
	}
	
	/**
	 * Collects the meta-type-info (recursively) for the specified directory.
	 */
	private void collectMetaTypeInfo(File metaTypeDir) {
		File[] metaTypeFiles = metaTypeDir.listFiles();
		if (metaTypeFiles != null) {
			for (File metaTypeFile : metaTypeFiles) {
				String fileName = metaTypeFile.getName();
				
				if (metaTypeFile.isDirectory() && !IGNORED_DIRECTORIES.contains(fileName)) {
					collectMetaTypeInfo(metaTypeFile);
					
				} else if (metaTypeFile.isFile() && !IGNORED_FILES.contains(fileName)) {
					if (fileName.endsWith("Meta.java")) {
						MetaTypeInfo metaTypeInfo = new MetaTypeInfo(metaTypeFile);

						String infoName = metaTypeInfo.getPackageID()+ "." + metaTypeInfo.getClassName();
						if (infoByName.putIfAbsent(infoName, metaTypeInfo) != null) {
							throw new RuntimeException("Found duplicate meta-type: {}" + infoName);
						} else {
							logger.info("Found MetaType: {}", infoName);
						}
					} else { // Fail hard => must verify the meta-type-dir (and setup filters) !!
						throw new RuntimeException("Found non-meta-type file: {}" + fileName);
					}
				}
			}
		}
	}
	
	public File getMetaTypeDir() {
		return metaTypeDir;
	}
	
	public Map<String, MetaTypeInfo> getInfoByName() {
		return Collections.unmodifiableMap(infoByName);
	}
	
	public Set<String> getInfoPrefixes() {
		return Collections.unmodifiableSet(infoPrefixes);
	}
	
	/**
	 * Returns the meta-type-info by unique constant (filters out formulas).
	 */
	public Map<String, MetaTypeInfo> getInfoByConstant() {
		
		// Find the constants with a unique constant definition.
		Map<String, MetaTypeInfo> infoByConstant = new LinkedHashMap<>();
		for (MetaTypeInfo metaTypeInfo : infoByName.values()) {
			if (AMBIGUOUS_PREFIXES.contains(metaTypeInfo.getKeyPrefix())) {
				continue; // Filter out ambiguous prefixes.
			}
			// Only consider the constants; not the formulas !!
			for (String constant : metaTypeInfo.getConstants().keySet()) {
				if (AMBIGUOUS_LITERALS.contains(constant)) {
					continue; // Filter out ambiguous literals.
				}
				// NOTE: Check for ambiguity (2 constants with same literal).
				if (infoByConstant.putIfAbsent(constant, metaTypeInfo) != null) {
					throw new RuntimeException("Found override: {}" + constant);
				}
			}
		}
		
		int totalFound = infoByConstant.keySet().size();
		logger.info("Constants found: {}", totalFound);
		
		// Only consider the constants that do not have an ambiguous formula.
		for (MetaTypeInfo metaTypeInfo : infoByName.values()) {
			for (String formula : metaTypeInfo.getFormulas().keySet()) {
				
				// NOTE: Check for ambiguity (constant and formula with same literal).
				if (infoByConstant.containsKey(formula)) {
					logger.info("Ignoring: {}", formula);
					infoByConstant.remove(formula);
				}
			}
		}
		
		int totalUsable = infoByConstant.keySet().size();
		logger.info("Constants usable: {}", totalUsable);
		logger.info("Constants skipped: {}", (totalFound - totalUsable));
		return Collections.unmodifiableMap(infoByConstant);
	}
	
	/**
	 * Returns the meta-type-info by unique formula (filters out constants).
	 */
	public Map<String, MetaTypeInfo> getInfoByFormula() {
		
		// Find the constants to filter the formulas.
		Set<String> infoConstants = new HashSet<>();
		for (MetaTypeInfo metaTypeInfo : infoByName.values()) {
			infoConstants.addAll(metaTypeInfo.getConstants().keySet());
		}
		
		// Find the formulas but do not check for uniqueness.
		MultiValuedMap<String, MetaTypeInfo> infoListByFormula = new ArrayListValuedHashMap<>();
		for (MetaTypeInfo metaTypeInfo : infoByName.values()) {
			for (String formula : metaTypeInfo.getFormulas().keySet()) {
				infoListByFormula.put(formula, metaTypeInfo);
			}
		}
		
		int totalFound = infoListByFormula.keySet().size();
		logger.info("Formulas found: {}", totalFound);
		
		// Only use the unique prefixed formulas without an ambiguous constant.
		Map<String, MetaTypeInfo> infoByFormula = new LinkedHashMap<>();
		for (String formula : infoListByFormula.keySet()) {
			int indexUnderscore = formula.indexOf('_');
			if (infoListByFormula.get(formula).size() > 1) {
				logger.info("Ignoring (ambiguous): {}", formula);
			} else if (infoConstants.contains(formula)) {
				logger.info("Ignoring (ambiguous): {}", formula);
			} else if (indexUnderscore == 4) {
				infoByFormula.put(formula, infoListByFormula.get(formula).iterator().next());
			} else {
				logger.info("Ignoring (no-prefix): {}", formula);
			}
		}
		
		int totalUsable = infoByFormula.keySet().size();
		logger.info("Formulas usable: {}", totalUsable);
		logger.info("Formulas skipped: {}", (totalFound - totalUsable));
		return Collections.unmodifiableMap(infoByFormula);
	}
	
	public MetaTypeID getMetaTypeID(String className, String identifier) {
		for (MetaTypeInfo metaType : infoByName.values()) {
			if (metaType.getClassName().equals(className)) {
				return metaType.getConstantID(identifier);
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		// Run this to verify (file+directory) filters.
		MetaTypeDirectory dir = new MetaTypeDirectory(new File("/Users/leonix/Desktop/model"));
		dir.getInfoByConstant();
		dir.getInfoByFormula();
		
		for (MetaTypeInfo metaTypeInfo : dir.getInfoByName().values()) {
			MetaTypeID metaID = metaTypeInfo.getUniqueID();
			if (metaID != null && !metaID.getIdentifier().equals("UNIQUE_IDENTIFIER")) {
				logger.info("Found Unique ID for {}: {}",  metaTypeInfo.getClassName(), metaID.getIdentifier());
			}
		}
	}
}
