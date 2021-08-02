package be.leonix.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.leonix.tools.model.SourceFile;
import be.leonix.tools.model.SourceLine;

/**
 * A {@link FileRefactor} using a set of {@link LineRefactor} implementations.
 * 
 * @author Ken Audenaert
 */
public class LineBasedRefactor implements FileRefactor {
	
	private static final Logger logger = LoggerFactory.getLogger(LineBasedRefactor.class);
	
	private final List<LineRefactor> lineRefactors = new ArrayList<>();
	
	public LineBasedRefactor(LineRefactor... lineRefactors) {
		this.lineRefactors.addAll(Arrays.asList(lineRefactors));
	}
	
	@Override
	public void refactorFile(File sourceFile, RefactorMode mode) {
		File refactorFile = new File(FileUtils.getTempDirectory(), sourceFile.getName());
		try {
			FileUtils.deleteQuietly(refactorFile);
			
			SourceFile source = new SourceFile(sourceFile);
			
			try (FileWriter fileWriter = new FileWriter(refactorFile)) {
				try (BufferedWriter writer = new BufferedWriter(fileWriter)) {
					
					for (SourceLine sourceLine : source.getSourceLines()) {
						String oldLine = sourceLine.getLineContent();
						
						String lineInfo = sourceFile.getName() + " @ line " + sourceLine.getLineNumber();
						String newLine = refactorFileLine(lineInfo, oldLine, mode);
						
						writer.write(newLine);
						if (sourceLine.getLineEnding() != null) {
							writer.write(sourceLine.getLineEnding());
						}
					}
				}
			}
			
			FileUtils.deleteQuietly(sourceFile);
			FileUtils.moveFile(refactorFile, sourceFile);
			
		} catch (RuntimeException | IOException ex) {
			throw new RuntimeException("Could not refactor source-file: " + sourceFile, ex);
		} finally {
			FileUtils.deleteQuietly(refactorFile);
		}
	}
	
	private String refactorFileLine(String sourceLineInfo, String sourceLine, RefactorMode mode) {
		String resultLine = sourceLine;
		for (LineRefactor lineRefactor : lineRefactors) {
			resultLine = lineRefactor.refactorLine(resultLine);
		}
		if (! StringUtils.equals(resultLine, sourceLine)) {
			logger.info(">> {}", sourceLineInfo);
			if (mode == RefactorMode.UPDATE_FILE) {
				return resultLine;
				
			} else if (mode == RefactorMode.ADD_COMMENT) {
				return resultLine + "// REFACTOR";
				
			} else if (mode == RefactorMode.LOG_CHANGE) {
				logger.info(">> --- {}", sourceLine.trim());
				logger.info(">> +++ {}", resultLine.trim());
				return sourceLine;
			}
		}
		return sourceLine;
	}
}
