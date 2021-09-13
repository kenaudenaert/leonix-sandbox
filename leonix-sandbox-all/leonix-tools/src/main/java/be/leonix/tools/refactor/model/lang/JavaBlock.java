package be.leonix.tools.refactor.model.lang;

import java.util.List;

import be.leonix.tools.refactor.model.repo.SourceLocation;

/**
 * The class defines a section (statement block) of a {@link JavaFile}.
 * 
 * @author Ken Audenaert
 */
public final class JavaBlock {
	
	public enum BlockType {
		CLASS, METHOD, ANON,
		FOR, WHILE, DO,
		IF, ELSE_IF, ELSE
	}
	
	private JavaBlock parent;			// The context block.
	private SourceLocation start;		// The block-opening: {
	private SourceLocation end;			// The block-closing: }
	private List<JavaBlock> children;	// The embedded blocks.
}
