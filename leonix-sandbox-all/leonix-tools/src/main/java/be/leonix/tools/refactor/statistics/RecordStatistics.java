package be.leonix.tools.refactor.statistics;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;

import be.leonix.tools.refactor.FileRefactor;
import be.leonix.tools.refactor.RefactorContext;
import be.leonix.tools.refactor.model.SourceFile;

public class RecordStatistics implements FileRefactor {

	private static final Logger logger = LoggerFactory.getLogger(RecordStatistics.class);
	
	private long matchedFileCount   = 0;
	private long recordUseFileCount = 0;
	private long asLongCountFiles   = 0;
	private long asLongCountCalls   = 0;
	private long asStringCountFiles = 0;
	private long asStringCountCalls = 0;
	
	@Override
	public String getDescription() {
		return getClass().getSimpleName();
	}
	
	@Override
	public void refactorStarted() {
		matchedFileCount   = 0;
		recordUseFileCount = 0;
		asLongCountFiles   = 0;
		asLongCountCalls   = 0;
		asStringCountFiles = 0;
		asStringCountCalls = 0;
	}
	
	@Override
	public void refactorStopped() {
		logger.info("Statistic: matched-files : {}", matchedFileCount);
		logger.info("Statistic: record-use-files : {}", recordUseFileCount);
		logger.info("Statistic: as-long-files : {}", asLongCountFiles);
		logger.info("Statistic: as-long-calls : {}", asLongCountCalls);
		logger.info("Statistic: as-string-files : {}", asStringCountFiles);
		logger.info("Statistic: as-string-calls : {}", asStringCountCalls);
	}
	
	@Override
	public void refactorFile(SourceFile sourceFile, RefactorContext context) {
		matchedFileCount++;
		
		int asLongCount   = 0;
		int asStringCount = 0;
		try {
			CompilationUnit compilationUnit = StaticJavaParser.parse(sourceFile.getSourceFile());
			for (MethodCallExpr methodCall : compilationUnit.findAll(MethodCallExpr.class)) {
				if (methodCall.getNameAsString().contains("getAsString")) {
					asStringCount++;
				} else if (methodCall.getNameAsString().contains("getAsLong")) {
					asLongCount++;
				}
			}
			if (asLongCount > 0) {
				asLongCountFiles++;
				asLongCountCalls += asLongCount;
			}
			if (asStringCount > 0) {
				asStringCountFiles++;
				asStringCountCalls += asStringCount;
			}
			boolean useRecord = false;
			for (VariableDeclarator var : compilationUnit.findAll(VariableDeclarator.class)) {
				if (var.getTypeAsString().contains("Map<String, Object>")) {
					useRecord = true;
				}
			}
			if (useRecord) {
				recordUseFileCount++;
			}
		} catch (IOException | RuntimeException ex) {
			throw new RuntimeException("Could not parse source-file: " + sourceFile, ex);
		}
	}
}
