package fr.minibilles.basics.model;

import java.lang.reflect.Method;
import java.util.List;

/**
 * {@link ChangeMark} to save a object removal in a {@link List}.
 *
 * @author Jean-Charles Roger.
 */
public class RemoveObjectChangeMark extends ChangeMark {

	protected final ModelObject receiver;
	protected final String attributeName;
	protected final int index;
	protected final Object objectIndex;
	protected final Object removedObject;

	public RemoveObjectChangeMark(long timestamp, ModelObject receiver, String attributeName, int index, Object removedObject) {
		super(timestamp);
		this.receiver = receiver;
		this.attributeName = attributeName;
		this.index = index;
		this.objectIndex = null;
		this.removedObject = removedObject;
	}

	public RemoveObjectChangeMark(long timestamp, ModelObject receiver, String attributeName, Object index, Object removedObject) {
		super(timestamp);
		this.receiver = receiver;
		this.attributeName = attributeName;
		this.index = -1;
		this.objectIndex = index;
		this.removedObject = removedObject;
	}
	
	protected String getAdderName() {
		String firstUppercaseAttribute = attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);
		return "add" + firstUppercaseAttribute;
	}
	
	protected String getPutterName() {
		String firstUppercaseAttribute = attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);
		return "put" + firstUppercaseAttribute;
	}
	

	@Override
	public void undo() {		
		try {
			if ( objectIndex == null ) {
				undoIntegerIndex();
			} else {
				undoObjectIndex();
			}
		} catch (Exception e) {
			System.err.println("RemoveObjectChangeMark.undo():" + e.getClass().getName());
			// can't undo
		}

	}

	private void undoIntegerIndex() throws Exception {
		Method method = getMethod(receiver.getClass(), getAdderName(), int.class, removedObject.getClass());
		if ( method != null ) {
			method.invoke(receiver, index, removedObject);
		} else {
			System.err.println("RemoveObjectChangeMark.undoIntegerIndex():No method to undo");
			// can't undo
		}
	}

	private void undoObjectIndex() throws Exception {
		Method method = getMethod(receiver.getClass(), getPutterName(), objectIndex.getClass(), removedObject.getClass());
		if ( method != null ) {
			method.invoke(receiver, objectIndex, removedObject);
		} else {
			System.err.println("RemoveObjectChangeMark.undoObjectIndex():No method to undo");
			// can't undo
		}
	}
	
	@Override
	public String toString() {
		return "Re|" + attributeName;
	}

}
