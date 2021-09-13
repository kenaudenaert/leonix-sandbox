package be.leonix.tools.refactor.model.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
	
	private final SourceLocation opening;
	private final SourceLocation closing;
	private final List<JavaBlock> children;
	private BlockType blockType;
	
	public JavaBlock(SourceLocation opening, SourceLocation closing) {
		this.opening = Objects.requireNonNull(opening);
		this.closing = Objects.requireNonNull(closing);
		this.children = new ArrayList<>();
	}
	
	/**
	 * Returns the block-opening (location of '{‘).
	 */
	public SourceLocation getOpening() {
		return opening;
	}
	
	/**
	 * Returns the block-closing (location of '}‘).
	 */
	public SourceLocation getClosing() {
		return closing;
	}
	
	/**
	 * Returns the children (sub-blocks) of this block.
	 */
	public List<JavaBlock> getChildren() {
		return children;
	}
	
	/**
	 * Returns the optional block-type of this block.
	 */
	public BlockType getBlockType() {
		return blockType;
	}
	
	/**
	 * Changes the optional block-type of this block.
	 */
	public void setBlockType(BlockType blockType) {
		this.blockType = blockType;
	}
}
