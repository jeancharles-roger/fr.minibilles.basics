package fr.minibilles.basics.tools.model;

import fr.minibilles.basics.generation.java.DependencyManager;
import fr.minibilles.basics.generation.java.Java;
import fr.minibilles.basics.generation.java.JavaContentFormatter;
import fr.minibilles.basics.generation.java.JavaContentHandler;
import fr.minibilles.basics.generation.java.JavaContentWriter;
import fr.minibilles.basics.tools.GeneratorEntryPoint;
import java.io.File;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Stack;
import org.eclipse.emf.codegen.util.CodeGenUtil;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

/**
 * Handles the generation of a whole ecore file. Not all ecore construction are
 * implemented.
 *
 * @author Jean-Charles Roger
 * 
 */
public class ModelGenerator extends GeneratorEntryPoint {

	private final String NL = System.getProperty("line.separator");
	
	public static final String GenModelSource = "http://www.eclipse.org/emf/2002/GenModel";
	public static final String GenModelDocumentation = "documentation";
	public static final String GenModelBody = "body";
	public static final String GenModelImport = "import";

	public static final String BasicsSource = "http://Basics/EcoreAnnotations";
	public static final String BasicsChangeRecorderContainer = "changeHandlerContainer";
	public static final String BasicsChangeRecorderProvider = "changeHandlerProvider";
	
	public static final String BasicsBody = "body";
	
	public static final String BasicsOldAttributes = "oldAttributes";
	public static final String BasicsOldReferences = "oldReferences";

	/** Base package name for generated classes */
	protected String basepackage = "";
	private String implPackage = null;
	private String implSuffix = "Impl";
	protected boolean useGeneratedInterface = false;

	protected boolean useGeneratedAnnotation = false;

	protected boolean generateVisitor = true;
	protected boolean generateWalkerAndCloner = false;
	protected boolean generateReplacer = false;
	
	/** If visitor is recursive, only one visitor is generated at root package. */
	protected boolean recursiveVisitor = false;
	
	protected boolean generateBoost = false;
	protected boolean generateSExp = false;
	protected boolean generateXml = false;
	
	/** If visitor is recursive, only one sexp handler is generated at root package. */
	protected boolean recursiveSerialization = false;

	protected boolean generateModel = true;

	protected String listImplementation = "java.util.ArrayList";
	
	protected final DependencyManager dependencyManager = new DependencyManager();

	protected JavaContentHandler javaContent;

	private Stack<String> visitorNameStack = new Stack<String>();
	
	public String getBasepackage() {
		return basepackage;
	}
	 
	public void setBasepackage(String basepackage) {
		this.basepackage = basepackage;
	}
	
	public String getImplPackage() {
		return implPackage;
	}

	public void setImplPackage(String implPackage) {
		this.implPackage = implPackage;
	}

	public boolean isUseGeneratedAnnotation() {
		return useGeneratedAnnotation;
	}
	
	public void setUseGeneratedAnnotation(boolean useGeneratedAnnotation) {
		this.useGeneratedAnnotation = useGeneratedAnnotation;
	}
	
	public boolean isGenerateVisitor() {
		return generateVisitor;
	}
	
	public void setGenerateVisitor(boolean generateVisitor) {
		this.generateVisitor = generateVisitor;
	}
	
	public boolean isRecursiveVisitor() {
		return recursiveVisitor;
	}
	
	public void setRecursiveVisitor(boolean recursiveVisitor) {
		this.recursiveVisitor = recursiveVisitor;
	}
	
	public boolean isGenerateWalkerAndCloner() {
		return generateWalkerAndCloner;
	}
	
	public void setGenerateWalkerAndCloner(boolean generateWalkerAndCloner) {
		this.generateWalkerAndCloner = generateWalkerAndCloner;
	}
	
	public boolean isGenerateReplacer() {
		return generateReplacer;
	}
	
	public void setGenerateReplacer(boolean generateReplacer) {
		this.generateReplacer = generateReplacer;
	}
	
	public boolean isGenerateBoost() {
		return generateBoost;
	}
	
	public void setGenerateBoost(boolean generateBoost) {
		this.generateBoost = generateBoost;
	}
	
	public boolean isGenerateSExp() {
		return generateSExp;
	}
	
