package be.leonix.tools.refactor.statistics;

import be.leonix.tools.refactor.FileRefactor;
import be.leonix.tools.refactor.RefactorContext;
import be.leonix.tools.refactor.model.SourceFile;

public class RecordStatistics implements FileRefactor {

	// The number of matches found.
	private long matchedCount = 0;
	// The number of changes done.
	private long changedCount = 0;

	@Override
	public String getDescription() {
		return FileRefactor.super.getDescription();
	}

	@Override
	public void refactorStarted() {
		FileRefactor.super.refactorStarted();
	}

	@Override
	public void refactorStopped() {
		FileRefactor.super.refactorStopped();
	}

	@Override
	public void refactorFile(SourceFile sourceFile, RefactorContext context) {

	}
}
