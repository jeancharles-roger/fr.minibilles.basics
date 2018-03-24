/**
 * 
 */
package fr.minibilles.basics.notification;

import java.util.EventObject;

/**
 * Notification description object.
 * 
 * @author Jean-Charles Roger
 *
 */
public class Notification extends EventObject {

	private static final long serialVersionUID = 1L;

	/** Notification type is unknown */
	public static final int TYPE_UNKNOWN = 0;
	/** Notification comes from UI */
	public static final int TYPE_UI = 1;
	/** Notification comes from API */
	public static final int TYPE_API = 2;
	
	/** Notification type */
	public final int type;
	
	/** Notification name */
	public final String name;
	
	/** New value, if applicable */
	public final Object newValue;
	
	/** Old value, if applicable */
	public final Object oldValue;
	
	/** Index of value, if applicable */
	public final int index;
	
	public Notification(Object source, int type, String name) {
		this(source, type, name, null, null, -1);
	}
	
	public Notification(Object source, int type, String name, Object newValue, Object oldValue) {
		this(source, type, name, newValue, oldValue, -1);
	}
	
	public Notification(Object source, int type, String name, Object newValue, Object oldValue, int index) {
		super(source);
		this.type = type;
		this.name = name;
		this.newValue = newValue;
		this.oldValue = oldValue;
		this.index = index;
	}
	
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("Notification(");
		switch (type) {
		case TYPE_API:
			buffer.append("API");
			break;
		case TYPE_UI:
			buffer.append("UI");
			break;
		case TYPE_UNKNOWN:
			buffer.append("UNKNOW");
			break;
		}
		buffer.append("):");
		buffer.append(name);
		return buffer.toString();
	}
}
