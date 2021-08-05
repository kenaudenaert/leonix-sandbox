package be.leonix.tools;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.leonix.tools.model.SourceFile;
import be.leonix.tools.model.SourceRepo;
import be.leonix.tools.model.SourceTree;
import be.leonix.tools.operation.DiamondRefactor;

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
			FileRefactor fileRefactor = new LineBasedRefactor(new DiamondRefactor());
			
			Set<String> repoPaths = Set.of(
			//	"/Users/leonix/github/leonix-maventools",
			//	"/Users/leonix/github/leonix-framework",
			//	"/Users/leonix/github/leonix-deploytools",
				"/Users/leonix/github/leonix-sandbox",
				"/Users/audenaer/Genohm/slims-repo"
			);
			
			RefactorContext context = new RefactorContext(RefactorMode.UPDATE_FILE);
			logger.info("Starting refactor.");
			
			for (String repoPath : repoPaths) {
				File repoDir = new File(repoPath);
				if (repoDir.isDirectory()) {
					
					SourceRepo sourceRepo = new SourceRepo(repoDir);
					for (SourceTree sourceTree : sourceRepo.getSourceTrees()) {
						List<SourceFile> javaFiles = sourceTree.getSourceFiles();
						logger.info("Sources: {} (count={})", sourceTree.getRootDir(), javaFiles.size());
						for (SourceFile javaFile : javaFiles) {
							fileRefactor.refactorFile(javaFile.getSourceFile(), context);
						}
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