	public void setGenerateSExp(boolean generateSExp) {
		this.generateSExp = generateSExp;
	}
	
	public boolean isGenerateXml() {
		return generateXml;
	}
	
	public void setGenerateXml(boolean generateXml) {
		this.generateXml = generateXml;
	}
	
	public boolean isRecursiveSerialization() {
		return recursiveSerialization;
	}
	
	public void setRecursiveSerialization(boolean recursiveSerialization) {
		this.recursiveSerialization = recursiveSerialization;
	}
	
	public boolean isGenerateModel() {
		return generateModel;
	}
	
	public void setGenerateModel(boolean generateModel) {
		this.generateModel = generateModel;
	}
	
	public String getImplSuffix() {
		return implSuffix;
	}
	
	public void setImplSuffix(String implSuffix) {
		this.implSuffix = implSuffix;
	}
	
	public boolean isUseGeneratedInterface() {
		return useGeneratedInterface;
	}
	
	public void setUseGeneratedInterface(boolean useGeneratedInterface) {
		this.useGeneratedInterface = useGeneratedInterface;
	}
	
	public String getListImplementation() {
		return listImplementation;
	}
	
	public void setListImplementation(String listImplementation) {
		this.listImplementation = listImplementation;
	}
	
	public void preGeneration() throws Exception {
		// does nothing.
	}
	
	public void postGeneration() throws Exception {
		// does nothing.
	}
	
	public void generate() throws Exception {
		preGeneration();
		
		URI modelURI = URI.createFileURI(getSourceFile().getCanonicalPath());
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		Resource resource = resourceSet.createResource(modelURI);
		resource.load(null);

		System.out.println("--------------------------------------------------");
		System.out.println("Resource " + modelURI + " is loaded.");

		JavaContentWriter javaWriter = new JavaContentWriter(getDestinationFile());
		javaContent = new JavaContentFormatter(javaWriter);

		javaContent.beginPackage(getBasepackage());
		for (EObject currentObject : resource.getContents()) {
			if (currentObject.eClass().getClassifierID() == EcorePackage.EPACKAGE) {
				final EPackage ePackage = (EPackage) currentObject;
				generateEPackage(true, ePackage);
			}
		}
		javaContent.endPackage(getBasepackage());

		postGeneration();
		System.out.println("Generation ends.");
	}
	
	/** 
	 * @return Java qualified name for given {@link EPackage}
	 */
	public String getQualifiedName(EPackage oneEpackage) {
		StringBuilder name =  new StringBuilder();
		
		EPackage currentPackage = oneEpackage;
		while (currentPackage!=null) {
			if ( name.length() > 0 ) name.insert(0, '.');
			name.insert(0, currentPackage.getName());
			
			currentPackage = currentPackage.getESuperPackage();
		}
		if ( getBasepackage() != null && getBasepackage().length() > 0 ) {
			name.insert(0, '.');
			name.insert(0,getBasepackage());
		}
		return name.toString();
	}

	/** 
	 * @return Java Class qualified name for given {@link EClass}
	 */
	public String getClassQualifiedName(EClass oneEClass) {
		StringBuilder name =  new StringBuilder();
		name.append(getQualifiedName(oneEClass.getEPackage()));
		name.append('.');
		if ( isUseGeneratedInterface() && getImplPackage() != null ) {
			name.append(getImplPackage()); 
			name.append('.');
		}
		name.append(oneEClass.getName());
		if ( isUseGeneratedInterface() && getImplSuffix() != null ) name.append(getImplSuffix()); 
		return name.toString();
	}
	
	/** 
	 * @return Java Interface qualified name for given {@link EClass}
	 */
	public String getInterfaceQualifiedName(EClass oneEClass) {
		StringBuilder name =  new StringBuilder();
		name.append(getQualifiedName(oneEClass.getEPackage()));
		name.append('.');
		name.append(oneEClass.getName());
		return name.toString();
	}
	
	/** 
	 * @return Java qualified name for given {@link EEnum}
	 */
	public String getQualifiedName(EEnum oneEEnum) {
		StringBuilder name =  new StringBuilder();
		name.append(getQualifiedName(oneEEnum.getEPackage()));
		name.append('.');
		name.append(oneEEnum.getName());
		return name.toString();
	}
	
