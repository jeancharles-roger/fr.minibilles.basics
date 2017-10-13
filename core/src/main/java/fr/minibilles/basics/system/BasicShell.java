/**
 * 
 */
package fr.minibilles.basics.system;

import fr.minibilles.basics.Basics;
import fr.minibilles.basics.error.Diagnostic;
import fr.minibilles.basics.error.ErrorHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <p>A Basics shell allows to execute system commands as shell would do it.
 * It only provides simple commands written in Java (system independent).</p>
 * <p>
 * It supports:
 * <ul>
 * <li>Execution commands:
 * <ul><li>exec on commands</li></ul>
 *</li>
 * <li>File and directory commands:
 * <ul>
 * <li>cd (use of a working directory).</li>
 * <li>copy files and directories.</li>
 * <li>delete files and directories.</li>
 * </ul>
 * </li>
 * </ul>
 * </p>
 * <p>
 * A <code>mv</code> command won't be implemented, the {@link File#renameTo(File)} works just fine.
 * </p> 
 * @author Jean-Charles Roger
 */
public class BasicShell {

	/** Default error handler for {@link BasicShell} */
	private ErrorHandler defaultErrorHandler = new ErrorHandler() {
		public void handleError(Diagnostic diagnostic) {
			BasicShell.this.printOut(diagnostic.toString());
		}
		
		public void handleError(int type, String message) {
			handleError(new Diagnostic.Stub(type, message));
		}
	};
	
	/** Current working directory. */
	private File workingDirectory;

	/** Shell's out writer */
	private PrintWriter out;
	
	/** Shell's err writer */
	private PrintWriter err;
	
	/** Error handler. */
	private ErrorHandler errorHandler = null;
	
	/** If false, {@link #printOut(String)} won't write anything to out. */
	private boolean verbose = true;

	public BasicShell(File startDirectory, PrintWriter out) {
		this(startDirectory, out, out);
	}
	
	public BasicShell(File startDirectory, PrintWriter out, PrintWriter err) {
		this.workingDirectory = startDirectory == null ? new File(".") : startDirectory;
		this.out = out;
		this.err = err;
	}
	
	private boolean hasFlag(int mask, int flags) { 
		return (flags & mask) != 0; 
	}

	/** Change working dir. */
	public boolean cd(String path) {
		if ( path == null ) return false;
		File pathFile = new File(path);
		File tempWd = pathFile.isAbsolute() ? pathFile :  new File(workingDirectory, path);
		if ( !tempWd.exists() || !tempWd.canRead() || !tempWd.isDirectory() ) return false;
		workingDirectory = tempWd;
		return true;
	}
	
	/** Get working directory. */
	public File pwd() {
		return workingDirectory;
	}
	
	/**
	 * <p>Lists the files in current directory that matches the filter.</p>
	 * @param filter RegEx to filter the result, if null there is no filtering. 
	 * 		  It doesn't affect the directories.
	 * @param flags 
	 * 			<ul><li>{@link Basics#RECURSIVE} searches inside sub directories</li></ul>
	 * @return the list of {@link File}s that matches the filter inside the given file.
	 * @see Pattern#compile(String)
	 * @see #pwd()
	 */
	public List<File> ls(String filter, int flags) {
		return ls(pwd(), filter, flags);
	}
	
	
	/**
	 * <p>Lists the files using the given file as starting point that matches the filter.</p>
	 * <p><b>Examples:</b>
	 * <ul>
	 * <li>
	 * <code>ls("tmp", "\\.java$", Basics.NONE)</code>
	 * will list all <b>Java files</b> in the directory <b>tmp</b>.
	 * </li>
	 * <li>
	 * <code>ls("tmp", "\\.java$", Basics.RECURSIVE)</code> 
	 * will list all Java files in the directory tmp and <b>sub directories</b>.
	 * </li>
	 * <li>
	 * <code>ls("tmp", "\\.java$", Basics.RECURSIVE | Basics.HIDDEN)</code> 
	 * will list all Java files in the directory tmp and sub directories including <b>hidden directories</b>.
	 * </li>
	 * <li>
	 * <code>ls("tmp", null, Basics.RECURSIVE | Basics.HIDDEN)</code> 
	 * will list <b>all files and directories</b> <b>recursively</b> in the directory tmp including <b>hidden</b> files.
	 * </li>
	 * </ul>
	 * @param filter RegEx to filter the result, if null there is no filtering. 
	 * 		  It doesn't affect the directories.
	 * @param flags 
	 * 			<ul><li>{@link Basics#RECURSIVE} searches inside sub directories</li></ul>
	 * @return the list of {@link File}s that matches the filter inside the given file.
	 * @see Pattern#compile(String)
	 * @see #pwd()
	 * @see #ls(String, String, int)
	 */
	public List<File> ls(String file, String filter, int flags) {
		return ls(new File(file), filter, flags);
	}

