package fr.minibilles.basics.tools.diagram;

import fr.minibilles.basics.generation.java.DependencyManager;
import fr.minibilles.basics.generation.java.Java;
import fr.minibilles.basics.generation.java.JavaContentFormatter;
import fr.minibilles.basics.generation.java.JavaContentHandler;
import fr.minibilles.basics.generation.java.JavaContentWriter;
import fr.minibilles.basics.tools.GeneratorEntryPoint;

public class BoxEdgeDiagramGenerator extends GeneratorEntryPoint {

	private String basepackage = "";
	private final DependencyManager dependencyManager = new DependencyManager();

	private String diagramClassName = null;
	private String boxElementClassName = null;
	private String edgeElementClassName = null;
	
	public BoxEdgeDiagramGenerator(String diagramClassName, String boxElementClassName, String arrowElementClassName) {
		this.diagramClassName = diagramClassName;
		this.boxElementClassName = boxElementClassName;
		this.edgeElementClassName = arrowElementClassName;
	}

	public String getBasepackage() {
		return basepackage;
	}
	 
	public void setBasepackage(String basepackage) {
		this.basepackage = basepackage;
	}
	
	public DependencyManager getDependencyManager() {
		return dependencyManager;
	}

	public String getDiagramClassName() {
		return diagramClassName;
	}
	
	public String getBoxElementClassName() {
		return boxElementClassName;
	}
	
	public String getEdgeElementClassName() {
		return edgeElementClassName;
	}
	
	@Override
	public void generate() throws Exception {
		JavaContentWriter javaWriter = new JavaContentWriter(getDestinationFile());
		JavaContentFormatter javaContent = new JavaContentFormatter(javaWriter);

		javaContent.beginPackage(getBasepackage());
		
		createDiagramClass(javaContent);
		
		createBoxElementClass(javaContent);
		createEdgeElementClass(javaContent);
		
		javaContent.endPackage(getBasepackage());
	}

	private void createDiagramClass(JavaContentFormatter content) {
		getDependencyManager().clear();
		content.beginFile(diagramClassName + ".java");
		content.markImports();
		
		getDependencyManager().getShortName("org.xid.basics.ui.diagram.Diagram");
		content.beginClass(Java.PUBLIC, diagramClassName, "Diagram", null);

		content.annotation("Override", null);
		content.beginMethod(Java.PUBLIC, "void", "build", null);
		content.codeln(0,"");
		content.codeln(0,"clearElements();");
		content.codeln(0,"");
		content.comment(Java.SINGLE_LINE, 0,"Constructs here the elements from your own model.");
		content.codeln(0, "addElement(new BoxElement(new float[] { 100, 100, 200, 200 }));");
		content.codeln(0, "addElement(new BoxElement(new float[] { 100, 300, 200, 400 }));");
		content.codeln(0, "addElement(new EdgeElement(new float[] { 200, 150, 250, 150, 250, 350, 200, 350 } ));");
		content.codeln(0, "addElement(new EdgeElement(new float[] { 100, 150, 50, 150, 50, 350, 100, 350 } ));");
 		content.codeln(0, "");
		content.endMethod("build");

		getDependencyManager().getShortName("org.eclipse.swt.SWT");
		getDependencyManager().getShortName("org.eclipse.swt.layout.FillLayout");
		getDependencyManager().getShortName("org.eclipse.swt.widgets.Canvas");
		getDependencyManager().getShortName("org.eclipse.swt.widgets.Display");
		getDependencyManager().getShortName("org.eclipse.swt.widgets.Shell");
		getDependencyManager().getShortName("org.xid.basics.ui.diagram.DiagramController");
		Java.beginMain(content);
		content.codeln(0, "");
		content.codeln(0, "Display display = Display.getDefault();");
		content.codeln(0, "Shell shell = new Shell(display);");
		content.codeln(0, "shell.setLayout(new FillLayout());");
		content.codeln(0, "");
		content.codeln(0, "BoxEdgeDiagram diagram = new BoxEdgeDiagram();");
		content.codeln(0, "Canvas canvas = new Canvas(shell, SWT.DOUBLE_BUFFERED | SWT.V_SCROLL | SWT.H_SCROLL);");
		content.codeln(0, "DiagramController controller = new DiagramController(diagram, DiagramController.MAIN_VIEW, canvas);");
		content.codeln(0, "controller.refreshElements(true);");
		content.codeln(0, "");
		content.codeln(0, "shell.setBounds(100, 100, 300, 500);");
		content.codeln(0, "shell.open();");
		content.codeln(0, "while ( !shell.isDisposed() ) {");
		content.codeln(1, "if ( !display.readAndDispatch() ) display.sleep();");
		content.codeln(0, "}");
		content.codeln(0, "");
		Java.endMain(content);
		
		content.endClass(diagramClassName);
		generateImports(content);
		content.endFile(diagramClassName + ".java");
	}
	
