package be.leonix.tools.refactor;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.leonix.tools.refactor.model.repo.SourceAuthor;
import be.leonix.tools.refactor.model.repo.SourceFile;
import be.leonix.tools.refactor.model.repo.SourceRepo;
import be.leonix.tools.refactor.model.repo.SourceTree;
import be.leonix.tools.refactor.operation.MetaInfoRefactor;

/**
 * The application (tool) for refactoring Java source files.
 *
 * @author Ken Audenaert
 */
public final class RefactorTool {

	private static final Logger logger = LoggerFactory.getLogger(RefactorTool.class);
	
	/**
	 * The application for executing code-refactors.
	 */
	public static void main(String[] args) {
		try {
			FileRefactor fileRefactor = new MetaInfoRefactor(
					"/Users/audenaer/Genohm/slims-repo/platform-api-model/gen-src/com/genohm/slims/common/model");

			Set<String> repoPaths = Set.of(
					//	"/Users/leonix/github/leonix-maventools",
					//	"/Users/leonix/github/leonix-framework",
					//	"/Users/leonix/github/leonix-deploytools",
					//	"/Users/leonix/github/leonix-sandbox",
					"/Users/audenaer/Genohm/slims-repo"
			);
			
			RefactorContext context = new RefactorContext(RefactorMode.UPDATE_FILE);
			logger.info("Starting refactor.");
			
			for (String repoPath : repoPaths) {
				File repoDir = new File(repoPath);
				if (repoDir.isDirectory()) {
					SourceRepo sourceRepo = new SourceRepo(repoDir);
					
					logger.info("Repository: {}", sourceRepo.getRepoDir());
					for (SourceTree sourceTree : sourceRepo.getSourceTrees()) {
						if (sourceTree.getRootDir().getPath().contains("buildSrc") ||
							sourceTree.getRootDir().getPath().contains("customization") ||
							sourceTree.getRootDir().getPath().contains("plugin")) {
							continue;
						}

						List<SourceFile> javaFiles = sourceTree.getSourceFiles();
						logger.info("Sources: {} (count={})", sourceTree.getRootDir(), javaFiles.size());
						for (SourceFile javaFile : javaFiles) {
							fileRefactor.refactorFile(javaFile, context);
						}
					}
					if (context.getMode() == RefactorMode.COMMIT_REPO) {
						SourceAuthor autor = new SourceAuthor("Ken Audenaert", (repoDir.getName().contains("slims") ? 
								"ken.audenaert@agilent.com" : "ken.audenaert@telenet.be"));
						sourceRepo.commitChanges(autor, fileRefactor.getDescription());
					}
				}
			}
			logger.info("Finished refactor.");
			context.logInfo();
			
		} catch (RuntimeException | Error ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
}
