/*
 * This file is part of REQTIFY
 * Copyright 2010 Geensoft. All rights reserved.
 */

package fr.minibilles.basics.ui.tree;

import java.util.List;
import org.eclipse.swt.graphics.Image;


/**
 * A tree content provider, for use with {@link SWTTreeController}.
 * @param <E> element type
 * @param <I> input type
 */
public interface TreePresenter<I, E> {

	List<? extends E> getRoots(I input);

	boolean hasChildren(E element);

	List<? extends E> getChildren(E element);

	String getKey(E element);
	
	String getText(int index, E element);

	/** Called when an item text is edited. */
	E setText(E element, String newText);
	
	Image getImage(int index, E element);

}