	public void generateEPackage(boolean root, EPackage oneEPackage) throws Exception {
		System.out.println("Generating package " + oneEPackage.getName());
		
		String packagePath = oneEPackage.getName();
		javaContent.beginPackage(packagePath);

		generateEPackageDocumentation(oneEPackage);
		
		// computes if visitor should be generated for this package.
		// Cases where it should be generated (in all isGenerateVisitor() must be true):
		// - Package is root and isRecursiveVisitor() is true,
		// - What ever package is root or not, isRecursiveVisitor() is false and package isn't empty.
		boolean visitorForPackage = false;
		if ( root ) {
			visitorForPackage = isGenerateVisitor() && (isRecursiveVisitor() || !oneEPackage.getEClassifiers().isEmpty());
		} else {
			visitorForPackage = isGenerateVisitor() && !isRecursiveVisitor() && !oneEPackage.getEClassifiers().isEmpty(); 
		}
		
		if (visitorForPackage ) {
			visitorNameStack.push(getVisitorQualifiedName(oneEPackage));
			generateEPackageVisitor(oneEPackage, isRecursiveVisitor());
			
			if ( isGenerateWalkerAndCloner() ) {
				generateEPackageWalker(oneEPackage, isRecursiveVisitor());
				generateEPackageCloner(oneEPackage, isRecursiveVisitor());
			}
			
			if ( isGenerateReplacer() ) {
				generateEPackageReplacer(oneEPackage, isRecursiveVisitor());
			}
		}

		// computes if sexp handler should be generated for this package.
		// Cases where it should be generated (in all isGenerateSEx() must be true):
		// - Package is root and isRecursiveSExp() is true,
		// - What ever package is root or not, isRecursiveSExp() is false and package isn't empty.
		boolean sexpForPackage = false;
		boolean xmlForPackage = false;
		if ( root ) {
			sexpForPackage = isGenerateSExp() && (isRecursiveSerialization() || !oneEPackage.getEClassifiers().isEmpty());
			xmlForPackage = isGenerateXml() && (isRecursiveSerialization() || !oneEPackage.getEClassifiers().isEmpty());
		} else {
			sexpForPackage = isGenerateSExp() && !isRecursiveSerialization() && !oneEPackage.getEClassifiers().isEmpty(); 
			xmlForPackage = isGenerateXml() && !isRecursiveSerialization() && !oneEPackage.getEClassifiers().isEmpty(); 
		}

		// generates SExp classes if needed
		if ( sexpForPackage ) {
			// generates SExpToModel
			getDependencyManager().clear();
			SExpToModelClassGenerator sexpToModelGenerator = new SExpToModelClassGenerator(this, isRecursiveSerialization());
			sexpToModelGenerator.generate(oneEPackage, javaContent);

			// generates ModelToSExp
			getDependencyManager().clear();
			ModelToSExpClassGenerator modelToSexpGenerator = new ModelToSExpClassGenerator(this, isRecursiveSerialization());
			modelToSexpGenerator.generate(oneEPackage, javaContent);
		}

		if ( xmlForPackage ) {
			// generates XmlToModel
			getDependencyManager().clear();
			XmlToModelClassGenerator sexpToModelGenerator = new XmlToModelClassGenerator(this, isRecursiveSerialization());
			sexpToModelGenerator.generate(oneEPackage, javaContent);
			
			// generates ModelToXml
			/*
			getDependencyManager().clear();
			ModelToSExpClassGenerator modelToSexpGenerator = new ModelToSExpClassGenerator(this, isRecursiveSerialization());
			modelToSexpGenerator.generate(oneEPackage, javaContent);
			*/
		}
		
		for (EClassifier oneEClassifier : oneEPackage.getEClassifiers()) {
			switch (oneEClassifier.eClass().getClassifierID()) {
			case EcorePackage.ECLASS:
				generateEClass((EClass) oneEClassifier);
				break;
			case EcorePackage.EENUM:
				generateEEnum((EEnum) oneEClassifier);
			}

		}

		for (EPackage oneChildEPackage : oneEPackage.getESubpackages()) {
			generateEPackage(false, oneChildEPackage);
		}
		
		if (visitorForPackage ) { visitorNameStack.pop(); }
		javaContent.endPackage(packagePath);
	}

