package be.leonix.tools.refactor.operation;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;

import be.leonix.tools.refactor.FileRefactor;
import be.leonix.tools.refactor.RefactorContext;
import be.leonix.tools.refactor.model.SourceChange;
import be.leonix.tools.refactor.model.SourceFile;

/**
 * A {@link FileRefactor} that refactors code to select a MetaInfo constant. 
 * 
 * @author Ken Audenaert
 */
public final class MetaTypeResolver implements FileRefactor {
	
	private static final Logger logger = LoggerFactory.getLogger(MetaTypeResolver.class);
	
	private final MetaTypeDirectory metaTypeDir;
	private final Set<String> prefixFilter = new TreeSet<>();
	private final Map<String, SourceChange> changes = new TreeMap<>();
	
	public MetaTypeResolver(String metaTypeDir) {
		this(new MetaTypeDirectory(new File(metaTypeDir)));
	}
	
	public MetaTypeResolver(MetaTypeDirectory metaTypeDir) {
		this.metaTypeDir = Objects.requireNonNull(metaTypeDir);
		
		for (MetaTypeInfo metaTypeInfo : this.metaTypeDir.getInfoByName().values()) {
			// MetaTypeID metaID = metaTypeInfo.getUniqueID();
			
		}
	}
	
	public Set<String> getPrefixFilter() {
		return prefixFilter;
	}
	
	@Override
	public String getDescription() {
		return "MetaTypeResolver (filter=" + prefixFilter + ")";
	}
	
	@Override
	public void refactorStarted() {
	}
	
	@Override
	public void refactorStopped() {
	}
	
	@Override
	public void refactorFile(SourceFile sourceFile, RefactorContext context) {
		File unitFile = sourceFile.getSourceFile();
		try {
			CompilationUnit compilationUnit = StaticJavaParser.parse(unitFile);
			refactorFile(compilationUnit, context);
			
		} catch (IOException | RuntimeException ex) {
			throw new RuntimeException("Could not parse compilation unit: " + unitFile, ex);
		}
	}
	
	private void refactorFile(CompilationUnit compilationUnit, RefactorContext context) {
		for (TypeDeclaration<?> type : compilationUnit.getTypes()) {
			
		}
	}
	
	/**
	 * Run this to verify (file+directory) filters.
	 */
	public static void main(String[] args) {
		File metaTypeDir = new File("/Users/leonix/Desktop/model");
		MetaTypeDirectory dir = new MetaTypeDirectory(metaTypeDir);
		dir.getInfoByConstant();
		
		MetaTypeResolver resolver = new MetaTypeResolver(dir);
		resolver.getDescription();
	}
}
