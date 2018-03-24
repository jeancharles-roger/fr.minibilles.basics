package fr.minibilles.basics.ui.tree;

public class ControllerEvent {

	public final static int CLICK = 1;
	public final static int SELECT = 2;
	
	public final static int EXPAND = 10;
	public final static int COLLAPSE = 11;
	
	public Object source;
	public Object sourceEvent;
	
	public int type;
	public Object target;
	
	public ControllerEvent (int type, Object source, Object target) {
		this(type, source, null, target);
	}
	
	public ControllerEvent (int type, Object source, Object sourceEvent, Object target) {
		this.type = type;
		this.source = source;
		this.sourceEvent = sourceEvent;
		this.target = target;
	}
	
}
