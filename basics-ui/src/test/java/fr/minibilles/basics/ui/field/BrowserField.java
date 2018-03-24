package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.notification.Notification;
import fr.minibilles.basics.ui.BasicsUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

public class BrowserField extends AbstractField {

	private String url;
	private Browser browser;
	
	public BrowserField(String label, int style) {
		super(label, style);
	}

	public boolean activate() {
		if (browser != null ) {
			browser.setFocus();
		}
		return false;
	}

	@Override
	public void createWidget(Composite parent) {
		super.createWidget(parent);
		createLabel(parent);
		createInfo(parent);
		createBrowser(parent);
		createButtonBar(parent);
	}

	private void createBrowser(Composite parent) {
		browser = new Browser(parent, SWT.NONE);
		attachFieldToWidget(browser);
		
		if ( url != null ) {
			browser.setUrl(url);
		} else {
			browser.setText("");
		}

		// sets layout
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, grabExcessVerticalSpace());
		data.minimumWidth = 80;
		data.minimumHeight = 80;
		data.horizontalSpan = fieldHorizontalSpan();
		data.verticalSpan = 1;
		browser.setLayoutData(data);
		
		fireWidgetCreation(browser);
	}
	
	@Override
	public boolean grabExcessVerticalSpace() {
		return true;
	}
	
	public String getUrl() {
		return url;
	}
	
	protected void setUrl(String url, int type) {
		String oldValue = this.url;
		this.url = url;
		if ( browser != null && type != Notification.TYPE_UI ) {
			if ( !browser.getUrl().equals(url == null ? "" : url) ) {
				if (url != null ) {
					browser.setUrl(url);
				} else {
					browser.setText("");
				}
			}
		}
		notificationSupport.fireValueNotification(type, BasicsUI.NOTIFICATION_VALUE, url, oldValue);
	}
	
	/** Sets the field's url */
	public void setUrl(String url) {
		setUrl(url, Notification.TYPE_API);
	}
	

}
