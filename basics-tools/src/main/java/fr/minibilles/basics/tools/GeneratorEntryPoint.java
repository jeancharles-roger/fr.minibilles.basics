package fr.minibilles.basics.tools;

import fr.minibilles.basics.generation.Merger;
import java.io.File;

public abstract class GeneratorEntryPoint {

	private File sourceFile;
	private File destinationFile;
	
	private String encoding;
	private Merger merger;
	
	public File getSourceFile() {
		return sourceFile;
	}


	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}


	public File getDestinationFile() {
		return destinationFile;
	}


	public void setDestinationFile(File destinationFile) {
		this.destinationFile = destinationFile;
	}
	
	public String getEncoding() {
		return encoding == null ? "UTF-8" : encoding;
	}
	
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public Merger getMerger() {
		return merger == null ? Merger.DEFAULT : merger;
	}
	
	public void setMerger(Merger merger) {
		this.merger = merger;
	}
	
	
	public void merge(File destinationFile, String contents) throws Exception {
		merger.merge(destinationFile, contents, getEncoding());
	}

	public abstract void generate() throws Exception;
}
