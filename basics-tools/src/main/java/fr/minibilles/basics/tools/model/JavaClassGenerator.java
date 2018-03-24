package fr.minibilles.basics.tools.model;

import fr.minibilles.basics.generation.java.Java;
import fr.minibilles.basics.generation.java.Java.Parameter;
import fr.minibilles.basics.generation.java.JavaContentHandler;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.emf.codegen.util.CodeGenUtil;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypeParameter;


/**
 * This generator writes into a {@link Writer} Java code for one Ecore
 * {@link EClass} for the tinymodel framework.
 * <p>
 * If a the source {@link EClass} contains multi inheritance, the result of the
 * generation won't be correct.
 * <p>
 * TODO: generic type parameter for attributes.
 *
 * @author Jean-Charles Roger
 * 
 */
public class JavaClassGenerator {

	public static final String oppositeSuffix = "AndOpposite";
	
	/** Object that calls generator. */
	private final ModelGenerator caller;

	public JavaClassGenerator(ModelGenerator caller) {
		this.caller = caller;
	}

	public void generate(EClass source, JavaContentHandler content) {

		final String filename = getClassName(source) + ".java";
		content.beginFile(filename);

		content.markImports();
		generateClassBeginning(source, content);

		Collection<EStructuralFeature> allFeaturesToImplement = caller.featuresToImplement(source);

		generateFeatures(allFeaturesToImplement, content);
		generateSmallConstructor(source, allFeaturesToImplement, content);
		if ( caller.isGenerateBoost()) generateBoostConstructor(source, allFeaturesToImplement, content);
		generateFeatureAccessors(allFeaturesToImplement, content);
		
		for (EOperation operation : caller.operationToImplement(source) ) {
			generateOperation(operation, content);
		}

		if ( caller.isGenerateBoost() ) generateWriteBoostMethod(source, allFeaturesToImplement, content);
		if ( caller.isGenerateVisitor() ) generateVisitorMethod(source, content);

		if ( caller.isGenerateModel()) generateModelElementMethod(source, content);
		generateClassEnding(source, content);	
		generateImports(content);
		content.endFile(filename);

	}

	public void generateOperation(EOperation operation, JavaContentHandler content) {
		String typeName = caller.getTypeName(operation.getEType(), operation.getEGenericType());
		caller.generatedJavaDoc(operation, true, content);
		
		final EAnnotation genModelAnnotation = operation.getEAnnotation(ModelGenerator.GenModelSource);
		final EAnnotation basicsModelAnnotation = operation.getEAnnotation(ModelGenerator.BasicsSource);
		
		if ( genModelAnnotation != null && genModelAnnotation.getDetails().containsKey(ModelGenerator.GenModelImport) ) {
			for ( String import_ : genModelAnnotation.getDetails().get(ModelGenerator.GenModelImport).split("\n") ) {
				if ( import_ != null && import_.length() > 0 ) {
					caller.getDependencyManager().getShortName(import_);
				}
			}
		}
		
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

		StringBuilder body = new StringBuilder();

		if (basicsModelAnnotation != null && basicsModelAnnotation.getDetails().containsKey(ModelGenerator.BasicsBody)) {
			// writes method body if it exist in ecore model.
			body.append(basicsModelAnnotation.getDetails().get(ModelGenerator.BasicsBody));
		}

		if (body.length() == 0) {
			if (genModelAnnotation != null && genModelAnnotation.getDetails().containsKey(ModelGenerator.GenModelBody)) {
				// writes method body if it exist in ecore model.
				body.append(genModelAnnotation.getDetails().get(ModelGenerator.GenModelBody));
			}
		}
		
		if (body.length() == 0) {
			body.append("// TODO implement " + operation.getName() + "(...)\n");
			body.append("throw new UnsupportedOperationException();");
		}

		content.codeln(0, body.toString());
		content.endMethod(operation.getName());
	}

	public void generateImports(JavaContentHandler content) {
		for ( String oneImport : caller.getDependencyManager().getJavaImports() ) {
			content.import_(Java.NONE, oneImport);
		}
	}

