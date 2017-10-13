package fr.minibilles.basics.sexp;

public class SAtom extends SExp {

	private String value;
	
	public SAtom() {
		// do nothing
	}

	public SAtom(String value) {
		setValue(value);
	}
	
	public boolean isAtom() { return true; }

	public boolean isNull() { 
		return "null".equals(getValue()) || "0".equals(getValue()) || "0.0".equals(getValue());
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public void accept(SExpVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public SAtom copy() {
		return new SAtom(value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( obj instanceof SAtom ) {
			SAtom other = (SAtom) obj;
			return value == null ? other.value == null : value.equals(other.value);
		}
		return false;
	}
	
	@Override
	public String toStringMultiline(int level) {
		StringBuilder text = new StringBuilder();
		text.append(tabs(level));
		text.append(value);
		text.append(nl());
		return text.toString();
	}
	
	@Override
	public String toStringOneline() {
		return value;
	}

	@Override
	public String toString() {
		return value;
	}
}
