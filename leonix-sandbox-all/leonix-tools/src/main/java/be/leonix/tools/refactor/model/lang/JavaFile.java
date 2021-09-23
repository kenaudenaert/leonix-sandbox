package be.leonix.tools.refactor.model.lang;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;

import be.leonix.tools.refactor.model.repo.SourceFile;

/**
 * This class encapsulates an editable Java source-file.
 * 
 * @author Ken Audenaert
 */
public final class JavaFile {
	
	private static final Logger logger = LoggerFactory.getLogger(JavaFile.class);
	
	private final SourceFile sourceFile;
	private final CompilationUnit javaSource;
	
	public JavaFile(SourceFile sourceFile) {
		this.sourceFile = Objects.requireNonNull(sourceFile);
		try {
			javaSource = StaticJavaParser.parse(sourceFile.getSourceFile());
			for (ImportDeclaration importLine : javaSource.getImports()) {
				logger.info("import-line: " + importLine.getName());
			}
			FileUtils.write(sourceFile.getSourceFile(), 
					javaSource.toString(), sourceFile.getSourceEncoding());
			
		} catch (IOException e) {
			throw new RuntimeException("No such file: " + e);
		}
	}
	
	public SourceFile getSourceFile() {
		return sourceFile;
	}
	
	public static void main(String[] args) {
		SourceFile sourceFile = new SourceFile(new File("/Users/leonix/Desktop/AvatarMeta.java"));
		JavaFile javaFile = new JavaFile(sourceFile);
	}
}
