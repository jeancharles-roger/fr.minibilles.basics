package fr.minibilles.basics.serializer;

/**
 * <p>
 * A class that implements this interface can be serialized
 * and de-serialized by a {@link Boost} instance.
 * </p>
 *
 * <p>
 * A {@link BoostObject} <b>must</b> also implement a constructor with 
 * one parameter typed {@link Boost} for reading.
 * </p>
 * @author Jean-Charles Roger.
 *
 */
public interface BoostObject {

	/** Write this contents to given {@link Boost} */
	void writeToBoost(Boost boost);

}
