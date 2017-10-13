package fr.minibilles.basics.ui.action;

import fr.minibilles.basics.Basics;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

/**
 * Utility methods to create menus and toolbars from {@link Action}s.
 * @author Jean-Charles Roger
 *
 */
public class ActionManager {

	public static final String ShowHiddenProperty = "basics.ui.actions.showhidden";
	public static final String ShowShortCutProperty = "basics.ui.actions.showshortcut";
	
	private final static String DATA_ACTION = Action.class.getCanonicalName();
	
	/** Generic selection listener for MenuItems that have Actions as data.  */
	protected final Listener selectionListener = new Listener() {
		public void handleEvent(Event event) {
			switch (event.type) {
			case SWT.Selection:
				if ( event.widget.getData(DATA_ACTION) instanceof Action) {
					Action action = (Action) event.widget.getData(DATA_ACTION);
					if ( action.getVisibility() == ActionRunnable.VISIBILITY_ENABLE ) {
						if ( event.widget instanceof Button && action.hasStyle(Action.STYLE_HIERARCHICAL) ) {
							// action is a button and hierarchical, show a menu
							Menu menu = new Menu(((Button) event.widget).getShell());
							fillMenu(menu, Action.STYLE_MENUPOPUP, action);
							menu.setVisible(true);
						} else {
							// action is regular, execute it

							// TODO handles end status
							getActionExecuter().executeAction(action);
						}
					}
				}
				break;
			case SWT.Dispose:
				event.widget.removeListener(SWT.Selection, selectionListener);
				event.widget.removeListener(SWT.Dispose, selectionListener);
			}
		}
	};

	/** Executes {@link Action} */
	protected ActionExecuter actionExecuter = ActionExecuter.DEFAULT_EXECUTER;
	
	/* if true, show hidden actions */
	protected boolean showHidden;
	
	protected boolean showShortCut;
	
	public ActionManager() {
		String hidden = System.getProperty(ShowHiddenProperty);
		showHidden = hidden == null ? false : Boolean.parseBoolean(hidden);

		String shortcut = System.getProperty(ShowShortCutProperty);
		showShortCut = shortcut == null ? true : Boolean.parseBoolean(shortcut);
	}
	
	public ActionExecuter getActionExecuter() {
		return actionExecuter;
	}
	
	public void setActionExecuter(ActionExecuter executer) {
		if ( executer != null ) {
			this.actionExecuter = executer;
		} else {
			this.actionExecuter = ActionExecuter.DEFAULT_EXECUTER;
		}
	}
	
	public boolean isShowHidden() {
		return showHidden;
	}
	
	public void setShowHidden(boolean showHidden) {
		this.showHidden = showHidden;
	}
	
	public boolean isShowShortCut() {
		return showShortCut;
	}
	
	public void setShowShortCut(boolean showShortCut) {
		this.showShortCut = showShortCut;
	}
	
