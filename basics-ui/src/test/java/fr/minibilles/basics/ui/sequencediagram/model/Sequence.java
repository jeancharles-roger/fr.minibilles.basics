package fr.minibilles.basics.ui.sequencediagram.model;

import fr.minibilles.basics.model.ChangeRecorder;
import fr.minibilles.basics.model.ModelChangeRecorder;
import fr.minibilles.basics.model.ModelObject;
import fr.minibilles.basics.serializer.Boost;
import fr.minibilles.basics.serializer.BoostObject;
import fr.minibilles.basics.serializer.BoostUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sequence implements BoostObject, ModelObject {
	
	private final ModelChangeRecorder changeHandler = new ModelChangeRecorder();
	
	private final ArrayList<LifeLine> lineList = new ArrayList<LifeLine>();
	
	private final ArrayList<SequenceItem> itemList = new ArrayList<SequenceItem>();
	
	@SuppressWarnings("unused")
	private Sequence(Boost boost) {
		lineList.addAll(BoostUtil.readObjectList(boost, LifeLine.class));
		this.lineList.trimToSize();
		itemList.addAll(BoostUtil.readObjectList(boost, SequenceItem.class));
		itemList.trimToSize();
	}
	
	public Sequence() {
	}
	
	public void addLine(LifeLine line) {
		addLine(lineList.size(), line);
	}
	
	public void addLine(int index, LifeLine line) {
		getChangeRecorder().recordAddObject(this, "line", index);
		lineList.add(index, line);
	}
	
	public void removeLine(LifeLine line) {
		removeLine(lineList.indexOf(line));
	}
	
	public void removeLine(int index) {
		if ( index < 0 ) return;
		LifeLine line = lineList.remove(index);
		getChangeRecorder().recordRemoveObject(this, "line", index, line);
	}

	public int getLineCount() {
		return lineList.size();
	}
	
	public List<LifeLine> getLifeLineList() {
		return Collections.unmodifiableList(lineList);
	}

	public void addItem(SequenceItem item) {
		addItem(itemList.size(), item);
	}
	
	public void addItem(int index, SequenceItem item) {
		getChangeRecorder().recordAddObject(this, "item", index);
		itemList.add(index, item);
	}
	
	public void removeItem(SequenceItem item) {
		removeItem(itemList.indexOf(item));
	}
	
	public void removeItem(int index) {
		if ( index < 0 ) return;
		SequenceItem item = itemList.remove(index);
		getChangeRecorder().recordRemoveObject(this, "item", index, item);
	}
	
	public int getItemCount() {
		return itemList.size();
	}
	
	public List<SequenceItem> getItemList() {
		return Collections.unmodifiableList(itemList);
	}
	
	public ChangeRecorder getChangeRecorder() {
		return changeHandler;
	}

	public void writeToBoost(Boost boost) {
		BoostUtil.writeObjectCollection(boost, lineList);
		BoostUtil.writeObjectCollection(boost, itemList);
	}

}
