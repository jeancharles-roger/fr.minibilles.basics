package fr.minibilles.basics.generation.java;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;



/**
 * <p>
 * This class handles the generation dependencies. It includes:
 * </p>
 * 
 * @author Jean-Charles Roger
 */
public class DependencyManager {

	/**
	 * <p>
	 * Set of all imports.
	 * </p>
	 */
	protected final Set<String> imports = new TreeSet<String>();

	/**
	 * <p>
	 * Short name map for import.
	 * </p>
	 */
	protected final Map<String, String> shortNameImport = new HashMap<String, String>();

	public DependencyManager() {
		initialize();
	}

	protected void initialize() {
		// initializes classic import of some java.lang.* classes that may cause problems
		getShortName("java.lang.String");
		getShortName("java.lang.Integer");
	}
	
	public void clear() {
		imports.clear();
		shortNameImport.clear();
		initialize();
	}
	
	/**
	 * <p>
	 * Returns the corresponding short name for given fully qualified name. The first a qualified name is given, it registers
	 * it in {@link #imports} to add all imports at the end of the template generation process.
	 * </p>
	 * 
	 * @param qualifiedName concerned classifier.
	 * @return the short name of the Java Class.
	 */
	public String getShortName(String qualifiedName) {
		if (!shortNameImport.containsKey(qualifiedName)) {

			//if exists a $ into the qualified name it's internal class.
			qualifiedName = qualifiedName.replace("$", ".");

			int dotIndex = qualifiedName.lastIndexOf('.');
			if (dotIndex < 0) {
				// if qualified name doesn't contain '.', registers the qualified name as shortname.
				shortNameImport.put(qualifiedName, qualifiedName);
			} else {
				String shortName = qualifiedName.substring(dotIndex + 1);
				if (shortNameImport.containsValue(shortName)) {
					// shortname already have been registered, registers the qualified name as shortname.
					shortNameImport.put(qualifiedName, qualifiedName);
				} else {
					// normal case, registers the shortname and imports.
					if ( !qualifiedName.startsWith("java.lang") ) {
						imports.add(qualifiedName);
					}
					shortNameImport.put(qualifiedName, shortName);
				}
			}
		}
		return shortNameImport.get(qualifiedName);

	}

	/**
	 * <p>
	 * Retrieves the list of imports separated by ' '.
	 * </p>
	 * 
	 * @return a list of qualified name to import as
	 */
	public String getSpaceSeparedImports() {
		StringBuffer buffer = new StringBuffer();
		for (String qualifiedName : imports) {
			buffer.append(qualifiedName + " ");
		}
		return buffer.toString();
	}

	/**
	 * <p>
	 * Retrieves the list of imports as Java imports.
	 * </p>
	 * @return a set of import declaration.
	 */
	public String[] getJavaImports() {
		String[] result = new String[imports.size()];
		return imports.toArray(result);
	}
}
