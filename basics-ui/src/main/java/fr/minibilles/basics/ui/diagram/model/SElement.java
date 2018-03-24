package fr.minibilles.basics.ui.diagram.model;

import fr.minibilles.basics.model.ChangeRecorder;
import fr.minibilles.basics.model.ModelObject;
import fr.minibilles.basics.serializer.Boost;
import fr.minibilles.basics.serializer.BoostObject;
import fr.minibilles.basics.serializer.BoostUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SElement implements BoostObject, ModelObject {

	private final SDiagram diagram;

	private final String type;
	private final List<String> modelList = new ArrayList<String>();
	private boolean visible = true;
	private float[] points;
	
	protected SElement(Boost boost) {
		diagram = boost.readObject(SDiagram.class);
		type = boost.readString();
		modelList.addAll(SDiagramUtil.readStringList(boost));
		visible = boost.readBoolean();
		points = BoostUtil.readFloatArray(boost);
	}
	
	public SElement(SDiagram diagram, String type) {
		this.diagram = diagram;
		this.type = type;
	}
	
	public SDiagram getDiagram() {
		return diagram;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		getChangeRecorder().recordChangeAttribute(this, "visible", this.visible);
		this.visible = visible;
	}
	
	public String getType() {
		return type;
	}
	
	public List<String> getModelList() {
		return Collections.unmodifiableList(modelList);
	}

	public int getModelCount() {
		return modelList.size();
	}

	public String getModel(int index) {
		if (index < 0 || index >= getModelCount()) {
			return null;
		}
		return modelList.get(index);
	}

	public void addModel(String newValue) {
		addModel(this.getModelCount(), newValue);
	}

	public void addModel(int index, String newValue) {
		getChangeRecorder().recordAddObject(this, "model", index);
		modelList.add(index, newValue);
	}

	public void removeModel(String element) {
		int index = modelList.indexOf(element);
		if (index >= 0) removeModel(index);
	}

	public void removeModel(int index) {
		String oldValue = this.modelList.get(index);
		if (oldValue == null) return;
		getChangeRecorder().recordRemoveObject(this, "model", index, oldValue);
		modelList.remove(index);
	}
	
	
	public float[] getPoints() {
		return points;
	}
	
	public void setPoints(float[] points) {
		getChangeRecorder().recordChangeAttribute(this, "points", this.points);
		this.points = points;
	}



	public boolean containsModel(String element) {
		return modelList.contains(element);
	}

	public void writeToBoost(Boost boost) {
		boost.writeString(type);
		SDiagramUtil.writeStringList(boost, modelList);
		boost.writeBoolean(visible);
		BoostUtil.writeFloatArray(boost, points);
		boost.writeObject(diagram);
	}


	public ChangeRecorder getChangeRecorder() {
		if ( getDiagram() != null ) return getDiagram().getChangeRecorder();
		return ChangeRecorder.Stub;
	}
	

}
