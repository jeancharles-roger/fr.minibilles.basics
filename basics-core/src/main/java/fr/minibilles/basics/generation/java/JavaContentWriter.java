package fr.minibilles.basics.generation.java;

import fr.minibilles.basics.error.Diagnostic;
import fr.minibilles.basics.error.ErrorHandler;
import fr.minibilles.basics.generation.Merger;
import fr.minibilles.basics.generation.java.Java.Parameter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Stack;

/**
 * This {@link JavaContentHandler} generates Java code to given output folder.
 * It creates the folder hierarchy for packages and the java files.
 * @author Jean-Charles Roger
 */
public class JavaContentWriter implements JavaContentHandler {

	private final String NL = System.getProperty("line.separator");

	/* Contexts */
	private enum Type {
		PACKAGE,
		CLASS,
		INTERFACE,
		ENUM,
		METHOD,
		ATTRIBUTE
	}
	
	// configuration parameters
	private String charSet = "UTF-8";
	
	/** Error handler. */
	private ErrorHandler errorHandler = ErrorHandler.simple;
	
	private final StringBuilder currentPackage = new StringBuilder();
	private Stack<StringBuilder> packageContent = new Stack<StringBuilder>();
	
	private File currentFolder;
	private StringBuilder currentFileContent = new StringBuilder();
	private int importMark = -1;
	
	private final Stack<Type> context = new Stack<Type>();
	private boolean abstractMethod = false;
	private boolean firstEnumLiteral = false;
	
	/** 
	 * If not null, the prefix will be added in the next 
	 * {@link #appendToFile(String)}, then cleared.
	 */
	private String appendPrefix = null;
	
	private Merger merger;
	
	public JavaContentWriter(File outputFolder) {
		if ( outputFolder == null ) throw new NullPointerException("Output folder can't be null");
		this.currentFolder = outputFolder;
	}
	
	public Merger getMerger() {
		return merger == null ? Merger.DEFAULT : merger;
	}
	
	public void setMerger(Merger merger) {
		this.merger = merger;
	}
	
	public void beginPackage(String name) {
		for ( String shortName : Java.splitPackageName(name) ) {
			currentFolder = new File(currentFolder, shortName);
			if (!currentFolder.exists() ) currentFolder.mkdir();
			
			if (currentPackage.length() != 0 ) {
				currentPackage.append(".");
			}
			currentPackage.append(shortName);
			
			pushContext(Type.PACKAGE);
		}
		// create string builder that collects information to write to 'package-info.java'
		packageContent.push(new StringBuilder());
	}
		

	public void endPackage(String name) {
		final StringBuilder currentPackageContent = packageContent.peek();
		if ( currentPackageContent.length() > 0 ) {
			// creates a 'package-info.java' file with currentPackageContext
			// appends package declaration
			currentPackageContent.append(NL);
			currentPackageContent.append("package ");
			currentPackageContent.append(currentPackage.toString());
			currentPackageContent.append(";");
			currentPackageContent.append(NL);
			
			try {
				getMerger().merge(new File(currentFolder, "package-info.java"), currentPackageContent.toString(), charSet);
			} catch (Exception e ) {
				errorHandler.handleError(Diagnostic.ERROR, e.getClass().getName() + ": " + e.getMessage());
				return;
			}
		}
		packageContent.pop();
		
		for ( String shortName : Java.splitPackageName(name) ) {

			currentFolder = currentFolder.getParentFile();
			int length = shortName.length();
			if ( currentPackage.length() <= length ) {
				currentPackage.delete(0, currentPackage.length());
			} else {
				currentPackage.delete(currentPackage.length() - (length+1), currentPackage.length());
			}
	
			popContext(Type.PACKAGE);
		}
	}

	public void beginFile(String name) {
		// checks if currentFileContent is empty, if not appends it to currentPackageContent
		if ( currentFileContent != null ) {
			packageContent.peek().append(currentFileContent);
		}
		
		// resets content.
		currentFileContent = new StringBuilder();
		importMark = -1;
		
		// checks if file is a java file.
		boolean javaFile = name != null && name.toLowerCase().endsWith(".java");
		if ( javaFile && currentPackage != null ) {
			appendToFile("package ");
			appendToFile(currentPackage.toString());
			appendToFile(";");
		}
	}

	public void endFile(String name) {
		try {
			getMerger().merge(new File(currentFolder, name), currentFileContent.toString(), charSet);
		} catch (Exception e ) {
			errorHandler.handleError(Diagnostic.ERROR, e.getClass().getName() + ": " + e.getMessage());
			return;
		}
		// resets content.
		currentFileContent = new StringBuilder();
	}
	
	public void markImports() {
		importMark = currentFileContent.length();
		appendToFile(NL);
	}
	
	public void import_(int flags, String importString) {
		StringBuilder builder = new StringBuilder();
		builder.append("import ");
		appendToFile(Java.printFlags(flags));
		builder.append(importString);
		builder.append(";");
		builder.append(NL);
		if ( importMark >= 0 ) {
			currentFileContent.insert(importMark, builder);
		} else {
			appendToFile(builder.toString());
		}
	}

	public void beginClass(int flags, String name, String parentClass, String implementedInterfaces) {
		appendToFile(Java.printFlags(flags));
		appendToFile("class ");
		appendToFile(name);
		if ( parentClass != null && parentClass.length() > 0) {
			appendToFile(" extends ");
			appendToFile(parentClass);
		}
		if ( implementedInterfaces != null && implementedInterfaces.length() > 0) {
			appendToFile(" implements ");
			appendToFile(implementedInterfaces);
		}
		appendToFile(" {");
		
		pushContext(Type.CLASS);
	}

