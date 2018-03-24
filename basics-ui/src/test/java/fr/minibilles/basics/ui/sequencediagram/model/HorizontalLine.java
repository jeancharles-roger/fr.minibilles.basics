package fr.minibilles.basics.ui.sequencediagram.model;

import fr.minibilles.basics.serializer.Boost;

public class HorizontalLine extends SequenceItem {

	protected HorizontalLine(Boost boost) {
		super(boost);
	}
	
	public HorizontalLine(Sequence sequence, String label) {
		super(sequence, label);
	}

}
