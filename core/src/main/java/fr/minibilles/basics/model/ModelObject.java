package fr.minibilles.basics.model;

/**
 * <p>
 * All model object must implements this interface
 * </p>
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
