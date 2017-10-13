/**
 * 
 */
package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.ui.BasicsUI;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Jean-Charles Roger
 */
public class MultiPageField extends VirtualMultiPageField {

	protected final List<Field> pageList = new ArrayList<Field>();

	public MultiPageField(String label, int style, Field ... pages) {
		super(label, style); 
		if ( pages != null ) {
			for ( Field onePage : pages ) pageList.add(onePage);
			super.setFieldCount(pages.length, false);
		}
	}
	
	public MultiPageField(Field ... pages) {
		this(null, BasicsUI.NONE, pages);
	}

	@Override
	public void setFieldCount(int fieldCount, boolean clear) {
		throw new UnsupportedOperationException("MultiPageField can't change field count.");
	}
	
	/* (non-Javadoc)
	 * @see fr.minibilles.basics.ui.field.VirtualMultiPageField#getField(int)
	 */
	@Override
	protected Field getField(int i) {
		return pageList.get(i);
	}
	
}
