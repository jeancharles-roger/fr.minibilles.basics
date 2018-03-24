package fr.minibilles.basics.notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface NotificationSupport {

	public static class Stub implements NotificationSupport {
		
		private final Object source;
		private List<NotificationListener> listeners;
		
		public Stub(Object source) {
			this.source = source;
		}
		public List<NotificationListener> getListeners() {
			if ( listeners == null ) return Collections.emptyList();
			return Collections.unmodifiableList(listeners);
		}
		
		public boolean addListener(NotificationListener listener) {
			if ( listeners == null ) { listeners = new ArrayList<NotificationListener>(); }
			return listeners.add(listener);
		}
		
		public boolean removeListener(NotificationListener listener) {
			if ( listeners == null ) return false;
			boolean removed = listeners.remove(listener);
			if ( listeners.isEmpty() ) { listeners = null; }
			return removed;
		}
		
		public void fireNotification(Notification notification) {
			if ( listeners != null ) {
				for ( NotificationListener listener : listeners ) {
					listener.notified(notification);
				}
			}
		}
		
		public void fireNotification(int type, String name) {
			fireNotification(new Notification(source, type, name));
		}
		
		public void fireValueNotification(int type, String name, Object newValue, Object oldValue) {
			fireNotification(new Notification(source, type, name, newValue, oldValue));
		}
		
		public void fireIndexedValueNotification(int type, String name, Object newValue, Object oldValue, int index) {
			fireNotification(new Notification(source, type, name, newValue, oldValue, index));
		}
		
	}
	
	boolean addListener(NotificationListener listener);
	boolean removeListener(NotificationListener listener);
}
