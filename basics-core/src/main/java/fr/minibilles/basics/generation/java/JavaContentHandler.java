/**
 * 
 */
package fr.minibilles.basics.generation.java;

import java.io.File;

/**
 * This interface proposes methods to handle Java Content in event based
 * mode (as SAX does).
 * 
 * @author Jean-Charles Roger
 */
public interface JavaContentHandler {

	/** Starts a package, it supports the dotted notation */
	void beginPackage(String name);
	/** Ends a package, it supports the dotted notation */
	void endPackage(String name);

	/** 
	 * Starts a textual file. If file name ends with '.java' (case ignored)
	 * it will considered as a Java file.
	 * @param name file name, must be a valid file name.
	 */
	void beginFile(String name);
	
	/**
	 * Ends a textual file.
	 * @param name file name must be a valid file name.
	 */
	void endFile(String name);
	
	/** Marks the location where to write import. */
	void markImports();
	
	/**
	 * Adds an import declaration to file. If {@link #markImports()} has
	 * been called, it will be inserted at the mark, if not at current place
	 * in file.
	 * 
	 * @param flags import flags, can be {@link Java#STATIC} or  {@link Java#NONE}.
	 * @param importString fully qualified name to import.
	 */
	void import_(int flags, String importString);
	
	/**
	 * Starts a Java class, it must be inside a file (between a
	 * {@link #beginFile(String)} and an {@link #endFile(String)}).
	 * <p>
	 *     Possible flags for a class are combination of:
	 * <ul>
	 * <li>{@link Java#ABSTRACT} or {@link Java#FINAL}</li>
	 * <li>{@link Java#STATIC}</li>
	 * <li>{@link Java#PRIVATE}, {@link Java#PROTECTED} or {@link Java#PUBLIC}, or</li>
	 * <li>{@link Java#NONE}</li>
	 * </ul>
	 *
	 * 
	 * @param flags the class flags combination.
	 * @param name the class name, must be a valid Java class name. Can't be
	 * 	null.
	 * @param parentClass the super class name. It can be the fully qualified 
	 * 	name or the short name if the class is imported. Can be null.
	 * @param implementedInterfaces a comma separated list of interface names
	 * 	which are the implemented interface for the class. Can be null.
	 */
	void beginClass(int flags, String name, String parentClass, String implementedInterfaces);
	
	/**
	 * Ends of a Java class.
	 * @param name the class name, must be a valid Java class name. Can't be
	 * 	null.
	 */
	void endClass(String name);
	
	void beginInterface(int flags, String name, String extendedInterfaces);
	void endInterface(String name);
	
	void beginEnum(int flags, String name);
	void beginEnumLiterals();
	void enumLiteral(String name, String ... arguments);
	void endEnumLiterals();
	void endEnum(String name);
	
	void beginAnonymousClass(String inherited, String ... arguments);
	void endAnnonymousClass(String inherited);

	void beginMethod(int flags, String returnType, String name, String exceptions, Java.Parameter... parameters);
	void endMethod(String name);
	
	void beginAttribute(int flags, String type, String name);
	void endAttribute(String name);
	
	void annotation(String name, String parameters);
	
	void codeln(int level, String code);
	void code(String code);
	
	/**
	 * Appends a comment.
	 * @param flags comment flag, must be one of {@link Java#SINGLE_LINE}, 
	 * 	{@link Java#MULTI_LINE} or {@link Java#JAVA_DOC} 
	 * @param level tabulation level.
	 * @param comment the actual comment (with out "//", "/*" or "/**" ).
	 */
	void comment(int flags, int level, String comment);
	
	/**
	 * Copy given file to current location. File is treated as binary file.
	 * It can be used for image resources for instance.
	 * 
	 * @param file file to copy
	 */
	void binaryFile(File file);

	/**
	 * Copy given file to current doc-files location. File is treated as
	 * binary file. It allow to add specific resources for JavaDoc.
	 * 
	 * @param file file to copy
	 */
	void documentFile(File file);
	
}
