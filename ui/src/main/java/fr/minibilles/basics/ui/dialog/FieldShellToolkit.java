/**
 * 
 */
package fr.minibilles.basics.ui.dialog;

import fr.minibilles.basics.error.Diagnostic;
import fr.minibilles.basics.notification.Notification;
import fr.minibilles.basics.notification.NotificationListener;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.Resources;
import fr.minibilles.basics.ui.action.Action;
import fr.minibilles.basics.ui.action.ActionExecuter;
import fr.minibilles.basics.ui.action.ActionManager;
import fr.minibilles.basics.ui.controller.Controller;
import fr.minibilles.basics.ui.field.AbstractField;
import fr.minibilles.basics.ui.field.Field;
import java.util.Arrays;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A {@link FieldShellToolkit} is a {@link Shell} filled by a {@link Field}.
 * It's able to handle:
 * <ul>
 * <li>header banner with image and diagnostic informations,</li>
 * <li>hints, ...</li>
 * </ul>
 * @author Jean-Charles Roger
 *
 */
public class FieldShellToolkit {

	protected static final String NO_HINTS = "";
	
	private String title;
	private Image bannerImage;
	
	private List<Action> menuActions;
	private ActionManager actionManager;
	
	private AbstractField field;
	protected Controller<?> controller;

	private NotificationListener listener;

	private Label titleLabel;
	private Label informationImageLabel;
	private Label informationLabel;
	private Label bannerImageLabel;
	
	private String initialMessage = "";
	
	private Group hintGroup;
	private Text hintText;
	private String hintString;
	
	private Composite headerComposite;
	private Composite fieldsComposite;
	
	/** Shell style */
	protected final int style;
	
	protected Resources resources;

	private final Shell dialogShell;
	
	private int buttonBarHeight = 0;
	
	public FieldShellToolkit(Shell shell, String title, int style, Field field) {
		this.dialogShell = shell;
		this.title = title;
		this.style = style;
		this.controller = null;
		this.field = (AbstractField) field;
	}
	
	public FieldShellToolkit(Shell shell, String title, int style, Controller<?> controller) {
		this.dialogShell = shell;
		this.title = title;
		this.style = style;
		this.controller = controller;
		this.field = null;
	}
	
	public void init() {
		if ( controller != null ) {
			// init field with controller.
			field = controller.createFields();
			listener = controller.createListener(getActionExecuter());
			field.addListener(listener);
		} 
		configureShell();
		refresh();
	}
	
	/** Gets the toolkit title. */
	public String getTitle() {
		return title;
	}
	
	/** Sets the toolkit title. */
	public void setTitle(String title) {
		this.title = title;
		if ( titleLabel != null ) {
			titleLabel.setText(this.title);
			titleLabel.getParent().layout();
		}
	}
	
	/** Gets the initial message for dialog. */
	public String getInitialMessage() {
		return initialMessage;
	}
	
	/** Sets the initial message for dialog. */
	public void setInitialMessage(String initialMessage) {
		this.initialMessage = initialMessage;
		if ( informationLabel != null ) {
			informationLabel.setText(initialMessage);
		}
	}
	
	/** Get dialog's banner image */ 
	public Image getBannerImage() {
		return bannerImage;
	}
	
	/** Sets banner image for dialog */
	public void setBannerImage(Image bannerImage) {
		this.bannerImage = bannerImage;
		if (bannerImageLabel != null ) {
			bannerImageLabel.setImage(bannerImage);
		}
	}
	
	public List<Action> getMenuActions() {
		return menuActions;
	}
	
	public void setMenuActions(Action ... menuActions) {
		if ( menuActions != null ) {
			this.menuActions = Arrays.asList(menuActions);
		} else {
			this.menuActions = null;
		}
	}
	
	public void setMenuActions(List<Action> menuActions) {
		this.menuActions = menuActions;
	}
	
	
	public int getButtonBarHeight() {
		return buttonBarHeight;
	}
	
	public void setButtonBarHeight(int buttonBarHeight) {
		this.buttonBarHeight = buttonBarHeight;
	}
	
	public boolean hasStyle(int mask) { return (style & mask) != 0; }
	
	protected Field getField() {
		return field;
	}
	
