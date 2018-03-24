/**
 * 
 */
package fr.minibilles.basics.ui.controller;

import fr.minibilles.basics.notification.Notification;
import fr.minibilles.basics.notification.NotificationListener;
import fr.minibilles.basics.progress.ActionMonitor;
import fr.minibilles.basics.ui.action.Action;
import fr.minibilles.basics.ui.action.ActionExecuter;
import fr.minibilles.basics.ui.field.CompositeField;
import fr.minibilles.basics.ui.field.Field;

/**
 * A {@link Controller} handles a set of {@link Field} for one specific object.
 * <p>
 * It handles it using 3 methods:
 * <ul>
 * <li>{@link #createFields()} which provides the {@link Field}s to edit the subject</li>
 * <li>{@link #refreshFields()} which refreshes the fields (UI part) using the subject and</li>
 * <li>{@link #updateSubject(Field)} which updates the subject upon the {@link Field}s
 * modifications</li>
 * </ul>
 *
 * @author Jean-Charles Roger
 */
public abstract class Controller<T> {

	/** Controller's subject. */
	protected T subject;
	
	public T getSubject() {
		return subject;
	}
	
	public void setSubject(T subject) {
		this.subject = subject;
	}
	
	/** Creates the {@link CompositeField} for controller. */
	public abstract CompositeField createFields();
	
	/** 
	 * Updates the subject.
	 * @param field the field that changed.
	 * @return true if the subject changed.
	 */
	public boolean updateSubject(Field field) {
		return false;
	}
	
	/**
	 * Refreshes the {@link Field}s.
	 */
	public void refreshFields() { }
	
	public final NotificationListener createListener(final ActionExecuter executer) {
		return new NotificationListener() {
			public void notified(final Notification notification) {
				if (notification.type == Notification.TYPE_UI) {
					if ( notification.getSource() instanceof Field ) {
						executer.executeAction(new Action.Stub("Update", null, Action.STYLE_TRANSACTIONNAL) {
							public int run(ActionMonitor monitor) {
								if ( updateSubject((Field) notification.getSource()) ) {
									return STATUS_OK;
								} else {
									return STATUS_CANCEL;
								}
							};
						});
					}
				}
			}
		};
	}
	
}
