package fr.minibilles.basics.ui.action;

import fr.minibilles.basics.error.Diagnostic;
import fr.minibilles.basics.progress.ActionMonitor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.swt.graphics.Image;

/**
 * A user action.
 * @author Jean-Charles Roger
 *
 */
public interface Action extends ActionRunnable {

	/** No special style */
	static final int STYLE_NONE = 0;
	
	/** Action is a separator */
	static final int STYLE_SEPARATOR = 1;
	
	/** Action will appears in the main menu bar */
	static final int STYLE_MENUBAR = 2;
	
	/** Action will appears in the system tray menu */
	static final int STYLE_MENUTRAY = 4;
		
	/** Action will appears in a popup menu */
	static final int STYLE_MENUPOPUP = 8;
	
	/** Action will appears as a button */
	static final int STYLE_BUTTON = 16;
	
	/** Action will appears as a hierarchy of actions. */
	static final int STYLE_HIERARCHICAL = 32;
	
	/** Action will be considered as default action */
	static final int STYLE_DEFAULTACTION = 64;
	
	/** Action will be executed as a transaction action */
	static final int STYLE_TRANSACTIONNAL = 128;
	
	/** Action has a boolean state */
	static final int STYLE_BOOLEAN_STATE = 256;
	
	/** Action will appears in menus except tray menu */
	static final int STYLE_MENU = STYLE_MENUBAR | STYLE_MENUPOPUP ;
	
	static final int STYLE_DEFAULT = STYLE_BUTTON | STYLE_MENU;
	
	/** Action's label */
	String getLabel();
	
	/** Action's image */
	Image getImage();
	
	/** @return true if action has given style */
	boolean hasStyle(int style);

	/** @return the list of children actions, used for {@link #STYLE_HIERARCHICAL} (can't be null). */
	List<Action> getActions();
	
	/** @return action's tooltip. */
	String getTooltip();

	KeyCode getKeyCode();
	
	/**
	 * A stub implementation of interface {@link Action}, suitable as a base
	 * class for concrete implementations.
	 */
	class Stub implements Action {

		protected String label;

		protected Image image;
		
		protected int style;

		protected KeyCode keycode;
		
		protected ActionRunnable runnable;
		
		/** Constructs an Action. */
		public Stub() {
			this("", null, STYLE_DEFAULT, null, new ActionRunnable.Stub());
		}
		
		/** Constructs an Action. */
		public Stub(ActionRunnable runnable) {
			this("", null, STYLE_DEFAULT, null, runnable);
		}
		
		/** Constructs an Action with given style. */
		public Stub(int style) {
			this("", null, style, null, null);
		}
		
		/** Constructs an Action with given style. */
		public Stub(int style, ActionRunnable runnable) {
			this("", null, style, null, runnable);
		}
		
		/** Constructs an Action with a given label. */
		public Stub(String label) {
			this(label, null, STYLE_DEFAULT, null, null);
		}
		
		/** Constructs an Action with a given label. */
		public Stub(String label, KeyCode keycode) {
			this(label, null, STYLE_DEFAULT, keycode, null);
		}
		
		/** Constructs an Action with a given label. */
		public Stub(String label, ActionRunnable runnable) {
			this(label, null, STYLE_DEFAULT, null, runnable);
		}
		
		/** Constructs an Action with a given label. */
		public Stub(String label, KeyCode keycode, ActionRunnable runnable) {
			this(label, null, STYLE_DEFAULT, keycode, runnable);
		}

		/** Constructs an Action with given label and image. */
		public Stub(String label, Image image) {
			this(label, image, STYLE_DEFAULT, null, null);
		}
		
		/** Constructs an Action with given label and image. */
		public Stub(String label, Image image, KeyCode keycode) {
			this(label, image, STYLE_DEFAULT, keycode, null);
		}
		
		/** Constructs an Action with given label and image. */
		public Stub(String label, Image image, ActionRunnable runnable) {
			this(label, image, STYLE_DEFAULT, null, runnable);
		}
		
		/** Constructs an Action with given label and image. */
		public Stub(String label, Image image, KeyCode keycode, ActionRunnable runnable) {
			this(label, image, STYLE_DEFAULT, keycode, runnable);
		}
		
		/** Constructs an Action with given label and style. */
		public Stub(String label, int style) {
			this(label, null, style, null, null);
		}
		
		/** Constructs an Action with given label and style. */
		public Stub(String label, int style, KeyCode keycode) {
			this(label, null, style, keycode, null);
		}
		
		/** Constructs an Action with given label and style. */
		public Stub(String label, int style, ActionRunnable runnable) {
			this(label, null, style, null, runnable);
		}
		
		/** Constructs an Action with given label and style. */
		public Stub(String label, int style, KeyCode keycode, ActionRunnable runnable) {
			this(label, null, style, keycode, runnable);
		}
		
		/** Constructs an Action with given label, image and style. */
		public Stub(String label, Image image, int style) {
			this(label, image, style, null, null);
		}
		
		/** Constructs an Action with given label, image and style. */
		public Stub(String label, Image image, int style, KeyCode keycode) {
			this(label, image, style, keycode, null);
		}
		
		/** Constructs an Action with given label, image and style. */
		public Stub(String label, Image image, int style, ActionRunnable runnable) {
			this(label, image, style, null, runnable);
		}
		
		/** Constructs an Action with given label, image and style. */
		public Stub(String label, Image image, int style, KeyCode keycode, ActionRunnable runnable) {
			this.label = label;
			this.image = image;
			this.style = style;
			this.keycode = keycode;
			this.runnable = runnable == null ? new ActionRunnable.Stub() : runnable;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}
		
		public Image getImage() {
			return image;
		}
		
		public void setImage(Image image) {
			this.image = image;
		}
				
		public int getStyle() {
			return style;
		}
		
		public void setStyle(int style) {
			this.style = style;
		}
		
		public final boolean hasStyle(int styleMask) {
			return (getStyle() & styleMask) != 0;
		}
		
		public String getTooltip() {
			return null;
		}
		
		public List<Action> getActions() {
			return Collections.emptyList();
		}
		
		public KeyCode getKeyCode() {
			return keycode;
		}
		
		public int getVisibility() {
			return runnable.getVisibility();
		}
		
		public int run(ActionMonitor monitor) {
			return runnable.run(monitor);
		}
		
		public boolean getState() {
			return runnable.getState();
		}
		
		public Diagnostic getDiagnostic() {
			return runnable.getDiagnostic();
		}
	}

	/**
	 * A stub implementation of interface {@link Action}, suitable for 
	 * a {@link Action#STYLE_HIERARCHICAL} action.
	 */
	public static class Container extends Stub {
		
		private final List<Action> actions;
		
		public Container(String label, Action ... actions) {
			this(label, null, STYLE_DEFAULT, actions);
		}
		
		public Container(String label, int style, Action ... actions) {
			this(label, null, style, actions);
		}
		
		public Container(String label, Image image, int style, Action ... actions) {
			super(label, image, style | STYLE_HIERARCHICAL, null, null);
			if ( actions != null ) {
				this.actions = Arrays.asList(actions);
			} else {
				this.actions = Collections.emptyList();
			}
		}
		
		@Override
		public List<Action> getActions() {
			return actions;
		}
	}
}
