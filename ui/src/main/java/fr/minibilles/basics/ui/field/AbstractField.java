/**
 * 
 */
package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.error.Diagnostic;
import fr.minibilles.basics.notification.Notification;
import fr.minibilles.basics.notification.NotificationListener;
import fr.minibilles.basics.notification.NotificationSupport;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.Resources;
import fr.minibilles.basics.ui.action.Action;
import fr.minibilles.basics.ui.action.ActionExecuter;
import fr.minibilles.basics.ui.action.ActionManager;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;

/**
 * Abstract class for {@link Field}s. It implements common methods.
 * 
 * @author Jean-Charles Roger
 * 
 */
public abstract class AbstractField implements Field {

	private static final String DATA_XID_FIELD = "XID_FIELD";			//$NON-NLS-1$
	
	public static final int READ_ONLY_COLOR = SWT.COLOR_WIDGET_LIGHT_SHADOW;
	
	protected final Resources resources = Resources.getInstance(Resources.class);
	
	protected String label;

	/** The widget that contains the label */
	protected Label swtLabel;
	
	protected boolean enable = true;

	/** Field style */
	protected final int style;

	/** Stores the information label to update error status */
	protected Label infoLabel;
	
	/** List of actions associated to field. */
	protected final List<Action> actions = new ArrayList<Action>();
	
	/** Action manager for field. */
	protected ActionManager actionManager;

	/** Composite that contains the buttons. */
	protected Composite buttonBar;
	
	/** The tooltip set to the label. */
	protected String tooltip;

	protected final NotificationSupport.Stub notificationSupport = new NotificationSupport.Stub(this);
	
	public AbstractField(String label, int style) {
		this.style = style;
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public boolean hasLabel() {
		return label != null;
	}
	
	protected String getLabelSuffix() {
		return ":";
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		boolean oldValue = this.enable;
		this.enable = enable;
		notificationSupport.fireValueNotification(Notification.TYPE_API, BasicsUI.NOTIFICATION_ENABLE, enable, oldValue);
	}

	public String getTooltip() {
		return tooltip;
	}
	
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
		if ( swtLabel != null ) swtLabel.setToolTipText(tooltip);
	}
	
	public boolean hasStyle(int mask) { return (style & mask) != 0; }

	protected Resources getResources() {
		return resources;
	}
	public void addAction(Action action) {
		actions.add(action);
	}
	
	public List<Action> getActions() {
		return actions;
	}
	
	public boolean hasActionsStyled(int style) {
		for ( Action action : actions) {
			if ( action.hasStyle(style) ) return true;
		}
		return false;
	}
	
	protected Action createRootAction() {
		return new Action.Stub(Action.STYLE_HIERARCHICAL) {
			@Override
			public List<Action> getActions() {
				return AbstractField.this.getActions();
			}
		};
	}
	
	public Diagnostic validate() {
		Diagnostic diagnostic = getDiagnostic();
		if ( !hasStyle(BasicsUI.NO_INFO) ) showInfo(diagnostic);
		return diagnostic;
	}
	
	public Diagnostic getDiagnostic() {
		return null;
	}

	public boolean addListener(NotificationListener listener) {
		return notificationSupport.addListener(listener);
	}

	public boolean removeListener(NotificationListener listener) {
		return notificationSupport.removeListener(listener);
	}

	/**
	 * Creates the SWT widget for this field.
	 * SWT specific
	 */
	public void createWidget(Composite parent) {
		addDisposeListener(parent);
	}

