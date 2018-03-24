package fr.minibilles.basics.sexp;

import java.util.Collections;
import java.util.List;


public abstract class SExp {
	
	private SExp parent;
	
	public SExp() {
	}

	public SExp getParent() {
		return parent;
	}

	public void setParent(SExp newValue) {
		if ( parent != newValue ) {
			this.parent= newValue;
		}
	}
	
	public boolean isAtom() { 
		return false; 
	}

	public boolean isVariable() { 
		return false; 
	}
	
	/**
	 * @return true for <code>null|0|0.0</code> and <code>([any atom] null|0|0.0)</code>, false otherwise. 
	 */
	public boolean isNull() {
		return false;
	}
	
	public boolean hasConstructor() {
		return false;
	}
	
	public String getConstructor() {
		return null;
	}
	
	public List<SExp> getChildList() {
		// returns empty list
		return Collections.emptyList();
	}

	public int getChildCount() {
		// returns zero
		return 0;
	}

	public SExp getChild(int index) {
		// returns null
		return null;
	}

	public void addChild(SExp newValue) {
		// do nothing
	}

	public void addChild(int index, SExp newValue) {
		// do nothing
	}

	public SExp setChild(int index, SExp value) {
		// do nothing
		return null;
	}
	
	public void removeChild(SExp value) {
		// do nothing
	}

	public void removeChild(int index) {
		// do nothing
	}

	public void addChildAndOpposite(SExp newValue) {
		// do nothing
	}

	public void addChildAndOpposite(int index, SExp newValue) {
		// do nothing
	}

	public SExp setChildAndOpposite(int index, SExp newValue) {
		// do nothing
		return null;
	}
	
	public void removeChildAndOpposite(SExp removed) {
		// do nothing
	}

	public void removeChildAndOpposite(int index) {
		// do nothing
	}

	public String getValue() {
		// return null
		return null;
	}

	public void setValue(String value) {
		// do nothing
	}
	
	public abstract void accept(SExpVisitor visitor);
	
	public abstract String toStringMultiline(int level);
	public abstract String toStringOneline();

	public abstract SExp copy();
	
	protected final String tabs(int level) {
		StringBuilder tabs = new StringBuilder();
		for ( int i=0; i<level; i++ ) tabs.append("\t");
		return tabs.toString();
	}

	protected final String nl() {
		return System.getProperty("line.separator");
	}
}
