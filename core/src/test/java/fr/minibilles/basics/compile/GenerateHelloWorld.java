package fr.minibilles.basics.compile;

import fr.minibilles.basics.generation.java.Java;
import fr.minibilles.basics.generation.java.JavaContentFormatter;
import fr.minibilles.basics.generation.java.JavaContentWriter;
import java.io.File;

/**
 * Generates a HelloWorld with {@link JavaContentWriter}.
 * @author Jean-Charles Roger
 *
 */
public class GenerateHelloWorld {

	public static void main(String[] args) {
		JavaContentWriter writer = new JavaContentWriter(new File("test/java/src"));
		JavaContentFormatter formatter = new JavaContentFormatter(writer);
		
		final String packageName = "hello";
		final String fileName = "Helloworld.java";
		final String className = "Helloworld";
		
		formatter.beginPackage(packageName);
		formatter.beginFile(fileName);
		formatter.beginClass(Java.PUBLIC, className, null, null);
		
		Java.beginMain(formatter);
		formatter.comment(Java.SINGLE_LINE, 0, " Prints Hello world.");
		formatter.codeln(0, "System.out.println(\"Hello world\");");
		Java.endMain(formatter);
		
		formatter.endClass(className);
		formatter.endFile(fileName);
		formatter.endPackage(packageName);
	}

}
