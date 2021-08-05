package be.leonix.tools.refactor.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

/**
 * This class encapsulates a source-repo.
 * 
 * @author Ken Audenaert
 */
public final class SourceRepo {
	
	private static final IOFileFilter gitFilter = FileFilterUtils.notFileFilter(
			FileFilterUtils.and(FileFilterUtils.directoryFileFilter(), FileFilterUtils.nameFileFilter(".git")));
	
	private static final Set<String> sourceFolders = Set.of("src", "source");
	
	private final File repoDir;
	private final List<SourceTree> sourceTrees;
	
	public SourceRepo(File repoDir) {
		this.repoDir = Objects.requireNonNull(repoDir);
		if (! repoDir.isDirectory()) {
			throw new IllegalArgumentException("Invalid repo-dir: " + repoDir);
		}
		this.sourceTrees = findSourceDirectories(repoDir).stream()
				.map(SourceTree::new).collect(Collectors.toList());
	}
	
	/**
	 * Returns the repository directory for this source-repository.
	 */
	public File getRepoDir() {
		return repoDir;
	}
	
	/**
	 * Returns all the source-trees in this source-repository.
	 */
	public List<SourceTree> getSourceTrees() {
		return Collections.unmodifiableList(sourceTrees);
	}
	
	/**
	 * Finds the source-directories in the specified directory.
	 */
	private static List<File> findSourceDirectories(File directory) {
		IOFileFilter fileFilter = FileFilterUtils.falseFileFilter();
		IOFileFilter dirFilter  = gitFilter;
		
		List<File> sourceFiles = new ArrayList<>();
		for (File file : FileUtils.listFilesAndDirs(directory, fileFilter, dirFilter)) {
			if (sourceFolders.contains(file.getName())) {
				sourceFiles.add(file);
			}
		}
		return Collections.unmodifiableList(sourceFiles);
	}
}
