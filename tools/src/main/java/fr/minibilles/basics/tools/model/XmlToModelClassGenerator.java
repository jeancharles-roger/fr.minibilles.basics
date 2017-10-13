package fr.minibilles.basics.tools.model;

import fr.minibilles.basics.generation.java.Java;
import fr.minibilles.basics.generation.java.JavaContentHandler;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.codegen.util.CodeGenUtil;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

public class XmlToModelClassGenerator {


	/** Object that calls generator. */
	private final ModelGenerator caller;
	
	private final boolean recursive;

	public XmlToModelClassGenerator(ModelGenerator caller, boolean recursive) {
		this.caller = caller;
		this.recursive = recursive;
	}

	private List<EClass> concreteClassList(EPackage source) {
		List<EClass> concreteClassList = new ArrayList<EClass>();
		for ( EClassifier classifier : source.getEClassifiers() ) {
			if (classifier instanceof EClass ) {
				EClass klass = (EClass) classifier;
				// adds klass to list if itsn't abstract or an interface
				if ( klass.isAbstract() == false && klass.isInterface() == false ) {
					concreteClassList.add(klass);
				}
			}
		}
		if ( recursive ) {
			for ( EPackage child : source.getESubpackages() ) {
				concreteClassList.addAll(concreteClassList(child));
			}
		}

		return concreteClassList;
	}
	
	private List<EAttribute> attributesToImplement(EClass source) {
		List<EAttribute> allAttributesToImplement = new ArrayList<EAttribute>();

		// searches attributes in source
		for ( EStructuralFeature feature : source.getEAllStructuralFeatures() ) {
			if ( feature.isTransient() == false && feature instanceof EAttribute ) {
				allAttributesToImplement.add((EAttribute) feature);
			}
		}
		return allAttributesToImplement;
	}
	
	private List<EAttribute> singleAttributesToImplement(EClass source) {
		List<EAttribute> singleToImplement = new ArrayList<EAttribute>();
		for ( EAttribute reference : attributesToImplement(source) ) {
			if ( reference.isMany() == false ) singleToImplement.add(reference);
		}
		return singleToImplement;
	}

	private List<EAttribute> manyAttributesToImplement(EClass source) {
		List<EAttribute> manyToImplement = new ArrayList<EAttribute>();
		for ( EAttribute reference : attributesToImplement(source) ) {
			if ( reference.isMany() ) manyToImplement.add(reference);
		}
		return manyToImplement;
	}

	private List<EReference> referencesToImplement(EClass source) {
		List<EReference> allReferencesToImplement = new ArrayList<EReference>();

		// searches attributes in source
		for ( EStructuralFeature feature : source.getEAllStructuralFeatures() ) {
			if ( feature.isTransient() == false && feature instanceof EReference ) {
				allReferencesToImplement.add((EReference) feature);
			}
		}
		
		return allReferencesToImplement;
	}

	private List<EReference> containmentsToImplement(EClass source) {
		List<EReference> allContainmentsToImplement = new ArrayList<EReference>();
		for ( EReference reference : referencesToImplement(source) ) {
			if ( reference.isContainment() ) allContainmentsToImplement.add(reference);
		}
		return allContainmentsToImplement;
	}

	private List<EReference> notContainmentsToImplement(EClass source) {
		List<EReference> allContainmentsToImplement = new ArrayList<EReference>();
		for ( EReference reference : referencesToImplement(source) ) {
			if ( reference.isContainment() == false ) allContainmentsToImplement.add(reference);
		}
		return allContainmentsToImplement;
	}
	
