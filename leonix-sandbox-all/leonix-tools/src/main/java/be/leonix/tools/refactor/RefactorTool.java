package be.leonix.tools.refactor;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.leonix.tools.refactor.model.SourceAuthor;
import be.leonix.tools.refactor.model.SourceFile;
import be.leonix.tools.refactor.model.SourceRepo;
import be.leonix.tools.refactor.model.SourceTree;
import be.leonix.tools.refactor.operation.DiamondRefactor;
import be.leonix.tools.refactor.operation.MetaTypeDirectory;
import be.leonix.tools.refactor.operation.MetaTypeRefactor;
import be.leonix.tools.refactor.operation.MetaTypeReplacer;
import be.leonix.tools.refactor.operation.MetaTypeResolver;
import be.leonix.tools.refactor.statistics.OptionalStatistics;
import be.leonix.tools.refactor.statistics.RecordStatistics;

/**
 * The application (tool) for refactoring Java source files.
 *
 * @author Ken Audenaert
 */
public final class RefactorTool {

	private static final Logger logger = LoggerFactory.getLogger(RefactorTool.class);
	
	private static final String GIT_AUTHOR_NAME = "Ken Audenaert";
	
	private static final String LEONIX_REPO_ROOT = "/Users/leonix/github";
	private static final String LEONIX_GIT_EMAIL = "ken.audenaert@telenet.be";
	
	private static final String SLIMS_REPO_PATH = "/Users/audenaer/Genohm/slims-repo";
	private static final String SLIMS_META_PATH = "platform-api-model/gen-src/com/genohm/slims/common/model";
	
	private static final String SLIMS_USER_NAME = "audenaer";
	private static final String SLIMS_GIT_EMAIL = "ken.audenaert@agilent.com";
	
	private static final Set<String> SLIMS_INCLUDES = Set.of(
			"platform", "slimsdao", "slimsservice", "slimsserver", "slimsclient", "slimsgate", "upgrade");
	private static final Set<String> SLIMS_EXCLUDES = Set.of(
			"buildSrc", "build", "gwt-binaries", "customization", "plugin");
	
	private final FileRefactor fileRefactor;
	private final RefactorMode refactorMode;
	
	private final String userHome;
	private final String userName;
	
	/**
	 * Creates a refactor-tool with the specified refactoring options.
	 */
	public RefactorTool(FileRefactor fileRefactor, RefactorMode refactorMode) {
		this.fileRefactor = Objects.requireNonNull(fileRefactor);
		this.refactorMode = Objects.requireNonNull(refactorMode);
		
		this.userHome = Objects.requireNonNull(System.getProperty("user.dir"));
		this.userName = Objects.requireNonNull(System.getProperty("user.name"));
	}
	
	/**
	 * Executes the refactor operation on the specified repository.
	 */
	public void refactorRepo(SourceRepo sourceRepo, Set<String> includes, Set<String> excludes) {
		logger.info("Refactor user: {}", userName);
		logger.info("Refactor home: {}", userHome);
		logger.info("Refactor type: {}", fileRefactor.getDescription());
		logger.info("Refactor mode: {}", refactorMode.name());
		logger.info("Refactor repo: {}", sourceRepo.getRepoDir());

		RefactorContext context = new RefactorContext(refactorMode);
		try {
			logger.info("Starting refactor.");
			fileRefactor.refactorStarted();
			
			for (SourceTree sourceTree : sourceRepo.getSourceTrees()) {
				String sourceTreePath = sourceTree.getRootDir().getPath();
				
				// Check whether tree-path is included.
				if (! includes.isEmpty()) {
					boolean included = false;
					for (String include : includes) {
						if (sourceTreePath.contains(include)) {
							included = true;
							break;
						}
					}
					if (! included) {
						logger.debug("Skipped (includes-filter): {}", sourceTreePath);
						continue;
					}
				}
				
				// Check whether tree-path is excluded.
				if (! excludes.isEmpty()) {
					boolean excluded = false;
					for (String exclude : excludes) {
						if (sourceTreePath.contains(exclude)) {
							excluded = true;
							break;
						}
					}
					if (excluded) {
						logger.debug("Skipped (excludes-filter): {}", sourceTreePath);
						continue;
					}
				}
				
				List<SourceFile> javaFiles = sourceTree.getSourceFiles();
				logger.info("Sources: {} (count={})", sourceTree.getRootDir(), javaFiles.size());
				for (SourceFile javaFile : javaFiles) {
					fileRefactor.refactorFile(javaFile, context);
				}
			}
		} finally {
			try {
				logger.info("Stopping refactor.");
				fileRefactor.refactorStopped();
				
			} finally {
				if (context.getMode() == RefactorMode.COMMIT_REPO) {
					SourceAuthor author;
					if (userName.equals(SLIMS_USER_NAME)) {
						author = new SourceAuthor(GIT_AUTHOR_NAME, SLIMS_GIT_EMAIL);
					} else {
						author = new SourceAuthor(GIT_AUTHOR_NAME, LEONIX_GIT_EMAIL);
					}
					sourceRepo.commitChanges(author, fileRefactor.getDescription());
				} else if (context.getMode() == RefactorMode.LOG_CHANGE) {
					context.logInfo();
				}
			}
		}
	}
	
