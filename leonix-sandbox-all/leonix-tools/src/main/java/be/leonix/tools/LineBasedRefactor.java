package be.leonix.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
			
			// Must retain the original line-ending!
			String eol = getLineEnding(sourceFile);
			
			try (FileReader fileReader = new FileReader(sourceFile)) {
				try (BufferedReader reader = new BufferedReader(fileReader)) {
					
					try (FileWriter fileWriter = new FileWriter(refactorFile)) {
						try (BufferedWriter writer = new BufferedWriter(fileWriter)) {
							
							int lineNumber = 0;
							String oldLine = reader.readLine();
							while (oldLine != null) {
								lineNumber++;
								String lineInfo = sourceFile.getName() + " @ line " + lineNumber;
								String newLine = refactorFileLine(lineInfo, oldLine, mode);
								
								// Check for last file-line!
								oldLine = reader.readLine();
								if (oldLine != null) {
									writer.write(newLine);
									writer.write(eol);
								} else {
									writer.write(newLine);
								}
							}
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
	
	private String getLineEnding(File sourceFile) {
		try (FileReader fileReader = new FileReader(sourceFile)) {
			try (BufferedReader reader = new BufferedReader(fileReader)) {
				int i = -1;
				while ((i = reader.read()) != -1) {
					if (i == '\r') {
						return "\r\n";
					} else if (i == '\n') {
						return "\n";
					}
				}
				return "\n";
			}
		} catch (RuntimeException | IOException ex) {
			throw new RuntimeException("Could not read source-file: " + sourceFile, ex);
		}
	}
}