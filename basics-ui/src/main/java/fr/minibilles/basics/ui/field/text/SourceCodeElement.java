package fr.minibilles.basics.ui.field.text;

/** {@link SourceCodeElement} class is a part of the source code. */
public class SourceCodeElement {
	private final int start;
	private final int type;
	private final String text;
	
	public SourceCodeElement(int start, int type, String text) {
		this.start = start;
		this.type = type;
		this.text = text;
	}
	
	public int getStart() {
		return start;
	}
	
	public int getLength() {
		return text.length();
	}
	
	public int getEnd() {
		return start + text.length() - 1;
	}
	
	public int getType() {
		return type;
	}
	
	public String getText() {
		return text;
	}
	
	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append(type);
		string.append("(");
		string.append(start);
		string.append(",");
		string.append(getEnd());
		string.append(")");
		string.append(":'");
		string.append(text);
		string.append("'");
		return string.toString();
	}
}
