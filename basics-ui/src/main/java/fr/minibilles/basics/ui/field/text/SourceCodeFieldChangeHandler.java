package fr.minibilles.basics.ui.field.text;

import fr.minibilles.basics.model.AttributeChangeMark;
import fr.minibilles.basics.model.ChangeHandler;
import fr.minibilles.basics.sexp.S;
import fr.minibilles.basics.sexp.SExp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class SourceCodeFieldChangeHandler implements ChangeHandler {

	private final SourceCodeField field;
	
	private final diff_match_patch diff_match_patch = new diff_match_patch();
	
	/**
	 * List of changes that can be undone.
	 */
	final private Stack<LinkedList<fr.minibilles.basics.ui.field.text.diff_match_patch.Patch>> pastChanges = new Stack<LinkedList<fr.minibilles.basics.ui.field.text.diff_match_patch.Patch>>();

	/**
	 * List of changes that can be redone.
	 */
	final private Stack<LinkedList<fr.minibilles.basics.ui.field.text.diff_match_patch.Patch>> futureChanges = new Stack<LinkedList<fr.minibilles.basics.ui.field.text.diff_match_patch.Patch>>();

	private long currentTimestamp = 0;

	/**
	 * undoing is true when {@link AttributeChangeMark} is undoing an action.
	 */
	private boolean undoing = false;

	public SourceCodeFieldChangeHandler(SourceCodeField field) {
		this.field = field;
	}
	
	public void recordTextChange(String oldValue, String newValue) {
		if ( oldValue == null ) oldValue = "";
		if ( newValue == null ) newValue = "";
		
		LinkedList<fr.minibilles.basics.ui.field.text.diff_match_patch.Patch> patches = diff_match_patch.patch_make(newValue, oldValue);
		if (isUndoing()) {
			futureChanges.add(patches);
		} else {
			pastChanges.add(patches);
			currentTimestamp += 1;
		}
	}
	
	private void updateValueWithPatches(LinkedList<fr.minibilles.basics.ui.field.text.diff_match_patch.Patch> patches) {
		// retrieves current value and patches list
		String currentValue = field.getValue();
		// applies patches
		Object[] applyResult = diff_match_patch.patch_apply(patches, currentValue); 
		String undoneValue = (String) applyResult[0];
		// sets undone value
		field.setValue(undoneValue);
	}

	public void undo() {
		undoing = true;
		updateValueWithPatches(pastChanges.pop());
		currentTimestamp -= 1;
		undoing = false;
	}

	public boolean canUndo() {
		return !pastChanges.isEmpty();
	}

	public void redo() {
		updateValueWithPatches(futureChanges.pop());
		currentTimestamp += 1;
	}

	public boolean canRedo() {
		return !futureChanges.isEmpty();
	}

	private boolean isUndoing() {
		return undoing;
	}

	public long getTimestamp() {
		return currentTimestamp;
	}

	public void newOperation() {
		// nothing to do
	}
	
	public void removePendingOperationMark() {
		// nothing to do
	}
	
	public void fromSExp(SExp sexp) {
		for ( int i=0; i<sexp.getChildCount(); i++ ) {
			SExp child = sexp.getChild(i);
			String constructor = child.getConstructor();
			if ( constructor.equals("timestamp") ) {
				this.currentTimestamp = S.sexpToLong(child);
				
			} else if ( constructor.equals("pastchanges") ) {
				pastChanges.clear();
				Collection<String> patchStringList = S.sexpToStringCollection(child);
				for ( String patchString : patchStringList ) {
					LinkedList<fr.minibilles.basics.ui.field.text.diff_match_patch.Patch> patches = (LinkedList<fr.minibilles.basics.ui.field.text.diff_match_patch.Patch>) diff_match_patch.patch_fromText(patchString);
					pastChanges.push(patches);
				}

			} else if ( constructor.equals("futurechanges") ) {
				futureChanges.clear();
				Collection<String> patchStringList = S.sexpToStringCollection(child);
				for ( String patchString : patchStringList ) {
					LinkedList<fr.minibilles.basics.ui.field.text.diff_match_patch.Patch> patches = (LinkedList<fr.minibilles.basics.ui.field.text.diff_match_patch.Patch>) diff_match_patch.patch_fromText(patchString);
					futureChanges.push(patches);
				}
			}
		}
	}
	
	public SExp toSExp() {
		SExp timeStampSExp = S.longToSExp("timestamp", currentTimestamp); 
		
		List<String> pastChangesStringList = new ArrayList<String>(pastChanges.size());
		for ( LinkedList<fr.minibilles.basics.ui.field.text.diff_match_patch.Patch> patches : pastChanges ) {
			String patchesString = diff_match_patch.patch_toText(patches);
			pastChangesStringList.add(patchesString);
		}
		SExp pastChangesSExp = S.stringCollectionToSExp("pastchanges", pastChangesStringList);

		List<String> futureChangesStringList = new ArrayList<String>(futureChanges.size());
		for ( LinkedList<fr.minibilles.basics.ui.field.text.diff_match_patch.Patch> patches : futureChanges ) {
			String patchesString = diff_match_patch.patch_toText(patches);
			futureChangesStringList.add(patchesString);
		}
		SExp futureChangesSExp = S.stringCollectionToSExp("futurechanges", futureChangesStringList);
		
		return S.slist(timeStampSExp, pastChangesSExp, futureChangesSExp);
	}
}
