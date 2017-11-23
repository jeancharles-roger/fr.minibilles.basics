package fr.minibilles.basics.ui.field.text;

import fr.minibilles.basics.error.Diagnostic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.swt.SWT;

public abstract class SourceCode implements CompletionProposer {
	
	/** {@link Pattern} to detect delimiters for multiline constructions. */
	private final Pattern delimitersPattern;
	{
		// constructs keywords regex
		StringBuilder keywordsRegex = new StringBuilder();
		keywordsRegex.append("\\b(?:");
		for ( String keyword : getKeywords() ) {
			if (keywordsRegex.length() > 5) keywordsRegex.append("|");
			keywordsRegex.append(keyword);
		}
		keywordsRegex.append(")\\b");

		// constructs complete regex
		StringBuilder regex = new StringBuilder();
		regex.append("(?:");
		regex.append(keywordsRegex);
		for ( String expression : getExpressions() ) {
			regex.append("|");
			regex.append(expression);
		}
		regex.append(")");
		delimitersPattern = Pattern.compile(regex.toString());
	}
	
	
	private String source;
	private List<SourceCodeElement> elementList = new ArrayList<SourceCodeElement>();

	public String getSource() {
		return source;
	}
	
	public void setSource(String source) {
		this.source = source;
		updateSourceCodeElementList();
	}
	
	protected void resetElementList() {
		elementList = new ArrayList<SourceCodeElement>();
	}
	/** Construct element list from current source. */
	protected void updateSourceCodeElementList() {
		resetElementList();
		if ( source != null ) {
			Matcher matcher = delimitersPattern.matcher(source);
			Stack<Integer> typeStack = new Stack<Integer>();
			typeStack.push(getDefaultStyle());
			int previous = 0;
			while ( matcher.find() ) {
				int groupStart = matcher.start();
				String group = matcher.group();
				previous = handleGroup(previous, typeStack, group, groupStart);
			}
			// handle end element
			addSourceCodeElement(new SourceCodeElement(previous, typeStack.pop(), source.substring(previous)));
		}
	}

	/** 
	 * Handles a group (separator) to create an {@link SourceCodeElement}
	 * (must be added using {@link #addSourceCodeElement(SourceCodeElement)}).
	 *
	 * @param lastIndex 
	 * 		It contains the index in the source code where {@link SourceCodeElement}
	 * 		analysis stops. The next element must start with this index.
	 * @param typeStack 
	 * 		stack of types that are contextual. For instances multiline comments.
	 * @param group 
	 * 		the matched group (separator).
	 * @param groupStart 
	 * 		the index where the group was matched.
	 * 
	 * @return 
	 * 		the new index to use to create the next element.
	 */
	protected abstract int handleGroup(final int lastIndex, Stack<Integer> typeStack, String group, int groupStart);
	
	/** Protected element add. */
	protected void addSourceCodeElement(SourceCodeElement element) {
		String text = element.getText();
		if ( text == null || text.length() == 0 ) return;
		elementList.add(element);
	}
	
	protected String substring(int startIndex) {
		return source.substring(startIndex);
	}
	
	protected String substring(int startIndex, int endIndex) {
		return source.substring(startIndex, endIndex);
	}
	
	public List<SourceCodeElement> getSourceCodeElementList() {
		return Collections.unmodifiableList(elementList);
	}
	
	/**
	 * Returns the {@link SourceCodeElement} that defines the given interval. The element
	 * indexes may be larger than given one. 
	 */
	public List<SourceCodeElement> getElementListForRange(int startIndex, int endIndex) {
		// defensive code
		if ( source == null ) return Collections.emptyList();
		if ( startIndex >= source.length() || startIndex < 0  ) return Collections.emptyList();
		if ( endIndex >= source.length() || endIndex < 0  ) return Collections.emptyList();

		final List<SourceCodeElement> result = new ArrayList<SourceCodeElement>();
		final int size = elementList.size();
		
		// searches for beginning 
		int i=0;
		
		for ( ; i<size; i++ ) {
			final SourceCodeElement element = elementList.get(i);
			if ( element.getStart() > startIndex ) {
				// we just passed the first one, get back once.
				// by construction, i is always greater than 1 here.
				i-=1;
				break;
			}
		}

		if ( i == size ) {
			// we are at the end, adds the last element
			result.add(elementList.get(size-1));
			
		} else {
			// adds element until the end
			for ( ; i<size; i++ ) {
				final SourceCodeElement element = elementList.get(i);
				result.add(element);
				
				if ( endIndex >= element.getStart() && 
					 endIndex < element.getEnd()
				) break;
			}
		}

		return result;
	}
	
	public abstract int getDefaultStyle();
	public abstract String[] getKeywords();
	public abstract String[] getExpressions();

	public boolean isKeyword(String word) {
		for ( String keyword : getKeywords() ) {
			if (keyword.equals(word) ) return true;
		}
		return false;
	}
	
	public abstract int getSystemColor(int type);
	public abstract int getStyle(int type);
	
	
	public CompletionProposal[] getProposals(String text, int index, String lastWord) {
		List<CompletionProposal> proposals = new ArrayList<CompletionProposal>();
		for ( String string : getKeywords() ) {
			if ( string.startsWith(lastWord) ) {
				CompletionProposal proposal = new CompletionProposal(lastWord.length(), string);
				proposals.add(proposal);
			}
		}
		return proposals.toArray(new CompletionProposal[proposals.size()]);
	}

	public String getStateText(int offset, int line, int column) {
		StringBuilder text = new StringBuilder();
		text.append("line ");
		text.append(line+1);
		text.append(" : column ");
		text.append(column+1);
		return text.toString();
	}
	
	public Diagnostic getDiagnostic() {
		return null;
	}
	
	/**
	 * Simple {@link SourceCode} that doesn't present coloration.
	 */
	public static final SourceCode simple = new SourceCode() {
		
		protected void updateSourceCodeElementList() {
			resetElementList();
			final String value = getSource();
			if ( value != null ) {
				addSourceCodeElement(new SourceCodeElement(0, value.length(), value));
			}
		}
		
		@Override
		protected int handleGroup(int lastIndex, Stack<Integer> typeStack, String group, int groupStart) {
			throw new RuntimeException("SourceCode.simple:handleGroup shoudn't be called.");
		}
		
		@Override
		public int getSystemColor(int type) {
			return SWT.COLOR_BLACK;
		}
		
		@Override
		public int getStyle(int type) {
			return SWT.NONE;
		}
		
		@Override
		public String[] getKeywords() {
			return new String[0];
		}
		
		@Override
		public String[] getExpressions() {
			return new String[0];
		}
		
		@Override
		public int getDefaultStyle() {
			return SWT.NONE;
		}
	};
	
}
