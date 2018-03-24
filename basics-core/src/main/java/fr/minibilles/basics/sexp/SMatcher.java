package fr.minibilles.basics.sexp;

import java.util.HashMap;
import java.util.Map;

public class SMatcher {

	private final SExpVisitor sCatcherSearchVisitor = new SExpVisitor.Walker() {
		@Override
		public void visit(SVariable toVisit) {
			String name = toVisit.getName();
			if ( variableMap.containsKey(name) ) {
				throw new IllegalArgumentException("Multiple catcher named '"+ name +"'.");
			}
			variableMap.put(name, toVisit);
		}
	};
	
	private final SExp root;
	
	private final Map<String, SExp> variableMap = new HashMap<String, SExp>();
	
	public SMatcher(SExp root) {
		this.root = root;
		// find catchers
		root.accept(sCatcherSearchVisitor);
	}

	public SMatcher(SExp ... elements) {
		this.root = new SList(elements);
		// find catchers
		root.accept(sCatcherSearchVisitor);
	}
	
	public SExp getRoot() {
		return root;
	}
	
	public SExp catched(String name) {
		return variableMap.get(name);
	}
	
	public boolean matches(SExp tested) {
		return recursiveMatches(tested, root);
	}
	
	private boolean recursiveMatches(SExp tested, SExp tester) {
		// if tester is a variable, catches and returns true
		if ( tester instanceof SVariable ) {
			final SVariable variable = (SVariable) tester;
			variableMap.put(variable.getName(), tested);
			return true;
		}
		
		// otherwise
		if ( tested instanceof SAtom && tester instanceof SAtom ) {
			// tests atom values
			return tested.getValue().equals(tester.getValue());
			
		} else if ( tested instanceof SList && tester instanceof SList) {
			// tests list children
			int count = tested.getChildCount();
			if ( tester.getChildCount() != count ) return false;
			
			for ( int i=0; i<count; i++ ) {
				SExp testedChild = tested.getChild(i);
				SExp testerChild = tester.getChild(i);
				
				if ( recursiveMatches(testedChild, testerChild) == false ) {
					return false;
				}
			}
			// all children matches
			return true;
		}
		
		return false;
	}
	
}

