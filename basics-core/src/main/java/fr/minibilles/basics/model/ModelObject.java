package fr.minibilles.basics.model;

/**
 * All model object must implements this interface
 *
 * @author Jean-Charles Roger.
 *
 */
public interface ModelObject {
	
	/**
	 * @return the {@link ChangeRecorder} that manage this object.
	 */
	public ChangeRecorder getChangeRecorder();
	
	//public ModelObject getObject(String localId);
}
