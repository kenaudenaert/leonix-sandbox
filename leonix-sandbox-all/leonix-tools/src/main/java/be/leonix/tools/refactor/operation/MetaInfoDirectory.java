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
 * This class encapsulates a directory containing meta-info classes.
 * 
 * @author leonix
 */
public final class MetaInfoDirectory {
	
	private static final Logger logger = LoggerFactory.getLogger(MetaInfoDirectory.class);
	
	private static final Set<String> DIRECTORY_EXCLUDES = Set.of("olss");
	private static final Set<String> FILE_EXCLUDES = Set.of(".DS_Store");
	
	// The filter for prefixes (see printer sub-package). 
	private static final Set<String> FILTER_PREFIXES = Set.of("prnt");
	// The filter for literals (see DataSourceMetaInfo).
	private static final Set<String> FILTER_LITERALS = Set.of("tbfl_fk_datatype");
	
	private final File metaInfoDir;
	
	// The details for the meta-info classes by their qualified class-name.
	private final Map<String, MetaInfo> infoByClassName = new LinkedHashMap<>();
	private final Set<String> infoPrefixes = new LinkedHashSet<>();
	
	public MetaInfoDirectory(File metaInfoDir) {
		this.metaInfoDir = Objects.requireNonNull(metaInfoDir);
		if (! metaInfoDir.isDirectory()) {
			throw new IllegalArgumentException("Invalid meta-info-dir: " + metaInfoDir);
		}
		
		logger.info("Searching MetaInfoDirectory: {}", metaInfoDir);
		collectMetaInfo(metaInfoDir);
		
		logger.info("Found MetaInfo classes: {}", infoByClassName.size());
		for (MetaInfo metaInfo : infoByClassName.values()) {
			infoPrefixes.add(metaInfo.getKeyPrefix());
		}
		logger.info("Found MetaInfo prefixes: {}", infoPrefixes.size());
	}
	
	/**
	 * Collects the meta-info (recursively) for the specified directory.
	 */
	private void collectMetaInfo(File metaInfoDir) {
		File[] metaInfoFiles = metaInfoDir.listFiles();
		if (metaInfoFiles != null) {
			for (File metaInfoFile : metaInfoFiles) {
				String fileName = metaInfoFile.getName();
				
				if (metaInfoFile.isDirectory() && !DIRECTORY_EXCLUDES.contains(fileName)) {
					collectMetaInfo(metaInfoFile);
					
				} else if (metaInfoFile.isFile() && !FILE_EXCLUDES.contains(fileName)) {
					if (fileName.endsWith("Meta.java")) {
						MetaInfo metaInfo = new MetaInfo(metaInfoFile);
						
						String className = metaInfo.getPackageID()+ "." + metaInfo.getInfoClass();
						if (infoByClassName.putIfAbsent(className, metaInfo) != null) {
							throw new RuntimeException("Found duplicate info: {}" + className);
						}
					} else { // Fail hard => must verify the meta-info-dir (and setup filters) !!
						throw new RuntimeException("Found non-meta-info file: {}" + fileName);
					}
				}
			}
		}
	}
	
	public File getMetaInfoDir() {
		return metaInfoDir;
	}
	
	public Set<String> getInfoPrefixes() {
		return Collections.unmodifiableSet(infoPrefixes);
	}
	
	/**
	 * Returns the meta-info by unique constant (filters out formulas).
	 */
	public Map<String, MetaInfo> getInfoByConstant() {
		
		// Find the constants with a unique constant definition.
		Map<String, MetaInfo> infoByConstant = new LinkedHashMap<>();
		for (MetaInfo metaInfo : infoByClassName.values()) {
			if (FILTER_PREFIXES.contains(metaInfo.getKeyPrefix())) {
				continue; // Filter out ambiguous prefixes.
			}
			// Only consider the constants; not the formulas !!
			for (String constant : metaInfo.getConstants().keySet()) {
				if (FILTER_LITERALS.contains(constant)) {
					continue; // Filter out ambiguous literals.
				}
				// NOTE: Check for ambiguity (2 constants with same literal).
				if (infoByConstant.putIfAbsent(constant, metaInfo) != null) {
					throw new RuntimeException("Found override: {}" + constant);
				}
			}
		}
		
		int totalFound = infoByConstant.keySet().size();
		logger.info("Constants found: {}", totalFound);
		
		// Only consider the constants that do not have an ambiguous formula.
		for (MetaInfo metaInfo : infoByClassName.values()) {
			for (String formula : metaInfo.getFormulas().keySet()) {
				
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
	 * Returns the meta-info by unique formula (filters out constants).
	 */
	public Map<String, MetaInfo> getInfoByFormula() {
		
		// Find the constants to filter the formulas.
		Set<String> infoConstants = new HashSet<>();
		for (MetaInfo metaInfo : infoByClassName.values()) {
			infoConstants.addAll(metaInfo.getConstants().keySet());
		}
		
		// Find the formulas but do not check for uniqueness.
		MultiValuedMap<String, MetaInfo> infoListByFormula = new ArrayListValuedHashMap<>();
		for (MetaInfo metaInfo : infoByClassName.values()) {
			for (String formula : metaInfo.getFormulas().keySet()) {
				infoListByFormula.put(formula, metaInfo);
			}
		}
		
		int totalFound = infoListByFormula.keySet().size();
		logger.info("Formulas found: {}", totalFound);
		
		// Only use the unique prefixed formulas without an ambiguous constant.
		Map<String, MetaInfo> infoByFormula = new LinkedHashMap<>();
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
	
	public static void main(String[] args) {
		// Run this to verify (file+directory) filters.
		MetaInfoDirectory dir = new MetaInfoDirectory(new File("/Users/leonix/Desktop/model"));
		dir.getInfoByConstant();
		dir.getInfoByFormula();
	}
}