	public void generate(EPackage source, JavaContentHandler content) {

		caller.getDependencyManager().clear();
		String className = "XmlTo" + CodeGenUtil.capName(source.getName());
		// protects class name from imports
		caller.getDependencyManager().getShortName(className);

		// starts class
		content.beginFile(className + ".java");
		content.markImports();
		
		StringBuilder comment = new StringBuilder();
		comment.append("<p>XML reader for model '");
		comment.append(source.getName()); 
		comment.append("'");
		if ( recursive ) {
			comment.append(" and children");
		}
		comment.append("."); 
		comment.append("</p>"); 
		
		content.comment(Java.JAVA_DOC, 0, comment.toString());

		content.beginClass(Java.PUBLIC, className, null, null);


		// adds referencer
		String referencerShortName = caller.getDependencyManager().getShortName("org.xid.basics.sexp.model.Referencer");
		content.beginAttribute(Java.PRIVATE | Java.FINAL, referencerShortName, "referencer");
		content.endAttribute("referencer");
		
		// adds context
		final String sexpToModelShortName = caller.getDependencyManager().getShortName("org.xid.basics.xml.XmlToModel");
		content.beginAttribute(Java.PRIVATE | Java.FINAL, sexpToModelShortName, "context");
		content.endAttribute("context");
		
		// adds xsi name map
		final String mapShortName = caller.getDependencyManager().getShortName("java.util.Map");
		final String hashmapShortName = caller.getDependencyManager().getShortName("java.util.HashMap");
	
		content.beginAttribute(Java.PRIVATE | Java.FINAL, mapShortName + "<Class<?>, String>", "xsiNameMap");
		content.code("new " + hashmapShortName + "<Class<?>, String>()");
		content.endAttribute("xsiNameMap");
		
		// constructors
		content.beginMethod(Java.PUBLIC, null, className, null, 
					new Java.Parameter(Java.NONE, referencerShortName, "referencer")
				);
		content.codeln(0, "this.referencer = referencer;");
		content.codeln(0, "this.context = new "+ sexpToModelShortName +"(referencer);");
		content.codeln(0, "initXsiNameMap();");
		content.endMethod(className);

		final List<EClass> concreteClassList = concreteClassList(source);
		generateXsiNameInitMethods(concreteClassList, content);
		
		generateErrorMethods(content);

		// generates create method
		for ( EClass eClass : concreteClassList ) {
			generateCreateMethod(eClass, content);
		}
		
		// generates main to
		generateMainToMethod(concreteClassList, content);
		
		// generates main create
		generateMainCreateMethod(concreteClassList, content);

		// generates unresolved references method
		final String setShortName = caller.getDependencyManager().getShortName("java.util.Set");
		content.beginMethod(Java.PUBLIC, setShortName + "<String>", "unresolvedReferences", null);
		content.codeln(0, "return context.unresolvedReferences();");
		content.endMethod("unresolvedReferences");
		
		// ends class
		content.endClass(className);

		for ( String oneImport : caller.getDependencyManager().getJavaImports() ) {
			content.import_(Java.NONE, oneImport);
		}
		
		content.endFile(className + ".java");
	
	}
	
	private String xsiName(EClass eClass) {
		StringBuilder name = new StringBuilder(eClass.getName());
		EPackage parent = eClass.getEPackage();
		while ( parent.getESuperPackage() != null ) {
			name.insert(0, ':');
			name.insert(0, parent.getName());
			parent = parent.getESuperPackage();
		}
		return name.toString();
	}
	
	
	private void generateXsiNameInitMethods(List<EClass> concreteClassList, JavaContentHandler content) {
		content.comment(Java.JAVA_DOC, 0, "<p>Initializes class to xsi name map for automatic resolution</p>.");
		content.beginMethod(Java.PRIVATE, "void", "initXsiNameMap", null);
		for ( EClass concreteClass : concreteClassList ) {
			String eclassShortName = caller.getDependencyManager().getShortName(caller.getClassQualifiedName(concreteClass));
			content.codeln(0, "xsiNameMap.put(" + eclassShortName +".class, \"" + xsiName(concreteClass) + "\");");
		}
		content.endMethod("initXsiNameMap");
	}

	private void generateMainToMethod(List<EClass> concreteClassList, JavaContentHandler content) {
		
		caller.getDependencyManager().getShortName("java.io.IOException");
		final String elementShortName = caller.getDependencyManager().getShortName("org.w3c.dom.Element");
		
		content.comment(Java.JAVA_DOC, 0, 
				"<p>Creates an instance of T from given Element. It create an instance.\n" +
						"and tries to resolve references. If a reference isn't resolved, it\n" +
						"throws an IOException.</p>\n"
				);
		content.beginMethod(Java.PUBLIC, "<T> T", "to", "IOException",
				new Java.Parameter(Java.NONE, "Class<T>", "klass"),
				new Java.Parameter(Java.NONE, elementShortName, "element")
			);
		
		
		content.comment(Java.SINGLE_LINE, 0, " calls general create method");
		content.codeln(0, "T result = create(klass, element);");
		content.codeln(0, "");
		
		final String setShortName = caller.getDependencyManager().getShortName("java.util.Set");
		content.comment(Java.SINGLE_LINE, 0, " checks for pending references and sends an exception if needed.");
		content.codeln(0, setShortName + "<String> unresolvedReferences = unresolvedReferences();");
		content.codeln(0, "if ( unresolvedReferences.isEmpty() == false ) {");
		content.codeln(1, "StringBuilder message = new StringBuilder();");
		content.codeln(1, "message.append(\"Unresolved reference(s): \");");
		content.codeln(1, "int length = message.length();");
		content.codeln(1, "for (String reference : unresolvedReferences ) {");
		content.codeln(2, "if ( message.length() > length) {");
		content.codeln(3, "message.append(\", \");");
		content.codeln(2, "}");
		content.codeln(2, "message.append(reference);");
		content.codeln(1, "}");
		content.codeln(1, "message.append(\".\");");
		content.codeln(1, "");
		content.codeln(1, "throw new IOException(message.toString());");
		content.codeln(1, "}");
		
		content.codeln(0, "");
		content.codeln(0, "return result;");
		content.endMethod("to");
		
	}

