package fr.minibilles.basics.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * This {@link ChangeRecorder} records all changes in a {@link Stack} of
 * {@link ChangeMark}.
 *
 * @author Jean-Charles Roger.
 * 
 */
public class ModelChangeRecorder implements ChangeRecorder {

	/**
	 * List of changes that can be undone.
	 */
	final protected Stack<ChangeMark> pastChanges = new Stack<ChangeMark>();

	/**
	 * List of changes that can be redone.
	 */
	final protected Stack<ChangeMark> futureChanges = new Stack<ChangeMark>();

	protected long currentTimestamp = 0;

	protected boolean recording = false;

	/**
	 * undoing is true when {@link AttributeChangeMark} is undoing an action.
	 */
	protected boolean undoing = false;

	protected void mark(ChangeMark mark) {
		if (!recording) {
			return;
		}
		if (isUndoing()) {
			futureChanges.add(mark);
		} else {
			if (pastChanges.isEmpty() && !mark.isOperationMark()) {
				newOperation();
			}
			pastChanges.add(mark);
		}
	}

	/**
	 * Stops recoding changes until next transition (recording will restart
	 * after the next call of {@link #newOperation()}).
	 */
	public void noRecord() {
		recording = false;
	}

	public void newOperation() {

		recording = true;
		clearFuture();
		if (!pastChanges.isEmpty() && (pastChanges.lastElement().isOperationMark())) {
			return;
		}

		// increase timestamp
		currentTimestamp++;
		mark(ChangeMark.Operation(currentTimestamp));
	}

	protected void clearFuture() {
		futureChanges.clear();
	}

	public void recordChangeAttribute(ModelObject receiver, String attributeName, Object oldValue) {
		mark(new AttributeChangeMark(currentTimestamp, receiver, attributeName, oldValue));
	}

	public void recordAddObject(ModelObject receiver, String attributeName, int index) {
		mark(new AddObjectChangeMark(currentTimestamp, receiver, attributeName, index));
	}

	public void recordRemoveObject(ModelObject receiver, String attributeName, int index, Object removedObject) {
		mark(new RemoveObjectChangeMark(currentTimestamp, receiver, attributeName, index, removedObject));
	}

	public void recordPutObject(ModelObject receiver, String attributeName, Object index, Object oldValue) {
		mark(new PutObjectChangeMark(currentTimestamp, receiver, attributeName, index, oldValue));
	}

	public void recordRemoveObject(ModelObject receiver, String attributeName, Object index, Object removedObject) {
		mark(new RemoveObjectChangeMark(currentTimestamp, receiver, attributeName, index, removedObject));
	}
	
	/**
	 * Returns the {@link List} of commands to undo in the order to be undone.
	 *
	 * @return a {@link List} of {@link ChangeMark}.
	 */
	protected List<ChangeMark> getChangesToUndo() {
		List<ChangeMark> result = new ArrayList<ChangeMark>();
		while (!pastChanges.peek().isOperationMark() && !pastChanges.isEmpty()) {
			result.add(pastChanges.pop());
		}

		// adds the transition mark at the begining of the list.
		result.add(0, pastChanges.pop());
		return result;
	}

	/**
	 * If the past changes ends with an operation mark (a changes that does 
	 * nothing) remove it. 
	 */
	public void removePendingOperationMark() {
		if ( pastChanges.isEmpty() ) return;
		
		// if last element is an OperationMark, removes it
		int lastIndex = pastChanges.size() - 1;
		if ( pastChanges.get(lastIndex).isOperationMark()) {
			pastChanges.remove(lastIndex);
		}
	}
	
	public void undo() {
		undoing = true;
		for (ChangeMark oneChange : getChangesToUndo()) {
			if (oneChange.isOperationMark()) {
				currentTimestamp = oneChange.getTimestamp();
				futureChanges.add(oneChange);
			}
			oneChange.undo();
		}
		currentTimestamp--;
		undoing = false;
	}

	public boolean canUndo() {
		return !pastChanges.isEmpty();
	}

	/**
	 * Returns the {@link List} of commands to redo in the order to be redone.
	 *
	 * @return a {@link List} of {@link ChangeMark}.
	 */
	protected List<ChangeMark> getChangesToRedo() {
		List<ChangeMark> result = new ArrayList<ChangeMark>();
		while (!futureChanges.peek().isOperationMark() && !futureChanges.isEmpty()) {
			result.add(futureChanges.pop());
		}

		// adds the transition mark at the begining of the list.
		result.add(0, futureChanges.pop());
		return result;
	}

	public void redo() {
		for (ChangeMark oneChange : getChangesToRedo()) {
			if (oneChange.isOperationMark()) {
				currentTimestamp = oneChange.getTimestamp();
				pastChanges.add(oneChange);
			}
			oneChange.undo();
		}
		currentTimestamp++;
	}

	public boolean canRedo() {
		return !futureChanges.isEmpty();
	}

	protected boolean isUndoing() {
		return undoing;
	}

	public long getTimestamp() {
		return currentTimestamp;
	}
}
