/**
 * 
 */
package fr.minibilles.basics.generation.c;

import java.io.InputStream;

/**
 * <p>This interface proposes methods to handle C Content in event based
 * mode (as SAX does).</p>
 * <p>It's a first version, this API may change in the next version.</p>
 * 
 * @author Jean-Charles Roger
 */
public interface CContentHandler {

	/** 
	 * <p>Creates and opens a directory. The files are created in the directory
	 * (until the next {@link #beginDirectory(String)} or 
	 * {@link #endDirectory(String)}).
	 * @param name directory name, must be a valid directory name.
	 */
	void beginDirectory(String name);
	
	/**
	 * <p>Ends directory.</p>
	 * @param name directory name, must be a valid directory name.
	 */
	void endDirectory(String name);

	/** 
	 * <p>Starts a textual file. If file name ends with '.c' or '.h' (case 
	 * ignored) it will considered as a C file.</p>
	 * @param name file name, must be a valid file name.
	 */
	void beginFile(String name);
	
	/**
	 * <p>Ends a textual file.</p>
	 * @param name file name, must be a valid file name.
	 */
	void endFile(String name);
	
	void functionDeclaration(int flags, String returnType, String name, C.Parameter... parameters);
	void beginFunction(int flags, String returnType, String name, C.Parameter... parameters);
	void endFunction(String name);
	
	void codeln(int level, String code);
	void code(String code);
	
	/**
	 * <p>Appends a comment.</p>
	 * @param flags comment flag, must be one of {@link C#SINGLE_LINE} or
	 * 	{@link C#MULTI_LINE}
	 * @param level tabulation level.
	 * @param comment the actual comment (with out "//" or "/*" ).
	 */
	void comment(int flags, int level, String comment);
	
	/**
	 * <p>Adds a file in current location with given content.</p>
	 * 
	 * @param stream used as content
	 * @param filename name of created file
	 */
	void addFile(InputStream stream, String filename);

}
