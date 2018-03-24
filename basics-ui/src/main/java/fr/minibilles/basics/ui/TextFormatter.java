package fr.minibilles.basics.ui;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * TextFormatter breaks texts into lines that fit into a given width.
 * <p>
 * First the text is divided into paragraphs by searching occurrences of
 * newlines, i.e. "\n" or "\r\n". The last paragraph may or may not be
 * terminated by a newline. Then each paragraph is split into lines that fit
 * within the specified width.
 * <p>
 * Typical usage:
 * <pre> TextFormatter tf = new TextFormatter(gc);
 * List&lt;TextLine> lines = tf.format(string, width));
 * DrawingKit.drawParagraph(gc, lines, x, y, textAlign, lineSkip, false);</pre>
 * <p>Formatter customization: following methods are intended to be redefined by subclasses:<br>
 * {@link TextFormatter#isPreferredCut(String, int)},
 * {@link TextFormatter}{@link #getLineWidth(int)}
 * @author Didier Simoneau
 */
public class TextFormatter {
	
	/** A line of text in a formatted paragraph. */
	public static class TextLine {
		public String text = "";
		public int width;
		public int height;
		public int selectionBgn = -1;
		public int selectionEnd = -1;
	}

	public static interface TextMetric {
		int[] stringExtent(String string);
	}
	
	/** The gc used to measure text extents. */
	public final GC gc;

	/** The average character width, measured in pixels, of the current font of the gc. */
	public final double averageCharWidth;
	
	/** The formatting width measured in pixels. */
	public int width;
	
	/** Verbose option flag, for debugging purposes. */
	public boolean verbose = false;
	
	protected String text;

	protected int textBgn;

	protected int textEnd;
	
	protected int selectionBgn;
	
	protected int selectionEnd;
	
	protected List<TextLine> lines = new ArrayList<TextLine>();
	
	/**
	 * Creates a new instance. The same instance can be safely reused for
	 * several text format operations.
	 * @param gc the metric used to measure text extents
	 */
	public TextFormatter(GC gc) {
		this.gc = gc;
		averageCharWidth = gc.stringExtent("nnnnnnnnnn").x / 10.0;
	}

	/**
	 * Formats a text into lines.
	 * @param text the text to format
	 * @param width the width in which each line of text must fit
	 * @return the formatted text lines
	 */
	public List<TextLine> formatText(String text, int width) {
		return formatText(text, width, -1, -1); // no selection
	}
	
	/**
	 * Formats a text into lines.
	 * @param text the text to format
	 * @param width the width in which each line of text must fit
	 * @param selectionRange start and end of selected zone
	 * @return the formatted text lines
	 */
	public List<TextLine> formatText(String text, int width, int[] selectionRange) {
		if (selectionRange == null) return formatText(text, width);
		return formatText(text, width, selectionRange[0], selectionRange[1]);
	}
	
	/**
	 * Formats a text into lines.
	 * @param text the text to format
	 * @param width the width in which each line of text must fit
	 * @param selectionBgn start of selected zone
	 * @param selectionEnd end of selected zone
	 * @return the formatted text lines
	 */
	public List<TextLine> formatText(String text, int width, int selectionBgn, int selectionEnd) {
		// initialize state
		this.text = text;
		this.textBgn = 0;
		this.textEnd = text.length();
		this.width = width;
		this.selectionBgn = selectionBgn;
		this.selectionEnd = selectionEnd;
		lines.clear();
		if (verbose) System.out.println("averageCharWidth: " + averageCharWidth);
		// main loop: splits into paragraphs
		int parBgn = textBgn;
		for (int i = textBgn; i < textEnd; i++) {
			if (text.charAt(i) == '\n') {
				int parEnd = i; // the final '\n' is excluded from the range
				if (parEnd - 1 >= parBgn && text.charAt(parEnd - 1) == '\r') parEnd--; // also discard any '\r' before the '\n'
				processParagraph(parBgn, parEnd);
				parBgn = i + 1;
			}
		}
		if (parBgn < textEnd) processParagraph(parBgn, textEnd);
		return lines;
	}
	
	/**
	 * Format a paragraph specified by the given character range. This range does not include any '\n'.
	 * @param parBgn the beginning index of the paragraph, inclusive
	 * @param parEnd the ending index of the paragraph, exclusive
	 */
	protected void processParagraph(int parBgn, int parEnd) {
		if (verbose) display("p", parBgn, parEnd, getLineWidth(0));
		if (parBgn == parEnd) {
			// trivial case of empty line:
			TextLine line = new TextLine();
			line.height = gc.stringExtent("").y;
			lines.add(line);
			return;
		}
		for (int i = parBgn; i < parEnd; i = processOneLine(i, parEnd));
	}

	/**
	 * Format a line.
	 * @param lineBgn the beginning index of the line, inclusive
	 * @param parEnd the ending index of the paragraph, exclusive.
	 * @return
	 */
	protected int processOneLine(int lineBgn, int parEnd) {
		int currentLineWidth = getLineWidth(lines.size());
		if (currentLineWidth < 0) currentLineWidth = 0;
		int lineEndOv = lineBgn + (int) Math.ceil(currentLineWidth / averageCharWidth * 1.2); // initial, over estimated guess
		if (lineEndOv > parEnd) lineEndOv = parEnd; // end of par reached
		String s = text.substring(lineBgn, lineEndOv);
		Point ext = gc.stringExtent(s);
		if (verbose) display("guess", lineBgn, lineEndOv, ext.x);
		// progress to the right until overflow, or parEnd reached:
		while (ext.x <= currentLineWidth && lineEndOv < parEnd) { 
			int incr = (int) Math.ceil((currentLineWidth - ext.x) / averageCharWidth);
			if (incr == 0) incr = 1; // at least one
			lineEndOv = lineEndOv + incr;
			if (lineEndOv > parEnd) lineEndOv = parEnd; // end of par reached
			s = text.substring(lineBgn, lineEndOv);
			ext = gc.stringExtent(s);
			if (verbose) display("right", lineBgn, lineEndOv, ext.x);
		}
		// progress to the left until fitting:
		int lineEnd = lineEndOv;
		while (ext.x > currentLineWidth && lineEnd > lineBgn) {
			lineEnd = previousCut(lineEnd, lineBgn);
			s = text.substring(lineBgn, lineEnd);
			ext = gc.stringExtent(s);
			if (verbose) display("left1", lineBgn, lineEnd, ext.x);	
		}
		if (lineEnd == lineBgn) {
			// no convenient cut; retry char by char; keep at least one char
			lineEnd = lineEndOv;
			while (true) {
				s = text.substring(lineBgn, lineEnd);
				ext = gc.stringExtent(s);
				if (verbose) display("left2", lineBgn, lineEnd, ext.x);	
				if (ext.x <= currentLineWidth || lineEnd <= lineBgn + 1) break;
				lineEnd--;
			}
		}
		int nextLineBgn = lineEnd;
		// remove white chars at end of line:
		while ((lineEnd - 1 >= lineBgn && Character.isWhitespace(text.charAt(lineEnd - 1)))) {
			lineEnd--;
			s = text.substring(lineBgn, lineEnd);
			ext = gc.stringExtent(s);
		}
		TextLine line = new TextLine();
		line.text = s;
		line.width = ext.x;
		line.height = ext.y;
		if (selectionBgn >=0 || selectionEnd >=0) {
			line.selectionBgn = selectionBgn - lineBgn;
			line.selectionEnd = selectionEnd - lineBgn;
		}
		lines.add(line);
		// remove white chars at begin of next line:
		while (nextLineBgn < parEnd && Character.isWhitespace(text.charAt(nextLineBgn))) nextLineBgn++;
		return nextLineBgn;
	}

	protected int previousCut(int index, int lineBgn) {
		while (true) {
			index--;
			if (index <= lineBgn) return lineBgn;
			if (isPreferredCut(text, index)) return index;
		}
	}

	protected void display(String tag, int beginIndex, int endIndex, int w) {
		System.out.printf("<%s w=%d>%s</%s>\n", tag, w, text.substring(beginIndex, endIndex), tag);
	}

	/**
	 * Subclasses can redefine this method.
	 * @param s
	 * @param i
	 * @return
	 */
	public boolean isPreferredCut(String s, int i) {
		char charBefore = s.charAt(i - 1);
		char charAfter = s.charAt(i);
		if (Character.isLetterOrDigit(charBefore) != Character.isLetterOrDigit(charAfter)) {
			if (charAfter == '.' || charAfter == ',' || charAfter == ';') return false; // keep punctuation on same line
			return true;
		}
		if (Character.isLowerCase(charBefore) && Character.isUpperCase(charAfter)) return true;
		return false;
	}

	/**
	 * Subclasses can redefine this method.
	 * @param lineNumber the zero-based line number
	 * @return the width of the line
	 */
	public int getLineWidth(int lineNumber) {
		return width;
	}
	
}
