package fr.minibilles.basics.ui.field.text;

import fr.minibilles.basics.Basics;
import fr.minibilles.basics.error.Diagnostic;
import fr.minibilles.basics.notification.Notification;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.action.Action;
import fr.minibilles.basics.ui.field.AbstractField;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TextChangeListener;
import org.eclipse.swt.custom.TextChangedEvent;
import org.eclipse.swt.custom.TextChangingEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * TODO Big big big comment to do.
 * 
 * @author Jean-Charles Roger (jean-charles.roger@gmail.com)
 *
 */
public class SourceCodeField extends AbstractField {

	private final int TIMER = 250;
	
	protected Composite sourceComposite;
	protected StyledText styledText;
	protected StyledText errorText;
	protected Label stateLabel; 
	
	private FindAndReplaceShell findAndReplaceShell; 
	
	private int fontSize = -1;
	private FontData sourceCodeFont;
	
	private boolean editable;
	
	private CodeCompletionHandler codeCompletionHandler;
	
	private String value = null;
	
	/** Current {@link SourceCode} that handles code presentation and edition.*/
	private SourceCode sourceCode;

	/** Handles local changes to edited text */
	private SourceCodeFieldChangeHandler changeHandler;
	
	/** Runnable used to asynchronously updates value */
	private final Runnable updateValueRunnable = new Runnable() {
		public void run() {
			String newText = styledText.getText();
			int length = newText.length();
			newText = length == 0 ? null : newText;
			if ( getValue() == null ? newText != null : !getValue().equals(newText) ) {	
				setValue(newText, Notification.TYPE_UI);
			}
			styledText.redrawRange(0, length, false);
		}
	};
	
	public SourceCodeField() {
		this(SourceCode.simple);
	}
	
	public SourceCodeField(SourceCode sourceCode) {
		super(null, BasicsUI.NONE);
		this.sourceCode = sourceCode;
	}

	public SourceCode getSourceCode() {
		return sourceCode;
	}
	
	public void setSourceCode(SourceCode sourceCode) {
		// sourceCode must not be null.
		if ( sourceCode == null ) sourceCode = SourceCode.simple;
		
		// cancel update if not needed.
		if ( this.sourceCode == sourceCode ) return;
		
		// updates field
		this.sourceCode = sourceCode;
		
		refresh();
	}

	public SourceCodeFieldChangeHandler getChangeHandler() {
		return changeHandler;
	}
	
	public void setChangeHandler(SourceCodeFieldChangeHandler changeHandler) {
		this.changeHandler = changeHandler;
	}
	
	public boolean activate() {
		if ( styledText != null ) return styledText.setFocus();
		return false;
	}
	
	public void createWidget(Composite parent) {
		createLabel(parent);
		createSourceComposite(parent);
		createButtonBar(parent);
	}

	private int styledTextStyle() {
		int textStyle = SWT.V_SCROLL | SWT.H_SCROLL;
		return textStyle ;
	}

