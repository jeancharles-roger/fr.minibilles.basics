package fr.minibilles.basics.sexp;

public class SVariable extends SExp {

	private final String name;
	
	public SVariable(String name) {
		this.name = name;
	}
	
	public boolean isVariable() {
		return true;
	}
	
	public String getName() {
		return name;
	}
	
	public void accept(SExpVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public SVariable copy() {
		return new SVariable(name);
	}
	
	@Override
	public String toStringMultiline(int level) {
		StringBuilder text = new StringBuilder();
		text.append(tabs(level));
		text.append("${");
		text.append(name);
		text.append("}");
		text.append(nl());
		return text.toString();
	}

	@Override
	public String toStringOneline() {
		StringBuilder text = new StringBuilder();
		text.append("${");
		text.append(name);
		text.append("}");
		return text.toString();
	}

}
