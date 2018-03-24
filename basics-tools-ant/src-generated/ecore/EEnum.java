package ecore;

import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import ecore.EcoreVisitor;
import ecore.EEnumLiteral;
import ecore.EDataType;


public class EEnum extends EDataType {

	/**
	 * eLiterals field.
	 */
	private final List<EEnumLiteral> eLiteralsList = new ArrayList<EEnumLiteral>();

	public EEnum() {
	}

	/**
	 * Returns all values of eLiterals.
	 */
	public List<EEnumLiteral> getELiteralsList() {
		return Collections.unmodifiableList(eLiteralsList);
	}

	/**
	 * Gets eLiterals object count.
	 */
	public int getELiteralsCount() {
		return eLiteralsList.size();
	}

	/**
	 * Gets eLiterals at given index.
	 */
	public EEnumLiteral getELiterals(int index) {
		if ( index < 0 || index >= getELiteralsCount() ) { return null; }
		return eLiteralsList.get(index);
	}

	/**
	 * Adds an object in eLiterals.
	 */
	public void addELiterals(EEnumLiteral newValue) {
		addELiterals(getELiteralsCount(), newValue);
	}

	/**
	 * Adds an object in eLiterals at given index.
	 */
	public void addELiterals(int index, EEnumLiteral newValue) {
		eLiteralsList.add(index, newValue);
	}

	/**
	 * Replaces an object in eLiterals at given index. Returns the old value.
	 */
	public EEnumLiteral setELiterals(int index, EEnumLiteral newValue) {
		return eLiteralsList.set(index, newValue);
	}

	/**
	 * Adds a collection of objects in eLiterals.
	 */
	public void addAllELiterals(Collection<EEnumLiteral> toAddList) {
		for (EEnumLiteral newValue : toAddList) {
			addELiterals(getELiteralsCount(), newValue);
		}
	}

	/**
	 * Removes given object from eLiterals.
	 */
	public void removeELiterals(EEnumLiteral value) {
		int index = eLiteralsList.indexOf(value);
		if (index >= 0 ) {
			removeELiterals(index);
		}
	}

	/**
	 * Removes object from eLiterals at given index.
	 */
	public void removeELiterals(int index) {
		eLiteralsList.remove(index);
	}

	/**
	 * Adds object to eLiterals and sets the corresponding eEnum.
	 */
	public void addELiteralsAndOpposite(EEnumLiteral newValue) {
		addELiterals(newValue);
		if ( newValue != null ) {
			newValue.setEEnum(this);
		}
	}

	/**
	 * Adds a collection of objects to eLiterals and sets the corresponding eEnum.
	 */
	public void addAllELiteralsAndOpposite(Collection<EEnumLiteral> toAddList) {
		for (EEnumLiteral newValue : toAddList) {
			addELiteralsAndOpposite(getELiteralsCount(), newValue);
		}
	}

	/**
	 * Adds object to eLiterals at given index and sets the corresponding eEnum.
	 */
	public void addELiteralsAndOpposite(int index, EEnumLiteral newValue) {
		addELiterals(index, newValue);
		if ( newValue != null ) {
			newValue.setEEnum(this);
		}
	}

	/**
	 * Replaces an object in eLiterals at given index. Returns the old value.
	 */
	public EEnumLiteral setELiteralsAndOpposite(int index, EEnumLiteral newValue) {
		EEnumLiteral oldValue = eLiteralsList.set(index, newValue);
		if ( newValue != null ) {
			newValue.setEEnum(this);
		}
		return oldValue;
	}

	/**
	 * Removes object from eLiterals and resets the corresponding eEnum.
	 */
	public void removeELiteralsAndOpposite(EEnumLiteral removed) {
		removeELiterals(removed);
		if ( removed != null ) {
			removed.setEEnum(null);
		}
	}

	/**
	 * Removes object at given index from eLiterals and resets the corresponding eEnum.
	 */
	public void removeELiteralsAndOpposite(int index) {
		EEnumLiteral removed = eLiteralsList.get(index);
		removeELiterals(index);
		if ( removed != null ) {
			removed.setEEnum(null);
		}
	}

	public EEnumLiteral getEEnumLiteral(String name) {
		// TODO implement getEEnumLiteral(...)
		throw new UnsupportedOperationException();
	}

	public EEnumLiteral getEEnumLiteral(int value) {
		// TODO implement getEEnumLiteral(...)
		throw new UnsupportedOperationException();
	}

	public EEnumLiteral getEEnumLiteralByLiteral(String literal) {
		// TODO implement getEEnumLiteralByLiteral(...)
		throw new UnsupportedOperationException();
	}

	/**
	 * Visitor accept method.
	 */
	public void accept(EcoreVisitor visitor) {
		visitor.visitEEnum(this);
	}

}