	protected void createSourceComposite(Composite parent) {
		
		sourceComposite = new Composite(parent, SWT.NONE);
		
		styledText = new StyledText(sourceComposite, styledTextStyle());
		attachFieldToWidget(styledText);
		
		findAndReplaceShell = new FindAndReplaceShell(styledText);
		
		int size = styledText.getFont().getFontData()[0].getHeight();
		if ( Basics.isMac() ) {
			sourceCodeFont = new FontData("Monaco", size, SWT.NORMAL);
		} else {
			sourceCodeFont = new FontData("Courier New", size, SWT.NORMAL);
		}
		
		
		updateFont();
		
		styledText.setText(value == null ? "" : value);
		styledText.setEditable(editable);
		
		styledText.addCaretListener(new CaretListener() {
			
			public void caretMoved(CaretEvent event) {
				int offset = event.caretOffset;
				int line = styledText.getLineAtOffset(offset);
				int column = offset - styledText.getOffsetAtLine(line);
				stateLabel.setText(sourceCode.getStateText(offset, line, column));
			}
		});
		
		styledText.getContent().addTextChangeListener(new TextChangeListener() {
			
			public void textSet(TextChangedEvent event) {
				// do nothing
			}
			
			public void textChanging(TextChangingEvent event) {
				// do nothing
			}
			
			public void textChanged(TextChangedEvent event) {
				styledText.getDisplay().timerExec(TIMER, updateValueRunnable);
			}
		});
		styledText.addLineStyleListener(new LineStyleListener() {
			public void lineGetStyle(LineStyleEvent event) {
				ArrayList<StyleRange> styles = new ArrayList<StyleRange>();
				int length = event.lineText.length();
				if ( length > 0 ) {
					List<SourceCodeElement> localElementList = sourceCode.getElementListForRange(event.lineOffset, event.lineOffset + length - 1);
					for ( SourceCodeElement element : localElementList ) {
						Color color = resources.getSystemColor(sourceCode.getSystemColor(element.getType()));
						int style = sourceCode.getStyle(element.getType());
						styles.add(new StyleRange(element.getStart(), element.getLength(), color, null , style));
					}
					
					/* For debug 
					System.out.println("--------------");
					System.out.println("Line ("+ event.lineOffset +", " + (event.lineOffset + length - 1) +"): " + event.lineText);
					for ( SourceCodeElement element : localElementList ) {
						System.out.println("- " + element);
					}
					*/
				}
				
				event.styles = styles.toArray(new StyleRange[styles.size()]);
			}
		});
		
		installBracketsHightlight();
		
		createCompletionHandler();
		
		createInfo(sourceComposite);
		errorText = new StyledText(sourceComposite, SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL );
		errorText.setBackground(sourceComposite.getBackground());
		
		stateLabel = new Label(sourceComposite, SWT.RIGHT);
		
		
		// sets layout
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, grabExcessVerticalSpace());
		data.minimumWidth = 80;
		data.minimumHeight = 80;
		data.horizontalSpan = fieldHorizontalSpan();
		data.verticalSpan = 1;
		sourceComposite.setLayoutData(data);

		sourceComposite.setLayout(AbstractField.createFieldLayout());
		styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		
		GridData errorData = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		errorData.heightHint = 16;
		errorText.setLayoutData(errorData);

		GridData stateData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		stateData.heightHint = 16;
		stateLabel.setLayoutData(stateData);
		
