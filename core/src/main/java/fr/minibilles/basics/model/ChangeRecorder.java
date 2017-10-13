package fr.minibilles.basics.model;


/**
 * <p>
 * Interface for all change recorders.
 * </p>
 *
 * @author Jean-Charles Roger.
 *
 */
public interface ChangeRecorder extends ChangeHandler {

	/**
	 * <p>
	 * This change handler records nothing.
	 * </p>
	 */
	public static final ChangeRecorder Stub  = new ChangeRecorder() {

		public void recordChangeAttribute(ModelObject receiver, String attributeName, Object oldValue) { }

		public void recordAddObject(ModelObject receiver, String attributeName, int index) { }

		public void recordRemoveObject(ModelObject receiver, String attributeName, int index, Object removedObject) { }

		public void recordPutObject(ModelObject receiver, String attributeName, Object index, Object oldValue) { }

		public void recordRemoveObject(ModelObject receiver, String attributeName, Object index, Object removedObject) { }

		public void newOperation() {	}

		public void undo() { }
	
		public boolean canUndo() { return false; }
		
		public void redo() { }
	
		public boolean canRedo() { return false; }
		
		public long getTimestamp() { return -1; }
		
		public void removePendingOperationMark() {	}
	};
	
	/**
	 * <p>
	 * Records the change of an attribute.
	 * </p>
	 * @param receiver object that contains the attribute.
	 * @param attributeName the attribute name.
	 * @param oldValue the old attribute's value.
	 */
	public void recordChangeAttribute(ModelObject receiver, String attributeName, Object oldValue);
	
	/**
	 * <p>
	 * Records the change of an attribute.
	 * </p>
	 * @param receiver object that contains the attribute.
	 * @param attributeName the attribute name.
	 * @param index the index of the object that has been added.
	 */
	public void recordAddObject(ModelObject receiver, String attributeName, int index);
	
	/**
	 * <p>
	 * Records the change of an attribute.
	 * </p>
	 * @param receiver object that contains the attribute.
	 * @param attributeName the attribute name.
	 * @param index the index where the object has removed
	 * @param removedObject the object that has been removed.
	 */
	public void recordRemoveObject(ModelObject receiver, String attributeName, int index, Object removedObject);
	
	/**
	 * <p>
	 * Records the change of an attribute.
	 * </p>
	 * @param receiver object that contains the attribute.
	 * @param attributeName the attribute name.
	 * @param index the object index of the object that has been added or put.
	 * @param oldValue the previous value of the indexed attribute.
	 */
	public void recordPutObject(ModelObject receiver, String attributeName, Object index, Object oldValue);

	/**
	 * <p>
	 * Records the change of an attribute.
	 * </p>
	 * @param receiver object that contains the attribute.
	 * @param attributeName the attribute name.
	 * @param index the object index of the object that has been removed.
	 * @param removedObject the removed object.
	 */
	public void recordRemoveObject(ModelObject receiver, String attributeName, Object index, Object removedObject);
}
