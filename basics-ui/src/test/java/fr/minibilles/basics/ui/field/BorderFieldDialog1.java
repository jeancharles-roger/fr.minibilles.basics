package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.dialog.FieldDialog;
import fr.minibilles.basics.ui.dialog.FieldShellToolkit;
import fr.minibilles.basics.ui.sequencediagram.SequenceDiagram;
import fr.minibilles.basics.ui.sequencediagram.SequenceDiagramApplication;
import fr.minibilles.basics.ui.sequencediagram.model.Sequence;
import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Simple {@link FieldDialog} with one {@link BorderField}.
 * @author Jean-Charles Roger
 */
public class BorderFieldDialog1 {

	public static void main(String[] args) {
		final TextField textField = new TextField(null, BasicsUI.NO_INFO);
		textField.setNbLines(3);
		final MultiTabField bottomTabField = new MultiTabField();
		bottomTabField.addTab(new CompositeField("Console", textField), textField.getResources().getImage("eclipse/console_view.gif"), false);
		final TreeField<File> treeField = createTreeField();
		final DiagramField<Sequence> diagramField = new DiagramField<Sequence>(new SequenceDiagram());
		diagramField.setValue(SequenceDiagramApplication.createSequence());
		
		final BorderField borderField = new BorderField(BasicsUI.NONE, diagramField);
		borderField.setWest(treeField, 20);
		borderField.setSouth(bottomTabField, 20);
		
		Shell shell = FieldShellToolkit.createShell("Test");
		FieldShellToolkit toolkit = new FieldShellToolkit(shell, "BorderFieldShell1", BasicsUI.SHOW_HINTS, borderField);
		toolkit.init();
		
		shell.open();
		
		Display display = shell.getDisplay();
		while ( !shell.isDisposed() ) {
			if ( !display.readAndDispatch() ) display.sleep();
		}
		
		display.dispose();
		
	}

	public static TreeField<File> createTreeField() {
		final TreeField<File> treeField = new TreeField<File>("Files", BasicsUI.NONE) {
			
			private DateFormat format = DateFormat.getDateTimeInstance();
			
			@Override
			public String getText(File element) {
				if ( element.isDirectory() ) {
					StringBuilder text = new StringBuilder();
					text.append(element.getName());
					text.append("/");
					return text.toString();
				}
				return element.getName();
			}
			
			@Override
			public Image getImage(File element) {
				if ( element.isDirectory() ) {
					return getResources().getImage("eclipse/fldr_obj.gif");
				} 
				String name = element.getName();
				int index = name.lastIndexOf('.');
				if ( index != -1 ) {
					return getResources().getProgramIcon(name.substring(index+1));
					
				}
				return null;
			}
			
			@Override
			public File getParent(File element) {
				return element.getParentFile();
			}
			
			@Override
			public List<File> getChildren(File element) {
				File [] files = element.listFiles();
				if ( files != null ) {
					List<File> result = new ArrayList<File>();
					for ( File child : files ) {
						if ( !child.isHidden() ) result.add(child);
					}
					return result;
				}
				return Collections.emptyList();
			}
			
			@Override
			public String getTooltip() {
				File selected = getSingleSelection();
				if ( selected != null ) {
					StringBuilder tooltip = new StringBuilder();
					tooltip.append(selected.getAbsolutePath());
					tooltip.append("\n");
					tooltip.append("Modified : ");
					tooltip.append(format.format(new Date(selected.lastModified())));
					return tooltip.toString();
				}
				return null;
			}
		};
		
		treeField.setValue(Arrays.asList(File.listRoots()));
		return treeField;
	}

}
