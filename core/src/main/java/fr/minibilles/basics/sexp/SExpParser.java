package fr.minibilles.basics.sexp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Stack;

public class SExpParser {
	
	private final String encoding;
	private Reader reader;
	
	private boolean eof = false;
	private char lookAheadChar = '\0';

	public SExpParser() {
		this("UTF-8");
	}
	
	public SExpParser(String encoding) {
		this.encoding = encoding;
	}
	
	/** Initializes parser to read in given stream. 
	 * @throws IOException */
	public SExp parse(InputStream stream) throws IOException {
		this.reader = new BufferedReader(new InputStreamReader(stream, encoding));

		// init token reader
		basicReadChar();
		
		// creates stack of slist
		Stack<SList> sListStack = new Stack<SList>();
		
		// root is a fake SList that will contains the stream contents as first child
		SList rootSList = new SList();

		// current list where to add newly created element.
		SList currentSList = rootSList;
		sListStack.push(currentSList);
		
		while (eof == false) {
			String next = nextToken();
			
			if ( next.equals("(") ) {
				// new SList
				SList newSList = new SList();
				currentSList.addChild(newSList);
				sListStack.push(newSList);
				currentSList = newSList;
				
			} else if (next.equals(")") ) {
				// end of SList
				if ( sListStack.size() < 1 ) {
					throw new IOException("Unexpected parenthesis ')'.");
				}
				sListStack.pop();
				currentSList = sListStack.peek();
				
			} else if (next.startsWith("${") && next.endsWith("}") ) {
				// SVariable
				String name = next.substring(2, next.length() - 1);
				currentSList.addChild(new SVariable(name));
			} else {
				// SAtom
				currentSList.addChild(new SAtom(next));
			}
			
		}
		
		// if there is another SList than root
		if ( sListStack.size() != 1 ) {
			throw new IOException("Missing parenthesis ')'.");
		}
		
		return sListStack.peek().getChild(0);
	}

	/**
	 * <p>
	 * Reads the next token from stream. It considers the tokens separated by
	 * '(', ')', ' ', tab, new line and '''. One separator is one token, nothing is lost,
	 * everything in the buffer will appear as tokens.
	 * </p>
	 * 
	 * @return the next token in the stream, null if eof is reached
	 * @throws IOException 
	 */
	private String nextToken() throws IOException {
		if ( eof ) return null;
		StringBuilder tokenBuffer = new StringBuilder();
		while (true) {
			if ( eof ) return tokenBuffer.toString();
			
			switch (lookAheadChar) {
			case '(': case ')':  
				if ( tokenBuffer.length() > 0 && tokenBuffer.charAt(0) == '\'' ) {
					// inside a string
					tokenBuffer.append(lookAheadChar);
					basicReadChar();
				} else {
					if (tokenBuffer.length() == 0) {
						tokenBuffer.append(lookAheadChar);
						basicReadChar();
					}
					return tokenBuffer.toString();
				}
				break;
				
			case ' ': case '\t': case '\n': case '\r':
				if ( tokenBuffer.length() > 0 && tokenBuffer.charAt(0) == '\'' ) {
					// inside a string
					tokenBuffer.append(lookAheadChar);
					basicReadChar();
				} else {
					basicReadChar();
					if ( tokenBuffer.length() > 0 ) {
						return tokenBuffer.toString();
					}
				}
				break;
				
			case '\'':
				tokenBuffer.append(lookAheadChar);
				basicReadChar();
				if (tokenBuffer.length() > 1 ) {
					return tokenBuffer.toString();
				}
				break;

			case '\\':
				// escape character, add next char
				// adds \
				tokenBuffer.append(lookAheadChar);
				basicReadChar();
				
				if ( tokenBuffer.length() > 0 && tokenBuffer.charAt(0) == '\'' ) {
					// inside a string
					// adds escaped character
					tokenBuffer.append(lookAheadChar);
					basicReadChar();
				}
				break;
				
			default: 
				tokenBuffer.append(lookAheadChar);
				basicReadChar();
				break;
			}
		}
	}
	
	private void basicReadChar() throws IOException {
		final int read = reader.read();
		eof = read == -1;
		lookAheadChar = (char) read;
	}

}
