package fr.minibilles.basics.model;


/**
 * Interface for all change recorders.
 *
 * @author Jean-Charles Roger.
 *
 */
public interface ChangeRecorder extends ChangeHandler {

	/**
	 * This change handler records nothing.
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
	 * Records the change of an attribute.
	 *
	 * @param receiver object that contains the attribute.
	 * @param attributeName the attribute name.
	 * @param oldValue the old attribute's value.
	 */
	public void recordChangeAttribute(ModelObject receiver, String attributeName, Object oldValue);
	
	/**
	 * Records the change of an attribute.
	 *
	 * @param receiver object that contains the attribute.
	 * @param attributeName the attribute name.
	 * @param index the index of the object that has been added.
	 */
	public void recordAddObject(ModelObject receiver, String attributeName, int index);
	
	/**
	 * Records the change of an attribute.
	 *
	 * @param receiver object that contains the attribute.
	 * @param attributeName the attribute name.
	 * @param index the index where the object has removed
	 * @param removedObject the object that has been removed.
	 */
	public void recordRemoveObject(ModelObject receiver, String attributeName, int index, Object removedObject);
	
	/**
	 * Records the change of an attribute.
	 *
	 * @param receiver object that contains the attribute.
	 * @param attributeName the attribute name.
	 * @param index the object index of the object that has been added or put.
	 * @param oldValue the previous value of the indexed attribute.
	 */
	public void recordPutObject(ModelObject receiver, String attributeName, Object index, Object oldValue);

	/**
	 * Records the change of an attribute.
	 *
	 * @param receiver object that contains the attribute.
	 * @param attributeName the attribute name.
	 * @param index the object index of the object that has been removed.
	 * @param removedObject the removed object.
	 */
	public void recordRemoveObject(ModelObject receiver, String attributeName, Object index, Object removedObject);
}