	/** 
	 * Creates a menu bar for given shell.
	 * It uses the {@link Action#STYLE_HIERARCHICAL} actions to create each menu.
	 *
	 */
	public Menu createMenuBar(Shell shell, List<Action> rootActions) {
		Menu menuBar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menuBar);
		for ( Action action : rootActions ) {
			if ( action.hasStyle(Action.STYLE_HIERARCHICAL) ) {
				createMenu(shell, menuBar, action);
			}
		}
		return menuBar;
	}


	/** Creates a cascaded {@link MenuItem} in a SWT.BAR menu from a  a hierarchical action. */
	public Menu createMenu(final Shell shell, final Menu bar, final Action rootAction) {
		MenuItem menuItem = new MenuItem(bar, SWT.CASCADE);
		attachActionToWidget(rootAction, menuItem);
		menuItem.setText(rootAction.getLabel());

		final Menu menu = new Menu(shell, SWT.DROP_DOWN);
		attachActionToWidget(rootAction, menu);
		menuItem.setMenu(menu);
		fillMenu(menu, Action.STYLE_MENUBAR, rootAction);
		return menu;
	}

	/** Creates a popup menu from a hierarchical action */
	public Menu createPopupMenu(final Control control, final Action rootAction) {
		final Menu menu = new Menu(control.getShell(), SWT.POP_UP);
		control.setMenu(menu);
		fillMenu(menu, Action.STYLE_MENUPOPUP, rootAction);
		return menu;
	}

	/** Adds listener to menu to populate it when needed. */
	public void fillMenu(final Menu menu, final int stylefilter, final Action rootAction) {
		final Listener listener = new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Show:
					refreshMenu(menu, stylefilter, rootAction);
					break;
				case SWT.Dispose:
					menu.removeListener(SWT.Show, this);
					menu.removeListener(SWT.Dispose, this);
				}
				
			}
		};
		refreshMenu(menu, stylefilter, rootAction);
		menu.addListener(SWT.Show, listener);
		menu.addListener(SWT.Dispose, listener);
	}
	
	
	
	public void refreshMenuBar(final Shell shell) {
		if ( shell == null || shell.getMenuBar() == null ) return;
		for ( MenuItem item : shell.getMenuBar().getItems() ) {
			refreshMenu(item.getMenu());
		}
	}
	
	public void refreshMenu(final Menu menu) {
		Action action = getWidgetAction(menu);
		if ( action != null ) {
			refreshMenu(menu, Action.STYLE_MENU, action);
		}
	}
	
	public void refreshMenu(final Menu menu, int stylefilter, final Action rootAction) {
		depopulateMenu(menu);
		populateMenu(menu, rootAction.getActions());
	}
	
	protected int getMenuStyle(Action action ) {
		if ( action.hasStyle(Action.STYLE_HIERARCHICAL) ) {
			return SWT.CASCADE;
		} else if ( action.hasStyle(Action.STYLE_BOOLEAN_STATE)) {
			return SWT.CHECK;
		} else {
			return SWT.PUSH;
		}
	}
	
	
	protected void populateMenu(Menu menu, List<Action> actions) {
		populateMenu(menu, Action.STYLE_MENU, actions);
	}
	
	/** Populates menu with the given actions.  */
	protected void populateMenu(Menu menu, int stylefilter, List<Action> actions) {
		int count = 0;
		boolean canSeperate = false;
		for (Action oneAction : actions) {
			if ( oneAction.hasStyle(stylefilter) ) {
				int visibility = oneAction.getVisibility();
				if (visibility != ActionRunnable.VISIBILITY_HIDDEN  || showHidden ) {
					if (oneAction.hasStyle(Action.STYLE_SEPARATOR)) {
						if ( canSeperate ) new MenuItem(menu, SWT.SEPARATOR);
						canSeperate = false;
					} else {
						MenuItem menuItem = new MenuItem(menu, getMenuStyle(oneAction));
						menuItem.setData(DATA_ACTION, oneAction);
					
						if ( Basics.isWindows() && isShowShortCut() && oneAction.getKeyCode() != null ) {
							// adds shortcut description 
							StringBuilder label = new StringBuilder();
							label.append(oneAction.getLabel());
							label.append("\t");
							label.append(oneAction.getKeyCode());
							menuItem.setText(label.toString());
						} else if ( oneAction.getLabel() != null ) {
							menuItem.setText(oneAction.getLabel());
						}
						menuItem.setImage(oneAction.getImage());
						if ( oneAction.getKeyCode() != null ) {
							menuItem.setAccelerator(oneAction.getKeyCode().getKeycode());
						}
						if ( visibility == ActionRunnable.VISIBILITY_DISABLE ) {
							menuItem.setEnabled(false);
						}
						if ( visibility == ActionRunnable.VISIBILITY_HIDDEN && !showHidden ) {
							menuItem.setEnabled(false);
						}
						if ( oneAction.hasStyle(Action.STYLE_DEFAULTACTION)) {
							menu.setDefaultItem(menuItem);
						}
						
						if ( oneAction.hasStyle(Action.STYLE_BOOLEAN_STATE)) {
							menuItem.setSelection(oneAction.getState());
						}
						
						if ( oneAction.hasStyle(Action.STYLE_HIERARCHICAL) ) {
							final Menu childMenu = new Menu(menu.getShell(), SWT.DROP_DOWN);
							menuItem.setMenu(childMenu);
							fillMenu(childMenu, stylefilter, oneAction);
						}
						
						menuItem.addListener(SWT.Selection,selectionListener);
						menuItem.addListener(SWT.Dispose,selectionListener);
						
						count++;
						canSeperate = true;
					}
				}
			}
		}
		if ( (stylefilter & Action.STYLE_MENUPOPUP) == 0 && count == 0 ) {
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
			menuItem.setText("None");
			menuItem.setEnabled(false);
		}
		
	}

	/** Removes all item from a menu. */
	protected void depopulateMenu(Menu menu) {
		MenuItem[] items = menu.getItems();
		for (int i = 0; i < items.length; i++) {
			items[i].dispose();
		}
	}
	
	public Composite createButtonBar(final Composite parent, int style, List<Action> actions) {
		Composite buttonBar = new Composite(parent, SWT.NONE);
		int perpandicular = SWT.NONE;
		GridLayout layout = null;
		if ( (style & SWT.HORIZONTAL) != 0 ) {
			layout = new GridLayout(actions.size(), false);
			perpandicular = SWT.VERTICAL;
		} else {
			layout = new GridLayout(1, false);
			perpandicular = SWT.HORIZONTAL;
		}
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		buttonBar.setLayout(layout);
		for (Action action : actions) {
			if ( action.hasStyle(Action.STYLE_BUTTON) ) {
				int visibility = action.getVisibility();
				if (visibility != ActionRunnable.VISIBILITY_HIDDEN ) {
					
					if ( action.hasStyle(Action.STYLE_SEPARATOR) ) {
						Label separator = new Label(buttonBar, SWT.SEPARATOR | perpandicular);
						separator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
						
					} else {
						Button button = new Button(buttonBar, getButtonStyle(action));
						attachActionToWidget(action, button);
						if ( visibility == ActionRunnable.VISIBILITY_RUNNING ) {
							attachRunningTimer(action, button);	
						}
						if ( action.getLabel() != null ) button.setText(action.getLabel());
						button.setImage(action.getImage());
						button.setEnabled(visibility == ActionRunnable.VISIBILITY_ENABLE);
						button.addListener(SWT.Selection,selectionListener);
						button.addListener(SWT.Dispose,selectionListener);
						button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
						if ( action.hasStyle(Action.STYLE_DEFAULTACTION) ) button.getShell().setDefaultButton(button);
					}

				}
			}
		}
		return buttonBar;
	}

	protected void attachActionToWidget(Action oneAction, Widget widget) {
		widget.setData(DATA_ACTION, oneAction);
	}
	
	protected int getButtonStyle(Action action) {
		int style = SWT.NONE;
		style |= (action.hasStyle(Action.STYLE_BOOLEAN_STATE) ? SWT.CHECK : SWT.PUSH);
		return style;
	}

	public void createToolbar(CoolBar coolBar, List<Action> actions) {
		ToolBar toolbar = new ToolBar(coolBar, SWT.FLAT | SWT.HORIZONTAL);
		for (Action action : actions) {
			if (action.hasStyle(Action.STYLE_BUTTON)) createToolItem(action, toolbar);
		}
		toolbar.pack();
		Point size = toolbar.getSize();
		CoolItem mainCoolItem = new CoolItem(coolBar, SWT.NONE);
		mainCoolItem.setControl(toolbar);
		Point preferred = mainCoolItem.computeSize(size.x, size.y);
		mainCoolItem.setPreferredSize(preferred);
	}
	

	
	/**
	 * Creates a ToolItem for a given Action. Sets the label, image, tooltip and listener.
	 */
	public ToolItem createToolItem(Action action, final ToolBar toolBar) {
		int visibility = action.getVisibility();
		if (visibility == ActionRunnable.VISIBILITY_HIDDEN) return null;
		if (action.hasStyle(Action.STYLE_SEPARATOR)) {
			return new ToolItem(toolBar, SWT.SEPARATOR);
		} else {
			final ToolItem toolItem = new ToolItem(toolBar, getToolStyle(action));
			attachActionToWidget(action, toolItem);
			
			String label = action.getLabel();
			if ( label != null ) toolItem.setText(action.getLabel());
			
			Image image = action.getImage();
			if (image != null) toolItem.setImage(image);

			toolItem.setToolTipText(action.getTooltip());
			refreshWidget(toolItem);
			if ( action.hasStyle(Action.STYLE_HIERARCHICAL) ) {
				final Menu menu = createPopupMenu(toolBar.getShell(), action);
				Listener listener = new Listener () {
					public void handleEvent (Event event) {
						switch (event.type) {
						case SWT.Selection:
							Rectangle rect = toolItem.getBounds ();
							Point pt = new Point (rect.x, rect.y + rect.height);
							pt = toolBar.toDisplay (pt);
							menu.setLocation (pt.x, pt.y);
							menu.setVisible(true);
							event.doit = false;
							break;
						case SWT.Dispose:
							toolItem.removeListener(SWT.Selection, this);
							toolItem.removeListener(SWT.Dispose, this);
							break;
						}
					}
				};
				toolItem.addListener (SWT.Selection,listener);
				toolItem.addListener (SWT.Dispose,listener);
			} else {
				toolItem.addListener(SWT.Selection, selectionListener);
				toolItem.addListener(SWT.Dispose, selectionListener);
			}

			return toolItem;
		}
	}

	protected int getToolStyle(Action action) {
		int style = SWT.PUSH;
		if ( action.hasStyle(Action.STYLE_BOOLEAN_STATE)) style = SWT.CHECK;
		if ( action.hasStyle(Action.STYLE_HIERARCHICAL) ) style = SWT.DROP_DOWN;
		return style;
	}

	private static String createDotLabel(int dotCount) {
		StringBuilder label = new StringBuilder();
		for (int i=0; i<dotCount; i++) label.append('.');
		return label.toString();
	}

	private static void attachRunningTimer(final Action action, final Button button) {
		final Display display = button.getDisplay();
		display.asyncExec(new Runnable() {
			int step = 0;
			public void run() {
				if ( button.isDisposed() == false ) {
					if ( action.getVisibility() == ActionRunnable.VISIBILITY_RUNNING ) {
						// refreshes label
						step = (step+1) % 3;
						button.setText(createDotLabel(step+1));
						// rearm timer
						display.timerExec(500, this);
					} else {
						// refreshes button and stops timer 
						refreshWidget(button);
					}
				}
			}
		});
	}

	public static void refreshWidgets(final Composite container) {
		if ( container != null ) {
			for (Control child : container.getChildren() ) {
				if ( child instanceof Composite ) {
					refreshWidgets((Composite) child); 
				} else {
					refreshWidget(child);
				}
			}
			if (container instanceof ToolBar ) {
				for ( ToolItem item : ((ToolBar) container).getItems() ) {
					refreshWidget(item);
				}
			}
		}
	}

	/** Refreshes a widget (MenuItem, ToolItem or Button) that have an Action associated as data. */
	public static void refreshWidget(Widget widget) {
		Action action = getWidgetAction(widget);
		if (action != null) {
			if (action.hasStyle(Action.STYLE_SEPARATOR)) return;
			boolean v = action.getVisibility() == ActionRunnable.VISIBILITY_ENABLE;
			if (widget instanceof ToolItem) {
				ToolItem toolItem = (ToolItem) widget;
				if (toolItem.isEnabled() != v) {
					toolItem.setEnabled(v);	
				}
				if (action.hasStyle(Action.STYLE_BOOLEAN_STATE)) {
					toolItem.setSelection(action.getState());
				}
				Image image = action.getImage();
				if ( image == null ) {
					toolItem.setText(action.getLabel());
				} else if ( image != toolItem.getImage() ) {
					toolItem.setImage(image);
				}
			}
			if (widget instanceof Button) {
				Button button = (Button) widget;
				if (button.isEnabled() != v) {
					button.setEnabled(v);	
				}
				if (action.hasStyle(Action.STYLE_BOOLEAN_STATE)) {
					button.setSelection(action.getState());
				}
				Image image = action.getImage();
				if ( image == null ) {
					button.setText(action.getLabel());
				} else if ( image != button.getImage() ) {
					button.setImage(image);
				}
				if ( action.getVisibility() == ActionRunnable.VISIBILITY_RUNNING ) {
					attachRunningTimer(action, button);	
				}
			}
			if (widget instanceof MenuItem) {
				MenuItem menuItem = (MenuItem) widget;
				menuItem.setText(action.getLabel());
				if (menuItem.isEnabled() != v) {
					menuItem.setEnabled(v);	
				}
				if (action.hasStyle(Action.STYLE_BOOLEAN_STATE)) {
					menuItem.setSelection(action.getState());
				}
				Image image = action.getImage();
				if ( image != menuItem.getImage() ) {
					menuItem.setImage(image);
				}
			}
		}
	}

	public static Action getWidgetAction(Widget widget) {
		if ( widget == null ) return null;
		return (Action) widget.getData(DATA_ACTION);
	}
}

