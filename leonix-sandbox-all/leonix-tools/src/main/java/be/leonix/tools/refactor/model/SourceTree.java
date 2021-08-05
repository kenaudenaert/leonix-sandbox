package be.leonix.tools.refactor.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

/**
 * This class encapsulates a source-tree.
 * 
 * @author Ken Audenaert
 */
public final class SourceTree {
	
	private final File rootDir;
	private final List<SourceFile> sourceFiles;
	
	public SourceTree(File rootDir) {
		this.rootDir = Objects.requireNonNull(rootDir);
		if (! rootDir.isDirectory()) {
			throw new IllegalArgumentException("Invalid root-dir: " + rootDir);
		}
		this.sourceFiles = findJavaSourceFiles(rootDir).stream()
				.map(SourceFile::new).collect(Collectors.toList());
	}
	
	/**
	 * Returns the tree-root directory of this source-tree.
	 */
	public File getRootDir() {
		return rootDir;
	}
	
	/**
	 * Returns all the source-files in this source-tree.
	 */
	public List<SourceFile> getSourceFiles() {
		return Collections.unmodifiableList(sourceFiles);
	}
	
	/**
	 * Finds the Java source-files in the specified directory.
	 */
	private static List<File> findJavaSourceFiles(File directory) {
		IOFileFilter fileFilter = FileFilterUtils.suffixFileFilter(".java");
		IOFileFilter dirFilter  = FileFilterUtils.trueFileFilter();
		
		List<File> sourceFiles = new ArrayList<>();
		for (File file : FileUtils.listFiles(directory, fileFilter, dirFilter)) {
			sourceFiles.add(file);
		}
		return Collections.unmodifiableList(sourceFiles);
	}
}
