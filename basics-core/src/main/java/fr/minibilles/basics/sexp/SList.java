package fr.minibilles.basics.sexp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SList extends SExp {

	private final List<SExp> childList = new ArrayList<SExp>();

	public SList() {
		// do nothing
	}
	
	public SList(SExp ... children) {
		for ( SExp child : children ) {
			addChildAndOpposite(child);
		}
	}
	
	public SList(List<SExp> children) {
		for ( SExp child : children ) {
			addChildAndOpposite(child);
		}
	}

	@Override
	public boolean hasConstructor() {
		return firstChildIsAtom();
	}
	
	@Override
	public boolean isNull() {
		return childList.size() == 2 && childList.get(1).isNull();
	}
	
	@Override
	public String getConstructor() {
		if ( childList.isEmpty() ) return null;
		return childList.get(0).getValue();
	}
	
	public List<SExp> getChildList() {
		return Collections.unmodifiableList(childList);
	}

	public int getChildCount() {
		return childList.size();
	}

	public SExp getChild(int index) {
		if ( index < 0 || index >= getChildCount() ) { return null; }
		return childList.get(index);
	}

	public void addChild(SExp newValue) {
		addChild(getChildCount(), newValue);
	}

	public void addChild(int index, SExp newValue) {
		childList.add(index, newValue);
	}
	
	public SExp setChild(int index, SExp value) {
		return childList.set(index, value);
	}

	public void removeChild(SExp value) {
		int index = childList.indexOf(value);
		if (index >= 0 ) {
			removeChild(index);
		}
	}

	public void removeChild(int index) {
		childList.remove(index);
	}

	public void addChildAndOpposite(SExp newValue) {
		addChild(newValue);
		if ( newValue != null ) {
			newValue.setParent(this);
		}
	}

	public void addChildAndOpposite(int index, SExp newValue) {
		addChild(index, newValue);
		if ( newValue != null ) {
			newValue.setParent(this);
		}
	}

	public SExp setChildAndOpposite(int index, SExp newValue) {
		SExp oldChild = setChild(index, newValue);
		if ( newValue != null ) {
			newValue.setParent(this);
		}
		if ( oldChild != null ) {
			oldChild.setParent(null);
		}
		return oldChild;
	}
	
	public void removeChildAndOpposite(SExp removed) {
		removeChild(removed);
		if ( removed != null ) {
			removed.setParent(null);
		}
	}

	public void removeChildAndOpposite(int index) {
		SExp removed = childList.get(index);
		removeChild(index);
		if ( removed != null ) {
			removed.setParent(null);
		}
	}

	public void accept(SExpVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public SList copy() {
		SList copy = new SList();
		for ( SExp child : childList ) {
			copy.addChild(child.copy());
		}
		return copy;
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( obj instanceof SList ) {
			SList other = (SList) obj;
			return childList.equals(other.childList);
		}
		return false;
	}

	private boolean allChildrenAreAtoms() {
		for ( SExp child : childList ) {
			if ( child.isAtom() == false ) return false;
		}
		return true;
	}
	
	private boolean firstChildIsAtom() {
		return childList.size() > 0 && childList.get(0).isAtom();
	}
	
	public String toStringMultiline(int level) {
		final String tabs = tabs(level);
		final String nl = nl();
		
		StringBuilder result = new StringBuilder();
		result.append(tabs);
		result.append("(");
		int size = childList.size(); 
		if ( size > 0 ) {
			if ( allChildrenAreAtoms() ) {
				for (int i=0; i<size; i++ ) {
					if ( i > 0 ) result.append(" ");
					SExp child = childList.get(i);
					result.append(child.toStringOneline());
				}
				
			} else {
				int start=0;
				if ( hasConstructor() ) {
					result.append(getConstructor());
					start = 1;
				} 
				
				if ( size > start ) {
					result.append(nl);
					for (int i=start; i<size; i++ ) {
						SExp child = childList.get(i);
						result.append(child.toStringMultiline(level+1));
					}
				
					result.append(tabs);
				}
			}
		}
		
		result.append(")");
		result.append(nl);

		return result.toString();
	}
	
	@Override
	public String toStringOneline() {
		StringBuilder text = new StringBuilder();
		text.append("(");
		for ( SExp exp : childList ) {
			if ( text.length() > 1 ) text.append(" ");
			text.append(exp.toStringOneline());
		}
		text.append(")");
		return text.toString();
	}
	
	@Override
	public String toString() {
		return toStringOneline();
	}
}
