package fr.minibilles.basics.compile;

import fr.minibilles.basics.progress.ActionMonitor;
import fr.minibilles.basics.system.Make;
import fr.minibilles.basics.system.Rule;
import java.io.PrintWriter;
import org.eclipse.jdt.core.compiler.batch.BatchCompiler;

public class JavaCompilerRule implements Rule {

	public static final String JAVA_SOURCE_DIR = "JAVA_SOURCE_DIR";
	public static final String JAVA_CLASS_DIR = "JAVA_CLASS_DIR";
	
	public JavaCompilerRule() {
	}

	public String getTarget(Make make) {
		StringBuilder builder = new StringBuilder();
		final String javaClassDir = make.getVariable(JAVA_CLASS_DIR);
		if ( javaClassDir != null ) {
			builder.append("");
			builder.append(javaClassDir);
			builder.append("/");
		}
		builder.append("(([\\d\\w]+/)*[\\d\\w]+)\\.class");
		return builder.toString();
	}
	
	public String[] getDependencies(Make make) {
		StringBuilder builder = new StringBuilder();
		final String javaSourceDir = make.getVariable(JAVA_SOURCE_DIR);
		if ( javaSourceDir != null ) {
			builder.append(javaSourceDir);
			builder.append("/");
		}
		builder.append("\\1.java");
		return new String [] { builder.toString() };
	}
	
	public int execute(String effectiveTarget, String[] effectiveDependencies, Make make, PrintWriter out, PrintWriter err, ActionMonitor progress) {
		StringBuilder commandBuilder = new StringBuilder();
		final String javaClassDir = make.getVariable(JAVA_CLASS_DIR);
		if ( javaClassDir != null) {
			commandBuilder.append("-d ");
			commandBuilder.append(make.getFile(javaClassDir).getAbsolutePath());
			commandBuilder.append(" ");
		}

		for ( String oneDep : effectiveDependencies) {
			commandBuilder.append(make.getFile(oneDep).getAbsolutePath());
			commandBuilder.append(' ');
		}
		
		System.out.println("Cmd: " + commandBuilder.toString());
		BatchCompiler.compile(commandBuilder.toString(), out, err, null);
		return 0;
	}
	
	public static void testJava() {
		
		Make make = new Make();
		make.setVariable(JavaCompilerRule.JAVA_CLASS_DIR, "bin");
		make.setVariable(JavaCompilerRule.JAVA_SOURCE_DIR, "src");
		
		make.cd("test/java");
		
		make.addRule(new JavaCompilerRule());
		
		make.build("bin/hello/Helloworld.class", new PrintWriter(System.out), new PrintWriter(System.err), null);
	}

	public static void main(String[] args) {
		testJava();
	}

}