	/**
	 * Lists the files in given directory that matches the filter.
	 * @param file a {@link File} which must be a directory.
	 * @param filter RegEx to filter the result, if null there is no filtering. 
	 * 		  It doesn't affect the directories.
	 * @param flags 
	 * 			<ul><li>{@link Basics#RECURSIVE} searches inside sub directories</li></ul>
	 * @return the list of {@link File}s that matches the filter inside the given file.
	 * @see Pattern#compile(String)
	 * @see #pwd()
	 */
	public List<File> ls(File file, String filter, int flags) {
		Pattern filterPattern = filter == null ? null : Pattern.compile(filter);
		List<File> result = new ArrayList<File>();
		if ( file.isDirectory() ) {
			final File[] listFiles = file.listFiles();
			for ( File child : listFiles ) {
				internalLs(child,filterPattern,flags,result);
			}
			if ( select(file, filterPattern, flags) ) result.add(file);
		} else {
			internalLs(file, filterPattern, flags, result);
		}
		return result;
	}
	
	/** Internal recursive implementation for ls */
	private void internalLs(File file, Pattern filterPattern, int flags, List<File> result) {
		if ( file.isDirectory() && hasFlag(Basics.RECURSIVE, flags) ) {
			for ( File child : file.listFiles() ) {
				if ( !hasFlag(Basics.HIDDEN, flags) && isHidden(child) ) continue;
				internalLs(child, filterPattern, flags, result);
			}
			if ( select(file, filterPattern, flags) ) result.add(file);
		} else {
			if ( select(file, filterPattern, flags) ) result.add(file);
		}
	}

	/** 
	 * Copies source file to destination file. 
	 * @param source source file name to copy. If it's a directory it copies it's members.
	 * @param destination destination file name.
	 * @param filter RegEx to filter the files to remove. If null all file will be removed.
	 * @param flags flags for copy {@link Basics#RECURSIVE}, {@link Basics#OVERWRITE}.
	 * @see {@link #ls(String, String, int)} for file selection for copy. 
	 */
	public void cp(String source, String destination, String filter, int flags) {
		cp(new File(source), new File(destination), filter, flags);
	}
	
	/** 
	 * Copies source file to destination file. 
	 * @param source source file to copy. If it's a directory it copies it's members.
	 * @param destination destination file.
	 * @param filter RegEx to filter the files to remove. If null all file will be removed.
	 * @param flags flags for copy {@link Basics#RECURSIVE}, {@link Basics#HIDDEN}, {@link Basics#OVERWRITE}.
	 * @see {@link #ls(String, String, int)} for file selection for copy. 
	 */
	public void cp(File source, File destination, String filter, int flags) {
		if ( !source.isAbsolute() ) source = new File(pwd(), source.getPath());
		if ( !destination.isAbsolute() ) destination = new File(pwd(), destination.getPath());
		if ( !source.exists() ) {
			getErrorHandler().handleError(Diagnostic.ERROR, "Copy source " + source + " doesn't exist.");
			return;
		}
		if ( !destination.exists() && !hasFlag(Basics.MKDIR, flags)) return;
		
		List<File> toCopy = ls(source, filter, flags);
		int sourcePathLength = source.getPath().length();
		for ( File current : toCopy ) {
			if ( current.isDirectory() ) continue;
			String delta = current.getName();
			if ( sourcePathLength < current.getPath().length() ) {
				// compute delta to recreate the directory structure in the destination directory.
				delta = current.getPath().substring(sourcePathLength);
			}
			try {
				copyFile(current, new File(destination, delta), hasFlag(Basics.OVERWRITE, flags));
			} catch (Exception e) {
				getErrorHandler().handleError(Diagnostic.ERROR, e.getClass().getSimpleName() + ": "+ e.getMessage());
			}
		}
	}
	
