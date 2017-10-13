package fr.minibilles.basics.model;


/**
 * <p>
 * Interface for all change handlers.
 * </p>
 *
 * @author Jean-Charles Roger.
 *
 */
public interface ChangeHandler {
	/**
	 * <p>
	 * Undo last transaction.
	 * </p>
	 */
	public void undo();
	
	/**
	 * @return true if something can be undone.
	 */
	public boolean canUndo();
	
	/**
	 * <p>
	 * Redo last undone transaction.
	 * </p>
	 */
	public void redo();
	
	/**
	 * @return true if something can be redone.
	 */
	public boolean canRedo();

	/**
	 * @return the current timestamp, used for dirty state.
	 */
	public long getTimestamp();

	/**
	 * <p>
	 * Add a new transaction marker in the changes.
	 * </p>
	 */
	public void newOperation();
		
	/**
	 * If the past changes ends with an operation mark (a changes that does 
	 * nothing) remove it. 
	 */
	void removePendingOperationMark();

}
