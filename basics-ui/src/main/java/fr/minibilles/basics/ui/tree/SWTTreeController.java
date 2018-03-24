package fr.minibilles.basics.ui.tree;

import java.util.Arrays;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;



/**
 * A tree controller. Manages a SWT Tree using a {@link TreePresenter} that
 * provides information to be displayed.
 * @author dsimoneau
 * @param <E> element type
 * @param <I> input type
 */
public class SWTTreeController<I, E> extends TreeController<I, E, TreeItem> {

	/** The SWT Tree control. */
	public final Tree tree;
	
	/** Constructor. */
	public SWTTreeController(Tree tree, TreePresenter<I, E> presenter) {
		super(presenter);
		this.tree = tree;
		this.installListeners();
	}

	@Override
	public TreeItem getParentItem(TreeItem item) {
		return item.getParentItem();
	}

	public List<TreeItem> getChildItems(TreeItem parentItem) {
		if (childrenArePending(parentItem)) doRefreshChildren(parentItem);
		TreeItem[] items = parentItem == null ? tree.getItems() : parentItem.getItems();
		return Arrays.asList(items);
	}
	
	@SuppressWarnings("unchecked")
	public E getElement(TreeItem item) {
		return (E) item.getData();
	}

	public void setInput(I input) {
		disposeAll(tree.getItems());
		for (E element : (List<? extends E>) getPresenter().getRoots(input)) {
			TreeItem item = new TreeItem(tree, SWT.NONE);
			initializeItem(item, element);
			if (getPresenter().hasChildren(element)) {
				createPendingItem(item);
			}
		}
	}
	
	@Override
	public List<TreeItem> getItemSelections() {
		return Arrays.asList(tree.getSelection());
	}
	
	@Override
	public void select(TreeItem item, boolean preserveSelection) {
		tree.select(item);
		fireEvent(ControllerEvent.SELECT, null, item);
	}
	
	@Override
	public void expand(TreeItem item) {
		if (childrenArePending(item)) doRefreshChildren(item);
		item.setExpanded(true);
		fireEvent(ControllerEvent.EXPAND, null, item);

	}
	
	@Override
	public void collapse(TreeItem item) {
		item.setExpanded(false);
		fireEvent(ControllerEvent.COLLAPSE, null, item);
	}
	
	@Override
	public void scrollTo(TreeItem item) {
		tree.showItem(item);
	}
	
	
	public void refresh(TreeItem item, E element) {
		initializeItem(item, element);
		if (getPresenter().hasChildren(element)) {
			if (item.getExpanded()) {
				List<? extends E> childElements = getPresenter().getChildren(element);
				adjustChildrenCount(item, childElements);
				int childSize = childElements.size();
				for (int i = 0; i < childSize; i++) {
					TreeItem childItem = item.getItem(i);
					E childElement = childElements.get(i);
					refresh(childItem, childElement);
				}
			} else {
				disposeAll(item.getItems());
				createPendingItem(item);
			}
		} else {
			disposeAll(item.getItems());
		}
	}

	public void refreshAll(I input) {
		List<? extends E> childElements = (List<? extends E>) getPresenter().getRoots(input);
		adjustChildrenCount(null, childElements);
		int childSize = childElements.size();
		for (int i = 0; i < childSize; i++) {
			TreeItem childItem = tree.getItem(i);
			E childElement = childElements.get(i);
			refresh(childItem, childElement);
		}
	}
	
	// ========== Internals ==========

	/**
	 * Initializes a {@link TreeItem} with local information from the given
	 * element. Assigns the given element as data of the item. Updates text and
	 * image. Does not impact the children of the item.
	 */
	public void initializeItem(TreeItem item, E element) {
		item.setData(element);
		final int columnCount = tree.getColumnCount();
		if ( columnCount > 0 ) {
			for (int i = 0; i < columnCount; i++ ) {
				String text = getPresenter().getText(i, element);
				if (text != null && text != item.getText(i)) item.setText(i, text);
				Image image = getPresenter().getImage(i, element);
				if (image != null && image != item.getImage(i)) item.setImage(i, image);
			}
		} else {
			String text = getPresenter().getText(0, element);
			if (text != item.getText()) item.setText(text);
			Image image = getPresenter().getImage(0, element);
			if (image != item.getImage()) item.setImage(image);
		}
	}
	
	/** Install the required listeners on the Tree control. */
	protected void installListeners() {
		Listener listener = new Listener() {
			public void handleEvent(final Event event) {
				TreeItem item = (TreeItem) event.item;
				switch (event.type) {
				case SWT.Expand:
					if (childrenArePending(item)) {
						doRefreshChildren(item);
					}
					fireEvent(ControllerEvent.EXPAND, event, item);
					break;
				case SWT.Collapse:
					fireEvent(ControllerEvent.COLLAPSE, event, item);
					break;
				case SWT.Selection:
					fireEvent(ControllerEvent.SELECT, event, item);
					break;
				}
			}
		};
		tree.addListener(SWT.Expand, listener);
		tree.addListener(SWT.Collapse, listener);
		tree.addListener(SWT.Selection, listener);
	}
	
	protected void doRefreshChildren(TreeItem parentItem) {
		disposeAll(parentItem.getItems());
		E parentElement = getElement(parentItem);
		for (E childElement : getPresenter().getChildren(parentElement)) {
			TreeItem childItem = new TreeItem(parentItem, SWT.NONE);
			initializeItem(childItem, childElement);
			if (getPresenter().hasChildren(childElement)) {
				createPendingItem(childItem);
			}
		}
	}

	/**
	 * Creates the pseudo item ("pending item") indicating that the given item
	 * has children that have not yet been requested to the tree provider. Used
	 * to force the display of a [+] sign before a closed item. This pseudo item is
	 * identified by its getData() being null, and is the only child of the item.
	 */
	protected void createPendingItem(TreeItem parentItem) {
		TreeItem treeItem = new TreeItem(parentItem, SWT.NONE);
		treeItem.setText("...");
	}

	/**
	 * Tests if the only child of the given item is the pseudo "pending" item.
	 * This is the indication that the real children have not yet been computed.
	 */
	protected boolean childrenArePending(TreeItem parentItem) {
		if (parentItem == null) return false; // no pending at root level
		return parentItem.getItemCount() == 1 && parentItem.getItem(0).getData() == null;
	}

	protected void adjustChildrenCount(TreeItem parentItem, List<? extends E> elements) {
		int actualSize = parentItem != null ? parentItem.getItemCount() : tree.getItemCount();
		int expectedSize = elements.size();
		if (actualSize == expectedSize) return;
		if (actualSize < expectedSize) {
			for (int i = actualSize; i < expectedSize; i++) {
				if (parentItem != null) new TreeItem(parentItem, SWT.None);
				else new TreeItem(tree, SWT.None);
			}
		} else {
			for (int i = actualSize - 1; i >= expectedSize; i--) {
				(parentItem != null ? parentItem.getItem(i) : tree.getItem(i)).dispose();
			}
		}
		// Unexpands items that do not match new element:
		for (int i = 0; i < expectedSize; i++) {
			TreeItem item = parentItem != null ? parentItem.getItem(i) : tree.getItem(i);
			final E element = getElement(item);
			if ( element == null ) continue;
			final String key = getPresenter().getKey(element);
			
			final E otherElement = elements.get(i);
			final String otherKey = getPresenter().getKey(otherElement);
			if (!key.equals(otherKey)) item.setExpanded(false);
		}
	}
	
	/** Disposes all the items in a given array. */
	protected static void disposeAll(TreeItem[] items) {
		for (int i = 0; i < items.length; i++) items[i].dispose();
	}

}