	private void generateEPackageDocumentation(EPackage oneEPackage) {
		StringBuilder completeDocumation = new StringBuilder();
		
		// appends documentation for package
		String documentation = getDocumentation(oneEPackage);
		if ( documentation != null ) {
			completeDocumation.append(documentation);
		}
		
		// checks if a png file of the package name exists in source file parent directory.
		// if it's the case it copied in the directory 'doc-files' for java doc purposes.
		final String imageName = oneEPackage.getName() + ".png";
		final File diagramFile = new File(getSourceFile().getParentFile(), imageName);
		if ( diagramFile.exists() ) {
			// copy the doc file.
			javaContent.documentFile(diagramFile);
			
			// appends image show to Java doc.
			completeDocumation.append(NL);
			completeDocumation.append("<img src=\"doc-files/"+ imageName +"\"/>");
		}
		
		if ( completeDocumation.length() > 0 ) {
			javaContent.comment(Java.JAVA_DOC, 0, completeDocumation.toString());
		}
	}
	
	public void generateEPackageVisitor(EPackage oneEPackage, boolean recursive) throws Exception {
		getDependencyManager().clear();
		VisitorInterfaceGenerator visitorGenerator = new VisitorInterfaceGenerator(this,recursive);
		visitorGenerator.generate(oneEPackage, javaContent);
		
	}

	public void generateEPackageWalker(EPackage oneEPackage, boolean recursive) throws Exception {
		getDependencyManager().clear();
		WalkerClassGenerator walkerGenerator = new WalkerClassGenerator(this,recursive);
		walkerGenerator.generate(oneEPackage, javaContent);
		
	}
	
	public void generateEPackageCloner(EPackage oneEPackage, boolean recursive) throws Exception {
		getDependencyManager().clear();
		ClonerClassGenerator clonerGenerator = new ClonerClassGenerator(this,recursive);
		clonerGenerator.generate(oneEPackage, javaContent);
	}
	
	public void generateEPackageReplacer(EPackage oneEPackage, boolean recursive) throws Exception {
		getDependencyManager().clear();
		ReplacerClassGenerator replacerGenerator = new ReplacerClassGenerator(this,recursive);
		replacerGenerator.generate(oneEPackage, javaContent);
	}
	
	public void generateEClass(EClass oneEClass) throws Exception {
		if (isMultiInheritance(oneEClass)) {
			System.err.println("Can't generate class " + oneEClass.getName() + ", multiple inheritance isn't supported.");
		}
		
		if ( oneEClass.isInterface() || isUseGeneratedInterface() ) {
			System.out.println("Generating interface " + oneEClass.getName());

			getDependencyManager().clear();
			JavaInterfaceGenerator interfaceGenerator = new JavaInterfaceGenerator(this);
			interfaceGenerator.generate(oneEClass, javaContent);
		}
		
		if (!oneEClass.isInterface() || isUseGeneratedInterface() ) {
			
			System.out.println("Generating class " + oneEClass.getName() );
			
			if ( isUseGeneratedInterface() ) javaContent.beginPackage(getImplPackage());
			
			getDependencyManager().clear();
			JavaClassGenerator classGenerator = new JavaClassGenerator(this);
			classGenerator.generate(oneEClass, javaContent);
			
			if ( isUseGeneratedInterface() ) javaContent.endPackage(getImplPackage());
		}
			
	}

	public void generateEEnum(EEnum oneEnum) throws Exception {
		System.out.println("Generating enum " + oneEnum.getName());

		getDependencyManager().clear();
		JavaEnumGenerator enumGenerator = new JavaEnumGenerator(this);
		enumGenerator.generate(oneEnum, javaContent);
	}

	
	public Collection<EStructuralFeature> featuresToImplement(EClass source) {
		Collection<EStructuralFeature> allFeaturesToImplement = new LinkedHashSet<EStructuralFeature>(source.getEStructuralFeatures());

		// searches in all implements EClass that are interfaces the methods to implements
		for (EClass oneSuperClass : source.getESuperTypes()) {
			if (oneSuperClass.isInterface()) {
				allFeaturesToImplement.addAll(oneSuperClass.getEAllStructuralFeatures());
			}
		}
		return allFeaturesToImplement;
	}

