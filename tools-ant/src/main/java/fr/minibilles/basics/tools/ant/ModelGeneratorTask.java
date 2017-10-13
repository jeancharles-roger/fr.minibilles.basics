package fr.minibilles.basics.tools.ant;

import fr.minibilles.basics.generation.Merger;
import fr.minibilles.basics.tools.model.ModelGenerator;
import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Ant task for Model generation.
 * @author Jean-Charles Roger 
 *
 */
public class ModelGeneratorTask extends Task {

	private String source;
	private String destination;
	private String basepackage = "";
	private String encoding = "UTF-8";
	
	private String visitor = "simple";
	private boolean walkerAndCloner = false;
	private boolean replacer = false;
	private boolean boost = false;
	
	private String sexp = "none";
	private String xml = "none";
	private boolean model = false;
	
	private boolean threadsafe = false;
	
	private String generatorClassName;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getBasepackage() {
		return basepackage;
	}

	public void setBasepackage(String basepackage) {
		this.basepackage = basepackage;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	public String getVisitor() {
		return visitor;
	}
	
	public void setVisitor(String visitor) {
		this.visitor = visitor;
	}
	
	public boolean isWalkerAndCloner() {
		return walkerAndCloner;
	}
	
	public void setWalkerAndCloner(boolean walkerAndCloner) {
		this.walkerAndCloner = walkerAndCloner;
	}
	
	public boolean isReplacer() {
		return replacer;
	}
	
	public void setReplacer(boolean replacer) {
		this.replacer = replacer;
	}
	
	public boolean isBoost() {
		return boost;
	}
	
	public void setBoost(boolean boost) {
		this.boost = boost;
	}
	
	public String getSexp() {
		return sexp;
	}
	
	public void setSexp(String sexp) {
		this.sexp = sexp;
	}
	
	public String getXml() {
		return xml;
	}
	
	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public boolean isModel() {
		return model;
	}
	
	public void setModel(boolean model) {
		this.model = model;
	}
	
	public boolean isThreadsafe() {
		return threadsafe;
	}
	
	public void setThreadsafe(boolean threadSafe) {
		this.threadsafe = threadSafe;
	}
	
	public String getGeneratorClassName() {
		return generatorClassName;
	}
	
	public void setGeneratorClassName(String generatorClassName) {
		this.generatorClassName = generatorClassName;
	}
	
	@Override
	public void execute() throws BuildException {
		if ( source == null ) {
			throw new BuildException("Source file isn't set (use the 'source' attribute).");
		}
		if ( destination == null ) {
			throw new BuildException("Destination folder isn't set (use the 'destination' attribute).");
		}
		
		if ( !"none".equals(visitor) && !"simple".equals(visitor) && !"recursive".equals(visitor) ) {
			throw new BuildException("Visitor attribute '"+ visitor +"' is unknown, only 'none', 'simple' and 'recursive' are supported.");
		}
		
		if ( !"none".equals(sexp) && !"simple".equals(sexp) && !"recursive".equals(sexp) ) {
			throw new BuildException("SExp attribute '"+ sexp +"' is unknown, only 'none', 'simple' and 'recursive' are supported.");
		}
		
		if ( !"none".equals(xml) && !"simple".equals(xml) && !"recursive".equals(xml) ) {
			throw new BuildException("XMl attribute '"+ xml +"' is unknown, only 'none', 'simple' and 'recursive' are supported.");
		}
		
		if (  "none".equals(sexp) == false && sexp.equals(visitor) == false ) {
			if ( "none".equals(visitor) ) {
				visitor = sexp;
				System.out.println("SExp option '"+ sexp +"' forces visitor '"+ visitor +"'.");
			} else {
				throw new BuildException("SExp handling needs a visitor of the same type.");
			}
		}
		
		if (  "none".equals(xml) == false && xml.equals(visitor) == false ) {
			if ( "none".equals(visitor) ) {
				visitor = xml;
				System.out.println("Xml option '"+ xml +"' forces visitor '"+ visitor +"'.");
			} else {
				throw new BuildException("Xml handling needs a visitor of the same type.");
			}
		}
		
		ModelGenerator generator = null;
		// creates model generator with given class name (if given).
		if ( generatorClassName != null && generatorClassName.length() > 0 ) {
			try {
				@SuppressWarnings("unchecked")
				final Class<ModelGenerator> generatorClass = (Class<ModelGenerator>) getClass().getClassLoader().loadClass(generatorClassName);
				generator = generatorClass.newInstance();
			} catch (Throwable e) {
				System.err.println("Can't load generator '"+ generatorClassName +"'.");
				generator = null;
			}
		}
		
		// generator wasn't given or failed, set default one.
		if ( generator == null ) {
			generator = new ModelGenerator();
		}
		
		generator.setSourceFile(new File(source));
		generator.setDestinationFile(new File(destination));
		generator.setBasepackage(basepackage);
		generator.setEncoding(encoding);
		generator.setMerger(Merger.DEFAULT);
		if ( visitor != null && visitor.length() > 0 && "none".equals(visitor) == false) {
			generator.setGenerateVisitor(true);
			generator.setGenerateWalkerAndCloner(isWalkerAndCloner());
			generator.setGenerateReplacer(isReplacer());
			if ( "simple".equals(visitor) ) generator.setRecursiveVisitor(false);
			if ( "recursive".equals(visitor) ) generator.setRecursiveVisitor(true);
		} else {
			generator.setGenerateVisitor(false);
			generator.setRecursiveVisitor(false);
			generator.setGenerateWalkerAndCloner(false);
			generator.setGenerateReplacer(false);
		}
		generator.setGenerateBoost(boost);

		if ( sexp != null && sexp.length() > 0 && "none".equals(sexp) == false) {
			generator.setGenerateSExp(true);
			if ( "simple".equals(sexp) ) generator.setRecursiveSerialization(false);
			if ( "recursive".equals(sexp) ) generator.setRecursiveSerialization(true);
		} else {
			generator.setGenerateSExp(false);
		}

		if ( xml != null && xml.length() > 0 && "none".equals(xml) == false) {
			generator.setGenerateXml(true);
			if ( "simple".equals(xml) ) generator.setRecursiveSerialization(false);
			if ( "recursive".equals(xml) ) generator.setRecursiveSerialization(true);
		} else {
			generator.setGenerateXml(false);
		}
		
		generator.setGenerateModel(model);
		if ( threadsafe ) {
			generator.setListImplementation("java.util.Vector");
		}
		
		try {
			generator.generate();
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}
	
}
