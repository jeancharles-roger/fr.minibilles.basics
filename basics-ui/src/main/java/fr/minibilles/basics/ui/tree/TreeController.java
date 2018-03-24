package fr.minibilles.basics.ui.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class TreeController<I, E, GE> {

	private final TreePresenter<I, E> presenter;
	private List<ControllerListener> listeners;
	
	public TreeController(TreePresenter<I, E> treePresenter) {
		this.presenter = treePresenter;
	}
	
	/** Returns the {@link TreePresenter} */
	public TreePresenter<I, E> getPresenter() {
		return presenter;
	}
	
	/** Adds a listener for {@link ControllerEvent} */
	public void addListener(ControllerListener listener) {
		if ( listeners == null ) {
			listeners = new ArrayList<ControllerListener>();
		}
		listeners.add(listener);
	}

	/** Removes a listener for {@link ControllerEvent} */
	public void removeListener(ControllerListener listener) {
		if ( listeners == null ) return;
		listeners.remove(listener);
		if ( listeners.isEmpty() ) listeners = null;
	}
	
	/** Notifies all listeners of the given event */
	protected void fireEvent(int type, Object sourceEvent, Object target) {
		if ( listeners != null ) {
			ControllerEvent event = new ControllerEvent(type, this, sourceEvent, target);
			for ( ControllerListener listener : listeners ) {
				listener.handleControllerEvent(event);
			}
		}
	}
	
	/** Returns a selected element, or null if no selection. */
	public E getSelection() {
		final GE itemSelection = getItemSelection();
		return itemSelection != null ? getElement(itemSelection) : null;
	}
	
	/** Returns the selected elements. */
	public List<E> getSelections() {
		List<E> result = new ArrayList<E>();
		for ( GE element :  getItemSelections() ) {
			result.add(getElement(element));
		}
		return result;
	}
	
	/** Returns a selected item, or null if no selection. */
	public GE getItemSelection() {
		List<GE> selections = getItemSelections();
		if ( selections.size() > 0 ) return selections.get(0);
		return null;
	}

	/**
	 * Finds an graphical element in the tree, by using a given relative path to identify the
	 * graphical element.
	 * @param startItem
	 *            the graphical element where the search is started, or null to start
	 *            searching at root level
	 * @param keyPath
	 *            a list of path members that describes a traversal of the tree
	 *            from the top to the element to be selected
	 * @return the list of tree items that match the path; note that the match
	 *         can be partial, in this case the size of the returned list is
	 *         smaller than the size of the keyPath.
	 */
	public List<GE> findItemPath(GE startItem, List<String> keyPath) {
		List<GE> itemPath = new ArrayList<GE>(keyPath.size());
		if (keyPath.isEmpty()) return itemPath;
		int lastIndex = keyPath.size() - 1;
		for (int index = 0; index <= lastIndex; index++) {
			GE foundItem = findChildItem(startItem, keyPath.get(index));
			if (foundItem == null) break;
			itemPath.add(foundItem);
			startItem = foundItem;
		}
		return itemPath;
	}

	public GE findChildItem(GE parentItem, String childKey) {
		for ( GE item : getChildItems(parentItem) ) {
			if ( getPresenter().getKey(getElement(item)).equals(childKey)) return item;
		}
		return null;
	}
	
	/**
	 * Returns the key path from startItem to endItem. The start item must be a parent of the end item.
	 * @param startItem the start item, if null it's considered as the root item.
	 * @param endItem the end item for the path.
	 * @return a list of string representing the path, null if no path can be found.
	 */
	public List<String> getRelativeKeyPath(GE startItem, GE endItem) {
		List<String> path = new ArrayList<String>();
		while ( endItem != null && endItem != startItem ) {
			path.add(getPresenter().getKey((getElement(endItem))));
			endItem = getParentItem(endItem);
		}
		if ( startItem != null && endItem == null ) { 
			// can't find startItem in endItem parent's, error ! 
			return null;
		}
		Collections.reverse(path);
		return path;
	}

	public List<GE> showPath(List<String> keyPath, boolean scroll) {
		List<GE> itemPath = findItemPath(null, keyPath);
		int size = itemPath.size();
		if (size > 0 && size == keyPath.size()) {
			for ( int i=0; i<size-1; i++) {
				expand(itemPath.get(i));
			}
			if (scroll) scrollTo(itemPath.get(size - 1));
		}
		return itemPath;
	}


	/**
	 * Expands a graphical element in the tree.
	 * @param item the item to expand
	 * @param scroll if true, scroll the view to show the item
	 */
	public void show(GE item, boolean scroll) {
		GE current =  getParentItem(item);
		while (current != null ) {
			expand(current);
			current = getParentItem(current);
		}
		if ( scroll ) scrollTo(item);
	}
	
	/**
	 * Returns the list of key path for all selected items. 
	 * @return a {@link List} of {@link List} of strings.
	 */
	public List<List<String>> getSelectedPathes() {
		List<List<String>> result = new ArrayList<List<String>>();
		for ( GE item : getItemSelections() ) {
			result.add(getRelativeKeyPath(null, item));
		}
		return result;
	}

	public void refresh(GE item) {
		refresh(item, getElement(item));
	}

	
	/** Assigns a new input. Updates the display. */
	public abstract void setInput(I input);
	
	/** Returns the selected items. */
	public abstract List<GE> getItemSelections();
	
	/** Returns the element associated to an graphical element,. */
	public abstract E getElement(GE item);

	/** Returns the parent item for a given item. */
	public abstract GE getParentItem(GE item);
		
	/**
	 * Returns the child items of a given parentItem. If the children have not
	 * yet been computed, builds them by requesting the tree provider. Otherwise does not
	 * refresh the children.
	 * @param parentItem the parent graphical element, or null: in this case the root items are returned
	 * @return the child items
	 */
	public abstract List<GE> getChildItems(GE parentItem);

	/**
	 * Select given item.
	 * @param item item to select.
	 * @param preserveSelection if true, add item to current selection.
	 */
	public abstract void select(GE item, boolean preserveSelection);
	
	/**
	 * Expands the given item. It only exposes the given item, not its parents.
	 * @param item item to expand.
	 */
	public abstract void expand(GE item);
	
	public abstract void collapse(GE item);
	
	/**
	 * Scrolls to the given it.
	 * @param item item to reveal.
	 */
	public abstract void scrollTo(GE item);
	
	public abstract void refresh(GE item, E element);
	
	public abstract void refreshAll(I input);
}