	protected void createHeader() {
		final Color whiteColor = resources.getSystemColor(SWT.COLOR_WHITE);

		headerComposite = new Composite(dialogShell, SWT.BORDER);
		headerComposite.setBackground(whiteColor);
		titleLabel = new Label(headerComposite, SWT.SINGLE);
		titleLabel.setText(title);
		titleLabel.setBackground(whiteColor);
		informationImageLabel = new Label(headerComposite, SWT.NONE);
		informationImageLabel.setBackground(whiteColor);
		informationLabel = new Label(headerComposite, SWT.RIGHT);
		informationLabel.setText(getInitialMessage());
		informationLabel.setBackground(whiteColor);
		if ( getBannerImage() != null ) {
			bannerImageLabel = new Label(headerComposite, SWT.NONE);
			bannerImageLabel.setImage(getBannerImage());
			bannerImageLabel.setBackground(whiteColor);
		}
		
		
		// set layout
		FormData compositeLayoutData = new FormData();
		compositeLayoutData.left = new FormAttachment(0);
		compositeLayoutData.right = new FormAttachment(100);
		compositeLayoutData.top = new FormAttachment(0);
		headerComposite.setLayoutData(compositeLayoutData);
		headerComposite.setLayout(new FormLayout());
		
		FormData layoutData = new FormData();
		layoutData.top = new FormAttachment(0, 5);
		titleLabel.setLayoutData(layoutData);
		
		layoutData = new FormData(16,16);
		layoutData.left = new FormAttachment(0);
		layoutData.right = new FormAttachment(informationLabel);
		layoutData.top = new FormAttachment(titleLabel, 5);
		informationImageLabel.setLayoutData(layoutData);
		
		layoutData = new FormData();
		layoutData.left = new FormAttachment(informationImageLabel);
		if ( getBannerImage() != null ) {
			layoutData.right = new FormAttachment(bannerImageLabel);
		} else {
			layoutData.right = new FormAttachment(100, -5);
		}
		layoutData.top = new FormAttachment(titleLabel, 5);
		layoutData.bottom = new FormAttachment(100, -5);
		informationLabel.setLayoutData(layoutData);
		
		if ( getBannerImage() != null ) {
			layoutData = new FormData();
			layoutData.right = new FormAttachment(100, 0);
			layoutData.top = new FormAttachment(0, 0);
			layoutData.bottom = new FormAttachment(100, 0);
			bannerImageLabel.setLayoutData(layoutData);
		}
	}

	protected void createHints() {
		hintGroup = new Group(dialogShell, SWT.SHADOW_IN);
		hintText = new Text(hintGroup, SWT.MULTI | SWT.WRAP | SWT.RIGHT | SWT.READ_ONLY);
		hintText.setText(NO_HINTS);
		hintText.setBackground(resources.getSystemColor(AbstractField.READ_ONLY_COLOR));

		// if there is no header and a banner image was provided
		boolean hasBannerImage = headerComposite == null && getBannerImage() != null;
		if (hasBannerImage) {
			bannerImageLabel = new Label(hintGroup, SWT.NONE);
			bannerImageLabel.setImage(getBannerImage());
			//final Color whiteColor = resources.getSystemColor(SWT.COLOR_WHITE);
			//bannerImageLabel.setBackground(whiteColor);
		}

			// set layout
		hintGroup.setLayout(new GridLayout(hasBannerImage ? 2 : 1, false));
		hintText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		if (hasBannerImage) {
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, false);
			bannerImageLabel.setLayoutData(gridData);
		}


		FormData compositeLayoutData = new FormData();
		compositeLayoutData.left = new FormAttachment(0);
		compositeLayoutData.right = new FormAttachment(100);
		if ( headerComposite != null ) {
			compositeLayoutData.top = new FormAttachment(headerComposite);
		} else {
			compositeLayoutData.top = new FormAttachment(0);
		}
		compositeLayoutData.height = 35;
		