	public Collection<EOperation> operationToImplement(EClass source) {
		Collection<EOperation> allFeaturesToImplement = new LinkedHashSet<EOperation>(source.getEOperations());
		
		// searches in all implements EClass that are interfaces the methods to implements
		for (EClass oneSuperClass : source.getESuperTypes()) {
			if (oneSuperClass.isInterface()) {
				allFeaturesToImplement.addAll(oneSuperClass.getEAllOperations());
			}
		}
		return allFeaturesToImplement;
	}
	
	
	public boolean isMultiInheritance(EClass source) {
		boolean foundOneSuperClass = false;
		for (EClass superClass : source.getESuperTypes()) {
			if (!superClass.isInterface()) {
				if (foundOneSuperClass) {
					return true;
				}
				foundOneSuperClass = true;
			}
		}
		return false;
	}

	public EClass getSuperClass(EClass source) {
		for (EClass superClass : source.getESuperTypes()) {
			if (!superClass.isInterface()) {
				return superClass;
			}
		}
		return null;
	}
	
	/** 
	 * Returns the feature Java name.
	 * @param feature to use
	 * @return a the feature Java (not a safe name).
	 */
	public String featureName(final EStructuralFeature feature) {
		String name = feature.getName();
		if ( feature.isMany() ) {
			name = name + "List";
		}
		return name;
	}

	/**
	 * Returns the Java class name from a {@link ETypedElement}, taking in
	 * account bounds and order.
	 *
	 * @param element
	 *            {@link ETypedElement} to analyze.
	 * @return a fully qualified Java class.
	 */
	public String elementTypeName(ETypedElement element) {
		String returnType = getTypeName(element.getEType(), element.getEGenericType());
		if (element.isMany()) {
			returnType = dependencyManager.getShortName("java.util.List") + "<" + element + ">";
		}
		return returnType;
	}

	public String getTypeName(EClassifier eClassifier, EGenericType eGenericType) {
		return getTypeName(eClassifier, eGenericType, true);
	}
	
	public String getTypeName(EClassifier eClassifier) {
		return getTypeName(eClassifier, null, false);
	}
	
	/**
	 * Returns the Java class name from an {@link EClassifier} and it's
	 * corresponding {@link EGenericType}.
	 * <p>
	 * It returns the simple name and registers the to the
	 * {@link DependencyManager}.
	 *
	 * @param eClassifier
	 *            {@link EClassifier} to analyze.
	 * @param eGenericType
	 *            associated {@link EGenericType}.
	 * @param printGeneric If true, it prints the generic type.
	 * @return a fully qualified name of a Java class.
	 */
	public String getTypeName(EClassifier eClassifier, EGenericType eGenericType, boolean printGeneric) {
		if (eClassifier == null) {
			return "void";
		}

		switch (eClassifier.eClass().getClassifierID()) {
		case EcorePackage.EDATA_TYPE:
			final String instanceClassName = eClassifier.getInstanceClassName();
			String shortName = getDependencyManager().getShortName(instanceClassName);
			if ( printGeneric ) { 
				return shortName + getParameterTypesDeclaration(eGenericType);
			} else {
				return shortName;
			}

		case EcorePackage.ECLASS:
			EClass eClass = (EClass) eClassifier;
			String qualifiedName = isUseGeneratedInterface() ? getInterfaceQualifiedName(eClass) : getClassQualifiedName(eClass);
			if ( printGeneric ) {
				return getDependencyManager().getShortName( qualifiedName + getParameterTypesDeclaration(eGenericType));
			} else {
				return getDependencyManager().getShortName(qualifiedName);
			}

		case EcorePackage.EENUM:
		default:
			return getDependencyManager().getShortName(getQualifiedName((EEnum) eClassifier));
		}
	}

	
	/**
	 * Creates the type parameter declaration from one {@link EGenericType}.
	 *
	 * @param eGenericType to be declared
	 * @return the declaration {@link String}, may by empty, never null.
	 */
	public String getParameterTypesDeclaration(EGenericType eGenericType) {
		StringBuilder parameterTypesBuilder = new StringBuilder();
		if (eGenericType != null && !eGenericType.getETypeArguments().isEmpty()) {
			parameterTypesBuilder.append("<");
			boolean first = true;
			for (EGenericType oneParameter : eGenericType.getETypeArguments()) {
				if (!first) {
					parameterTypesBuilder.append(",");
				}
				first = false;
				if ( oneParameter.getEClassifier() == null ) {
					parameterTypesBuilder.append("?");
				} else {
					String parameterTypeName = getTypeName(oneParameter.getEClassifier(), oneParameter.getELowerBound());
					parameterTypesBuilder.append(parameterTypeName);
				}
			}
			parameterTypesBuilder.append(">");
		}
		return parameterTypesBuilder.toString();
	}

