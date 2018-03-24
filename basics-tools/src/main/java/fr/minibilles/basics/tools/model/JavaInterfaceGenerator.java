package fr.minibilles.basics.tools.model;

import fr.minibilles.basics.generation.java.Java;
import fr.minibilles.basics.generation.java.Java.Parameter;
import fr.minibilles.basics.generation.java.JavaContentHandler;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.emf.codegen.util.CodeGenUtil;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypeParameter;


/**
 * This generator writes into a {@link Writer} Java code for one Ecore
 * {@link EClass} as an interface for the tinymodel framework.
 *
 * @author Jean-Charles Roger
 */
public class JavaInterfaceGenerator {
	/** Object that calls generator. */
	private final ModelGenerator caller;

	public JavaInterfaceGenerator(ModelGenerator caller) {
		this.caller = caller;
	}
	
	public void generate(EClass source, JavaContentHandler content) {

		final String filename = getInterfaceName(source) + ".java";
		content.beginFile(filename);
		content.markImports();
		generateInterfaceBeginning(source, content);

		Collection<EStructuralFeature> allFeaturesToImplement = caller.featuresToImplement(source);
		for (EStructuralFeature feature : allFeaturesToImplement) {
			
			if (feature.isMany()) {
				generateListFeature(feature, content);
			} else {
				generateSingleFeature(feature, content);
			}
		}

		for (EOperation operation : source.getEOperations()) {
			generateOperation(operation, content);
		}

		if (caller.isGenerateVisitor() ) generateVisitorMethod(source, content);
		generateInterfaceEnding(source, content);	
		generateImports(content);
		content.endFile(filename);

	}

	public void generateOperation(EOperation operation, JavaContentHandler content) {
		String typeName = caller.getTypeName(operation.getEType(), operation.getEGenericType());
		caller.generatedJavaDoc(operation, true, content);
		
		// check 
		String returnType = typeName;
		if (operation.isMany()) {
			final String listShortName = caller.getDependencyManager().getShortName("java.util.List");
			returnType = listShortName + "<" + typeName + ">";
		}

		Parameter[] parameters = new Parameter[operation.getEParameters().size()];
		for (int i=0; i<operation.getEParameters().size() ; i++) {
			EParameter parameter = operation.getEParameters().get(i);
			String parameterTypeName = caller.getTypeName(parameter.getEType(), parameter.getEGenericType());
			parameters[i] = new Parameter(Java.NONE, parameterTypeName, parameter.getName());
		}
		content.beginMethod(Java.PUBLIC, returnType, operation.getName(), null, parameters);
		content.endMethod(operation.getName());
	}

	public void generateImports(JavaContentHandler content) {
		for ( String oneImport : caller.getDependencyManager().getJavaImports() ) {
			content.import_(Java.NONE, oneImport);
		}
	}
	
	private String getInterfaceName(EClass source) {
		return source.getName();
	}

	/** 
	 * <p>
	 * Generates the declaration name for an interface. It generates the parameter types 
	 * declaration added to the given class's name.
	 * @param className the class's name, it may not be the source's name.
	 * @param source the source.
	 * @return a {@link String}, for example <code>TTypeRectangle&lt;T extends TType & Serializable&gt;</code>.
	 */
	private String getDeclarationInterfaceName(String className, EClass source) {
		StringBuilder builder = new StringBuilder();
		builder.append(getInterfaceName(source));
		if ( !source.getETypeParameters().isEmpty() ) {
			builder.append("<");
			boolean firstParameter = true;
			for ( ETypeParameter parameter : source.getETypeParameters() ) {
				if ( !firstParameter ) builder.append(", ");
				firstParameter = false;
				builder.append(parameter.getName());
				
				if ( !parameter.getEBounds().isEmpty() ) {
					builder.append(" extends ");
					boolean firstBound = true;
					for ( EGenericType oneType : parameter.getEBounds() ) {
						if ( !firstBound ) builder.append(" & ");
						firstBound = false;
						builder.append(caller.getTypeName(oneType.getEClassifier(), oneType.getELowerBound()));
					}
				}
			}
			builder.append(">");
		}
		return builder.toString();
	}

