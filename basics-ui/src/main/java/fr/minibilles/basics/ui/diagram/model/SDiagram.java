package fr.minibilles.basics.ui.diagram.model;

import fr.minibilles.basics.model.ChangeRecorder;
import fr.minibilles.basics.model.ModelObject;
import fr.minibilles.basics.serializer.Boost;
import fr.minibilles.basics.serializer.BoostObject;
import fr.minibilles.basics.serializer.BoostUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO
 * @author Jean-Charles Roger
 *
 */
public class SDiagram implements BoostObject, ModelObject {

	private String name;
	
	private final List<SElement> elementList = new ArrayList<SElement>();
	
	private ChangeRecorder changeHandler;
	
	protected SDiagram(Boost boost) {
		name = boost.readString();
		elementList.addAll(BoostUtil.readObjectList(boost, SElement.class));
	}
	
	public SDiagram(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		getChangeRecorder().recordChangeAttribute(this, "name", this.name);
		this.name = name;
	}
	
	public List<SElement> getSElementList() {
		return Collections.unmodifiableList(elementList);
	}

	public int getSElementCount() {
		return elementList.size();
	}

	public SElement getSElement(int index) {
		if (index < 0 || index >= getSElementCount()) {
			return null;
		}
		return elementList.get(index);
	}

	public void addSElement(SElement newValue) {
		addSElement(this.getSElementCount(), newValue);
	}

	public void addSElement(int index, SElement newValue) {
		getChangeRecorder().recordAddObject(this, "element", index);
		elementList.add(index, newValue);
	}

	public void removeSElement(SElement element) {
		int index = elementList.indexOf(element);
		if (index >= 0) {
			removeSElement(index);
		}
	}

	public void removeSElement(int index) {
		SElement oldValue = this.elementList.get(index);
		if (oldValue == null) {
			return;
		}
		getChangeRecorder().recordRemoveObject(this, "element", index, oldValue);
		elementList.remove(index);
	}

	public boolean containsSElement(SElement element) {
		return elementList.contains(element);
	}

	public void writeToBoost(Boost boost) {
		boost.writeString(name);
		BoostUtil.writeObjectCollection(boost, elementList);
	}
	
	public ChangeRecorder getChangeRecorder() {
		return changeHandler == null ? ChangeRecorder.Stub : changeHandler;
	}
	
	public void setChangeRecorder(ChangeRecorder changeHandler) {
		this.changeHandler = changeHandler;
	}
}
