package fr.minibilles.basics.ui.sequencediagram.model;

import fr.minibilles.basics.serializer.Boost;

public final class LifeLine extends SequenceItem {

	private float x;
	
	/** Constructor for Boost, do not suppress */
	protected LifeLine(Boost boost) {
		super(boost);
		x = boost.readFloat();
	}
	
	public LifeLine(Sequence sequence, String label, float x) {
		super(sequence, label);
		this.x = x;
	}
	
	public float getX() {
		return x;
	}
	
	public void setX(float x) {
		getChangeRecorder().recordChangeAttribute(this, "x", this.x);
		this.x = x;
	}
	

	public void writeToBoost(Boost boost) {
		super.writeToBoost(boost);
		boost.writeFloat(x);
	}

}
