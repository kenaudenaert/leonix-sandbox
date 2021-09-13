package be.leonix.tools.refactor.model.repo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * This class encapsulates an editable source-file.
 * 
 * @author Ken Audenaert
 */
public final class SourceFile {
	
	private static final Pattern EOL_PATTERN = Pattern.compile("\r?\n");
	
	private final File sourceFile;
	private final Charset sourceEncoding;
	private final List<SourceLine> sourceLines;
	
	public SourceFile(File sourceFile) {
		this(sourceFile, StandardCharsets.UTF_8);
	}
	
	public SourceFile(File sourceFile, Charset sourceEncoding) {
		this.sourceFile     = Objects.requireNonNull(sourceFile);
		this.sourceEncoding = Objects.requireNonNull(sourceEncoding);
		
		sourceLines = new ArrayList<>();
		loadContents();
	}
	
	/**
	 * Loads (reads from disk) the content of this source-file.
	 */
	public void loadContents() {
		sourceLines.clear();
		try {
			String content = FileUtils.readFileToString(sourceFile, sourceEncoding);
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
	
	/**
	 * Saves (writes to disk) the content of this source-file.
	 */
	public void saveContents() {
		File outputFile = new File(FileUtils.getTempDirectory(), sourceFile.getName());
		FileUtils.deleteQuietly(outputFile);
		try {
			try (FileWriter fileWriter = new FileWriter(outputFile, sourceEncoding)) {
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
		return sourceLines;
	}
	
	/**
	 * Returns the import-lines for this source-file.
	 */
	public List<SourceLine> getImportLines() {
		List<SourceLine> importLines = new ArrayList<>();
		for (SourceLine sourceLine : sourceLines) {
			if (sourceLine.getLineContent().trim().startsWith("import ")) {
				importLines.add(sourceLine);
			}
		}
		return importLines;
	}
	
	/**
	 * Returns the import-line for the given class or null.
	 */
	public SourceLine getImportLine(String className) {
		String importText = "import " + className + ";";
		for (SourceLine sourceLine : sourceLines) {
			if (sourceLine.getLineContent().trim().contains(importText)) {
				return sourceLine;
			}
		}
		return null;
	}
	
	/**
	 * Adds and returns the import-line for the given class.
	 */
	public SourceLine addImportLine(String className) {
		SourceLine importLine = getImportLine(className);
		if (importLine == null) {
			String importText = "import " + className + ";";
			String lineEnding = sourceLines.iterator().next().getLineEnding();
			importLine = new SourceLine(0, importText, lineEnding);
			
			List<SourceLine> importLines = getImportLines();
			sourceLines.add(sourceLines.indexOf(importLines.iterator().next()), importLine);
		}
		return importLine;
	}
}
