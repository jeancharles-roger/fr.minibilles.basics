package fr.minibilles.basics.ui.sequencediagram.model;

import fr.minibilles.basics.serializer.Boost;

public class Pause extends SequenceItem {

	/** Constructor for Boost, do not suppress */
	protected Pause(Boost boost) {
		super(boost);
	}
	
	public Pause(Sequence sequence) {
		super(sequence, "Pause");
	}

}
