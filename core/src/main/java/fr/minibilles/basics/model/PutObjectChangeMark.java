package fr.minibilles.basics.model;

import java.lang.reflect.Method;
import java.util.List;

/**
 * <p>
 * {@link ChangeMark} to save a object add in a {@link List}.
 * </p>
 *
 * @author Jean-Charles Roger.
 *
 */
public class PutObjectChangeMark extends ChangeMark {

	protected final ModelObject receiver;
	protected final String attributeName;
	protected final Object index;
	protected final Object oldValue;

	/**
	 * @param timestamp
	 */
	public PutObjectChangeMark(long timestamp, ModelObject receiver, String attributeName, Object index, Object oldValue) {
		super(timestamp);
		this.receiver = receiver;
		this.attributeName = attributeName;
		this.index = index;
		this.oldValue = oldValue;
	}

	protected String getRemoverName() {
		String firstUppercaseAttribute = attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);
		return "remove" + firstUppercaseAttribute;
	}
	
	protected String getPutterName() {
		String firstUppercaseAttribute = attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);
		return "put" + firstUppercaseAttribute;
	}
	

	@Override
	public void undo() {		
		try {
			if ( oldValue == null ) {	
				undoAdd();
			} else {
				undoPut();
			}
		} catch (Exception e) {
			System.err.println("PutObjectChangeMark.undo(): " + e.getClass().getName());
			// can't undo
		}

	}

	private void undoPut() throws Exception {
		Method method = getMethod(receiver.getClass(), getPutterName(), index.getClass(), oldValue.getClass());
		if ( method != null ) {
			method.invoke(receiver, index, oldValue);
		} else {
			System.err.println("PutObjectChangeMark.undoPut():No method to undo");
			// can't undo
		}
	}

	private void undoAdd() throws Exception {
		Method method = getMethod(receiver.getClass(), getRemoverName(), (Class<?>) null);
		if ( method != null ) {
			method.invoke(receiver, index);
		} else {
			System.err.println("PutObjectChangeMark.undoAdd():No method to undo");
			// can't undo
		}
	}

	@Override
	public String toString() {
		return "Put|" + attributeName;
	}

}