		fireWidgetCreation(styledText);
	}

	/**
	 * Creates {@link CodeCompletionHandler} for current {@link SourceCode}.
	 */
	private void createCompletionHandler() {
		codeCompletionHandler = new CodeCompletionHandler(sourceCode);
		codeCompletionHandler.installCompletion(styledText);
	}
	
	@Override
	protected int fieldHorizontalSpan() {
		return 2 + (hasLabel() ? 0 : 1) + (hasActionsStyled(Action.STYLE_BUTTON) ? 0 : 1);
	}

	private int closingIndex = -1;

	private void invalidateClosingIndex() {
		if ( closingIndex >= 0 && closingIndex < styledText.getCharCount() ) {
			Point point = styledText.getLocationAtOffset(closingIndex);
			styledText.redraw(point.x - 10, point.y - 8, 25, 33, false);
		}
	}

	private void installBracketsHightlight() {
		styledText.addCaretListener(new CaretListener() {
			public void caretMoved(CaretEvent event) {
				int caretOffset = event.caretOffset;
				invalidateClosingIndex();
				closingIndex = -1;
				if ( caretOffset > 0 ) {
					String text = styledText.getText();
					char carretChar = text.charAt(caretOffset - 1);
					boolean searchRight = true;
					char opposite = ' ';
					if ( '{' == carretChar ) { opposite = '}'; }
					if ( '[' == carretChar ) { opposite = ']'; }
					if ( '(' == carretChar ) { opposite = ')'; }
					if ( '<' == carretChar ) { opposite = '>'; }
					if ( '}' == carretChar ) { searchRight = false; opposite = '{'; }
					if ( ']' == carretChar ) { searchRight = false; opposite = '['; }
					if ( ')' == carretChar ) { searchRight = false; opposite = '('; }
					if ( '>' == carretChar ) { opposite = '<'; }
					if ( opposite == ' ' ) return;
					int count = 0;
					if ( searchRight ) {
						for (int i=caretOffset; i<text.length(); i++ ) {
							char c = text.charAt(i);
							if ( c == carretChar ) count++;
							if ( c == opposite ) {
								if ( count == 0) {
									closingIndex = i;
									invalidateClosingIndex();
									break;
								}
								count--;
							}
						}
					} else {
						for (int i=caretOffset-2; i>=0; i-- ) {
							char c = text.charAt(i);
							if ( c == carretChar ) count++;
							if ( c == opposite ) {
								if ( count == 0) {
									closingIndex = i;
									invalidateClosingIndex();
									break;
								}
								count--;
							}
						}
						
					}
				}
			}
		});
		
		
		styledText.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				if ( closingIndex >= 0 ) {
					GC gc = e.gc;
					gc.setLineWidth(0);
					gc.setForeground(getResources().getSystemColor(SWT.COLOR_GRAY));
					Point point = styledText.getLocationAtOffset(closingIndex);
					gc.drawRectangle(point.x-1, point.y+1, 6, 15);
				}
			}
		});

	}
	
	
	public boolean grabExcessVerticalSpace() {
		return true;
	};
	
	/** @return the field's value */
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		setValue(value, Notification.TYPE_API);
	}
	

	private void setValue(String value, int type) {
		String oldValue = this.value;
		this.value = value;
		this.closingIndex = -1;
		sourceCode.setSource(value);
		if ( styledText != null && type != Notification.TYPE_UI ) {
			String text = value == null ? "" : value;
			if ( !text.equals(styledText.getText()) ) {
				// saves caret position
				int caret = styledText.getCaretOffset();
				int top = styledText.getTopPixel();
				
				// updates text
				styledText.setText(text);
				
				// restores caret position
				if ( caret >= 0 ) {
					styledText.setCaretOffset(caret);
				}
				styledText.setTopPixel(top);
			}
		}
		
		// records change if a change handler is given
		if (changeHandler != null) {
			changeHandler.recordTextChange(oldValue, value);
		}

		// notify listeners 
		notificationSupport.fireValueNotification(type, BasicsUI.NOTIFICATION_VALUE, value, oldValue);
	}
	
	public boolean isEditable() {
		return editable;
	}
	
	public void setEditable(boolean editable) {
		this.editable = editable;
		if ( styledText != null ) styledText.setEditable(editable);
	}
	
	public String getSelectionText() {
		if ( styledText != null ) {
			return styledText.getSelectionText();
		}
		return null;
	}
	
	public void setSelection(int start, int end) {
		setSelection(start, end, true);
	}
	
	public void setSelection(int start, int end, boolean show) {
		if ( styledText != null ) {
			styledText.setSelectionRange(start, end);
			if ( show ) styledText.showSelection();
		}
	}

	public int getTopPixel() {
		if ( styledText != null ) {
			return styledText.getTopPixel();
		}
		return 0;
	}
	
	/**
	 * Scroll text to set the given pixel on top of the field.
	 * @param pixel the top.
	 */
	public void setTopPixel(int pixel) {
		if ( styledText != null ) {
			styledText.setTopPixel(pixel);
		}
	}
	
	public void refresh() {
		this.sourceCode.setSource(value);
		if ( styledText != null && styledText.isDisposed() == false ) {
			createCompletionHandler();
			styledText.redraw();
			showInfo(getDiagnostic());
		}
	}

	public void selectAll() {
		if ( styledText != null ) {
			styledText.selectAll();
		}
	}
	
	public void cut() {
		if ( styledText != null ) {
			styledText.cut();
		}
	}
	
	public void copy() {
		if ( styledText != null ) {
			styledText.copy();
		}
	}
	
	public void paste() {
		if ( styledText != null ) {
			styledText.paste();
		}
	}
	
	public int getFontSize() {
		return fontSize;
	}
	
	public void setFontSize(int fontSize) {
		if ( this.fontSize != fontSize ) {
			this.fontSize = fontSize;
			updateFont();
		}
	}
	
	private void updateFont() {
		if (styledText != null ) {
			if ( fontSize < 6 ) {
				styledText.setFont(resources.getFont(sourceCodeFont));
			} else {
				FontData newFontData = new FontData();
				newFontData.setName(sourceCodeFont.getName());
				newFontData.setLocale(sourceCodeFont.getLocale());
				newFontData.setStyle(sourceCodeFont.getStyle());
				newFontData.setHeight(fontSize);
				
				styledText.setFont(resources.getFont(newFontData));
			}
		}
	}
	
	@Override
	public Diagnostic getDiagnostic() {
		return sourceCode.getDiagnostic();
	}
	
	@Override
	protected void showInfo(Diagnostic diagnostic) {
		super.showInfo(diagnostic);
		if ( errorText != null ) {
			String message = diagnostic == null ? "" : diagnostic.getMessage();
			errorText.setText(message);
		}
	}
	
	public void openSearchAndReplaceShell() {
		if ( styledText != null ) {
			findAndReplaceShell.open();
		}
	}
	
}
