package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.controller.Controller;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link PropertiesField} is a special {@link VirtualMultiPageField} which presents
 * a page for each type of subject. The presented page is a {@link CompositeField}
 * provided by a {@link Controller}. The {@link Controller} is found using reflection.
 * For a given subject, this searches for a {@link Controller} and loads it in the 
 * package given within the constructor. The controller's name is created using
 * the subject's class name. For instance, for {@link String} subject, the controller
 * name will be <code>StringController</code>
 * <p>
 * It handles the whole {@link Controller} life cycle.
 * 
 * @author Jean-Charles Roger
 */
public class PropertiesField extends VirtualMultiPageField {

	private class Page {
		Class<?> clazz;
		Controller<Object> controller;
		Field field;
	}

	private final List<Page> pages = new ArrayList<Page>();
	private final String controllerPackage;
	
	public PropertiesField() {
		this(null);
	}

	public PropertiesField(String controllerPackage) {
		super(null, BasicsUI.SCROLL);
		this.controllerPackage = controllerPackage;
		addEmptyPage();
	}
	
	@Override
	protected Field getField(int i) {
		return pages.get(i).field;
	}
	
	private void addEmptyPage() {
		Page page = new Page();
		TextField textField = new TextField(null, BasicsUI.NO_INFO | BasicsUI.READ_ONLY);
		textField.setValue("Empty");
		page.field = textField;
		pages.add(page);
		setFieldCount(pages.size(), false);
	}
	
	private int addPage(Class<?> clazz, Controller<Object> controller) {
		final Page page = new Page();
		page.clazz = clazz;
		page.controller = controller;
		page.field = controller.createFields();
		page.field.addListener(controller.createListener(getActionExecuter()));
		pages.add(page);
		setFieldCount(pages.size(), false);
		return pages.size() - 1;
	}

	private int searchPage(Class<?> clazz) {
		for ( int i=0; i<pages.size(); i++ ) {
			if ( pages.get(i).clazz == clazz ) return i; 
		}
		return -1;
	}
	
	@SuppressWarnings("unchecked")
	private int createPage(Class<?> clazz) {
		try {
			Controller<Object> controller = (Controller<Object>) createController(clazz);
			return addPage(clazz, controller);
		} catch (Exception e) {
			System.err.println("No controller for class '" + clazz.getCanonicalName() + "'.");
			return -1;
		}
	}
	
	protected String getControllerClassName(Class<?> clazz) {
		return controllerPackage + "." + clazz.getSimpleName() + "Controller";
	}
	
	@SuppressWarnings("unchecked")
	protected Controller<?> createController(Class<?> clazz) throws Exception {
		final String controllerClassName = getControllerClassName(clazz);
		Class<? extends Controller<?>> controllerClass = (Class<? extends Controller<?>>) getClass().getClassLoader().loadClass(controllerClassName);
		return (Controller<Object>) controllerClass.newInstance();
	}
	
	public void setSubject(Object subject) {
		if ( subject == null ) {
			setSelected(0);
		} else {
			Class<?> clazz = subject.getClass();
			int index = searchPage(clazz);
			if ( index < 0 ) {
				index = createPage(clazz);
				if ( index < 0 ) {
					index = 0;
				}
			}
			Page page = pages.get(index);
			if ( page.controller != null ) {
				page.controller.setSubject(subject);
				page.controller.refreshFields();
			}
			setSelected(index);
		}
	}

	
	public void refresh() {
		Page page = pages.get(getSelected());
		if ( page.controller != null ) {
			page.controller.refreshFields();
		}
	}
}
