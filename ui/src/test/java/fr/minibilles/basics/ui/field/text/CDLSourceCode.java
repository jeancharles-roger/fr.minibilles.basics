package fr.minibilles.basics.ui.field.text;

import fr.minibilles.basics.error.Diagnostic;
import java.util.Stack;
import org.eclipse.swt.SWT;

public class CDLSourceCode extends SourceCode {

	public final static int CDL = 1;
	public final static int COMMENT = 2;
	public final static int PID = 5;
	public final static int KEYWORD = 6;
	
	private String[] keywords;
		
	private String[] expressions;

	protected int handleGroup(final int lastIndex, Stack<Integer> typeStack, String group, int groupStart) {
		// all possible cases
		if ( group.equals("/*") ) {
			// comment start.
			addSourceCodeElement(new SourceCodeElement(lastIndex, typeStack.peek(), substring(lastIndex, groupStart)));
			typeStack.push(COMMENT);
			return groupStart;
			
		} else if ( group.equals("*/") ) { 
			// comment end.
			addSourceCodeElement(new SourceCodeElement(lastIndex, typeStack.peek(), substring(lastIndex, groupStart+2)));
			if ( typeStack.peek() == COMMENT) typeStack.pop();
			return groupStart+2;
			
		} else if ( group.startsWith("//") ) {
			// one line comment.
			addSourceCodeElement(new SourceCodeElement(lastIndex, typeStack.peek(), substring(lastIndex, groupStart)));
			addSourceCodeElement(new SourceCodeElement(groupStart, COMMENT, group));
			return groupStart+group.length();
			
		} else if ( group.startsWith("{") && group.lastIndexOf("}") != -1 ) {
			// pid
			addSourceCodeElement(new SourceCodeElement(lastIndex, typeStack.peek(), substring(lastIndex, groupStart)));
			addSourceCodeElement(new SourceCodeElement(groupStart, PID, group));
			return groupStart+group.length();
			
		} else if ( typeStack.peek() != COMMENT && isKeyword(group) ) {
			// keyword outside of LUA code.
			addSourceCodeElement(new SourceCodeElement(lastIndex, typeStack.peek(), substring(lastIndex, groupStart)));
			addSourceCodeElement(new SourceCodeElement(groupStart, KEYWORD, group));
			return groupStart+group.length();
		}
		return lastIndex;
	}
	
	@Override
	public int getDefaultStyle() {
		return CDL;
	}
	
	@Override
	public String[] getKeywords() {
		if ( keywords == null ) {
			keywords = new String[] {
						// declaration
						"activity",
						"event",
						"property",
						"pattern",
						"cdl",
						"variable",
						"is",

						// all
						"options",
						
						// event
						"send",
						"to",
						"receive",
						"from",
						"informal",
						"becomes",

						// activity
						"atomic",
						"loop",
						"skip",

						// cdl
						"main",
						"init",
						"assert",
						"properties",
						
						// expression
						"true",
						"false",
						"any",
						"and",
						"or",
						"not",
					};
		}
		return keywords;
	}

	@Override
	public String[] getExpressions() {
		if ( expressions == null ) {
			expressions = new String[] {
					// pid
					"\\{[a-zA-Z_][a-zA-Z0-9]*\\}[0-9]+",
					// one line comment
					"//.*",
					// comment separator
					"/\\*", 
					"\\*/" 
				};
		}
		return expressions;
	}
	
	@Override
	public int getSystemColor(int type) {
		switch (type) {
		case COMMENT:
			return SWT.COLOR_DARK_GRAY;
		case PID:
			return SWT.COLOR_DARK_MAGENTA;
		case KEYWORD:
			return SWT.COLOR_DARK_BLUE;
		default:
			return SWT.COLOR_BLACK;
		}
	}
	
	@Override
	public int getStyle(int type) {
		switch (type) {
		case PID:
			return SWT.ITALIC;
		
		case KEYWORD:
			return SWT.BOLD | SWT.ITALIC;
		
		case COMMENT:
		default:
			return SWT.NONE;
		}
	}
	
	@Override
	public Diagnostic getDiagnostic() {
		if ( getSource() != null && getSource().length() > 0 ) {
			return new Diagnostic.Stub(Diagnostic.ERROR, "Error\nLine 2");
		}
		return null;
	}
}