		hintGroup.setLayoutData(compositeLayoutData);

	}
	
	private void addHintListener() {
		field.addListener(new NotificationListener() {
			public void notified(Notification notification) {
				// using == for string in purpose for performances
				if ( notification.type == Notification.TYPE_UNKNOWN && BasicsUI.NOTIFICATION_WIDGET_CREATION == notification.name ) {
					Control control = (Control) notification.newValue;

					final Field widgetField = AbstractField.getWidgetField(control);
					if ( widgetField != null ) {
						final Listener mouseListener = new Listener() {
							public void handleEvent(Event event) {
								switch (event.type) {
								case SWT.MouseMove:
								case SWT.MouseEnter:
								case SWT.MouseHover:
									String newTooltip = widgetField.getTooltip();
									if ( newTooltip != null && !newTooltip.equals(hintString) ) {
										hintString = newTooltip;
										hintText.setText(hintString);
									}
									break;
								case SWT.MouseExit:
									hintString = NO_HINTS;
									hintText.setText(NO_HINTS);
									break;
								}
							}
						};
						control.addListener(SWT.MouseMove, mouseListener);
						control.addListener(SWT.MouseEnter, mouseListener);
						control.addListener(SWT.MouseHover, mouseListener);
						control.addListener(SWT.MouseExit,  mouseListener);
					}
					final Action widgetAction = ActionManager.getWidgetAction(control);
					if ( widgetAction != null ) {
						control.addListener(SWT.MouseEnter, new Listener() {
							public void handleEvent(Event event) {
								if ( widgetAction.getTooltip() != null ) hintText.setText(widgetAction.getTooltip());
							}
						});
						control.addListener(SWT.MouseExit, new Listener() {
							public void handleEvent(Event event) {
								hintText.setText(NO_HINTS);
							}
						});
					}
				}
			}
		});
	}
	
	protected void createFields() {
		fieldsComposite = new Composite(dialogShell, SWT.NONE);
		field.createWidget(fieldsComposite);
		
		field.addListener(new NotificationListener() {
			public void notified(Notification notification) {
				if ( !hasStyle(BasicsUI.NO_AUTOMATIC_VALIDATION) ) validateAll();
				field.refreshButtonBar();
			}
		});

		// set layout
		FormData compositeLayoutData = new FormData();
		compositeLayoutData.left = new FormAttachment(0);
		compositeLayoutData.right = new FormAttachment(100);
		if ( hintGroup != null  ) {
			compositeLayoutData.top = new FormAttachment(hintGroup);
		} else if ( headerComposite != null ) {
			compositeLayoutData.top = new FormAttachment(headerComposite);
		} else {
			compositeLayoutData.top = new FormAttachment(0);
		}
		
		compositeLayoutData.bottom = new FormAttachment(100, - getButtonBarHeight());
		
		fieldsComposite.setLayoutData(compositeLayoutData);
		
		GridLayout fieldsLayout = AbstractField.createFieldLayout();
		fieldsLayout.marginTop = 5;
		fieldsLayout.marginBottom = 0;
		fieldsComposite.setLayout(fieldsLayout);	
	}

	public Diagnostic validateAll() {
		Diagnostic diagnostic = field.validate();
		if ( informationImageLabel != null ) {
			if ( diagnostic != null ) {
				informationImageLabel.setImage(resources.getImage(AbstractField.getLevelImageName(diagnostic.getLevel())));
				informationLabel.setText(diagnostic.getMessage());
				
			} else {
				informationImageLabel.setImage(null);
				informationLabel.setText(getInitialMessage());
			}
		}
		return diagnostic;
	}

	
	protected void configureShell() {
		resources = Resources.getInstance(Resources.class);
		
		dialogShell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				field.removeListener(listener);
				Resources.releaseInstance(Resources.class);
			}
		});
		dialogShell.setLayout(new FormLayout());
		
		//field.setActionExecuter(getActionExecuter());
		createShellMenuBar();
		createShellContent();
		
		// pack dialog to get minimum size.
		dialogShell.pack();
		// do not allow size of shell to be smaller than 7/8 of the packed size.
		Point size = dialogShell.getSize();
		size.x = size.x * 7 / 8;
		size.y = size.y * 7 / 8;
		dialogShell.setMinimumSize(size);
		
		// validate fields (it's done after packing).
		if ( !hasStyle(BasicsUI.NO_AUTOMATIC_VALIDATION) ) validateAll();
	}

	protected void createShellContent() {
		if ( !hasStyle(BasicsUI.NO_HEADER) ) createHeader();
		if ( hasStyle(BasicsUI.SHOW_HINTS) ) {
			createHints();
			// adds a mouse over listener to show hints
			addHintListener();
		}
		createFields();
	}
	
	protected void createShellMenuBar() {
		if ( menuActions != null ) {
			getActionManager().createMenuBar(dialogShell, menuActions);
		}
	}
	
	public void refresh() {
		if ( controller != null ) controller.refreshFields();
		if ( !hasStyle(BasicsUI.NO_AUTOMATIC_VALIDATION) ) validateAll();
		field.refreshButtonBar();
	}

	public ActionManager getActionManager() {
		if ( actionManager == null ) {
			actionManager = new ActionManager();
		}
		return actionManager;
	}
	
	public ActionExecuter getActionExecuter() {
		return getActionManager().getActionExecuter();
	}
	
	public void setActionExecuter(ActionExecuter executer) {
		getActionManager().setActionExecuter(executer);
	}
	
	public Shell getShell() {
		return dialogShell;
	}
	
	/** Creates a {@link Shell} */
	public static Shell createShell(String title) {
		return createShell((Display) null, title);
	}
	
	/** Creates a {@link Shell} */
	public static Shell createShell(Shell parentShell, String title) {
		Shell shell = new Shell(parentShell, SWT.SHEET);
		shell.setText(title);
		return shell;
	}

	/** Creates a {@link Shell} */
	public static Shell createShell(Display display, String title) {
		Shell shell = new Shell(display);
		shell.setText(title);
		return shell;
	}
}
