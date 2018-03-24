package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.dialog.FieldDialog;
import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.eclipse.swt.graphics.Image;

/**
 * Simple {@link FieldDialog} with one {@link TreeField} showing file system.
 * @author Jean-Charles Roger
 */
public class TreeFieldDialog1 {

	public static void main(String[] args) {

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
		
		FieldDialog dialog = new FieldDialog("Test", "TreeFieldDialog1", BasicsUI.SHOW_HINTS, new CompositeField(treeField));
		int resultButton = dialog.open();
		
		System.out.println("Button index is " + resultButton);
	}

}
