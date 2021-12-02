package be.leonix.tools.refactor.statistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.leonix.tools.refactor.FileRefactor;
import be.leonix.tools.refactor.RefactorContext;
import be.leonix.tools.refactor.model.SourceFile;

public class OptionalStatistics implements FileRefactor {

	private static final Logger logger = LoggerFactory.getLogger(OptionalStatistics.class);

	private long importNoOptional    = 0;
	private long importGuavaOptional = 0;
	private long importJavaOptional  = 0;

	@Override
	public String getDescription() {
		return getClass().getSimpleName();
	}

	@Override
	public void refactorStarted() {
		importNoOptional = 0;
		importGuavaOptional = 0;
		importJavaOptional = 0;
	}

	@Override
	public void refactorStopped() {
		long importAll = importNoOptional + importGuavaOptional + importJavaOptional;
		double onePercent = (importAll / 100d);
		logger.info("Statistic: no-optional-import    : {} {}", importNoOptional, importNoOptional/ onePercent);
		logger.info("Statistic: guava-optional-import : {} {}", importGuavaOptional, importGuavaOptional/ onePercent);
		logger.info("Statistic: java-optional-import  : {} {}", importJavaOptional, importJavaOptional/ onePercent);
	}

	@Override
	public void refactorFile(SourceFile sourceFile, RefactorContext context) {
		if (sourceFile.getImportLine("com.google.common.base.Optional") != null) {
			importGuavaOptional++;
		} else if (sourceFile.getImportLine("java.util.Optional") != null) {
			importJavaOptional++;
		} else {
			importNoOptional++;
		}
	}
}