	private void generateMainCreateMethod(List<EClass> concreteClassList, JavaContentHandler content) {
		
		caller.getDependencyManager().getShortName("java.io.IOException");
		final String elementShortName = caller.getDependencyManager().getShortName("org.w3c.dom.Element");
		
		content.comment(Java.JAVA_DOC, 0, 
		  "<p>Creates an instance of T from given Element. It create an instance.\n" +
		  "and tries to resolve references. If a reference isn't resolved, it\n" +
	      "keeps them stored for later calls to create.</p>\n"
		);
		content.beginMethod(Java.PUBLIC, "<T> T", "create", "IOException",
				new Java.Parameter(Java.NONE, "Class<T>", "klass"),
				new Java.Parameter(Java.NONE, elementShortName, "element")
			);
		
		content.comment(Java.SINGLE_LINE, 0, "Get element type");
		content.codeln(0, "String type = element.getAttribute(\"xsi:type\");");
		content.codeln(0, "if ( type.length() == 0 ) {");
		content.codeln(1, "type = xsiNameMap.get(klass);");
		content.codeln(0, "}");
		
		content.codeln(0, "Object result = null;");
		content.codeln(0, "");
		
		// test all names
		for (EClass eClass : concreteClassList ) {
			caller.getDependencyManager().getShortName(eClass.getName());
			String sexpClassName = xsiName(eClass);
			String createMethodName = "create" + CodeGenUtil.capName(eClass.getName());
			content.codeln(0, "if ( \"" + sexpClassName + "\".equals(type) ) {");
			content.codeln(1, "result = " + createMethodName + "(element);");
			content.codeln(1, "");
			content.codeln(0, "} else");
		}
		// else case
		content.codeln(0, "{");
		content.codeln(1, "StringBuilder message = new StringBuilder();");
		content.codeln(1, "if ( type == null ) {");
		content.codeln(2, "message.append(\"Can't create a '\");");
		content.codeln(2, "message.append(klass.getSimpleName());");
		content.codeln(2, "message.append(\"' instance from '\");");
		content.codeln(2, "message.append(element);");
		content.codeln(2, "message.append(\"'.\");");
		content.codeln(1, "} else {");
		content.codeln(2, "message.append(\"Unknown type '\");");
		content.codeln(2, "message.append(type);");
		content.codeln(2, "message.append(\"'.\");");
		content.codeln(1, "}");
		content.codeln(1, "throw new IOException(message.toString());");
		
		content.codeln(0, "}");
		content.codeln(0, "");
		content.codeln(0, "");
		
		
		// cast result to T
		content.comment(Java.SINGLE_LINE, 0, "return casted result.");
		content.comment(Java.SINGLE_LINE, 0, "If result isn't of klass, it throws a ClassCastException.");
		content.codeln(0, "return result == null ? null : klass.cast(result);");
		
		content.endMethod("create");
		
	}