	public void endClass(String name) {
		appendToFile("}");
		popContext(Type.CLASS);
	}

	public void beginInterface(int flags, String name, String extendedInterfaces) {
		appendToFile(Java.printFlags(flags));
		appendToFile("interface ");
		appendToFile(name);
		if ( extendedInterfaces != null && extendedInterfaces.length() > 0) {
			appendToFile(" extends ");
			appendToFile(extendedInterfaces);
		}
		appendToFile(" {");
		
		pushContext(Type.INTERFACE);
	}

	public void endInterface(String name) {
		appendToFile("}");
		popContext(Type.INTERFACE);
	}

	public void beginEnum(int flags, String name) {
		appendToFile(Java.printFlags(flags));
		appendToFile("enum ");
		appendToFile(name);
		appendToFile(" {");
		pushContext(Type.ENUM);
	}

	public void beginEnumLiterals() {
		firstEnumLiteral = true;
	}
	
	public void enumLiteral(String name, String ... arguments) {
		if ( firstEnumLiteral == false ) appendToFile(",");
		appendToFile(name);
		if ( arguments != null && arguments.length > 0) {
			appendToFile("(");
			for (int i=0; i<arguments.length; i++) {
				if ( i > 0 ) appendToFile(", ");
				appendToFile(arguments[i]);
			}
			appendToFile(")");
		}
		firstEnumLiteral = false;
	}
	
	public void endEnumLiterals() {
		appendToFile(";");
	}
	
	public void endEnum(String name) {
		appendToFile("}");
		popContext(Type.ENUM);
	}

	public void beginAnonymousClass(String inherited, String ... arguments) {
		appendToFile("new ");
		appendToFile(inherited);
		appendToFile("(");
		for (int i=0; i<arguments.length; i++) {
			if ( i > 0 ) appendToFile(", ");
			appendToFile(arguments[i]);
		}
		appendToFile(") {");
		pushContext(Type.CLASS);
	}

	public void endAnnonymousClass(String inherited) {
		appendToFile("}");
		popContext(Type.CLASS);
	}

	public void beginMethod(int flags, String returnType, String name, String exceptions, Parameter ... parameters) {
		appendToFile(Java.printFlags(flags));
		if ( returnType != null ) {
			appendToFile(returnType);
			appendToFile(" ");
		}
		appendToFile(name);
		appendToFile("(");
		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			appendToFile(Java.printFlags(parameter.flags));
			appendToFile(parameter.type);
			appendToFile(" ");
			appendToFile(parameter.name);
			if(i + 1 < parameters.length) {
				appendToFile(", ");
			}
		}
		appendToFile(")");
		if(exceptions != null && exceptions.length() >0) {
			appendToFile(" throws ");
			appendToFile(exceptions);
		}
		
		abstractMethod = Java.has(flags, Java.ABSTRACT);
		if ( context.peek() == Type.INTERFACE || abstractMethod) {
			appendToFile(";");
		} else {
			appendToFile(" {");
		}
		pushContext(Type.METHOD);
	}
	
	public void endMethod(String name) {
		popContext(Type.METHOD);
		if ( context.peek() != Type.INTERFACE && !abstractMethod) {
			appendToFile("}");
		}
	}
	
	public void beginAttribute(int flags, String type, String name) {
		appendToFile(Java.printFlags(flags));
		appendToFile(type);
		appendToFile(" ");
		appendToFile(name);
		pushContext(Type.ATTRIBUTE);
		appendPrefix = " = ";
	}

	public void endAttribute(String name) {
		appendPrefix = null;
		popContext(Type.ATTRIBUTE);
		appendToFile(";");
	}
	
	public void beginBlock(int flags) {
		appendToFile(Java.printFlags(flags));
		appendToFile("{");
	}

	public void endBlock() {
		appendToFile("}");
	}

	public void annotation(String name, String parameters) {
		appendToFile("@");
		appendToFile(name);
		if ( parameters != null ) {
			appendToFile("(");
			appendToFile(parameters);
			appendToFile(")");
		}
		appendToFile(NL);
	}

	public void codeln(int level, String code) {
		appendToFile(code);
	}
	
	public void code(String code) {
		appendToFile(code);
	}
	
	public void comment(int type, int level, String comment) {
		switch (type) {
		case Java.SINGLE_LINE:
			appendToFile("//");
			appendToFile(comment);
			appendToFile(NL);
			break;
		case Java.MULTI_LINE:
			appendToFile("/*");
			appendToFile(comment);
			appendToFile("*/");
			break;
		case Java.JAVA_DOC:
			appendToFile("/**\n");
			appendToFile("\n * ");
			appendToFile(comment);
			appendToFile("*/");
			break;
		}
	}
	 
	public void binaryFile(File file) {
		File destinationFile = new File(currentFolder, file.getName());
		copyFile(file, destinationFile);
	}
	
	public void documentFile(File file) {
		// creates doc-files directory if needed.
		String docfiles = "doc-files";
		File docFilesDirectory = new File(currentFolder, docfiles);
		if (docFilesDirectory.exists() == false ) {
			docFilesDirectory.mkdir();
		}

		// copies file.
		File destinationFile = new File(docFilesDirectory, file.getName());
		copyFile(file, destinationFile);
	}
	
	/** File copy method. */
	private void copyFile(File sourceFile, File destinationFile) {
		try {
			FileInputStream inputStream  = new FileInputStream(sourceFile);
		    FileOutputStream outputStream = new FileOutputStream(destinationFile);
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
