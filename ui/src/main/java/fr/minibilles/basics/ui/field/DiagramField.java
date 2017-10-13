/**
 * 
 */
package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.notification.Notification;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.action.Action;
import fr.minibilles.basics.ui.action.ActionExecuter;
import fr.minibilles.basics.ui.diagram.Diagram;
import fr.minibilles.basics.ui.diagram.DiagramContext;
import fr.minibilles.basics.ui.diagram.DiagramController;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Field that embeds a {@link Diagram}.
 * @author Jean-Charles Roger
 *
 */
public class DiagramField<M> extends AbstractField {

	private DiagramController controller;
	private Diagram<M> diagram;
	
	private int[] size = new int[] { 400, 300};

	private CoolBar coolbar;
	private Canvas canvas; 
	
	public DiagramField(Diagram<M> diagram) {
		super(null, BasicsUI.NO_INFO);
		this.diagram = diagram;
		controller = new DiagramController(diagram, DiagramContext.MAIN_VIEW, null);
		controller.setActionExecuter(new ActionExecuter() {
			public void executeAction(Action action) {
				getActionExecuter().executeAction(action);
				refreshDiagram();
			}
		});
	}
	
	public boolean activate() {
		return false;
	}
	
	@Override
	public void createWidget(Composite parent) {
		super.createWidget(parent);

		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, grabExcessVerticalSpace(), 4, 1));

		composite.setLayout(new FormLayout());
		coolbar = new CoolBar(composite, SWT.HORIZONTAL);
		coolbar.setEnabled(isEnable());
		FormData coolData = new FormData();
		coolData.left = new FormAttachment(0);
		coolData.top = new FormAttachment(0);
		coolData.right = new FormAttachment(100);
		coolbar.setLayoutData(coolData);

		canvas = new Canvas(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
		canvas.setEnabled(isEnable());
		FormData canvasData = new FormData();
		canvasData.right = new FormAttachment(100);
		canvasData.left = new FormAttachment(0);
		canvasData.top = new FormAttachment(coolbar);
		canvasData.bottom = new FormAttachment(100);
		canvas.setLayoutData(canvasData);

		coolbar.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				composite.layout();
			}
		});

		
		List<Action> actions = new ArrayList<Action>();
		actions.addAll(getActions());
		diagram.computeActions(actions, controller);
		controller.getActionManager().createToolbar(coolbar, actions);
		
		//canvas.addKeyListener(menuManager);
		diagram.build();
		
		controller.setActionComposite(coolbar);
		controller.initControl(canvas);
		
		fireWidgetCreation(coolbar);
		fireWidgetCreation(canvas);
	}
	
	@Override
	public boolean grabExcessVerticalSpace() {
		return true;
	}

	@Override
	public void setEnable(boolean enable) {
		super.setEnable(enable);
		if ( canvas != null ) {
			canvas.setEnabled(enable);
		}
	}
	
	public M getValue() {
		return diagram.getModel();
	}
	
	protected void setValue(M value, int type) {
		if ( value == diagram.getModel() ) return;
		Diagram<M> oldValue = this.diagram;
		this.diagram.setModel(value);
		if ( type != Notification.TYPE_UI ) {
			refreshDiagram();
		}
		notificationSupport.fireValueNotification(type, BasicsUI.NOTIFICATION_VALUE, this.diagram, oldValue);
	}
	
	public void refreshDiagram() {
		controller.refreshElements(true);
		controller.refreshInteractions();
	}

	/** Sets the field's value */
	public void setValue(M value) {
		setValue(value, Notification.TYPE_API);
	}
	
	public DiagramController getController() {
		return controller;
	}
	
	public int[] getSize() {
		return size;
	}
	
	public void setSize(int width, int height) {
		this.size[0] = width;
		this.size[1] = height;
	}
	
}
