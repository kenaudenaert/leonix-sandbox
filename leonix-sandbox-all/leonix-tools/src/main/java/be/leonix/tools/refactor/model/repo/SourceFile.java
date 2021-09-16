package be.leonix.tools.refactor.model.repo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
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
	 * Returns the package-name for this source-file.
	 */
	public String getPackageName() {
		Iterator<SourceLine> lines = sourceLines.iterator();
		if (! lines.hasNext()) {
			throw new RuntimeException("Invalid (empty file) Java file: " + sourceFile);
		}
		for (;;) {
			String lineContent = lines.next().getLineContent().trim();
			
			// Get the package-name from the package declaration.
			if (lineContent.startsWith("package ") && lineContent.endsWith(";")) {
				return lineContent.substring("package ".length(), lineContent.length() - 1).trim();
				
			} else if (! lines.hasNext()) {
				throw new RuntimeException("Invalid (no package) Java file: " + sourceFile);
			}
		}
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
	
	/**
	 * Sorts the import-lines for this source-file.
	 */
	public void sortImportLines() {
		List<SourceLine> importLines = getImportLines();
		if (importLines.size() > 1) {
			String lineEnding = importLines.iterator().next().getLineEnding();
			
			// Get lines before imports.
			int minIndex = sourceLines.indexOf(importLines.get(0));
			List<SourceLine> beforeLines = new ArrayList<SourceLine>(
					sourceLines.subList(0, minIndex));
			
			// Get lines after imports.
			int maxIndex = sourceLines.indexOf(importLines.get(importLines.size()-1));
			List<SourceLine> afterLines = new ArrayList<SourceLine>(
					sourceLines.subList(maxIndex+1, sourceLines.size()));
			
			// Sort the import lines.
			Map<String, SourceLine> staticImportsByClassName = new TreeMap<>();
			Map<String, SourceLine> jdkImportsByClassName = new TreeMap<>();
			Map<String, SourceLine> extImportsByClassName = new TreeMap<>();
			Map<String, SourceLine> orgImportsByClassName = new TreeMap<>();
			Map<String, SourceLine> simpleImportsByClassName = new TreeMap<>();
			
			for (SourceLine importLine : importLines) {
				String importText = importLine.getLineContent().trim();
				importText = StringUtils.removeStart(importText, "import ");
				importText = StringUtils.removeEnd(importText, ";");
				importText = importText.trim();
				
				if (importText.startsWith("static ")) {
					importText = StringUtils.removeStart(importText, "import ");
					importText = importText.trim();
					staticImportsByClassName.put(importText, importLine);
				} else if (importText.startsWith("java.")) {
					jdkImportsByClassName.put(importText, importLine);
				} else if (importText.startsWith("javax.")) {
					extImportsByClassName.put(importText, importLine);
				} else if (importText.startsWith("org.")) {
					orgImportsByClassName.put(importText, importLine);
				} else {
					simpleImportsByClassName.put(importText, importLine);
				}
			}
			
			// Generate import sections (static, jdk, ext, simple).
			List<SourceLine> sortedImports = new ArrayList<SourceLine>();
			if (! staticImportsByClassName.isEmpty()) {
				for (Map.Entry<String, SourceLine> entry : staticImportsByClassName.entrySet()) {
					sortedImports.add(entry.getValue());
				}
				sortedImports.add(new SourceLine(0, "", lineEnding));
			}
			
			if (! jdkImportsByClassName.isEmpty()) {
				for (Map.Entry<String, SourceLine> entry : jdkImportsByClassName.entrySet()) {
					sortedImports.add(entry.getValue());
				}
				sortedImports.add(new SourceLine(0, "", lineEnding));
			}
			
			if (! extImportsByClassName.isEmpty()) {
				for (Map.Entry<String, SourceLine> entry : extImportsByClassName.entrySet()) {
					sortedImports.add(entry.getValue());
				}
				sortedImports.add(new SourceLine(0, "", lineEnding));
			}

			if (! orgImportsByClassName.isEmpty()) {
				for (Map.Entry<String, SourceLine> entry : orgImportsByClassName.entrySet()) {
					sortedImports.add(entry.getValue());
				}
				sortedImports.add(new SourceLine(0, "", lineEnding));
			}

			String lastGroup = null;
			for (Map.Entry<String, SourceLine> entry : simpleImportsByClassName.entrySet()) {
				String entryGroup = entry.getKey().substring(0, entry.getKey().indexOf('.'));
				if (lastGroup != null && !lastGroup.equals(entryGroup)) {
					sortedImports.add(new SourceLine(0, "", lineEnding));
				}
				sortedImports.add(entry.getValue());
				lastGroup = entryGroup;
			}
			
			// Replace file contents.
			sourceLines.clear();
			sourceLines.addAll(beforeLines);
			sourceLines.addAll(sortedImports);
			sourceLines.addAll(afterLines);
		}
	}
	
	public static void main(String[] args) {
		SourceFile sourceFile = new SourceFile(new File("/Users/leonix/Desktop/AvatarMeta.java"));
		sourceFile.sortImportLines();
		sourceFile.saveContents();
	}
}
