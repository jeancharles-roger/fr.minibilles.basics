/**
 * 
 */
package fr.minibilles.basics.system;

import fr.minibilles.basics.error.Diagnostic;
import fr.minibilles.basics.error.ErrorHandler;
import fr.minibilles.basics.progress.ActionMonitor;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * <p>Static methods to execute a command and redirects its i/o.</p>
 * @author Jean-Charles Roger
 */
public class BatchCommand {
	
	/**
	 * <p>Executes command and redirect its outputs to outWriter and errWriter.</p>
	 * @param command command to execute.
	 * @param outWriter writer for standard output.
	 * @param errWriter writer for error output.
	 * @return true if command was executed, false otherwise.
	 */
	public static boolean execute(String command, PrintWriter outWriter, PrintWriter errWriter) {
		return execute(command, null, null, outWriter, errWriter, null, null);
	}

	/**
	 * <p>Executes command and redirect its outputs to outWriter and errWriter.</p>
	 * @param command command to execute.
	 * @param workingDir working directory for command.
	 * @param outWriter writer for standard output.
	 * @param errWriter writer for error output.
	 * @return true if command was executed, false otherwise.
	 */
	public static boolean execute(String command, File workingDir, PrintWriter outWriter, PrintWriter errWriter) {
		return execute(command, workingDir, null, outWriter, errWriter, null, null);
	}
	
	/**
	 * <p>Executes command and redirect its outputs to outWriter and errWriter.</p>
	 * @param command command to execute.
	 * @param workingDir working directory for command.
	 * @param outWriter writer for standard output.
	 * @param errWriter writer for error output.
	 * @param progress monitoring call-backs for execution. It only uses 
	 * 		  {@link ActionMonitor#setTaskName(String)} and {@link ActionMonitor#done()}.
	 * @return true if command was executed, false otherwise.
	 */
	public static boolean execute(String command, File workingDir, PrintWriter outWriter, PrintWriter errWriter, ActionMonitor progress) {
		return execute(command, workingDir, null, outWriter, errWriter, progress, null);
	}
	
	/**
	 * <p>Executes command and redirect its outputs to outWriter and errWriter.</p>
	 * @param command command to execute.
	 * @param workingDir working directory for command.
	 * @param env environment variable to add to process. A variable is represented by two 
	 * 		  strings, the first one its name, the second the value.
	 * @param outWriter writer for standard output.
	 * @param errWriter writer for error output.
	 * @param monitor monitoring call-backs for execution. It only uses
	 * 		  {@link ActionMonitor#setTaskName(String)} and {@link ActionMonitor#done()}.
	 * @return true if command was executed, false otherwise.
	 */
	public static boolean execute(String command, File workingDir, String[] env, PrintWriter outWriter, PrintWriter errWriter, ActionMonitor monitor, ErrorHandler errorHandler) {
		
		if ( monitor == null ) monitor = ActionMonitor.empty;
		if ( errorHandler == null ) errorHandler = ErrorHandler.simple;
		
		int status = -1;
		try {
			monitor.setTaskName("Executing " + command);
			Process process = Runtime.getRuntime().exec(command, env, workingDir);
			
			BufferedReader outReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			
			// Redirect writing until process ends.
			while ( isRunning(process) ) {
				if ( outReader.ready() ) {
					outWriter.println(outReader.readLine());
					outWriter.flush();
				}
				if ( errReader.ready() ) {
					errWriter.println(errReader.readLine());
					errWriter.flush();
				}
				try { Thread.sleep(20); } catch (InterruptedException e) { }
			}
			
			// Now flush the buffers
			flushReader(outReader, outWriter);
			flushReader(errReader, errWriter);
			
			status = process.exitValue();
			monitor.done();
		} catch (Exception e) {
			errorHandler.handleError(Diagnostic.ERROR, e.getClass().getSimpleName() + ": " + e.getMessage());
			return false;
		}
		return status == 0;
	}
	
	
	/** @return true if process is running, false otherwise. */
	private static boolean isRunning(Process process) {
		try {
			process.exitValue();
		} catch (IllegalThreadStateException e) {
			return true;
		}
		return false;
	}
	
	private static void flushReader(BufferedReader reader, PrintWriter writer) throws IOException {
		String line = reader.readLine();
		while ( line != null ) {
			writer.println(line);
			line = reader.readLine();
		}
		writer.flush();
	}

}
