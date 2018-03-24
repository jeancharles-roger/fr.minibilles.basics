package fr.minibilles.basics.sexp;

public interface SExpVisitor {

	void visit(SList toVisit);
	void visit(SAtom toVisit);
	void visit(SVariable toVisit);
	
	
	public static class Stub implements SExpVisitor {
		public void visit(SAtom toVisit) {}
		public void visit(SVariable toVisit) {}
		public void visit(SList toVisit) {}
	}
	
	public static class Walker extends Stub {
		public void visit(SList toVisit) {
			for ( SExp child : toVisit.getChildList() ) {
				child.accept(this);
			}
		}
	}
}
