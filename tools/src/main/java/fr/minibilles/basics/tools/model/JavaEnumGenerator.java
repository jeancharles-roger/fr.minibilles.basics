package fr.minibilles.basics.tools.model;

import fr.minibilles.basics.generation.java.Java;
import fr.minibilles.basics.generation.java.JavaContentHandler;
import org.eclipse.emf.codegen.util.CodeGenUtil;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;


/**
 * TODO comments
 *
 * @author Jean-Charles Roger
 */
public class JavaEnumGenerator {
	
	/** Object that calls generator. */
	private final ModelGenerator caller;

	public JavaEnumGenerator(ModelGenerator caller) {
		this.caller = caller;
	}

	public void generate(EEnum source, JavaContentHandler content) {

		final String name = CodeGenUtil.safeName(source.getName());
		content.beginFile(name + ".java");
		content.markImports();
		
		String [] literals = new String[source.getELiterals().size()];
		int [] values = new int[source.getELiterals().size()];
		for ( int i=0; i<source.getELiterals().size(); i++ ) {
			EEnumLiteral eEnumLiteral = source.getELiterals().get(i);
			literals[i] = CodeGenUtil.safeName(eEnumLiteral.getName());
			values[i] = eEnumLiteral.getValue();
		}
		
		caller.generatedJavaDoc(source, true, content);
		content.beginEnum(Java.PUBLIC, name);

		boolean needValue = needValue(values);
		
		
		// creates literals
		content.beginEnumLiterals();
		for ( int i=0; i<literals.length; i++) {
			if ( needValue ) {
				content.enumLiteral(literals[i], Integer.toString(values[i]));
			} else {
				content.enumLiteral(literals[i]);
			}
		}
		content.endEnumLiterals();
		
		// if a value is associated, creates a field to store it.
		if ( needValue ) {
			content.beginAttribute(Java.PRIVATE | Java.FINAL, "int", "value");
			content.endAttribute("value");
			
			content.beginMethod(Java.PRIVATE, null, name, null, 
					new Java.Parameter(Java.NONE, "int", "value")
				);
			content.codeln(0, "this.value = value;");
			content.endMethod(name);
			
			content.beginMethod(Java.PUBLIC, "int", "getValue", null);
			content.codeln(0, "return value;");
			content.endMethod("getValue");
		}
		
		content.endEnum(name);
		
		content.endFile(name + ".java");
	}
	
	/**
	 * @param values to analyze.
	 * @return true if a least two values are different.
	 */
	private boolean needValue(int[] values) {
		if ( values.length == 0 ) return false;
		int firstValue = values[0];
		for ( int i=1; i < values.length; i++ ) {
			if ( values[i] != firstValue) return true;
		}
		return false;
	}
}
