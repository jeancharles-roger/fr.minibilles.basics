package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.progress.ActionMonitor;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.action.Action;
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
public class BorderFieldDialog2 {

	public static void main(String[] args) {
		final TextField northField = new TextField(null, BasicsUI.NO_INFO);
		northField.setValue("North");
		northField.setNbLines(3);
		
		final TextField eastField = new TextField(null, BasicsUI.NO_INFO);
		eastField.setValue("East");
		eastField.setNbLines(3);
		
		final TextField southField = new TextField(null, BasicsUI.NO_INFO);
		southField.setValue("South");
		southField.setNbLines(3);
		
		final TextField westField = new TextField(null, BasicsUI.NO_INFO);
		westField.setValue("West");
		westField.setNbLines(3);
		
		final DiagramField<Sequence> centerField = new DiagramField<Sequence>(new SequenceDiagram());
		centerField.setValue(SequenceDiagramApplication.createSequence());
		
		final BorderField borderField = new BorderField(BasicsUI.NONE, centerField);
		borderField.setNorth(northField, 25);
		borderField.setEast(eastField, 25);
		borderField.setSouth(southField, 25);
		borderField.setWest(westField, 25);

		borderField.addAction(createMaximizeAction("North", borderField, northField));
		borderField.addAction(createMaximizeAction("East", borderField, eastField));
		borderField.addAction(createMaximizeAction("South", borderField, southField));
		borderField.addAction(createMaximizeAction("West", borderField, westField));
		
		Shell shell = FieldShellToolkit.createShell("Test");
		FieldShellToolkit toolkit = new FieldShellToolkit(shell, "BorderFieldShell2", BasicsUI.NO_HEADER, borderField);
		toolkit.init();
		shell.setSize(600, 400);
		shell.open();
		
		Display display = shell.getDisplay();
		while ( !shell.isDisposed() ) {
			if ( !display.readAndDispatch() ) display.sleep();
		}
		
		display.dispose();
	}

	private static Action createMaximizeAction(String name, final BorderField borderField, final Field toMaximize) {
		return new Action.Stub(name) {
			public int run(ActionMonitor monitor) {
				if ( borderField.getMaximized() != toMaximize ) {
					borderField.setMaximized(toMaximize);
				} else {
					borderField.setMaximized(null);
				}
				return STATUS_OK;
			}
		};
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
