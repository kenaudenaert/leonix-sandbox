package be.leonix.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Use xxxMeta-constants in all code. 
// deprecated: Lists.newArrayList()
// deprecated: Maps.newHashMap()
// deprecated: Sets.newHashSet()
// Use diamond syntax (list/map/set).

public final class Refactor {

	private static final Logger logger = LoggerFactory.getLogger(Refactor.class);

	private static final IOFileFilter gitFilter = FileFilterUtils.notFileFilter(
			FileFilterUtils.and(FileFilterUtils.directoryFileFilter(), FileFilterUtils.nameFileFilter(".git")));

	private static final Set<String> SOURCE_FOLDERS = Set.of("src", "generated-src");
	
	public static List<File> findSrcFolders(File projectDir) {
		Objects.requireNonNull(projectDir);
		if (!projectDir.isDirectory()) {
			throw new RuntimeException("Invalid project-dir: " + projectDir);
		}
		
		IOFileFilter fileFilter = FileFilterUtils.falseFileFilter();
		
		List<File> sourceFiles = new ArrayList<File>();
		for (File file : FileUtils.listFilesAndDirs(projectDir, fileFilter, gitFilter)) {
			if (SOURCE_FOLDERS.contains(file.getName())) {
				sourceFiles.add(file);
			}
		}
		return Collections.unmodifiableList(sourceFiles);
	}
	
	public static List<File> findJavaSources(File srcDir) {
		Objects.requireNonNull(srcDir);
		if (!srcDir.isDirectory()) {
			throw new RuntimeException("Invalid sources-dir: " + srcDir);
		}
		
		IOFileFilter fileFilter = FileFilterUtils.suffixFileFilter(".java");
		
		List<File> sourceFiles = new ArrayList<File>();
		for (File file : FileUtils.listFiles(srcDir, fileFilter, gitFilter)) {
			sourceFiles.add(file);
		}
		return Collections.unmodifiableList(sourceFiles);
	}
	
	/**
	 * The application for executing some code-refactors.
	 */
	public static void main(String[] args) {
		try {
			File projectDir = new File("/Users/leonix/github/leonix-framework");
			
			logger.info("Starting refactor.");
			for (File srcDir : findSrcFolders(projectDir)) {
				List<File> javaFiles = findJavaSources(srcDir);
				logger.info("Found src-directory: {} (count={})", srcDir, javaFiles.size());
			}
			logger.info("Finished refactor.");
			
		} catch (RuntimeException | Error ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
}
