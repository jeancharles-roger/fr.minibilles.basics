package fr.minibilles.basics.model;


/**
 * Interface for all change handlers.
 *
 * @author Jean-Charles Roger.
 */
public interface ChangeHandler {
	/**
	 * Undo last transaction.
	 */
	public void undo();
	
	/**
	 * @return true if something can be undone.
	 */
	public boolean canUndo();
	
	/**
	 * Redo last undone transaction.
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
	 * Add a new transaction marker in the changes.
	 */
	public void newOperation();
		
	/**
	 * If the past changes ends with an operation mark (a changes that does 
	 * nothing) remove it. 
	 */
	void removePendingOperationMark();

}
