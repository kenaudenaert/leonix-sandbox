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

import be.leonix.tools.operation.DiamondRefactor;

/**
 * The application (tool) for refactoring Java source files.
 * 
 * @author Ken Audenaert
 */
public final class RefactorTool {
	
	private static final Logger logger = LoggerFactory.getLogger(RefactorTool.class);
	
	private static final IOFileFilter gitFilter = FileFilterUtils.notFileFilter(
			FileFilterUtils.and(FileFilterUtils.directoryFileFilter(), FileFilterUtils.nameFileFilter(".git")));
	
	private static final Set<String> sourceFolders = Set.of("src", "source");
	
	/**
	 * Finds the source-folders in the specified project-directory.
	 */
	private static List<File> findSourceFolders(File projectDir) {
		Objects.requireNonNull(projectDir);
		if (! projectDir.isDirectory()) {
			throw new IllegalArgumentException("Invalid project-dir: " + projectDir);
		}
		
		IOFileFilter fileFilter = FileFilterUtils.falseFileFilter();
		
		List<File> sourceFiles = new ArrayList<>();
		for (File file : FileUtils.listFilesAndDirs(projectDir, fileFilter, gitFilter)) {
			if (sourceFolders.contains(file.getName())) {
				sourceFiles.add(file);
			}
		}
		return Collections.unmodifiableList(sourceFiles);
	}
	
	/**
	 * Finds the Java sources in the specified source-directory.
	 */
	private static List<File> findJavaSources(File sourceDir) {
		Objects.requireNonNull(sourceDir);
		if (! sourceDir.isDirectory()) {
			throw new IllegalArgumentException("Invalid source-dir: " + sourceDir);
		}
		
		IOFileFilter fileFilter = FileFilterUtils.suffixFileFilter(".java");
		
		List<File> sourceFiles = new ArrayList<>();
		for (File file : FileUtils.listFiles(sourceDir, fileFilter, gitFilter)) {
			sourceFiles.add(file);
		}
		return Collections.unmodifiableList(sourceFiles);
	}
	
	/**
	 * The application for executing code-refactors.
	 */
	public static void main(String[] args) {
		try {
			Set<String> projectDirs = Set.of(
			//	"/Users/leonix/github/leonix-maventools",
			//	"/Users/leonix/github/leonix-framework",
			//	"/Users/leonix/github/leonix-deploytools",
				"/Users/leonix/github/leonix-sandbox",
				"/Users/audenaer/Genohm/slims-repo"
			);
			
			FileRefactor fileRefactor = new LineBasedRefactor();
			
			logger.info("Starting refactor.");
			for (String projectDir : projectDirs) {
				File directory = new File(projectDir);
				if (directory.isDirectory()) {
					for (File srcDir : findSourceFolders(directory)) {
						if (! srcDir.getAbsolutePath().contains("platform-api")) {
							continue;
						}
						List<File> javaFiles = findJavaSources(srcDir);
						logger.info("Refactor src-directory: {} (count={})", srcDir, javaFiles.size());
						for (File javaFile : javaFiles) {
							fileRefactor.refactorFile(javaFile, RefactorMode.UPDATE_FILE);
						}
					}
				}
			}
			logger.info("Finished refactor.");
			
		} catch (RuntimeException | Error ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
}