	public DependencyManager getDependencyManager() {
		return dependencyManager;
	}

	public String getDocumentation(EModelElement element) {
		if (element != null) {
			EAnnotation genModelAnnotation = element.getEAnnotation(ModelGenerator.GenModelSource);
			if (genModelAnnotation != null && genModelAnnotation.getDetails().containsKey(ModelGenerator.GenModelDocumentation)) {
				return genModelAnnotation.getDetails().get(ModelGenerator.GenModelDocumentation);
			}
		}
		return null;
	}
	
	public void generatedJavaDoc(EModelElement element, boolean generatedAnnotation, JavaContentHandler content) {
		generatedJavaDoc(null, element, generatedAnnotation, content);
	}
	
	/**
	 * Generates a JavaDoc for given element.
	 *
	 * @param addition added at the beginning of the documentation.
	 * @param element
	 *            a {@link EModelElement}.
	 * @param generatedAnnotation
	 *            if true, adds the '@generated' annotation.
	 */
	public void generatedJavaDoc(String addition, EModelElement element, boolean generatedAnnotation, JavaContentHandler content) {
		StringBuilder comments = new StringBuilder();
		String documentation = getDocumentation(element);
		if ( addition != null ) {
			comments.append(addition);
			comments.append(NL);
		}
		if (documentation != null) {
			if ( element instanceof ENamedElement ) {
				comments.append("<b>");
				comments.append(((ENamedElement) element).getName());
				comments.append("</b>: ");
			}
			comments.append(documentation);
			comments.append(NL);
		}
		if (generatedAnnotation && isUseGeneratedAnnotation()) {
			comments.append("@generated");
			comments.append(NL);
		}
		
		if ( comments.length() > 0) {
			content.comment(Java.JAVA_DOC, 0, comments.toString());
		}
	}


	public EClass getUniqueInheritanceRootClass(EPackage ePackage, boolean recursive) {
		Set<EClass> possibleRootSet = getInheritanceRootClassSet(ePackage, recursive);
		if ( possibleRootSet.size() == 1 ) {
			// ugly access but sets are pain in the ass to access
			return possibleRootSet.iterator().next();
		} else {
			// no unique root
			return null;
		}
	}
	public Set<EClass> getInheritanceRootClassSet(EPackage ePackage, boolean recursive) {
		Set<EClass> possibleRootSet = new LinkedHashSet<EClass>();
		
		for ( EClassifier eClassifier : ePackage.getEClassifiers() ) {
			if ( eClassifier instanceof EClass ) {
				EClass eClass = (EClass) eClassifier;
				if ( eClass.getESuperTypes().isEmpty() ) {
					possibleRootSet.add(eClass);
				}
			}
		}
		
		if ( recursive ) {
			for ( EPackage eChildPackage : ePackage.getESubpackages() ) {
				// only checks non empty package.
				if ( eChildPackage.getEClassifiers().size() > 0 ) {
					possibleRootSet.addAll(getInheritanceRootClassSet(eChildPackage, true));
				}
			}
		}
		return possibleRootSet;
	}
	
	public String getCurrentVisitorName() {
		return visitorNameStack.peek();
	}
	
	public String getVisitMethodName(EClass eClass) {
		return "visit" + eClass.getName();
	}
	
	private String getQualifiedNamePrefix(EPackage source) {
		return getQualifiedName(source) + "." + CodeGenUtil.capName(source.getName());
	}

	public String getVisitorQualifiedName(EPackage source) {
		return getQualifiedNamePrefix(source) + "Visitor";
	}
	
	public String getWalkerQualifiedName(EPackage source) {
		return getQualifiedNamePrefix(source) + "Walker";
	}
	
	public String getClonerQualifiedName(EPackage source) {
		return getQualifiedNamePrefix(source) + "Cloner";
	}
	
	public String getReplacerQualifiedName(EPackage source) {
		return getQualifiedNamePrefix(source) + "Replacer";
	}
}