	/**
	 * Adds a dispose listener to dispose resources.
	 */
	protected void addDisposeListener(Composite parent) {
		parent.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				dispose();
			}
		});
	}
	
	/**
	 * Attach this to given widget. 
	 * SWT specific
	 */
	protected void attachFieldToWidget(Widget widget) {
		if (widget != null ) {
			widget.setData(DATA_XID_FIELD, this);
		}
	}

	/**
	 * Fire widget creation notification. 
	 * Parameter is SWT specific.
	 * @param widget the widget that have been created.
	 */
	protected void fireWidgetCreation(Widget widget) {
		notificationSupport.fireValueNotification(Notification.TYPE_UNKNOWN, BasicsUI.NOTIFICATION_WIDGET_CREATION, widget, null);
	}
	
	/**
	 * Creates the label for the field. 
	 * SWT specific
	 */
	protected void createLabel(Composite parent) {
		if (hasLabel()) {
			swtLabel = new Label(parent, SWT.LEFT);
			attachFieldToWidget(swtLabel);
			
			swtLabel.setText(label + getLabelSuffix());
			swtLabel.setToolTipText(getTooltip());
			
			swtLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

			fireWidgetCreation(swtLabel);
		}
	}
	
	/**
	 * Creates the information label for this filed.
	 * SWT specific
	 */
	protected void createInfo(Composite parent) {
		if ( !hasStyle(BasicsUI.NO_INFO) ) {
			infoLabel = new Label(parent, SWT.NONE);
			attachFieldToWidget(infoLabel);
			GridData layoutData = new GridData(16, 16);
			layoutData.verticalAlignment = SWT.TOP;
			infoLabel.setLayoutData(layoutData);
			fireWidgetCreation(infoLabel);
		}
	}

	/**
	 * Creates the actions buttons
	 * SWT specific
	 */
	protected void createButtonBar(Composite parent) {
		if ( hasActionsStyled(Action.STYLE_BUTTON) ) {
			final int alignment = hasStyle(BasicsUI.HORIZONTAL) ? SWT.HORIZONTAL : SWT.VERTICAL;
			buttonBar = getActionManager().createButtonBar(parent, alignment, actions);
			buttonBar.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
			for ( Control child : buttonBar.getChildren() ) fireWidgetCreation(child);
		}
	}
	
	protected void createMenu(Control control) {
		if ( hasActionsStyled(Action.STYLE_MENUPOPUP)) {
			getActionManager().createPopupMenu(control, createRootAction());
		}
	}
	
	/**
 	 * Update info label with diagnostic/
	 * SWT specific
	 */
	protected void showInfo(Diagnostic diagnostic) {
		if ( infoLabel != null ) {
			if ( diagnostic != null ) {
				infoLabel.setImage(getInfoImage(diagnostic.getLevel()));
				infoLabel.setToolTipText(diagnostic.getMessage());
			} else {
				infoLabel.setImage(null);
				infoLabel.setToolTipText(null);
			}
		}
	}

	private Image getInfoImage(int level) {
		String imageName = getLevelImageName(level);
		Image image = resources.getImage(imageName); 
		return image;
	}

	/**
	 * Calculates the horizontal span for the field value part. 
	 * SWT specific
	 */
	protected int fieldHorizontalSpan() {
		return 1 + (hasLabel() ? 0 : 1) + (hasStyle(BasicsUI.NO_INFO) ? 1 : 0) + (hasActionsStyled(Action.STYLE_BUTTON) ? 0 : 1);
	}
	
	public boolean grabExcessVerticalSpace() {
		return false;
	}
	
	public ActionManager getActionManager() {
		if ( actionManager == null ) {
			actionManager = new ActionManager();
		}
		return actionManager;
 	}
	
	public void refreshButtonBar() {
		ActionManager.refreshWidgets(buttonBar);
	}
	
	public ActionExecuter getActionExecuter() {
		return getActionManager().getActionExecuter();
	}
	
	public void setActionExecuter(ActionExecuter executer) {
		getActionManager().setActionExecuter(executer);
	}
	
	/** 
	 * Called when the field is disposed. 
	 * SWT Specific
	 */
	protected void dispose() {
		Resources.releaseInstance(Resources.class);
	}
	
	/** 
	 * SWT specific
	 * @return a layout able to contains fields 
	 */
	public static GridLayout createFieldLayout() {
		GridLayout gridLayout = new GridLayout(4, false);
//		gridLayout.marginWidth = 0;
//		gridLayout.marginLeft = 0;
//		gridLayout.marginRight = 0;
//		gridLayout.marginHeight = 0;
//		gridLayout.marginTop = 0;
//		gridLayout.marginBottom = 0;
		return gridLayout;
	}
	
	/** 
	 * SWT specific
	 * @return the field attached to widget using {@link #attachFieldToWidget(Widget)}.
	 */	
	public static Field getWidgetField(Widget widget) {
		if (widget == null ) return null;
		return (Field) widget.getData(DATA_XID_FIELD);
	}
	
	/**
	 * @return the image name for given information level.
	 */
	public static String getLevelImageName(int level) {
		String imageName = "info_tsk.gif";	//$NON-NLS-1$
		if ( level == Diagnostic.WARNING ) imageName = "warn_tsk.gif";	//$NON-NLS-1$
		if ( level == Diagnostic.ERROR ) imageName = "error_tsk.gif";	//$NON-NLS-1$
		return imageName;
	}
}