	private String getClassName(EClass source) {
		if ( caller.isUseGeneratedInterface() && caller.getImplSuffix() != null ) {
			return  source.getName() + caller.getImplSuffix();
		}
		return source.getName();
	}

	/** 
	 * <p>
	 * Generates the declaration name for a class. It generates the parameter types 
	 * declaration added to the given class's name.
	 * @param className the class's name, it may not be the source's name.
	 * @param source the source.
	 * @return a {@link String}, for example <code>TTypeRectangle&lt;T extends TType & Serializable&gt;</code>.
	 */
	private String getDeclarationClassName(String className, EClass source) {
		StringBuilder builder = new StringBuilder();
		builder.append(getClassName(source));
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
	
	public void generateClassBeginning(EClass source, JavaContentHandler content) {

		String className = getClassName(source);
		// protects class name from imports
		caller.getDependencyManager().getShortName(className);
		
		String modelInterfaceName = null;
		String boostInterfaceName = null;
		if ( caller.isUseGeneratedInterface() ) { 
			String qualifiedName = caller.getInterfaceQualifiedName(source);
			if ( caller.isGenerateModel()) {
				modelInterfaceName = caller.getDependencyManager().getShortName(qualifiedName);
			}
		} else {
			if ( caller.isGenerateModel()) {
				modelInterfaceName = caller.getDependencyManager().getShortName("fr.minibilles.basics.model.ModelObject");
			}
			if ( caller.isGenerateBoost() ) {
				boostInterfaceName = caller.getDependencyManager().getShortName("fr.minibilles.basics.serializer.BoostObject");
			}
		}
		
		int flags = Java.PUBLIC;
		caller.generatedJavaDoc(source, true, content);
		content.codeln(0, "");
		
		if (source.isAbstract()) flags |= Java.ABSTRACT;

		
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
		
		StringBuilder interfaces = new StringBuilder();
		if ( modelInterfaceName != null ) {
			interfaces.append(modelInterfaceName);
		}
		if ( boostInterfaceName != null ) {
			if ( interfaces.length() > 0 ) interfaces.append(", ");
			interfaces.append(boostInterfaceName);
		}
		
		for (Entry<EClass, EGenericType> oneSuperInterfaceEntry : implementedInterfaces.entrySet()) {
			if ( interfaces.length() > 0 ) interfaces.append(", ");
			interfaces.append(caller.getTypeName(oneSuperInterfaceEntry.getKey(), oneSuperInterfaceEntry.getValue()));
		}

		final String classDeclarationName = getDeclarationClassName(className, source);
		String superClassTypeName = null;
		if ( superClass != null ) { 
			if ( caller.isUseGeneratedInterface() ) {
				superClassTypeName = caller.getDependencyManager().getShortName(caller.getClassQualifiedName(superClass));
				superClassTypeName += caller.getParameterTypesDeclaration(genericSuperType);
			} else {
				superClassTypeName = caller.getTypeName(superClass, genericSuperType);
			}
		}
		content.beginClass(flags, classDeclarationName, superClassTypeName, interfaces.toString());
	}

	public void generateModelElementMethod(EClass source, JavaContentHandler content) {
		String[] references = null;
		boolean container = false;

		// checks for changeHandler annotations
		EMap<String, String> details = null;
		if (source.getEAnnotation(ModelGenerator.BasicsSource) != null) {
			details = source.getEAnnotation(ModelGenerator.BasicsSource).getDetails();
		}

		if (details != null) {
			// checks if class is a change handler container
			if (details.containsKey(ModelGenerator.BasicsChangeRecorderContainer)) {
				container = Boolean.parseBoolean(details.get(ModelGenerator.BasicsChangeRecorderContainer));
			}
			// checks if details contains the provider references
			if (details.containsKey(ModelGenerator.BasicsChangeRecorderProvider)) {
				references = details.get(ModelGenerator.BasicsChangeRecorderProvider).split(",");
			}
		}

		// if references is null, constructs references from container references
		if (references == null) {
			List<String> referencesList = new ArrayList<String>();
			for (EReference reference : source.getEAllReferences()) {
				if (reference.isContainer()) {
					referencesList.add(reference.getName());
				}
			}
			if (!referencesList.isEmpty()) {
				references = new String[referencesList.size()];
				referencesList.toArray(references);
			}
		}

		
		final String returnType = container ? "ModelChangeRecorder" : "ChangeRecorder";
		caller.getDependencyManager().getShortName("fr.minibilles.basics.model." + returnType);
		
		if (container) {
			// class is change handler container, creates a instance variable.
			caller.generatedJavaDoc(null, true, content);
			content.beginAttribute(Java.PRIVATE, returnType, "changeHandler");
			content.codeln(0, "new ModelChangeRecorder()");
			content.endAttribute("changeHandler");
		}

		// implements getChangeRecorder method.
		caller.generatedJavaDoc(null, true, content);
		content.beginMethod(Java.PUBLIC, returnType, "getChangeRecorder", null);

		if (container) {
			// class is change handler container, returns instance variable.
			content.codeln(0, "return changeHandler;");

		} else {
			if (references != null) {
				// for all references creates the link
				for (String reference : references) {
					String capName = CodeGenUtil.capName(reference);
					content.codeln(0,"if ( get" + capName + "() != null ) {");
					content.codeln(1,"return get" + capName + "().getChangeRecorder();");
					content.codeln(0,"}");
				}
			} else {

				// there is nothing to identify the change handler provider, writes a _TODO
				content.comment(Java.SINGLE_LINE, 0, "TODO implements getChangeRecorder");
			}

			if (caller.getSuperClass(source) != null) {
				// calls the super type changeHandler.
				content.codeln(0,"return super.getChangeRecorder();");
			} else {
				// return the ChangeRecorder stub.
				content.codeln(0,"return ChangeRecorder.Stub;");
			}
		}
		content.endMethod("getChangeRecorder");
	}

	
	private void generateSmallConstructor(EClass source, Collection<EStructuralFeature> allFeaturesToImplement, JavaContentHandler content) {
		caller.generatedJavaDoc(null, true, content);
		content.beginMethod(Java.PUBLIC, null, getClassName(source), null);
		content.endMethod(getClassName(source));
	}
	
	
	private void generateBoostConstructor(EClass source, Collection<EStructuralFeature> allFeaturesToImplement, JavaContentHandler content) {
		caller.generatedJavaDoc(null, true, content);
		content.beginMethod(Java.PROTECTED, null, getClassName(source), null, new Parameter(Java.NONE, "Boost", "boost"));
		if (caller.getSuperClass(source) != null) {
			// calls the super write boost.
			content.codeln(0,"super(boost);");
		} else {
			content.codeln(0, "boost.register(this);");
		}
		for ( EStructuralFeature feature : allFeaturesToImplement ) {
			generateReadBoostCode(feature, content);
		}
		content.endMethod(getClassName(source));
	}

	private void generateWriteBoostMethod(EClass source, Collection<EStructuralFeature> allFeaturesToImplement, JavaContentHandler content) {
		caller.getDependencyManager().getShortName("fr.minibilles.basics.serializer.Boost");
		caller.generatedJavaDoc(null, true, content);
		content.beginMethod(Java.PUBLIC, "void", "writeToBoost", null, new Parameter(Java.NONE, "Boost", "boost"));
		if (caller.getSuperClass(source) != null) {
			// calls the super write boost.
			content.codeln(0,"super.writeToBoost(boost);");
		}
		for ( EStructuralFeature feature : allFeaturesToImplement ) {
			generateWriteBoostCode(feature, content);
		}
		content.endMethod("writeToBoost");
	}
	
	public void generateWriteBoostCode(EStructuralFeature feature, JavaContentHandler content) {
		if ( feature.isTransient() ) {
			content.comment(Java.SINGLE_LINE, 0, feature.getName() + " is transient");
			return;
		}
	
	
		if ( feature.isMany() ) {
			// feature is many
			final String fieldName = caller.featureName(feature);
			final String boostUtil = caller.getDependencyManager().getShortName("fr.minibilles.basics.serializer.BoostUtil");
			if ( feature.getEType() instanceof EDataType ) {
				String typeName = caller.getTypeName(feature.getEType(), feature.getEGenericType(), true);
				content.codeln(0, boostUtil +".write"+ typeName +"Collection(boost, "+ fieldName + ");");
			} else {
				content.codeln(0, boostUtil +".writeObjectCollection(boost, "+ fieldName + ");");
			}
			return;
		}
		
		//feature is single
		String featureName = CodeGenUtil.safeName(feature.getName());
		if ( feature.getEType() instanceof EEnum ) {
			content.codeln(0, "boost.writeEnum(" + featureName + ");");
		} else if ( feature.getEType() instanceof EDataType ) {
			final EDataType dataType = (EDataType) feature.getEType();
			final String instanceTypeName = dataType.getInstanceTypeName();
			if ( instanceTypeName != null && instanceTypeName.endsWith("[]") ) {
				// handle array
				final String boostUtil = caller.getDependencyManager().getShortName("fr.minibilles.basics.serializer.BoostUtil");
				String type = CodeGenUtil.capName(instanceTypeName.substring(0, instanceTypeName.length()-2));
				content.codeln(0, boostUtil +".write"+ type +"Array(boost, "+ featureName + ");");
			} else {
				content.codeln(0, "boost.write" + getBoostSuffixForType(feature.getEType().getName()) + "(" + featureName + ");");
			}
		} else {
			content.codeln(0, "boost.writeObject(" + featureName + ");");
		}
		
	}

	public void generateReadBoostCode(EStructuralFeature feature, JavaContentHandler content) {
		if ( feature.isTransient() ) {
			content.comment(Java.SINGLE_LINE, 0, feature.getName() + " is transient");
			return;
		}
		

		if ( feature.isMany() ) {
			// feature is many
			String typeName = caller.getTypeName(feature.getEType(), feature.getEGenericType(), true);
			String shortTypeName = caller.getTypeName(feature.getEType(), feature.getEGenericType(), false);
			String boostUtil = caller.getDependencyManager().getShortName("fr.minibilles.basics.serializer.BoostUtil");
			String fieldName = caller.featureName(feature);
			if ( feature.getEType() instanceof EDataType ) {
				content.codeln(0, "for ( " + typeName + " oneChild : "+ boostUtil +".read"+ typeName +"Array(boost) ) {");
				content.codeln(1, fieldName + ".add(oneChild);");
				content.codeln(0, "}");
				
			} else {
				content.codeln(0, "for ( " + typeName + " oneChild : "+boostUtil +".readObjectList(boost, " + shortTypeName + ".class) ) {");
				content.codeln(1, fieldName + ".add(oneChild);");
				content.codeln(0, "}");
			}
			return;
		}
		
		//feature is single
		String featureName = CodeGenUtil.safeName(feature.getName());
		if ( feature.getEType() instanceof EEnum ) {
			String typeName = caller.getTypeName(feature.getEType(), feature.getEGenericType());
			content.codeln(0, featureName + " = boost.readEnum(" + typeName+ ".class);");
		} else if ( feature.getEType() instanceof EDataType ) {
			final EDataType dataType = (EDataType) feature.getEType();
			final String instanceTypeName = dataType.getInstanceTypeName();
			if ( instanceTypeName != null && instanceTypeName.endsWith("[]") ) {
				// handle array
				String type = CodeGenUtil.capName(instanceTypeName.substring(0, instanceTypeName.length()-2));
				String boostUtil = caller.getDependencyManager().getShortName("fr.minibilles.basics.serializer.BoostUtil");
				content.codeln(0, featureName + " = "+ boostUtil +".read"+ type +"Array(boost);");
			} else {
				content.codeln(0, featureName + " = boost.read" + getBoostSuffixForType(feature.getEType().getName()) + "();");
			}
		} else {
			String typeName = caller.getTypeName(feature.getEType(), feature.getEGenericType());
			content.codeln(0, featureName + " = boost.readObject(" + typeName + ".class);");
		}
	}

	protected String getBoostSuffixForType(String typeName) {
		if ( typeName.startsWith("E") && typeName.length() > 1 && Character.isUpperCase(typeName.charAt(1)) ) {
			return typeName.substring(1);
		}
		return typeName;
	}

	public void generateVisitorMethod(EClass source, JavaContentHandler content) {
		String visitorName = caller.getDependencyManager().getShortName(caller.getCurrentVisitorName());
		visitorName = caller.getDependencyManager().getShortName(visitorName);
		int style = Java.PUBLIC;
		if ( source.isAbstract() ) style |= Java.ABSTRACT;
		content.comment(Java.JAVA_DOC, 0, "Visitor accept method.");
		content.beginMethod(style, "void", "accept", null, new Parameter(Java.NONE, visitorName, "visitor"));
		if ( !source.isAbstract() ) {
			final String visitName = caller.getVisitMethodName(source);
			content.codeln(0, "visitor."+ visitName +"(this);");
		}
		content.endMethod("accept");
	}
	
	public void generateClassEnding(EClass source, JavaContentHandler content) {
		content.endClass(getClassName(source));
	}

	private void generateFeatures(Collection<EStructuralFeature> allFeaturesToImplement, JavaContentHandler content) {
		for (EStructuralFeature feature : allFeaturesToImplement) {
			
			if (feature.isMany()) {
				generateListFeature(feature, content);
			} else {
				generateSingleFeature(feature, content);
			}
		}
	}

	private void generateFeatureAccessors(Collection<EStructuralFeature> allFeaturesToImplement, JavaContentHandler content) {
		for (EStructuralFeature feature : allFeaturesToImplement) {
			
			if (feature.isMany()) {
				generateListFeatureAccessors(feature, content);
			} else {
				generateSingleFeatureAccessors(feature, content);
			}
		}
	}
	
	public void generateSingleFeature(EStructuralFeature feature, JavaContentHandler content) {
		String typeName = caller.getTypeName(feature.getEType(), feature.getEGenericType());
		String featureName = CodeGenUtil.safeName(feature.getName());

		caller.generatedJavaDoc(feature, true, content);
		int style = Java.PRIVATE;
		if ( feature.isTransient() ) style |= Java.TRANSIENT;
		content.beginAttribute(style, typeName, featureName);
		final String defaultValueLiteral = feature.getDefaultValueLiteral();
		if ( defaultValueLiteral != null && defaultValueLiteral.length() > 0 ) {
			content.code(defaultValueLiteral);
		}
		content.endAttribute(featureName);
	}
	
	public void generateSingleFeatureAccessors(EStructuralFeature feature, JavaContentHandler content) {
		final String typeName = caller.getTypeName(feature.getEType(), feature.getEGenericType());
		final String featureName = CodeGenUtil.safeName(feature.getName());
		final String capName = CodeGenUtil.capName(feature.getName());
		
		final String getPrefix = typeName.equals("boolean") ? "is" : "get";
		final String getterName = CodeGenUtil.safeName(getPrefix + capName);

		caller.generatedJavaDoc("Gets " + feature.getName() +".", feature, true, content);
		content.beginMethod(Java.PUBLIC, typeName, getterName, null);
		content.codeln(0, "return " + featureName + ";");
		content.endMethod(getterName);
		
		String setterName = CodeGenUtil.safeName("set" + capName);
		
		caller.generatedJavaDoc("Sets " + feature.getName() +".", feature, true, content);
		content.beginMethod(Java.PUBLIC, "void", setterName, null, new Parameter(Java.NONE, typeName, "newValue"));
		content.codeln(0, "if (" + inequalityTest(featureName, "newValue", typeName) + ") {");
		if ( caller.isGenerateModel() ) {
			content.codeln(1, "getChangeRecorder().recordChangeAttribute(this, \"" + feature.getName() + "\", this." + featureName + ");");
		}
		content.codeln(1, "this." + featureName + "= newValue;");
		content.codeln(0, "}");
		content.endMethod(setterName);

		if ( feature instanceof EReference ) {
			final EReference reference = (EReference) feature;
			final EReference opposite = reference.getEOpposite();
			if ( !reference.isContainer() && opposite != null ) {
				final String methodOppositeName = CodeGenUtil.safeName("set" + capName + oppositeSuffix);
				caller.generatedJavaDoc("Sets " + feature.getName() + " and sets the corresponding "+ opposite.getName() +".", feature, true, content);
				content.beginMethod(Java.PUBLIC, "void", methodOppositeName, null, new Parameter(Java.NONE, typeName, "newValue"));
				unsetOpposite(opposite, 0, featureName, content);
				setOpposite(opposite, 0, "newValue", content);
				content.codeln(0, setterName + "(newValue);");
				content.endMethod(methodOppositeName);
				
			}
		}
	}
	
	private String inequalityTest(String left, String right, String typeName) {
		if ( Character.isLowerCase(typeName.charAt(0)) ) {
			// type name first letter is lower, assumes that is a primitive type
			return left + " != " + right;
		} else {
			// assumes it's a class
			return left + " == null ? " + right + " != null : (" + left + ".equals("+ right +") == false)";
		}
	}

	public void generateListFeature(EStructuralFeature feature, JavaContentHandler content) {
		// imports
		caller.getDependencyManager().getShortName("java.util.List");
		String list = caller.getDependencyManager().getShortName(caller.getListImplementation());
		caller.getDependencyManager().getShortName("java.util.Collections");

		String typeName = caller.getTypeName(feature.getEType(), feature.getEGenericType());
		String fieldName = caller.featureName(feature);

		caller.generatedJavaDoc(feature.getName() + " field.", feature, true, content);
		int style = Java.PRIVATE | Java.FINAL;
		if ( feature.isTransient() ) style |= Java.TRANSIENT;
		content.beginAttribute(style, "List<" + typeName + ">", fieldName);
		content.code("new "+list +"<" + typeName + ">()");
		content.endAttribute(fieldName);

	}

	public void generateListFeatureAccessors(EStructuralFeature feature, JavaContentHandler content) {
		// imports
		caller.getDependencyManager().getShortName("java.util.List");
		caller.getDependencyManager().getShortName("java.util.Collections");
		
		String typeName = caller.getTypeName(feature.getEType(), feature.getEGenericType());
		String fieldName = caller.featureName(feature);
		String capFieldName = CodeGenUtil.capName(fieldName);
		String capName = CodeGenUtil.capName(feature.getName());
		
		caller.generatedJavaDoc("Returns all values of " + feature.getName() +".", feature, true, content);
		content.beginMethod(Java.PUBLIC, "List<" + typeName + ">", "get" + capFieldName, null);
		content.codeln(0, "return Collections.unmodifiableList(" + fieldName + ");");
		content.endMethod("get" + capFieldName);
		
		caller.generatedJavaDoc("Gets " + feature.getName() + " object count.",feature, true, content);
		content.beginMethod(Java.PUBLIC, "int", "get" + capName + "Count", null);
		content.codeln(0, "return " + fieldName + ".size();");
		content.endMethod("get" + capName + "Count");
		
		caller.generatedJavaDoc("Gets " + feature.getName() + " at given index.",feature, true, content);
		content.beginMethod(Java.PUBLIC, typeName, "get" + capName, null, new Parameter(Java.NONE, "int", "index"));
		content.codeln(0, "if ( index < 0 || index >= get" + capName + "Count() ) { return null; }");
		content.codeln(0, "return " + fieldName + ".get(index);");
		content.endMethod("get" + capName);
		
		final String collectionShortname = caller.getDependencyManager().getShortName("java.util.Collection");
		
		caller.generatedJavaDoc("Adds an object in " + feature.getName() + ".", feature, true, content);
		content.beginMethod(Java.PUBLIC, "void", "add" + capName, null, new Parameter(Java.NONE, typeName, "newValue"));
		content.codeln(0, "add" + capName + "(get" + capName + "Count(), newValue);");
		content.endMethod("add" + capName);
		
		caller.generatedJavaDoc("Adds an object in " + feature.getName() + " at given index.", feature, true, content);
		content.beginMethod(Java.PUBLIC, "void", "add" + capName, null, new Parameter(Java.NONE, "int", "index"), new Parameter(Java.NONE, typeName, "newValue"));
		if ( caller.isGenerateModel() ) {
			content.codeln(0, "getChangeRecorder().recordAddObject(this, \"" + feature.getName() + "\", index);");
		}
		content.codeln(0, fieldName + ".add(index, newValue);");

		content.endMethod("add" + capName);
		
		caller.generatedJavaDoc("Replaces an object in " + feature.getName() + " at given index. Returns the old value.", feature, true, content);
		content.beginMethod(Java.PUBLIC, typeName, "set" + capName, null, new Parameter(Java.NONE, "int", "index"), new Parameter(Java.NONE, typeName, "newValue"));
		if ( caller.isGenerateModel() ) {
			content.codeln(0, typeName + " oldValue = " + fieldName + ".set(index, newValue);");
			content.codeln(0, "getChangeRecorder().recordRemoveObject(this, \"" + feature.getName() + "\", index, oldValue);");
			content.codeln(0, "getChangeRecorder().recordAddObject(this, \"" + feature.getName() + "\", index);");
			content.codeln(0, "return oldValue;");
		} else {
			content.codeln(0, "return " + fieldName + ".set(index, newValue);");
		}
		content.endMethod("set" + capName);
		
		caller.generatedJavaDoc("Adds a collection of objects in "+ feature.getName() +".", feature, true, content);
		content.beginMethod(Java.PUBLIC, "void", "addAll" + capName, null, new Parameter(Java.NONE, collectionShortname + "<" + typeName + ">", "toAddList"));
		content.codeln(0, "for (" + typeName + " newValue : toAddList) {");
		content.codeln(1, "add" + capName + "(get" + capName + "Count(), newValue);");
		content.codeln(0, "}");

		content.endMethod("addAll" + capName);
		
		caller.generatedJavaDoc("Removes given object from " + feature.getName() +".", feature, true, content);
		content.beginMethod(Java.PUBLIC, "void", "remove" + capName, null, new Parameter(Java.NONE, typeName, "value"));
		content.codeln(0, "int index = " + fieldName + ".indexOf(value);");
		content.codeln(0, "if (index >= 0 ) {");
		content.codeln(1,  "remove" + capName + "(index);");
		content.codeln(0, "}");
		content.endMethod("remove" + capName);
		
		
		caller.generatedJavaDoc("Removes object from " + feature.getName() +" at given index.", feature, true, content);
		content.beginMethod(Java.PUBLIC, "void", "remove" + capName, null, new Parameter(Java.NONE, "int", "index"));
		if ( caller.isGenerateModel() ) {
			content.codeln(0, typeName + " oldValue = " + fieldName + ".get(index);");
			content.codeln(0, "getChangeRecorder().recordRemoveObject(this, \"" + feature.getName() + "\", index, oldValue);");
		}
		content.codeln(0, fieldName + ".remove(index);");
		content.endMethod("remove" + capName);
		
		if ( feature instanceof EReference ) {
			final EReference reference = (EReference) feature;
			final EReference opposite = reference.getEOpposite();
			if ( !reference.isContainer() && opposite != null ) {
				final String methodOppositeAddName = "add"+capName+oppositeSuffix;
				final String methodOppositeAddAllName = "addAll"+capName+oppositeSuffix;
				
				caller.generatedJavaDoc("Adds object to " + feature.getName() +" and sets the corresponding "+ opposite.getName() +".", feature, true, content);
				content.beginMethod(Java.PUBLIC, "void", methodOppositeAddName, null, new Parameter(Java.NONE, typeName, "newValue"));
				content.codeln(0, "add"+ capName + "(newValue);");
				setOpposite(opposite, 0, "newValue", content);
				content.endMethod(methodOppositeAddName);
				
				caller.generatedJavaDoc("Adds a collection of objects to " + feature.getName() +" and sets the corresponding "+ opposite.getName() +".", feature, true, content);
				content.beginMethod(Java.PUBLIC, "void", methodOppositeAddAllName, null, new Parameter(Java.NONE, collectionShortname + "<" + typeName + ">", "toAddList"));
				content.codeln(0, "for (" + typeName + " newValue : toAddList) {");
				content.codeln(1, methodOppositeAddName + "(get" + capName + "Count(), newValue);");
				content.codeln(0, "}");
				content.endMethod(methodOppositeAddAllName);
				
				caller.generatedJavaDoc("Adds object to " + feature.getName() +" at given index and sets the corresponding "+ opposite.getName() +".", feature, true, content);
				content.beginMethod(Java.PUBLIC, "void", methodOppositeAddName, null, 
						new Parameter(Java.NONE, "int", "index"),
						new Parameter(Java.NONE, typeName, "newValue")
					);
				content.codeln(0, "add"+ capName + "(index, newValue);");
				setOpposite(opposite, 0, "newValue", content);
				content.endMethod(methodOppositeAddName);

				final String methodOppositeSetName = "set"+capName+oppositeSuffix;
				caller.generatedJavaDoc("Replaces an object in " + feature.getName() + " at given index. Returns the old value.", feature, true, content);
				content.beginMethod(Java.PUBLIC, typeName, methodOppositeSetName, null, new Parameter(Java.NONE, "int", "index"), new Parameter(Java.NONE, typeName, "newValue"));
				content.codeln(0, typeName + " oldValue = " + fieldName + ".set(index, newValue);");
				if ( caller.isGenerateModel() ) {
					content.codeln(0, "getChangeRecorder().recordRemoveObject(this, \"" + feature.getName() + "\", index, oldValue);");
					content.codeln(0, "getChangeRecorder().recordAddObject(this, \"" + feature.getName() + "\", index);");
				}
				setOpposite(opposite, 0, "newValue", content);
				content.codeln(0, "return oldValue;");
				content.endMethod(methodOppositeSetName);
				
				final String methodOppositeRemoveName = "remove"+capName+oppositeSuffix;

				caller.generatedJavaDoc("Removes object from " + feature.getName() +" and resets the corresponding "+ opposite.getName() +".", feature, true, content);
				content.beginMethod(Java.PUBLIC, "void", methodOppositeRemoveName, null, new Parameter(Java.NONE, typeName, "removed"));
				content.codeln(0, "remove"+ capName + "(removed);");
				unsetOpposite(opposite, 0, "removed", content);
				content.endMethod(methodOppositeRemoveName);
				
				caller.generatedJavaDoc("Removes object at given index from " + feature.getName() +" and resets the corresponding "+ opposite.getName() +".", feature, true, content);
				content.beginMethod(Java.PUBLIC, "void", methodOppositeRemoveName, null, new Parameter(Java.NONE, "int", "index"));
				content.codeln(0, typeName + " removed = " + fieldName + ".get(index);");
				content.codeln(0, "remove"+ capName + "(index);");
				unsetOpposite(opposite, 0, "removed", content);
				content.endMethod(methodOppositeRemoveName);
			}
		}
	}
	
	public void setOpposite(EReference eOpposite, int level, String variable, JavaContentHandler content) {
		String capName = CodeGenUtil.capName(eOpposite.getName());
		content.codeln(level, "if ( " + variable + " != null ) {");
		if (eOpposite.isMany()) {
			content.codeln(level+1, variable + ".add" + capName + "(this);");
		} else {
			content.codeln(level+1, variable + ".set" + capName + "(this);");
		}
		content.codeln(level, "}");
	}

	public void unsetOpposite(EReference eOpposite, int level, String variable, JavaContentHandler content) {
		String capName = CodeGenUtil.capName(eOpposite.getName());
		content.codeln(level, "if ( " + variable + " != null ) {");
		if (eOpposite.isMany()) {
			content.codeln(level+1, variable + ".remove" + capName + "(this);");
		} else {
			content.codeln(level+1,  variable + ".set" + capName + "(null);");
		}
		content.codeln(level, "}");
	}

}
