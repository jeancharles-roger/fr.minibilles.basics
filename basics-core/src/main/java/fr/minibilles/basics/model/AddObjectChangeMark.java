package fr.minibilles.basics.model;

import java.lang.reflect.Method;
import java.util.List;

/**
 * {@link ChangeMark} to save a object add in a {@link List}.
 *
 * @author Jean-Charles Roger.
 */
public class AddObjectChangeMark extends ChangeMark {

	protected final ModelObject receiver;
	protected final String attributeName;
	protected final int index;

	/**
	 * @param timestamp
	 */
	public AddObjectChangeMark(long timestamp, ModelObject receiver, String attributeName, int index) {
		super(timestamp);
		this.receiver = receiver;
		this.attributeName = attributeName;
		this.index = index;
	}

	protected String getRemoverName() {
		String firstUppercaseAttribute = attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);
		return "remove" + firstUppercaseAttribute;
	}
	

	@Override
	public void undo() {		
		try {
			Method method = getMethod(receiver.getClass(), getRemoverName(), int.class);
			if ( method != null ) {
				method.invoke(receiver, index);
			} else {
				System.err.println("AddObjectChangeMark.undo():No method to undo");
				// can't undo
			}

		} catch (Exception e) {
			System.err.println("AddObjectChangeMark.undo(): " + e.getClass().getName());
			// can't undo
		}

	}

	@Override
	public String toString() {
		return "Ad|" + attributeName;
	}

}
