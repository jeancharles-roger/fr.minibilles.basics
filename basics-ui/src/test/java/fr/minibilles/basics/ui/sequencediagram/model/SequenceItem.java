package fr.minibilles.basics.ui.sequencediagram.model;

import fr.minibilles.basics.model.ChangeRecorder;
import fr.minibilles.basics.model.ModelObject;
import fr.minibilles.basics.serializer.Boost;
import fr.minibilles.basics.serializer.BoostObject;

public abstract class SequenceItem implements ModelObject, BoostObject {

	private Sequence sequence;
	private String label;

	protected SequenceItem(Boost boost) {
		sequence = boost.readObject(Sequence.class);
		label = boost.readString();
	}
	
	public SequenceItem(Sequence sequence, String label) { 
		this.sequence = sequence;
		this.label = label;
	}

	public Sequence getSequence() {
		return sequence;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		getChangeRecorder().recordChangeAttribute(this, "label", this.label);
		this.label = label;
	}
	public ChangeRecorder getChangeRecorder() {
		return sequence == null ? ChangeRecorder.Stub : sequence.getChangeRecorder();
	}

	public void writeToBoost(Boost boost) {
		boost.writeObject(sequence);
		boost.writeString(label);
	}

}