	private void createBoxElementClass(JavaContentFormatter content) {
		getDependencyManager().clear();
		content.beginFile(boxElementClassName + ".java");
		content.markImports();
		
		getDependencyManager().getShortName("org.xid.basics.ui.diagram.Element");
		content.beginClass(Java.PUBLIC, boxElementClassName, "Element.Stub", null);

		content.beginAttribute(Java.PRIVATE, "float[]", "rectangle");
		content.endAttribute("rectangle");

		content.beginMethod(Java.PUBLIC, null, boxElementClassName, null, 
				new Java.Parameter(Java.NONE, "float[]", "rectangle")
			);
		content.codeln(0, "this.rectangle = rectangle;");
		content.endMethod(boxElementClassName)	;	
		

		content.beginMethod(Java.PUBLIC, boxElementClassName, "getModel", null);
		content.codeln(0, "return this;");
		content.endMethod("getModel");
		
		content.beginMethod(Java.PUBLIC, "float[]", "getPoint", null);
		content.codeln(0, "return rectangle;");
		content.endMethod("getPoint");

		getDependencyManager().getShortName("org.xid.basics.ui.diagram.DiagramContext");
		getDependencyManager().getShortName("org.xid.basics.ui.diagram.geometry.Geometry");
		content.beginMethod(Java.PUBLIC, "void", "computeBounds", null,  
				new Java.Parameter(Java.NONE, "float[]", "result"),
				new Java.Parameter(Java.NONE, "DiagramContext", "context")
			);
		content.codeln(0,"Geometry.copyPoints(rectangle, result);");
		content.codeln(0,"Geometry.expandRectangle(result, 4f, 4f);");
		content.endMethod("computeBounds");
		
		getDependencyManager().getShortName("org.xid.basics.ui.diagram.gc.GC");
		getDependencyManager().getShortName("org.xid.basics.ui.diagram.gc.GcUtils");
		getDependencyManager().getShortName("org.eclipse.swt.SWT");
		content.beginMethod(Java.PUBLIC, "void", "display", null,  
				new Java.Parameter(Java.NONE, "GC", "gc"),
				new Java.Parameter(Java.NONE, "DiagramContext", "context")
		);
		content.codeln(0, "gc.setBackground(context.getResources().getSystemColor(SWT.COLOR_GRAY));");
		content.codeln(0, "GcUtils.drawRoundRectangle(gc, rectangle, 5f, 5f, true);");
		content.endMethod("display");
		
		
		content.endClass(boxElementClassName);
		
		generateImports(content);
		content.endFile(boxElementClassName + ".java");
	}

	private void createEdgeElementClass(JavaContentFormatter content) {
		getDependencyManager().clear();
		content.beginFile(edgeElementClassName + ".java");
		content.markImports();
		
		getDependencyManager().getShortName("org.xid.basics.ui.diagram.Element");
		content.beginClass(Java.PUBLIC, edgeElementClassName, "Element.Stub", null);
		
		content.beginAttribute(Java.PRIVATE, "float[]", "points");
		content.endAttribute("points");
		
		content.beginMethod(Java.PUBLIC, null, edgeElementClassName, null, 
				new Java.Parameter(Java.NONE, "float[]", "points")
			);
		content.codeln(0, "this.points = points;");
		content.endMethod(edgeElementClassName)	;	
		
		content.beginMethod(Java.PUBLIC, edgeElementClassName, "getModel", null);
		content.codeln(0, "return this;");
		content.endMethod("getModel");
		
		content.beginMethod(Java.PUBLIC, "float[]", "getPoint", null);
		content.codeln(0, "return points;");
		content.endMethod("getPoint");
		
		getDependencyManager().getShortName("org.xid.basics.ui.diagram.DiagramContext");
		getDependencyManager().getShortName("org.xid.basics.ui.diagram.geometry.Geometry");
		content.beginMethod(Java.PUBLIC, "void", "computeBounds", null,  
				new Java.Parameter(Java.NONE, "float[]", "result"),
				new Java.Parameter(Java.NONE, "DiagramContext", "context")
		);
		content.codeln(0,"Geometry.computeBoundingRectangle(points, result);");
		content.codeln(0,"Geometry.expandRectangle(result, 4f, 4f);");
		content.endMethod("computeBounds");
		
		getDependencyManager().getShortName("org.xid.basics.ui.diagram.gc.GC");
		getDependencyManager().getShortName("org.xid.basics.ui.diagram.gc.GcUtils");
		content.beginMethod(Java.PUBLIC, "void", "display", null,  
				new Java.Parameter(Java.NONE, "GC", "gc"),
				new Java.Parameter(Java.NONE, "DiagramContext", "context")
		);
		content.codeln(0, "GcUtils.drawPolyline(gc, points);");
		content.endMethod("display");
		
		
		content.endClass(edgeElementClassName);
		
		generateImports(content);
		content.endFile(edgeElementClassName + ".java");
	}
	
	public void generateImports(JavaContentHandler content) {
		for ( String oneImport : getDependencyManager().getJavaImports() ) {
			content.import_(Java.NONE, oneImport);
		}
	}

}
