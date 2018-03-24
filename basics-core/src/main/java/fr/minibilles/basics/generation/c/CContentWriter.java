package fr.minibilles.basics.generation.c;

import fr.minibilles.basics.error.Diagnostic;
import fr.minibilles.basics.error.ErrorHandler;
import fr.minibilles.basics.generation.Merger;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Stack;

/**
 * This {@link CContentHandler} generates C code to given output folder.
 * @author Jean-Charles Roger
 */
public class CContentWriter implements CContentHandler {

	private final String NL = System.getProperty("line.separator");

	/* Contexts */
	private enum Type {
		FUNCTION
	}

	// configuration parameters
	private final String charSet;
	
	/** Error handler. */
	private ErrorHandler errorHandler = ErrorHandler.simple;
	
	private File currentFolder;
	private StringBuilder currentFileContent;

	private final Stack<Type> context = new Stack<Type>();
	
	/** 
	 * If not null, the prefix will be added in the next 
	 * {@link #appendToFile(String)}, then cleared.
	 */
	private String appendPrefix = null;
	
	private Merger merger;
	
	public CContentWriter(File outputFolder, String charSet) {
		if ( outputFolder == null ) throw new NullPointerException("Output folder can't be null");
		this.currentFolder = outputFolder;
        this.charSet = charSet;
	}

    public CContentWriter(File outputFolder) {
        this(outputFolder, "UTF-8");
    }
	
	public Merger getMerger() {
		return merger == null ? Merger.DEFAULT : merger;
	}
	
	public void setMerger(Merger merger) {
		this.merger = merger;
	}
	
	public void beginDirectory(String name) {
		currentFolder = new File(currentFolder, name);
		if (!currentFolder.exists() ) currentFolder.mkdir();
	}
		

	public void endDirectory(String name) {
		currentFolder = currentFolder.getParentFile();
	}

	public void beginFile(String name) {
		// resets content.
		currentFileContent = new StringBuilder();
	}

	public void endFile(String name) {
		try {
			getMerger().merge(new File(currentFolder, name), currentFileContent.toString(), charSet);
		} catch (Exception e ) {
			errorHandler.handleError(Diagnostic.ERROR, e.getClass().getName() + ": " + e.getMessage());
		}
	}
	
	public void functionDeclaration(int flags, String returnType, String name, C.Parameter... parameters) {
		appendToFile(C.printFlags(flags));
		if ( returnType != null ) {
			appendToFile(returnType);
			appendToFile(" ");
		}
		appendToFile(name);
		appendToFile("(");
		for (int i = 0; i < parameters.length; i++) {
			C.Parameter parameter = parameters[i];
			appendToFile(C.printFlags(parameter.flags));
			appendToFile(parameter.type);
			appendToFile(" ");
			appendToFile(parameter.name);
			if(i + 1 < parameters.length) {
				appendToFile(", ");
			}
		}
		appendToFile(")");
		appendToFile(";");
	}
	
	public void beginFunction(int flags, String returnType, String name, C.Parameter... parameters) {
		appendToFile(C.printFlags(flags));
		if ( returnType != null ) {
			appendToFile(returnType);
			appendToFile(" ");
		}
		appendToFile(name);
		appendToFile("(");
		for (int i = 0; i < parameters.length; i++) {
			C.Parameter parameter = parameters[i];
			appendToFile(C.printFlags(parameter.flags));
			appendToFile(parameter.type);
			appendToFile(" ");
			appendToFile(parameter.name);
			if(i + 1 < parameters.length) {
				appendToFile(", ");
			}
		}
		appendToFile(")");
		appendToFile(" {");
		pushContext(Type.FUNCTION);
	}
	
	public void endFunction(String name) {
		popContext(Type.FUNCTION);
		appendToFile("}");
	}
	
	public void codeln(int level, String code) {
		appendToFile(code);
	}
	
	public void code(String code) {
		appendToFile(code);
	}
	
	public void comment(int type, int level, String comment) {
		switch (type) {
		case C.SINGLE_LINE:
			appendToFile("//");
			appendToFile(comment);
			appendToFile(NL);
			break;
		case C.MULTI_LINE:
			appendToFile("/*");
			appendToFile(comment);
			appendToFile("*/");
			break;
		}
	}
	 
	public void addFile(InputStream stream, String filename) {
		// creates file reference.
		final File destinationFile = new File(currentFolder, filename);
		//  creates parent directories if needed.
		destinationFile.getParentFile().mkdirs();
		
		// copy file content
		try {
			final InputStream inputStream  = new BufferedInputStream(stream);
			final OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destinationFile));
			try {
				byte[] buf = new byte[2048];
				int i = 0;
				while ((i = inputStream.read(buf)) != -1) {
					outputStream.write(buf, 0, i);
				}
			} finally {
				if (inputStream != null) inputStream.close();
				if (outputStream != null) outputStream.close();
			}
		} catch (IOException e) {
			errorHandler.handleError(Diagnostic.ERROR, e.getClass().getName() + ": " + e.getMessage());
		}
	}
	
	private void pushContext(Type type) {
		context.push(type);
	}
	
	private void popContext(Type expected) {
		if ( context.isEmpty() ) {
			errorHandler.handleError(Diagnostic.ERROR, "Closing a not openned " + expected + ".");
		}
		final Type current = context.pop();
		if ( expected != current ) {
			errorHandler.handleError(Diagnostic.ERROR, "Closing a " + current + " while expecting a " + expected + ".");
		}
	}
	
	private void appendToFile(String text) {
		if ( appendPrefix != null ) {
			currentFileContent.append(appendPrefix);
			appendPrefix = null;
		}
		currentFileContent.append(text);
	}
	
}
