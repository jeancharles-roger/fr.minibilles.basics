package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.ui.BasicsUI;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class ConsoleField extends AbstractField {

	private static final int PRINT_WRITER_BUFFER_SIZE = 128;
	
	private StyledText styledText;
	
	public ConsoleField(String label, int style) {
		super(label, style);
	}
	
	public ConsoleField() {
		this(null, BasicsUI.NO_INFO);
	}

	public boolean activate() {
		if ( styledText != null ) {
			return styledText.setFocus();
		}
		return false;
	}
	
	@Override
	public void createWidget(Composite parent) {
		super.createWidget(parent);
		createLabel(parent);
		createInfo(parent);
		createText(parent);
		createButtonBar(parent);
	}
	
	private void createText(Composite parent) {
		styledText = new StyledText(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY | SWT.MULTI);
		attachFieldToWidget(styledText);

		styledText.setEnabled(isEnable());
		createMenu(styledText);

		styledText.setBackground(parent.getBackground());
		// sets layout
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, grabExcessVerticalSpace());
		data.minimumWidth = 80;
		data.minimumHeight = 80;
		data.horizontalSpan = fieldHorizontalSpan();
		data.verticalSpan = 1;
		styledText.setLayoutData(data);
		
		fireWidgetCreation(styledText);
	}
	
	@Override
	public boolean grabExcessVerticalSpace() {
		return true;
	}
	
	public void log(String text) {
		if (styledText != null && !styledText.isDisposed() && text.length() > 0 ) {
			styledText.append(text);
		}
	}
	
	public void log(String text, int color, int style) {
		if (styledText != null && !styledText.isDisposed() && text.length() > 0 ) {
			int start = styledText.getCharCount();
			
			styledText.append(text);
			StyleRange range = new StyleRange(start, text.length(), getResources().getSystemColor(color), null, style );
			styledText.setStyleRange(range);
		}
	}
	
	public void scrollToTheEnd() {
		if (styledText != null && !styledText.isDisposed() ) {
			styledText.setTopIndex(styledText.getLineCount() - 1);
		}
	}
	
	public void clear() {
		styledText.setText("");
	}
	
	@Override
	public void setEnable(boolean enable) {
		if ( styledText != null ) styledText.setEnabled(enable);
		super.setEnable(enable);
	}
	
	/**
	 * Writes console current contents to give {@link OutputStream}.
	 * @param stream an opened stream (doesn't close it).
	 * @param charsetName charset to use
	 * @throws IOException if it doesn't work ...
	 */
	public void writeContents(OutputStream stream, String charsetName) throws IOException {
		stream.write(styledText.getText().getBytes(charsetName));
	}
	
	/**
	 * Creates a {@link PrintWriter} that will write on view's console.
	 * @param color Color to use (in {@link SWT}.COLOR_* selection only).
	 * @param style some of the flags {@link SWT#BOLD}, {@link SWT#ITALIC}, ...
	 * @return a {@link PrintWriter} (never null).
	 */
	public PrintWriter createPrintWriter(final int color, final int style) {
		return createPrintWriter(false, color, style);
	}

	public PrintWriter createPrintWriter(final boolean scrollToTheEnd, final int color, final int style) {
		return new PrintWriter(new BufferedWriter(new Writer() {
			private final Display display = styledText.getDisplay();

			private StringBuilder stringBuffer = new StringBuilder();
			private boolean closed = false;

			private final Runnable autoFlushRunnable = new Runnable() {
				public void run() {
					try {
						flush(true);
						if ( display.isDisposed() == false ) {
							display.timerExec(1000, this);
						}
					} catch (IOException e) {
						// in case of error, doesn't repost timer
					}
				}
			};

			{
				// post timer
				display.asyncExec(autoFlushRunnable);
			}
			
			@Override
			public void write(final char[] buffer, final int offset, final int length) throws IOException {
				final String text = new String(buffer, offset, length);
				stringBuffer.append(text);
				if ( stringBuffer.length() > PRINT_WRITER_BUFFER_SIZE ) flush(false);
			}
			
			@Override
			public void flush() throws IOException {
				flush(false);
			}

			private void flush(boolean force) throws IOException {
				if ( stringBuffer.length() > PRINT_WRITER_BUFFER_SIZE || force ) {
					if ( stringBuffer == null || styledText == null ) return;
					final String text = stringBuffer.toString(); 
					stringBuffer = new StringBuilder();
					
					if ( styledText.isDisposed() || closed ) return;
	
					display.asyncExec(new Runnable() {
						public void run() {
							log(text, color, style);
							if ( scrollToTheEnd ) scrollToTheEnd();
						}
					});
				}
			}
			
			@Override
			public void close() throws IOException {
				flush(true);
				closed = true;
			}
		}));
	}

	/**
	 * Creates a {@link PrintWriter} that will write on view's console.
	 * @return a {@link PrintWriter} (never null).
	 */
	public PrintWriter createPrintWriter() {
		return createPrintWriter(false);
	}
	
	public PrintWriter createPrintWriter(final boolean scrollToTheEnd) {
		return new PrintWriter(new BufferedWriter(new Writer() {
			private final Display display = styledText.getDisplay();
			
			private StringBuilder stringBuffer = new StringBuilder();
			private boolean closed = false;

			private final Runnable autoFlushRunnable = new Runnable() {
				public void run() {
					try {
						flush(true);
						if ( display.isDisposed() == false ) {
							display.timerExec(1000, this);
						}
					} catch (IOException e) {
						// in case of error, doesn't repost timer
					}
				}
			};

			{
				// post timer
				display.asyncExec(autoFlushRunnable);
			}

			@Override
			public void write(final char[] buffer, final int offset, final int length) throws IOException {
				final String text = new String(buffer, offset, length);
				stringBuffer.append(text);
				if ( stringBuffer.length() > PRINT_WRITER_BUFFER_SIZE ) flush(false);
			}
			
			@Override
			public void flush() throws IOException {
				flush(false);
			}

			private void flush(boolean force) throws IOException {
				if ( stringBuffer.length() > PRINT_WRITER_BUFFER_SIZE || force ) {
					if ( stringBuffer == null || styledText == null ) return;
					final String text = stringBuffer.toString(); 
					stringBuffer = new StringBuilder();
					
					if ( styledText.isDisposed() || closed ) return;
	
					display.asyncExec(new Runnable() {
						public void run() {
							log(text);
							if ( scrollToTheEnd ) scrollToTheEnd();
						}
					});
				}
			}
			
			@Override
			public void close() throws IOException {
				flush(true);
				closed = true;
			}
		}));
	}
}