	/**
	 * The code-refactor for {@link DiamondRefactor}.
	 */
	private static void refactorDiamond() {
		FileRefactor fileRefactor = new LineBasedRefactor(new DiamondRefactor());
		RefactorTool refactorTool = new RefactorTool(fileRefactor, RefactorMode.UPDATE_FILE);
		
		Set<String> repoPaths = Set.of(SLIMS_REPO_PATH,
				LEONIX_REPO_ROOT + "/leonix-maventools",
				LEONIX_REPO_ROOT + "/leonix-framework",
				LEONIX_REPO_ROOT + "/leonix-deploytools",
				LEONIX_REPO_ROOT + "/leonix-sandbox");
		
		for (String repoPath : repoPaths) {
			File repoDir = new File(repoPath);
			if (repoDir.isDirectory()) {
				SourceRepo sourceRepo = new SourceRepo(repoDir);
				
				if (! repoPath.equals(SLIMS_REPO_PATH)) {
					refactorTool.refactorRepo(sourceRepo, Collections.emptySet(), Collections.emptySet());
				} else {
					refactorTool.refactorRepo(sourceRepo, SLIMS_INCLUDES, SLIMS_EXCLUDES);
				}
			}
		}
	}
	
	/**
	 * The code-refactor for {@link MetaTypeRefactor}.
	 */
	private static void refactorMetaType() {
		String metaTypePath = SLIMS_REPO_PATH + '/' + SLIMS_META_PATH;
		MetaTypeDirectory metaTypeDir = new MetaTypeDirectory(new File(metaTypePath));
		
		FileRefactor fileRefactor = new MetaTypeRefactor(metaTypeDir);
		RefactorTool refactorTool = new RefactorTool(fileRefactor, RefactorMode.UPDATE_FILE);
		
		SourceRepo sourceRepo = new SourceRepo(new File(SLIMS_REPO_PATH));
		refactorTool.refactorRepo(sourceRepo, SLIMS_INCLUDES, SLIMS_EXCLUDES);
	}
	
	/**
	 * The code-refactor for {@link MetaTypeReplacer}.
	 */
	private static void replaceMetaType() {
		String metaTypePath = SLIMS_REPO_PATH + '/' + SLIMS_META_PATH;
		MetaTypeDirectory metaTypeDir = new MetaTypeDirectory(new File(metaTypePath));
		
		FileRefactor fileRefactor = new MetaTypeReplacer(metaTypeDir);
		RefactorTool refactorTool = new RefactorTool(fileRefactor, RefactorMode.UPDATE_FILE);
		
		SourceRepo sourceRepo = new SourceRepo(new File(SLIMS_REPO_PATH));
		refactorTool.refactorRepo(sourceRepo, SLIMS_INCLUDES, SLIMS_EXCLUDES);
	}
	
	/**
	 * The code-refactor for {@link MetaTypeResolver}.
	 */
	private static void resolveMetaType() {
		String metaTypePath = SLIMS_REPO_PATH + '/' + SLIMS_META_PATH;
		MetaTypeDirectory metaTypeDir = new MetaTypeDirectory(new File(metaTypePath));
		
		SourceRepo sourceRepo = new SourceRepo(new File(SLIMS_REPO_PATH));
		Set<String> prefixes = metaTypeDir.getInfoPrefixes();
		for (String prefix : prefixes) {
			MetaTypeResolver refactor = new MetaTypeResolver(metaTypeDir);
			refactor.getPrefixFilter().clear();
			refactor.getPrefixFilter().add(prefix);
			
			RefactorTool refactorTool = new RefactorTool(refactor, RefactorMode.LOG_CHANGE);
			refactorTool.refactorRepo(sourceRepo, SLIMS_INCLUDES, SLIMS_EXCLUDES);
		}
	}

	/**
	 * The code-refactor for {@link OptionalStatistics}.
	 */
	private static void printOptionalStats() {
		FileRefactor fileRefactor = new OptionalStatistics();
		RefactorTool refactorTool = new RefactorTool(fileRefactor, RefactorMode.LOG_CHANGE);

		SourceRepo sourceRepo = new SourceRepo(new File(SLIMS_REPO_PATH));
		refactorTool.refactorRepo(sourceRepo, SLIMS_INCLUDES, SLIMS_EXCLUDES);
	}

	/**
	 * The code-refactor for {@link RecordStatistics}.
	 */
	private static void printRecordStats() {
		FileRefactor fileRefactor = new RecordStatistics();
		RefactorTool refactorTool = new RefactorTool(fileRefactor, RefactorMode.LOG_CHANGE);

		SourceRepo sourceRepo = new SourceRepo(new File(SLIMS_REPO_PATH));
		refactorTool.refactorRepo(sourceRepo, SLIMS_INCLUDES, SLIMS_EXCLUDES);
	}

	/**
	 * The application for executing code-refactors.
	 */
	public static void main(String[] args) {
		try {
			// Get the operations (sequence) from arguments.
			Set<String> operations = new LinkedHashSet<>();
			for (String arg : args) {
				operations.addAll(Stream.of(arg.split(","))
						.filter(v -> !v.isBlank())
						.collect(Collectors.toList()));
			}
			
			// Choose a default operation when none given.
			if (operations.isEmpty()) {
				logger.info("Using default refactor");
				operations.add("records");
			}
			
			// Perform the operations in the given order.
			logger.info("Executing refactors: {}", operations.toArray());
			for (String operation : operations) {
				switch (operation) {
				case "diamond":
					refactorDiamond();
					break;
				case "meta-type":
					refactorMetaType();
					break;
				case "replacer":
					replaceMetaType();
					break;
				case "resolver":
					resolveMetaType();
					break;
				case "optionals":
					printOptionalStats();
					break;
				case "records":
					printRecordStats();
					break;
				default:
					logger.error("No such refactor: {}", operation);
					break;
				}
			}
		} catch (RuntimeException | Error ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
}
