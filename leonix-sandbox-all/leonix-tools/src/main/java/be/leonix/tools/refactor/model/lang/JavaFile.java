package be.leonix.tools.refactor.model.lang;

import java.util.List;
import java.util.Objects;

import be.leonix.tools.refactor.model.repo.SourceFile;

/**
 * This class encapsulates an editable Java source-file.
 * 
 * @author Ken Audenaert
 */
public final class JavaFile {
	
	private final SourceFile sourceFile;
	
	private String packageID;
	private List<String> imports;
	private List<JavaBlock> blocks;
	
	public JavaFile(SourceFile sourceFile) {
		this.sourceFile = Objects.requireNonNull(sourceFile);
	}
}
