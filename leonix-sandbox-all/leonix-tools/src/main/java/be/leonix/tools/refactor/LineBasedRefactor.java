package be.leonix.tools.refactor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import be.leonix.tools.refactor.model.repo.SourceFile;
import be.leonix.tools.refactor.model.repo.SourceLine;

/**
 * A {@link FileRefactor} using a set of {@link LineRefactor} implementations.
 * 
 * @author Ken Audenaert
 */
public final class LineBasedRefactor implements FileRefactor {
	
	private final List<LineRefactor> lineRefactors = new ArrayList<>();
	
	public LineBasedRefactor(LineRefactor... lineRefactors) {
		this.lineRefactors.addAll(Arrays.asList(lineRefactors));
	}
	
	@Override
	public String getDescription() {
		StringBuilder builder = new StringBuilder();
		builder.append("Line-based refactor composed of ");
		if (lineRefactors.isEmpty()) {
			builder.append("no line-refactors");
		} else {
			Iterator<LineRefactor> iterator = lineRefactors.iterator();
			while (iterator.hasNext()) {
				LineRefactor lineRefactor = iterator.next();
				
				builder.append(lineRefactor.getDescription());
				if (iterator.hasNext()) {
					builder.append(',');
				} else {
					builder.append('.');
				}
			}
		}
		return builder.toString();
	}
	
	@Override
	public void refactorFile(SourceFile sourceFile, RefactorContext context) {
		if (lineRefactors.isEmpty()) {
			long changeCount = 0;
			
			for (SourceLine sourceLine : sourceFile.getSourceLines()) {
				String oldLine = sourceLine.getLineContent();
				for (LineRefactor lineRefactor : lineRefactors) {
					lineRefactor.refactorLine(sourceLine, context);
				}
				
				String newLine = sourceLine.getLineContent();
				if (! StringUtils.equals(newLine, oldLine)) {
					
					String lineInfo = sourceFile.getSourceFile() + " @ line " + sourceLine.getLineNumber();
					context.addInfo(">> " + lineInfo);
					
					if (context.getMode() == RefactorMode.LOG_CHANGE) {
						context.addInfo(">> --- " + oldLine.trim());
						context.addInfo(">> +++ " + newLine.trim());
					}
				}
			}
			if (context.getMode() == RefactorMode.UPDATE_FILE && changeCount > 0) {
				sourceFile.saveContents();
			}
		}
	}
}
