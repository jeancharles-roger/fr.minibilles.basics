package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.notification.NotificationListener;


/**
 * Common interface for any structure field.
 * @author Jean-Charles Roger
 *
 */
public interface Structure extends Field {

	boolean addStructureListener(NotificationListener listener);
	boolean removeStructureListener(NotificationListener listener);

}
