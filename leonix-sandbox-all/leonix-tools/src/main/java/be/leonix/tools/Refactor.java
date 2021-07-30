package be.leonix.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.StringUtils;
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

	private static final Set<String> sourceFolders = Set.of("src", "gen-src", "generated-src");
	
	private static final Pattern GENERICS = Pattern.compile("(= new ArrayList<)\\w+(>\\(\\);)");
	
	/**
	 * Finds the source-folders in the specified project-directory.
	 */
	protected static List<File> findSourceFolders(File projectDir) {
		Objects.requireNonNull(projectDir);
		if (! projectDir.isDirectory()) {
			throw new IllegalArgumentException("Invalid project-dir: " + projectDir);
		}
		
		IOFileFilter fileFilter = FileFilterUtils.falseFileFilter();
		
		List<File> sourceFiles = new ArrayList<File>();
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
	protected static List<File> findJavaSources(File sourceDir) {
		Objects.requireNonNull(sourceDir);
		if (! sourceDir.isDirectory()) {
			throw new IllegalArgumentException("Invalid source-dir: " + sourceDir);
		}
		
		IOFileFilter fileFilter = FileFilterUtils.suffixFileFilter(".java");
		
		List<File> sourceFiles = new ArrayList<File>();
		for (File file : FileUtils.listFiles(sourceDir, fileFilter, gitFilter)) {
			sourceFiles.add(file);
		}
		return Collections.unmodifiableList(sourceFiles);
	}
	
	/**
	 * Refactors the specified source-line.
	 */
	public static String refactorLine(String sourceLine) {
		StringBuilder builder = new StringBuilder();
		if (StringUtils.isNotEmpty(sourceLine)) {
			
			int offset = 0;
			Matcher matcher = GENERICS.matcher(sourceLine);
			while (matcher.find(offset)) {
				// Copy unmatched leading section.
				if (matcher.start() > offset) {
					builder.append(sourceLine.substring(offset, matcher.start()));
				}
				// Execute refactor for pattern.
				// String reference = matcher.group();
				for (int group = 1; group <= matcher.groupCount(); group++)
					builder.append(matcher.group(group));
				offset = matcher.end();
			}
			// Copy unmatched trailing section.
			if (offset < sourceLine.length()) {
				builder.append(sourceLine.substring(offset));
			}
		}
		return builder.toString();
	}
	
	public enum Action { ADD_TAG, LOG_CHANGE, UPDATE_FILE }
	
	/**
	 * Refactors the specified source-file.
	 */
	public static void refactorFile(File sourceFile, Action action) {
		File refactorFile = new File(FileUtils.getTempDirectory(), sourceFile.getName());
		try {
			FileUtils.deleteQuietly(refactorFile);
			
			try (FileReader fileReader = new FileReader(sourceFile)) {
				try (BufferedReader reader = new BufferedReader(fileReader)) {
					
					try (FileWriter fileWriter = new FileWriter(refactorFile)) {
						try (BufferedWriter writer = new BufferedWriter(fileWriter)) {
							
							int lineNumber = 0;
							String line = reader.readLine();
							while (line != null) {
								lineNumber++;
								String newLine = refactorLine(line);
								if (! StringUtils.equals(line, newLine)) {
									if (action == Action.UPDATE_FILE) {
										writer.write(newLine);
										writer.write('\n');
										
									} else if (action == Action.ADD_TAG) {
										writer.write(line);
										writer.write(" // REFACTOR");
										writer.write('\n');
										
									} else if (action == Action.LOG_CHANGE) {
										logger.info(">> {} @ line {}", sourceFile, lineNumber);
										logger.info(">> --- {}", line);
										logger.info(">> +++ {}", newLine);
										
										writer.write(line);
										writer.write('\n');
									}
								} else {
									writer.write(line);
									writer.write('\n');
								}
								line = reader.readLine();
							}
						}
					}
				}
			}
			FileUtils.deleteQuietly(sourceFile);
			FileUtils.moveFile(refactorFile, sourceFile);
			
		} catch (RuntimeException | IOException ex) {
			throw new RuntimeException("Could not refactor source-file: " + sourceFile, ex);
		} finally {
			FileUtils.deleteQuietly(refactorFile);
		}
	}
	
	/**
	 * The application for executing some code-refactors.
	 */
	public static void main(String[] args) {
		try {
			File projectDir = new File("/Users/leonix/github/leonix-framework");
			
			logger.info("Starting refactor.");
			for (File srcDir : findSourceFolders(projectDir)) {
				List<File> javaFiles = findJavaSources(srcDir);
				logger.info("Found src-directory: {} (count={})", srcDir, javaFiles.size());
				
				for (File javaFile : javaFiles) {
					refactorFile(javaFile, Action.LOG_CHANGE);
				}
			}
			logger.info("Finished refactor.");
			
		} catch (RuntimeException | Error ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
}
