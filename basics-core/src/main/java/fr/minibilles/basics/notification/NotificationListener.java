package fr.minibilles.basics.notification;

/**
 * {@link Notification} listener.
 * @author Jean-Charles Roger
 *
 */
public interface NotificationListener {

	/** Method called by {@link NotificationSupport} for listened object with notification. */
	public void notified(Notification notification);
	
}
