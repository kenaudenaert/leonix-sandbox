package be.leonix.tools.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * This class encapsulates a source-file.
 * 
 * @author leonix
 */
public final class SourceFile {
	
	private static final Pattern EOL_PATTERN = Pattern.compile("\r?\n");
	
	private final File sourceFile;
	private final List<SourceLine> sourceLines;
	
	public SourceFile(File sourceFile) {
		this.sourceFile = Objects.requireNonNull(sourceFile);
		this.sourceLines = new ArrayList<>();
		loadContents();
	}
	
	public void loadContents() {
		sourceLines.clear();
		try {
			String content = FileUtils.readFileToString(sourceFile, StandardCharsets.UTF_8);
			if (StringUtils.isNotEmpty(content)) {
				int lineNumber = 0;
				int lineOffset = 0;
				
				Matcher matcher = EOL_PATTERN.matcher(content);
				while (matcher.find(lineOffset)) {
					String lineContent = "";
					if (matcher.start() > lineOffset) {
						lineContent = content.substring(lineOffset, matcher.start());
					}
					String lineEnding = matcher.group();
					sourceLines.add(new SourceLine(++lineNumber, lineContent, lineEnding));
					
					lineOffset = matcher.end();
				}
				
				// The last line may have no line-ending!
				if (lineOffset < content.length()) {
					String lineContent = content.substring(lineOffset);
					sourceLines.add(new SourceLine(++lineNumber, lineContent, null));
				}
			}
		} catch (RuntimeException | IOException ex) {
			throw new RuntimeException("Could not load source-file: " + sourceFile, ex);
		}
	}
	
	public void saveContents() {
		File outputFile = new File(FileUtils.getTempDirectory(), sourceFile.getName());
		FileUtils.deleteQuietly(outputFile);
		try {
			try (FileWriter fileWriter = new FileWriter(outputFile, StandardCharsets.UTF_8)) {
				try (BufferedWriter writer = new BufferedWriter(fileWriter)) {
					
					for (SourceLine sourceLine : sourceLines) {
						writer.write(sourceLine.getLineContent());
						if (sourceLine.getLineEnding() != null) {
							writer.write(sourceLine.getLineEnding());
						}
					}
				}
			}
			FileUtils.deleteQuietly(sourceFile);
			FileUtils.moveFile(outputFile, sourceFile);
			
		} catch (RuntimeException | IOException ex) {
			throw new RuntimeException("Could not save source-file: " + sourceFile, ex);
		} finally {
			FileUtils.deleteQuietly(outputFile);
		}
	}
	
	/**
	 * Returns the source-file.
	 */
	public File getSourceFile() {
		return sourceFile;
	}
	
	/**
	 * Returns the source-lines for the source-file.
	 */
	public List<SourceLine> getSourceLines() {
		return Collections.unmodifiableList(sourceLines);
	}
	
	/**
	 * Returns the import-line for the given class or null.
	 */
	public SourceLine getImportLine(String className) {
		String importText = "import " + className + ";";
		for (SourceLine sourceLine : sourceLines) {
			if (sourceLine.getLineContent().contains(importText)) {
				return sourceLine;
			}
		}
		return null;
	}
}
