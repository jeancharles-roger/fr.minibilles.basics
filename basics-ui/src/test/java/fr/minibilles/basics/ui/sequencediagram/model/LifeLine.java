package fr.minibilles.basics.ui.sequencediagram.model;

import fr.minibilles.basics.serializer.Boost;

public final class LifeLine extends SequenceItem {

	private float x;

	private float size;

	/** Constructor for Boost, do not suppress */
	protected LifeLine(Boost boost) {
		super(boost);
		x = boost.readFloat();
		size = boost.readFloat();
	}
	
	public LifeLine(Sequence sequence, String label, float x, float size) {
		super(sequence, label);
		this.x = x;
		this.size = size;
	}
	
	public float getX() {
		return x;
	}
	
	public void setX(float x) {
		getChangeRecorder().recordChangeAttribute(this, "x", this.x);
		this.x = x;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		getChangeRecorder().recordChangeAttribute(this, "size", this.size);
		this.size = size;
	}


	public void writeToBoost(Boost boost) {
		super.writeToBoost(boost);
		boost.writeFloat(x);
		boost.writeFloat(size);
	}

}
