package be.leonix.tools.refactor.model.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import be.leonix.tools.refactor.model.repo.SourceFile;
import be.leonix.tools.refactor.model.repo.SourceLine;
import be.leonix.tools.refactor.model.repo.SourceLocation;

/**
 * This class encapsulates an editable Java source-file.
 * 
 * @author Ken Audenaert
 */
public final class JavaFile {
	
	private final SourceFile sourceFile;
	private final String packageID;
	private final List<JavaBlock> blocks;
	
	public JavaFile(SourceFile sourceFile) {
		this.sourceFile = Objects.requireNonNull(sourceFile);
		
		Iterator<SourceLine> lines = sourceFile.getSourceLines().iterator();
		if (! lines.hasNext()) {
			throw new RuntimeException("Invalid (empty file) Java file: " + sourceFile.getSourceFile());
		} else {
			String packageLine = lines.next().getLineContent();
			
			// Get the package-name of the meta-info.
			if (packageLine.startsWith("package ") && packageLine.endsWith(";")) {
				packageID = packageLine.substring("package ".length(), packageLine.length() - 1);
			} else {
				throw new RuntimeException("Invalid (no package) Java file: " + sourceFile.getSourceFile());
			}
		}
		
		List<SourceLine> sourceLines = sourceFile.getSourceLines();
		SourceLine openingLine = getHeadNonBlankLine(sourceLines);
		SourceLine closingLine = getTailNonBlankLine(sourceLines);
		if (openingLine != null && closingLine != null) {
			SourceLocation fileOpening = new SourceLocation(sourceFile, openingLine, 0);
			SourceLocation fileClosing = new SourceLocation(sourceFile, closingLine, closingLine.getLineContent().length() - 1);
			this.blocks = listBlocks(fileOpening, fileClosing);
		} else {
			this.blocks = Collections.emptyList();
		}
	}
	
	public SourceFile getSourceFile() {
		return sourceFile;
	}
	
	public String getPackageID() {
		return packageID;
	}
	
	public List<JavaBlock> getBlocks() {
		return blocks;
	}
	
	private static SourceLine getHeadNonBlankLine(List<SourceLine> sourceLines) {
		ListIterator<SourceLine> headIterator = sourceLines.listIterator();
		while (headIterator.hasNext()) {
			SourceLine line = headIterator.next();
			if (! line.getLineContent().isEmpty()) {
				return line;
			}
		}
		return null;
	}
	
	private static SourceLine getTailNonBlankLine(List<SourceLine> sourceLines) {
		ListIterator<SourceLine> tailIterator = sourceLines.listIterator(sourceLines.size()-1);
		while (tailIterator.hasPrevious()) {
			SourceLine line = tailIterator.previous();
			if (! line.getLineContent().isEmpty()) {
				return line;
			}
		}
		return null;
	}
	
	private List<JavaBlock> listBlocks(SourceLocation opening, SourceLocation closing) {
		List<JavaBlock> rootBlocks = new ArrayList<JavaBlock>();
		return rootBlocks;
	}
}
