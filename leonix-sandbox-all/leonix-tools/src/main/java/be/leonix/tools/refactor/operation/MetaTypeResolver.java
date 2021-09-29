package be.leonix.tools.refactor.operation;

import java.io.File;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.leonix.tools.refactor.FileRefactor;
import be.leonix.tools.refactor.RefactorContext;
import be.leonix.tools.refactor.model.repo.SourceFile;

/**
 * A {@link FileRefactor} that refactors code to select a MetaInfo constant. 
 * 
 * @author Ken Audenaert
 */
public class MetaTypeResolver implements FileRefactor {
	
	private static final Logger logger = LoggerFactory.getLogger(MetaTypeResolver.class);
	
	private final MetaTypeDirectory metaTypeDir;
	private final Set<String> prefixFilter = new LinkedHashSet<String>();
	
	public MetaTypeResolver(String metaTypeDir) {
		this(new MetaTypeDirectory(new File(metaTypeDir)));
	}
	
	public MetaTypeResolver(MetaTypeDirectory metaTypeDir) {
		this.metaTypeDir = Objects.requireNonNull(metaTypeDir);
		for (MetaTypeInfo metaTypeInfo : this.metaTypeDir.getInfoByName().values()) {
			MetaTypeID metaID = metaTypeInfo.getUniqueID();
			
		}
	}
	
	@Override
	public void refactorFile(SourceFile sourceFile, RefactorContext context) {
		
	}
	
	public Set<String> getPrefixFilter() {
		return prefixFilter;
	}
}
