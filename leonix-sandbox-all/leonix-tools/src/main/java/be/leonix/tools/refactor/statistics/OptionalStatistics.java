package be.leonix.tools.refactor.statistics;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;

import be.leonix.tools.refactor.FileRefactor;
import be.leonix.tools.refactor.RefactorContext;
import be.leonix.tools.refactor.model.SourceFile;

public class OptionalStatistics implements FileRefactor {

	private static final Logger logger = LoggerFactory.getLogger(OptionalStatistics.class);

	private long importNoOptional    = 0;
	private long importGuavaOptional = 0;
	private long importJavaOptional  = 0;
	private long memberOptionalFiles = 0;
	private long memberOptionalCount = 0;
	
	@Override
	public String getDescription() {
		return getClass().getSimpleName();
	}

	@Override
	public void refactorStarted() {
		importNoOptional    = 0;
		importGuavaOptional = 0;
		importJavaOptional  = 0;
		memberOptionalFiles = 0;
		memberOptionalCount = 0;
	}
	
	@Override
	public void refactorStopped() {
		long importAll = importNoOptional + importGuavaOptional + importJavaOptional;
		double onePercent = (importAll / 100d);
		logger.info("Statistic: no-optional-import    : {} {}", importNoOptional, importNoOptional/ onePercent);
		logger.info("Statistic: guava-optional-import : {} {}", importGuavaOptional, importGuavaOptional/ onePercent);
		logger.info("Statistic: java-optional-import  : {} {}", importJavaOptional, importJavaOptional/ onePercent);
		logger.info("Statistic: optional-member-files : {}", memberOptionalFiles);
		logger.info("Statistic: optional-member-count : {}", memberOptionalCount);
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
		
		int optionalCount = 0;
		try {
			CompilationUnit compilationUnit = StaticJavaParser.parse(sourceFile.getSourceFile());
			for (FieldDeclaration field : compilationUnit.findAll(FieldDeclaration.class)) {
				for (VariableDeclarator variable : field.getVariables()) {
					if (variable.getTypeAsString().contains("Optional")) {
						optionalCount++;
					}
				}
			}
			if (optionalCount > 0) {
				memberOptionalFiles++;
				memberOptionalCount += optionalCount;
			}
		} catch (IOException | RuntimeException ex) {
			throw new RuntimeException("Could not parse source-file: " + sourceFile, ex);
		}
	}
}
