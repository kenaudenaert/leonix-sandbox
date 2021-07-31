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
			
			try (FileReader fileReader = new FileReader(sourceFile)) {
				try (BufferedReader reader = new BufferedReader(fileReader)) {
					
					try (FileWriter fileWriter = new FileWriter(refactorFile)) {
						try (BufferedWriter writer = new BufferedWriter(fileWriter)) {
							
							int lineNumber = 0;
							String oldLine = reader.readLine();
							while (oldLine != null) {
								lineNumber++;
								String newLine = oldLine;
								for (LineRefactor lineRefactor : lineRefactors) {
									newLine = lineRefactor.refactorLine(newLine);
								}
								if (! StringUtils.equals(oldLine, newLine)) {
									logger.info(">> {} @ line {}", sourceFile, lineNumber);
									
									if (mode == RefactorMode.UPDATE_FILE) {
										writer.write(newLine);
										writer.write('\n');
										
									} else if (mode == RefactorMode.ADD_COMMENT) {
										writer.write(oldLine);
										writer.write(" // REFACTOR");
										writer.write('\n');
										
									} else if (mode == RefactorMode.LOG_CHANGE) {
										logger.info(">> --- {}", oldLine.trim());
										logger.info(">> +++ {}", newLine.trim());
										
										writer.write(oldLine);
										writer.write('\n');
									}
								} else {
									writer.write(oldLine);
									writer.write('\n');
								}
								oldLine = reader.readLine();
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
}
