package ecore;

import org.eclipse.emf.common.util.Enumerator;
import ecore.EcoreVisitor;
import ecore.ENamedElement;
import ecore.EEnum;


public class EEnumLiteral extends ENamedElement {

	private int value;

	private transient Enumerator instance;

	private String literal;

	private transient EEnum eEnum;

	public EEnumLiteral() {
	}

	/**
	 * Gets value.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Sets value.
	 */
	public void setValue(int newValue) {
		if (value != newValue) {
			this.value= newValue;
		}
	}

	/**
	 * Gets instance.
	 */
	public Enumerator getInstance() {
		return instance;
	}

	/**
	 * Sets instance.
	 */
	public void setInstance(Enumerator newValue) {
		if (instance == null ? newValue != null : (instance.equals(newValue) == false)) {
			this.instance= newValue;
		}
	}

	/**
	 * Gets literal.
	 */
	public String getLiteral() {
		return literal;
	}

	/**
	 * Sets literal.
	 */
	public void setLiteral(String newValue) {
		if (literal == null ? newValue != null : (literal.equals(newValue) == false)) {
			this.literal= newValue;
		}
	}

	/**
	 * Gets eEnum.
	 */
	public EEnum getEEnum() {
		return eEnum;
	}

	/**
	 * Sets eEnum.
	 */
	public void setEEnum(EEnum newValue) {
		if (eEnum == null ? newValue != null : (eEnum.equals(newValue) == false)) {
			this.eEnum= newValue;
		}
	}

	/**
	 * Visitor accept method.
	 */
	public void accept(EcoreVisitor visitor) {
		visitor.visitEEnumLiteral(this);
	}

}