	private void generateErrorMethods(JavaContentHandler content) {
		caller.getDependencyManager().getShortName("java.io.IOException");
		content.beginMethod(Java.PRIVATE, "IOException", "createUnknownAttributeException", null, 
				new Java.Parameter(Java.NONE, "String", "attribute"),
				new Java.Parameter(Java.NONE, "String", "type")
			);
		
		content.codeln(0, "StringBuilder message = new StringBuilder();");
		content.codeln(0, "message.append(\"Unknown attribute '\");");
		content.codeln(0, "message.append(attribute);");
		content.codeln(0, "message.append(\"' for type '\");");
		content.codeln(0, "message.append(type);");
		content.codeln(0, "message.append(\"'.\");");
		content.codeln(0, "return new IOException(message.toString());");

		content.endMethod("createUnknownAttributeException");
		
	}
	private void generateCreateMethod(EClass eClass, JavaContentHandler content) {
		
		caller.getDependencyManager().getShortName("java.io.IOException");
		final String nodeShortName = caller.getDependencyManager().getShortName("org.w3c.dom.Node");
		final String nodeListShortName = caller.getDependencyManager().getShortName("org.w3c.dom.NodeList");
		final String namedNodeMapListShortName = caller.getDependencyManager().getShortName("org.w3c.dom.NamedNodeMap");
		final String elementShortName = caller.getDependencyManager().getShortName("org.w3c.dom.Element");
		final String attrShortName = caller.getDependencyManager().getShortName("org.w3c.dom.Attr");
		
		
		String className = caller.getDependencyManager().getShortName(caller.getClassQualifiedName(eClass));
		String createMethodName = "create" + CodeGenUtil.capName(eClass.getName());

		// starts method
		content.beginMethod(Java.PRIVATE, className, createMethodName, "IOException", 
					new Java.Parameter(Java.NONE, elementShortName, "element")
				);

		// creates result
		content.codeln(0, className + " result = new " + className + "();");
		content.codeln(0, "");
		
		// reads attributes
		content.codeln(0, "final "+ namedNodeMapListShortName +" attributes = element.getAttributes();");
		content.codeln(0, "for ( int i=0; i<attributes.getLength(); i++ ) {");
		content.codeln(1, "final "+ attrShortName +" attribute = ("+ attrShortName +") attributes.item(i);");
		content.codeln(1, "final String type = attribute.getNodeName();");
		content.codeln(1, "final String value = attribute.getValue();");
		
		for ( EAttribute attribute : singleAttributesToImplement(eClass) ) {
			String attributeName = attribute.getName();
			content.codeln(1, "if ( \""+ attributeName +"\".equals(type) ) {");
			generateAttributeRead(2, attribute, content, "value");
			content.codeln(1, "} else");
		}

		for ( EReference reference : notContainmentsToImplement(eClass) ) {
			String attributeName = reference.getName();
			content.codeln(1, "if ( \""+ attributeName +"\".equals(type) ) {");
			generateReferenceRead(reference, content);
			content.codeln(1, "} else");
		}
		
		// handles old attributes and reference to ignore
		final EAnnotation basicsAnnotation = eClass.getEAnnotation(ModelGenerator.BasicsSource);
		if ( basicsAnnotation != null ) {
			String oldFeatures = basicsAnnotation.getDetails().get(ModelGenerator.BasicsOldAttributes);
			if ( oldFeatures != null ) {
				for ( String oldFeature : oldFeatures.split(",") ) {
					oldFeature = oldFeature.trim();
					
					content.codeln(1, "if (\""+ oldFeature + "\".equals(type) ) {");
					content.comment(Java.SINGLE_LINE, 2, "Ignores old attributes: " + oldFeature);
					content.codeln(1, "} else");
				}
			}
		}
		
		// handles xsi attributes to avoid false errors
		content.codeln(1, "if ( \"xmi:id\".equals(type) ) {");
		content.codeln(2, "referencer.forceReference(result, value);");
		content.codeln(1, "} else");
		content.codeln(1, "if ( type.startsWith(\"xsi:\") || type.startsWith(\"xmlns:\") || type.startsWith(\"xmi:\") ) {");
		content.comment(Java.SINGLE_LINE, 2, "Nothing to do.");
		content.codeln(1, "} else");
		
		// else close
		generateElseCase(1, eClass, className, content);
		
		content.codeln(0, "}");
			
		// loads containments
		final List<EAttribute> manyAttributeList = manyAttributesToImplement(eClass);
		final List<EReference> containmentList = containmentsToImplement(eClass);
		if ( manyAttributeList.size() > 0 || containmentList.size() > 0 ) {
			// pushes objects
			content.codeln(0, "context.push(result);");

			// loads references
			content.codeln(0, "final "+ nodeListShortName +" children = element.getChildNodes();");
			content.codeln(0, "for ( int i=0; i<children.getLength(); i++) {");
			content.codeln(1, "final "+ nodeShortName +" currentNode = children.item(i);");
			content.codeln(1, "if ( currentNode instanceof "+ elementShortName +") {");
			content.codeln(2, "final "+ elementShortName +" child = ("+ elementShortName +") currentNode;");
			content.codeln(2, "final String type = child.getNodeName();");
			content.codeln(0, "");
			
			for ( EAttribute attribute : manyAttributeList ) {
				String referenceName = attribute.getName();
				content.codeln(2, "if ( \""+ referenceName +"\".equals(type) ) {");
				generateAttributeRead(3, attribute, content, "child.getTextContent()");
				content.codeln(2, "} else");
			}

			for ( EReference containment : containmentList ) {
				String referenceName = containment.getName();
				content.codeln(2, "if ( \""+ referenceName +"\".equals(type) ) {");
				generateContainmentRead(containment, content);
				content.codeln(2, "} else");
			}
			
			// handles old attributes and reference to ignore
			if ( basicsAnnotation != null ) {
				String oldContainments = basicsAnnotation.getDetails().get(ModelGenerator.BasicsOldReferences);
				if ( oldContainments != null ) {
					for ( String oldContainement : oldContainments.split(",") ) {
						oldContainement = oldContainement.trim();
						
						content.codeln(2, "if (\""+ oldContainement + "\".equals(type) ) {");
						content.comment(Java.SINGLE_LINE, 2, "Ignores old references: " + oldContainement);
						content.codeln(2, "} else");
					}
				}
			}

			// else close
			generateElseCase(2, eClass, className, content);
			
			content.codeln(1, "}");
			content.codeln(0, "}");
		
			content.codeln(0, "");
			
			// pops object and return result
			content.codeln(0, "context.pop(result);");
		}
		
		content.codeln(0, "return result;");

		// ends method
		content.endMethod(createMethodName);
		
	}
	
