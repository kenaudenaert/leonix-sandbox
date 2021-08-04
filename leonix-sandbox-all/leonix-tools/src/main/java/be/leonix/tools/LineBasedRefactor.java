package be.leonix.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import be.leonix.tools.model.SourceFile;
import be.leonix.tools.model.SourceLine;

/**
 * A {@link FileRefactor} using a set of {@link LineRefactor} implementations.
 * 
 * @author Ken Audenaert
 */
public class LineBasedRefactor implements FileRefactor {
	
	private final List<LineRefactor> lineRefactors = new ArrayList<>();
	
	public LineBasedRefactor(LineRefactor... lineRefactors) {
		this.lineRefactors.addAll(Arrays.asList(lineRefactors));
	}
	
	@Override
	public void refactorFile(File sourceFile, RefactorContext context) {
		
		SourceFile source = new SourceFile(sourceFile);
		long changeCount = 0;
		for (SourceLine sourceLine : source.getSourceLines()) {
			String oldLine = sourceLine.getLineContent();
			
			String lineInfo = sourceFile.getName() + " @ line " + sourceLine.getLineNumber();
			String newLine = refactorFileLine(lineInfo, oldLine, context);
			
			if (! StringUtils.equals(oldLine, newLine)) {
				sourceLine.setLineContent(newLine);
				changeCount++;
			}
		}
		if (changeCount > 0) {
			source.saveContents();
		}
	}
	
	private String refactorFileLine(String sourceLineInfo, String sourceLine, RefactorContext context) {
		String resultLine = sourceLine;
		for (LineRefactor lineRefactor : lineRefactors) {
			resultLine = lineRefactor.refactorLine(resultLine);
		}
		if (! StringUtils.equals(resultLine, sourceLine)) {
			context.addInfo(">> " + sourceLineInfo);
			if (context.getMode() == RefactorMode.UPDATE_FILE) {
				return resultLine;
				
			} else if (context.getMode() == RefactorMode.ADD_COMMENT) {
				return resultLine + "// REFACTOR";
				
			} else if (context.getMode() == RefactorMode.LOG_CHANGE) {
				context.addInfo(">> --- " + sourceLine.trim());
				context.addInfo(">> +++ " + resultLine.trim());
				return sourceLine;
			}
		}
		return sourceLine;
	}
}
