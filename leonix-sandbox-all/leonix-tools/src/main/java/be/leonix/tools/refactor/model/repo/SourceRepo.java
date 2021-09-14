package be.leonix.tools.refactor.model.repo;

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
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * This class encapsulates a source-repo containing {@link SourceTree}s.
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
		
		// Currently only a GIT repository is supported.
		File gitRepo = new File(repoDir, ".git");
		if (! gitRepo.isDirectory()) {
			throw new IllegalArgumentException("Invalid git-repo: " + repoDir);
		}
		try {
			git = Git.open(repoDir);
		} catch (IOException | RuntimeException ex) {
			throw new RuntimeException("Could not open git-repo.", ex);
		}
		
		// Perform a recursive seach for (non-empty) source-trees.
		this.sourceTrees = listSourceDirectories(repoDir).stream()
				.map(SourceTree::new)
				.filter(v -> !v.getSourceFiles().isEmpty())
				.collect(Collectors.toList());
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
			AddCommand addCommand = git.add();
			addCommand.addFilepattern(".");
			addCommand.setUpdate(true); // do not stage new files.
			addCommand.call();
			
			CommitCommand commit = git.commit();
			commit.setAuthor(author.getName(), author.getEmail());
			commit.setMessage(message);
			commit.call();
			
		} catch (GitAPIException | RuntimeException ex) {
			throw new RuntimeException("Could not commit changes.", ex);
		}
	}
	
	/**
	 * Lists the source-directories in the specified directory.
	 */
	private static List<File> listSourceDirectories(File directory) {
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
