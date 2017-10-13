package fr.minibilles.basics.ui;

import fr.minibilles.basics.ui.action.Action;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.eclipse.swt.widgets.Shell;

/**
 * <p> 
 * @author Jean-Charles Roger
 */
public class PlatformUtil {

	private final static String CocoaUIEnhancerClassName = "fr.minibilles.basics.ui.cocoa.CocoaUIEnhancer";
	private final static String FullscreenUtilClassName = "fr.minibilles.basics.ui.cocoa.FullscreenUtil";
	
	/**
	 * <p>Registers the name for a Cocoa application, it's about and preferences action.</p>
	 * @param name App name
	 * @param aboutAction about action
	 * @param preferencesAction preferences actions.
	 */
	public static void registerCocoaNameAboutAndPreference(String name, Action aboutAction, Action preferencesAction) {
		try {
			@SuppressWarnings("unchecked")
			Class<Runnable> cocoaUIEnhancerClass = (Class<Runnable>) PlatformUtil.class.getClassLoader().loadClass(CocoaUIEnhancerClassName);
			Constructor<Runnable> contructor = cocoaUIEnhancerClass.getConstructor(String.class, Action.class, Action.class);
			Runnable cocoaUIEnhancer = contructor.newInstance(name, aboutAction, preferencesAction);
			cocoaUIEnhancer.run();
		} catch (Throwable e) {
			// class doesn't exist in class path, nothing to do.
		}
	}

	public static void setupFullscreen(Shell shell) {
		try {
			Class<?> fullscreenUtilClass = (Class<?>) PlatformUtil.class.getClassLoader().loadClass(FullscreenUtilClassName);
			Method method = fullscreenUtilClass.getMethod("setupFullscreen", Shell.class);
			method.invoke(null, shell);
		} catch (Throwable e) {
			// class doesn't exist in class path, nothing to do.
		}
	}
	
	private final static String logicLineFeed = "\n";
	private final static String platformLineFeed = System.getProperty("line.separator");
	
	/** Transforms source as a platform string to a logic string. */
	public static String platformToLogicString(String source) {
		if ( source == null ) return null;
		return source.replaceAll(platformLineFeed, logicLineFeed);
	}
}
