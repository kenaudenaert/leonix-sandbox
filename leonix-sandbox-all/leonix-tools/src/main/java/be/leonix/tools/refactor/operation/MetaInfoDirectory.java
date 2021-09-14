package be.leonix.tools.refactor.operation;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class encapsulates a directory containing meta-info classes.
 * 
 * @author leonix
 */
public class MetaInfoDirectory {
	
	private static final Logger logger = LoggerFactory.getLogger(MetaInfoDirectory.class);
	
	private static final Set<String> DIRECTORY_EXCLUDES = Set.of("olss");
	private static final Set<String> FILE_EXCLUDES = Set.of(".DS_Store");
	
	private final File metaInfoDir;
	
	// The details for the meta-info classes by their qualified class-name.
	private final Map<String, MetaInfo> infoByName = new LinkedHashMap<>();
	
	public MetaInfoDirectory(File metaInfoDir) {
		this.metaInfoDir = Objects.requireNonNull(metaInfoDir);
		if (! metaInfoDir.isDirectory()) {
			throw new IllegalArgumentException("Invalid meta-info-dir: " + metaInfoDir);
		}
		
		logger.info("Searching MetaInfoDirectory: {}", metaInfoDir);
		collectMetaInfo(metaInfoDir);
		logger.info("Found MetaInfo classes: {}", infoByName.size());
	}
	
	/**
	 * Collects the meta-info (recursively) for the specified directory.
	 */
	private void collectMetaInfo(File metaInfoDir) {
		File[] metaInfoFiles = metaInfoDir.listFiles();
		if (metaInfoFiles != null) {
			for (File metaInfoFile : metaInfoFiles) {
				String fileName = metaInfoFile.getName();
				
				if (metaInfoFile.isDirectory() && ! DIRECTORY_EXCLUDES.contains(fileName)) {
					collectMetaInfo(metaInfoFile);
					
				} else if (metaInfoFile.isFile() && ! FILE_EXCLUDES.contains(fileName)) {
					if (fileName.endsWith("Meta.java")) {
						MetaInfo metaInfo = new MetaInfo(metaInfoFile);
						
						String metaInfoName = metaInfo.getPackageID()+ "." + metaInfo.getInfoClass();
						if (infoByName.putIfAbsent(metaInfoName, metaInfo) != null) {
							throw new RuntimeException("Found duplicate info: {}" + metaInfoName);
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
	
	public Map<String, MetaInfo> getInfoByName() {
		return Collections.unmodifiableMap(infoByName);
	}
	
	public static void main(String[] args) {
		new MetaInfoDirectory(new File("/Users/leonix/Desktop/model"));
	}
}