	private void generateAttributeRead(int level, EAttribute attribute, JavaContentHandler content, String value) {
		// reads attribute
		final String methodName = setterName(attribute);
		final String readCall = getXmlTypeReadCall(attribute, value);
		content.codeln(level, "result." + methodName + "("+ readCall + ");");
	}

	private void generateReferenceRead(EReference reference, JavaContentHandler content) {
		// reads reference
		final String referenceName = CodeGenUtil.uncapName(reference.getName());;
		if ( reference.isMany() ) {
			content.codeln(2, "for (String reference : value.split(\" \") ) {");
			content.codeln(3, "context.registerReference(result, \""+ referenceName +"\", true, reference);");
			content.codeln(2, "}");
		} else {
			content.codeln(2, "context.registerReference(result, \""+ referenceName +"\", false, value);");
		}
	}
	
	private void generateContainmentRead(EReference reference, JavaContentHandler content) {
		// reads containment reference
		final String typeName = caller.getDependencyManager().getShortName(caller.getClassQualifiedName(reference.getEReferenceType()));
		final String methodName = setterName(reference);
		content.codeln(3, "result." + methodName + "(create("+ typeName + ".class, child));");
	}
	
	private void generateElseCase(int level, EClass eClass, String className, JavaContentHandler content) {
		// handles error creation
		content.codeln(level, "{");
		content.codeln(level+1, "throw new IOException(createUnknownAttributeException(type, \""+ className +"\"));");
		content.codeln(level, "}");
	}
	
	private String setterName(EStructuralFeature feature) {
		String capName = CodeGenUtil.capName(feature.getName());
		String getPrefix = feature.isMany() ? "add" : "set";
		return CodeGenUtil.safeName(getPrefix + capName);
	}

	private String getXmlTypeReadCall(EAttribute attribute, String value) {
		final EClassifier classifier = attribute.getEType();
		final String xmlShortName = caller.getDependencyManager().getShortName("org.xid.basics.xml.XML");
		if (classifier instanceof EEnum ) {
			String shortName = caller.getDependencyManager().getShortName(caller.getTypeName(classifier, null));
			return xmlShortName + ".stringToEnum" +"(" + shortName +".class, " + value + ")";
		} else {
			String xmlType = classifier.getName();
			// removes starting E if needed.
			if ( xmlType.startsWith("E") && xmlType.length() > 1 && Character.isUpperCase(xmlType.charAt(1)) ) {
				xmlType = xmlType.substring(1);
			}

			if ( classifier.getInstanceTypeName() != null ) { 
				xmlType = caller.getDependencyManager().getShortName(classifier.getInstanceTypeName());
				xmlType = CodeGenUtil.capName(xmlType);

			}				
				
			// string type doesn't need conversion
			if ( "String".equals(xmlType) ) {
				return value;
			} else {
				return xmlShortName + ".stringTo" + xmlType +"(" + value + ")";
			}
		}
	}
}