	public void generateInterfaceBeginning(EClass source, JavaContentHandler content) {

		caller.generatedJavaDoc(source, true, content);
		
		EClass superClass = null;
		EGenericType genericSuperType = null;
		Map<EClass, EGenericType> implementedInterfaces = new LinkedHashMap<EClass, EGenericType>();
		for (int i = 0; i < source.getESuperTypes().size(); i++) {
			EClass oneSuperClass = source.getESuperTypes().get(i);
			EGenericType oneGenericSuperType = source.getEGenericSuperTypes().get(i);

			if (oneSuperClass.isInterface()) {
				implementedInterfaces.put(oneSuperClass, oneGenericSuperType);
			} else {
				superClass = oneSuperClass;
				genericSuperType = oneGenericSuperType;
			}
		}
		
		String className = getInterfaceName(source);
		// protects class name from imports
		caller.getDependencyManager().getShortName(className);
	
		StringBuilder interfaces = new StringBuilder();
		if ( caller.isGenerateModel() ) {
			String modelInterfaceName = caller.getDependencyManager().getShortName("fr.minibilles.basics.model.ModelObject");
			interfaces.append(modelInterfaceName);
		}
		if ( caller.isGenerateBoost() ) {
			String boostInterfaceName = caller.getDependencyManager().getShortName("fr.minibilles.basics.serializer.BoostObject");
			if ( interfaces.length() > 0 ) interfaces.append(", ");
			interfaces.append(boostInterfaceName);
		}
		if ( superClass != null )  {
			if ( interfaces.length() > 0 ) interfaces.append(", ");
			interfaces.append(caller.getTypeName(superClass, genericSuperType));
		}
		for (Entry<EClass, EGenericType> oneSuperInterfaceEntry : implementedInterfaces.entrySet()) {
			if ( interfaces.length() > 0 ) interfaces.append(", ");
			interfaces.append(caller.getTypeName(oneSuperInterfaceEntry.getKey(), oneSuperInterfaceEntry.getValue()));
		}

		final String classDeclarationName = getDeclarationInterfaceName(className, source);
		content.beginInterface(Java.PUBLIC, classDeclarationName, interfaces.toString());
	}

	public void generateVisitorMethod(EClass source, JavaContentHandler content) {
		String visitorName = caller.getDependencyManager().getShortName(caller.getCurrentVisitorName());
		content.comment(Java.JAVA_DOC, 0, "Visitor accept method.");
		content.beginMethod(Java.PUBLIC, "void", "accept", null, new Parameter(Java.NONE, visitorName, "visitor"));
        content.endMethod("accept");
	}
	
	public void generateInterfaceEnding(EClass source, JavaContentHandler content) {
		content.endInterface(getInterfaceName(source));
	}

	public void generateSingleFeature(EStructuralFeature feature, JavaContentHandler content) {
		final String typeName = caller.getTypeName(feature.getEType(), feature.getEGenericType());
		final String capName = CodeGenUtil.capName(feature.getName());

		caller.generatedJavaDoc(feature, true, content);
		String getPrefix = typeName.equals("boolean") ? "is" : "get";
		String getterName = CodeGenUtil.safeName(getPrefix +capName);
		content.beginMethod(Java.PUBLIC, typeName, getterName, null);
		content.endMethod(getterName);
		
		caller.generatedJavaDoc(feature, true, content);
		String setterName = CodeGenUtil.safeName("set"+capName);
		content.beginMethod(Java.PUBLIC, "void", setterName, null, new Parameter(Java.NONE, typeName, "newValue"));
		content.endMethod(setterName);
	}