	/** File copy method. */
	private void copyFile(File sourceFile, File destinationFile, boolean overwrite) throws IOException {
		if ( !destinationFile.exists() || overwrite ) {
			// create destination folder is needed
			destinationFile.getParentFile().mkdirs();
			
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
		    printOut("File " + sourceFile + " copied to " + destinationFile + " (overwrite is " + overwrite + ")");
		}
	}
	
	/**
	 * Removes given filename.
	 * @param file file to remove
	 * @param filter filter to apply to the files
	 * @param flags flags for rm {@link Basics#RECURSIVE}, {@link Basics#HIDDEN}.
	 * @see {@link #ls(String, String, int)} for file selection for remove. 
	 */
	public void rm(String file, String filter, int flags) {
		rm(new File(file), filter, flags);
	}
	
	
	/**
	 * <p>Removes given file.</p> 
	 * <p>A directory which isn't empty won't be removed. Rm uses 
	 * {@link #ls(File, String, int)}  to select files to remove.</p>
	 * <p>To be certain that a directory will be entirely removed use the flags 
	 * {@link Basics#RECURSIVE} and {@link Basics#HIDDEN} (but with care :) ).</p>
	 * @param file file to remove
	 * @param filter filter to apply to the files
	 * @param flags flags for rm {@link Basics#RECURSIVE}, {@link Basics#HIDDEN}.
	 * @see {@link #ls(String, String, int)} for file selection for remove. 
	 */
	public void rm(File file, String filter, int flags) {
		if ( !file.isAbsolute() ) file = new File(pwd(), file.getPath());
		if ( !file.exists() ) {
			getErrorHandler().handleError(Diagnostic.ERROR, "Remove file " + file + " doesn't exist.");
			return;
		}
		List<File> toRemove = ls(file, filter, flags);
		for (File current : toRemove) {
			if ( current.delete() ) {
				printOut("File " + current + " removed.");
			} else {
				printOut("File " + current + " wasn't removed.");
			}
		}
	}
	
	private boolean isHidden(File file) {
		return file.isHidden() || file.getName().startsWith(".");
	}
	
	private boolean select(File file, Pattern filter, int flags) {
		if ( !hasFlag(Basics.HIDDEN, flags) && isHidden(file) ) return false;
		return filter == null || filter.matcher(file.getName() ).find();
	}
	
	/**
	 * Execute given command.
	 * @param command command to execute.
	 * @return true if command was executed, false otherwise.
	 */
	public boolean exec(String command) {
		printOut("Exec: " + command);
		return BatchCommand.execute(command, pwd(), out, err);
	}
	
	/** Gets the error handler. */
	public ErrorHandler getErrorHandler() {
		return errorHandler == null ? defaultErrorHandler : errorHandler;
	}
	
	/** Sets an error handler. */
	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}
	
	public PrintWriter getOut() {
		return out;
	}
	
	public void setOut(PrintWriter out) {
		this.out = out;
	}
	
	public PrintWriter getErr() {
		return err;
	}
	
	public void setErr(PrintWriter err) {
		this.err = err;
	}
	
	public boolean isVerbose() {
		return verbose;
	}
	
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public void printOut(String message) {
		if ( !isVerbose() ) return;
		out.println(message);
		out.flush();
	}

	public void printErr(String message) {
		err.println(message);
		err.flush();
	}
}
