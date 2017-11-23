package fr.minibilles.basics.system;

import fr.minibilles.basics.error.Diagnostic;
import fr.minibilles.basics.error.ErrorHandler;
import fr.minibilles.basics.progress.ActionMonitor;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link Make} provides a simple way to handle dependencies to build
 * targets (aka files). It's directly inspired from GNU Make. From a 
 * set of rules (maybe generic) it can walk through dependencies and
 * rebuild only what it's needed.
 *
 * @author Jean-Charles Roger
 */
public class Make {

	/** The list of rules */
	private final List<Rule> rules = new ArrayList<Rule>();
	
	/** Stores the variables */
	private final Map<String, String> variables = new HashMap<String, String>();
	
	/** Current working directory. */
	private File workingFolder;

	/** Error handler. */
	private ErrorHandler errorHandler = ErrorHandler.simple;

	/** Change working dir. */
	public boolean cd(String path) {
		if ( path == null ) return false;
		File tempWd = new File(workingFolder, path);
		if ( !tempWd.exists() || !tempWd.canRead() || !tempWd.isDirectory() ) return false;
		workingFolder = tempWd;
		return true;
	}
	
	/** Get working directory. */
	public File getWorkingDir() {
		return workingFolder;
	}
	
	/** Gets the error handler. */
	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}
	
	/** Sets an error handler. */
	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler == null ? ErrorHandler.simple : errorHandler;
	}
	
	/** Gets the value for a variable. */
	public String getVariable(String name) {
		return variables.get(name);
	}
	
	/** Sets the value for a variable. */
	public void setVariable(String name, String value) {
		variables.put(name, value);
	}
	
	/** Adds a rule to make. */
	public void addRule(Rule rule) {
		rules.add(rule);
	}
	
	private boolean targetExists(String target) {
		return getFile(target).exists();
	}

	/** @return the {@link File} represented by the target. */
	public File getFile(String target) {
		return new File(getWorkingDir(), target);
	}
	
	/** Build given target. */
	public boolean build(String target, PrintWriter out, PrintWriter err, ActionMonitor progress) {
		if ( progress == null ) progress = ActionMonitor.empty;
		progress.begin(100);
		boolean result = internalBuild(target, out, err, progress);
		progress.done();
		return result;
	}
	
	/** Build given target. */
	private boolean internalBuild(String target, PrintWriter out, PrintWriter err, ActionMonitor progress) {
		if ( target == null || target.length() == 0 ) {
			getErrorHandler().handleError(Diagnostic.ERROR, "Target is null or empty.");
			return false;
		}
		for ( Rule rule : rules ) {
			Pattern targetPattern = Pattern.compile(rule.getTarget(this));
			Matcher targetMatcher = targetPattern.matcher(target);
			if ( targetMatcher.matches() ) {
				// found rule, build it
				
				// first instanciates dependencies
				final String[] dependencies = rule.getDependencies(this);
				String [] effectiveDependencies = new String[dependencies.length];
				for ( int i=0; i<dependencies.length; i++ ) {
					String effective = dependencies[i];
					for ( int j=1; j<targetMatcher.groupCount()+1; j++ ) {
						effective = effective.replaceAll("\\\\" + j, targetMatcher.group(j));
					}
					effectiveDependencies[i] = effective;
				}
				
				// call build on dependencies and determines if target has to be built
				boolean needBuild = false;
				
				File targetFile = getFile(target);
				if ( !targetFile.exists() ) needBuild = true;
				for ( String oneDependency : effectiveDependencies ) {
					if ( build(oneDependency, out, err, progress) ) needBuild = true;
					
					if ( !needBuild ) {
						File depFile = getFile(oneDependency);
						if ( !depFile.exists() || depFile.lastModified() > targetFile.lastModified() ) {
							needBuild = true;
						}
					}
				}
				
				if ( needBuild ) {
					// build dependencies
					rule.execute(target, effectiveDependencies, this, out, err, progress);
					return true;
				}
				return false;
			}
		}
		// if target exits.
		if ( targetExists(target) ) return false;
		
		getErrorHandler().handleError(Diagnostic.ERROR, "No rule found for target: " + target);
		return false;
	}
}
