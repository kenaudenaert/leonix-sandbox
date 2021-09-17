package be.leonix.tools.refactor.model.lang;

import java.util.Iterator;
import java.util.Objects;

import be.leonix.tools.refactor.model.repo.SourceFile;
import be.leonix.tools.refactor.model.repo.SourceLine;

/**
 * This class encapsulates an editable Java source-file.
 * 
 * @author Ken Audenaert
 */
public final class JavaFile {
	
	private final SourceFile sourceFile;
	private final String packageID;
	
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
	}
	
	public SourceFile getSourceFile() {
		return sourceFile;
	}
	
	public String getPackageID() {
		return packageID;
	}
}
