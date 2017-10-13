package fr.minibilles.basics.ui.sequencediagram.model;

import fr.minibilles.basics.model.ChangeRecorder;
import fr.minibilles.basics.serializer.Boost;

public class Message extends SequenceItem {

	private Sequence sequence;
	private LifeLine source;
	private LifeLine target;
	private String label;
	
	/** Constructor for Boost, do not suppress */
	protected Message(Boost boost) {
		super(boost);
		source = boost.readObject(LifeLine.class);
		target = boost.readObject(LifeLine.class);
	}
	
	public Message(Sequence sequence, String label, LifeLine source, LifeLine target) {
		super(sequence, label);
		this.source = source;
		this.target = target;
	}
	
	public LifeLine getSource() {
		return source;
	}

	public void setSource(LifeLine source) {
		getChangeRecorder().recordChangeAttribute(this, "source", this.source);
		this.source = source;
	}
	
	public LifeLine getTarget() {
		return target;
	}
	
	public void setTarget(LifeLine target) {
		getChangeRecorder().recordChangeAttribute(this, "target", this.target);
		this.target = target;
	}
	
	public ChangeRecorder getChangeRecorder() {
		return sequence == null ? ChangeRecorder.Stub : sequence.getChangeRecorder();
	}

	public void writeToBoost(Boost boost) {
		super.writeToBoost(boost);
		boost.writeObject(source);
		boost.writeObject(target);
		boost.writeString(label);
	}

}
