package be.leonix.tools.refactor.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

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
	private final Git git;
	
	public SourceRepo(File repoDir) {
		this.repoDir = Objects.requireNonNull(repoDir);
		if (! repoDir.isDirectory()) {
			throw new IllegalArgumentException("Invalid repo-dir: " + repoDir);
		}
		File gitRepo = new File(repoDir, ".git");
		if (! gitRepo.isDirectory()) {
			throw new IllegalArgumentException("Invalid git-repo: " + repoDir);
		}
		try {
			git = Git.open(repoDir);
		} catch (IOException | RuntimeException ex) {
			throw new RuntimeException("Could not open git-repo.", ex);
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
	 * Commits all the changes in this source-repository.
	 */
	public void commitChanges(SourceAuthor author, String message) {
		Objects.requireNonNull(author);
		Objects.requireNonNull(message);
		try {
			git.commit().setAuthor(author.getName(), author.getEmail()).setMessage(message).call();
		} catch (GitAPIException ex) {
			throw new RuntimeException("Could not commit changes.", ex);
		}
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
