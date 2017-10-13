/**
 * 
 */
package fr.minibilles.basics.ui.field.swt;

import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.field.AbstractField;
import fr.minibilles.basics.ui.field.CompositeField;
import fr.minibilles.basics.ui.field.Field;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * {@link CompositeField} that use a {@link GridLayout}.
 * It's SWT specific. It can be reproduced in other framework, but not used as if.
 * 
 * @author Jean-Charles Roger
 *
 */
public class GridCompositeField extends CompositeField {

	protected final List<GridData> gridDataList = new ArrayList<GridData>();

	private int numColumns = 1;
	private boolean makeColumnsEqualWidth = false;
	
	public GridCompositeField() {
		this(null, BasicsUI.NONE);
	}
	
	public GridCompositeField(String label, int style) {
		super(label, style);
	}

	public void addField(Field field, GridData data) {
		super.addField(field);
		gridDataList.add(data);
	}
	
	public void addField(Field field) {
		addField(field, null);
	}
	
	public int getNumColumns() {
		return numColumns;
	}
	
	public void setNumColumns(int numColumns) {
		this.numColumns = numColumns;
	}
	
	public boolean isMakeColumnsEqualWidth() {
		return makeColumnsEqualWidth;
	}
	
	public void setMakeColumnsEqualWidth(boolean makeColumnsEqualWidth) {
		this.makeColumnsEqualWidth = makeColumnsEqualWidth;
	}
	
	@Override
	public void createWidget(Composite parent) {
		// replaces the call to super 
		parent.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				dispose();
			}
		});
		// ends of super 
		
		
		Composite composite = null;
		if ( hasStyle(BasicsUI.GROUP) ) {
			composite = new Group(parent, SWT.NONE);
			if (getLabel() != null ) ((Group) composite).setText(getLabel());
		} else {
			composite = new Composite(parent, SWT.NONE);
		}
		
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, grabExcessVerticalSpace(), 4, 1));
		composite.setLayout(new GridLayout(numColumns, makeColumnsEqualWidth));
		
		for ( int i=0; i < fieldList.size(); i++ ) {
			final Composite subComposite = new Composite(composite, SWT.NONE);
			GridData subData = gridDataList.get(i);
			final AbstractField abstractField = (AbstractField) fieldList.get(i);
			if ( subData == null ) {
				subData = new GridData(SWT.FILL, SWT.FILL, true, abstractField.grabExcessVerticalSpace());
			}
			subComposite.setLayoutData(subData);
			subComposite.setLayout(createFieldLayout());
			abstractField.createWidget(subComposite);
		}
	}


}
