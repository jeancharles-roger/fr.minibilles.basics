package fr.minibilles.basics.ui.sequencediagram;

import fr.minibilles.basics.ui.Resources;
import fr.minibilles.basics.ui.sequencediagram.model.HorizontalLine;
import fr.minibilles.basics.ui.sequencediagram.model.LifeLine;
import fr.minibilles.basics.ui.sequencediagram.model.Message;
import fr.minibilles.basics.ui.sequencediagram.model.Pause;
import fr.minibilles.basics.ui.sequencediagram.model.Sequence;
import fr.minibilles.basics.ui.sequencediagram.model.SequenceXmlParser;
import java.io.IOException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.xml.sax.SAXException;

public class SequenceDiagramApplication {
	private Resources resources;
	
	private Display display;
	private Shell shell;
	private SequenceDiagramController diagramController;
	private Sequence sequence;
	
	public SequenceDiagramApplication(Sequence sequence) {
		this.sequence = sequence;
	}
	
	private Shell createEditorShell() {

		// shell
		shell = new Shell(display);
		shell.setLayout(new FillLayout());
		shell.setText("Sequence diagram");

		// - diagram
		Composite diagramComposite = new Composite(shell, SWT.NONE);
		createEditorControl(diagramComposite);

		return shell;
	}

	private void createEditorControl(final Composite composite) {
		composite.setLayout(new FillLayout());
		diagramController = new SequenceDiagramController(sequence);
		diagramController.createComposite(composite);
	}
	
	public void start() {
		
		display = Display.getDefault();
		resources = Resources.getInstance(Resources.class);
		createEditorShell();

		// open shell
		shell.open();
		
		// main event loop.
		while (!shell.isDisposed()) {
			try {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
	

	public Display getDisplay() {
		return display;
	}
	
	public Resources getResources() {
		return resources;
	}
	
	public static void main(String[] args) throws SAXException, IOException {
		//Sequence sequence = createSequence();
		//Sequence sequence = SequenceXmlParser.parseSequence("test/diagram/cdl.msc");
		if ( args.length != 1 ) {
			System.err.println("Usage: MscViewer file.");
			return;
		}
		Sequence sequence = SequenceXmlParser.parseSequence(args[0]);
		SequenceDiagramApplication app = new SequenceDiagramApplication(sequence);
		app.start();
	}
	
	public static Sequence createSequence() {
		Sequence sequence = new Sequence();
		final LifeLine producer = new LifeLine(sequence, "producer", 100f);
		final LifeLine bus = new LifeLine(sequence, "bus", 300f);
		final LifeLine consumer = new LifeLine(sequence, "consumer", 500f);

		sequence.addLine(producer);
		sequence.addLine(bus);
		sequence.addLine(consumer);
		
		for ( int i=0; i<50; i++ ) {
			sequence.addItem(new HorizontalLine(sequence, "Produce a then b"));
			sequence.addItem(new Message(sequence, "a", producer, bus));
			sequence.addItem(new Message(sequence, "b", producer, bus));
			sequence.addItem(new Message(sequence, "a", bus, consumer));
			sequence.addItem(new Message(sequence, "ok", consumer, bus));
			sequence.addItem(new Message(sequence, "ok", bus, producer));
			sequence.addItem(new Message(sequence, "b", bus, consumer));
			sequence.addItem(new Message(sequence, "ok", consumer, bus));
			sequence.addItem(new Message(sequence, "ok", bus, producer));
			sequence.addItem(new Pause(sequence));
		}
		
		return sequence;
	}
}
