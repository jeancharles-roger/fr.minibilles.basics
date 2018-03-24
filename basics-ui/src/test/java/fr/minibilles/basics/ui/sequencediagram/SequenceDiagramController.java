package fr.minibilles.basics.ui.sequencediagram;

import fr.minibilles.basics.progress.ActionMonitor;
import fr.minibilles.basics.ui.action.Action;
import fr.minibilles.basics.ui.action.ActionExecuter;
import fr.minibilles.basics.ui.diagram.DiagramContext;
import fr.minibilles.basics.ui.diagram.DiagramController;
import fr.minibilles.basics.ui.sequencediagram.model.Sequence;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


public class SequenceDiagramController {

	private Composite composite;
	private SequenceDiagram diagram;
	private DiagramController diagramController;
	private Sequence sequence;
	
	public SequenceDiagramController(Sequence sequence) {
		this.sequence = sequence;
	}
	
	public Composite getComposite() {
		return composite;
	}
	
	public void createComposite(Composite parent) {
		
		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		

		Control canvas = new Canvas(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
		//canvas.addKeyListener(menuManager);

		diagram = new SequenceDiagram(sequence);
		diagram.build();
		
		diagramController = new DiagramController(diagram, DiagramContext.MAIN_VIEW, canvas);
		diagramController.setActionExecuter(new ActionExecuter() {
			public void executeAction(Action action) {
				action.run(ActionMonitor.empty);
				refreshUI();
			}
		});
	}
	
	public void refreshUI() {
		diagramController.refreshElements(true);
		diagramController.refreshInteractions();
	}
	

}