	public void generateListFeature(EStructuralFeature feature, JavaContentHandler content) {
		// imports
		caller.getDependencyManager().getShortName("java.util.List");

		String typeName = caller.getTypeName(feature.getEType(), feature.getEGenericType());
		String capFieldName = CodeGenUtil.capName(caller.featureName(feature));
		String capName = CodeGenUtil.capName(feature.getName());

		caller.generatedJavaDoc(feature, true, content);
		content.beginMethod(Java.PUBLIC, "List<" + typeName + ">", "get" + capFieldName, null);
		content.endMethod("get" + capFieldName);
		
		caller.generatedJavaDoc(feature, true, content);
		content.beginMethod(Java.PUBLIC, "int", "get" + capName + "Count", null);
		content.endMethod("get" + capName + "Count");

		caller.generatedJavaDoc(feature, true, content);
		content.beginMethod(Java.PUBLIC, typeName, "get" + capName, null, new Parameter(Java.NONE, "int", "index"));
		content.endMethod("get" + capName);
		
		final String collectionShortname = caller.getDependencyManager().getShortName("java.util.Collection");
		
		caller.generatedJavaDoc(feature, true, content);
		content.beginMethod(Java.PUBLIC, "void", "add" + capName, null, new Parameter(Java.NONE, typeName, "newValue"));
		content.endMethod("add" + capName);

		caller.generatedJavaDoc(feature, true, content);
		content.beginMethod(Java.PUBLIC, "void", "add" + capName, null, new Parameter(Java.NONE, "int", "index"), new Parameter(Java.NONE, typeName, "newValue"));
		content.endMethod("add" + capName);
		
		caller.generatedJavaDoc("Adds a collection of objects in "+ feature.getName() +".", feature, true, content);
		content.beginMethod(Java.PUBLIC, "void", "addAll" + capName, null, new Parameter(Java.NONE, collectionShortname + "<" + typeName + ">", "toAddList"));
		content.endMethod("addAll" + capName);

		caller.generatedJavaDoc(feature, true, content);
		content.beginMethod(Java.PUBLIC, typeName, "set" + capName, null, new Parameter(Java.NONE, "int", "index"), new Parameter(Java.NONE, typeName, "newValue"));
		content.endMethod("set" + capName);
		
		caller.generatedJavaDoc(feature, true, content);
		content.beginMethod(Java.PUBLIC, "void", "remove" + capName, null, new Parameter(Java.NONE, typeName, "value"));
		content.endMethod("remove" + capName);

		caller.generatedJavaDoc(feature, true, content);
		content.beginMethod(Java.PUBLIC, "void", "remove" + capName, null, new Parameter(Java.NONE, "int", "index"));
		content.endMethod("remove" + capName);
		
		if ( feature instanceof EReference ) {
			final EReference reference = (EReference) feature;
			final EReference opposite = reference.getEOpposite();
			if ( !reference.isContainer() && opposite != null ) {
				final String methodOppositeAddName = "add"+capName+JavaClassGenerator.oppositeSuffix;
				final String methodOppositeAddAllName = "addAll"+capName+JavaClassGenerator.oppositeSuffix;
				
				caller.generatedJavaDoc(feature, true, content);
				content.beginMethod(Java.PUBLIC, "void", methodOppositeAddName, null, new Parameter(Java.NONE, typeName, "newValue"));
				content.endMethod(methodOppositeAddName);

				caller.generatedJavaDoc("Adds a collection of objects to " + feature.getName() +" and sets the corresponding "+ opposite.getName() +".", feature, true, content);
				content.beginMethod(Java.PUBLIC, "void", methodOppositeAddAllName, null, new Parameter(Java.NONE, collectionShortname + "<" + typeName + ">", "toAddList"));
				content.endMethod(methodOppositeAddAllName);

				caller.generatedJavaDoc(feature, true, content);
				content.beginMethod(Java.PUBLIC, "void", methodOppositeAddName, null, 
						new Parameter(Java.NONE, "int", "index"),
						new Parameter(Java.NONE, typeName, "newValue"));
				content.endMethod(methodOppositeAddName);
				
				final String methodOppositeSetName = "set"+capName+JavaClassGenerator.oppositeSuffix;
				caller.generatedJavaDoc(feature, true, content);
				content.beginMethod(Java.PUBLIC, typeName, methodOppositeSetName, null, new Parameter(Java.NONE, "int", "index"), new Parameter(Java.NONE, typeName, "newValue"));
				content.endMethod(methodOppositeSetName);
		
				final String methodOppositeRemoveName = "remove"+capName+JavaClassGenerator.oppositeSuffix;
				caller.generatedJavaDoc(feature, true, content);
				content.beginMethod(Java.PUBLIC, "void", methodOppositeRemoveName, null, new Parameter(Java.NONE, typeName, "removed"));
				content.endMethod(methodOppositeRemoveName);
				
				caller.generatedJavaDoc(feature, true, content);
				content.beginMethod(Java.PUBLIC, "void", methodOppositeRemoveName, null, new Parameter(Java.NONE, "int", "index"));
				content.endMethod(methodOppositeRemoveName);
				
			}
		}
	}
}
