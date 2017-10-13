/**
 * 
 */
package fr.minibilles.basics.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;

/**
 * {@link Resources} handles load and dispose of graphical objects.
 * It has a mechanism to count references to instances, which allows correct disposal. 
 * 
 * @author Jean-Charles Roger
 *
 */
public class Resources {

	private final static Map<Class<Resources>, Resources> references = new HashMap<Class<Resources>, Resources>();

    /** Number of references for this */
    protected int referenceCount = 0;

	/**
	 * Get a shared instance of given class of {@link Resources}.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Resources> T getInstance(Class<T> clazz) {
		T resource = (T) references.get(clazz);
		Display display = Display.getDefault();
		if ( resource == null) {
			try {
				resource = clazz.getConstructor(Display.class).newInstance(display);
				references.put((Class<Resources>) clazz, resource );
			} catch (Throwable e) {
				return null;
			}
		}
		resource.referenceCount++;
		return resource;
	}
	
	/**
	 * Release the given {@link Resources} instance.
	 * If no more references exists on this {@link Resources}, it's removed.
	 */
	public static <T extends Resources> void releaseInstance(Class<T> clazz) {
		Resources resource = references.get(clazz);
		if ( resource != null ) {
			resource.referenceCount--;
			if ( resource.referenceCount <= 0) {
				references.remove(resource);
				resource.dispose();
			}
		}
	}
	
	/**
	 * Converts a HTML color string to an array of 3 integers Red/Green/Blue.
	 */
	public static RGB getRGB(String description) {
		if ( description == null || !description.matches("^#[0-9a-fA-F]{6}$") ) return null;
		int red = Integer.valueOf(description.substring(1, 3), 16);
		int green = Integer.valueOf(description.substring(3, 5), 16);
		int blue = Integer.valueOf(description.substring(5, 7), 16);
		return new RGB(red, green, blue);
	}

	/** {@link Display} used in this. */
	protected final Display display; 

	/** Already created {@link Color}s. */
	protected final Map<RGB, Color> colors = new HashMap<RGB, Color>();
	
	/** Already loaded {@link Image}s. */
	protected final Map<String, Image> images = new HashMap<String, Image>();
	
	/** Already loaded {@link Font}s. */
	protected final Map<String, Font> fonts = new HashMap<String, Font>();
	
	public Resources(Display display) {
		this.display = display;
	}

	/** Get {@link Color} for given {@link RGB}. */
	public Color getColor(RGB rgb) {
		Color color = colors.get(rgb);
		if ( color == null ) {
			color = new Color(display, rgb);
			colors.put(rgb, color);
		}
		return color;
	}
	
	/** Get {@link Color} from system */
	public Color getSystemColor(int color) {
		return display.getSystemColor(color);
	}
	
	/** 
	 * Get {@link Image} for given {@link String}. 
	 * It locates images from the package where is the instantiated {@link Resources} class.
	 */
	public Image getImage(String filename) {
		Image image = images.get(filename);
		if ( image == null ) {
			Class<?> currentClass = getClass(); 
			InputStream stream = null;
			while ( stream == null && currentClass != null && currentClass != Object.class ) {
				String imageFullpath = currentClass.getPackage().getName().replace('.', '/') + "/images/" + filename;
				stream = currentClass.getClassLoader().getResourceAsStream(imageFullpath);
				currentClass = currentClass.getSuperclass();
			}
			if ( stream == null ) return null;
			try {
				image = new Image(display, stream);
			} catch (Throwable e) {
				return null;
			} finally {
				try {
					stream.close();
				} catch (IOException e) {
					return null;
				}
			}
			images.put(filename, image);
		}
		return image;
	}
	
	/**
	 * Store an {@link Image} in the resources to be disposed when 
	 * the resource is released.
	 */
	public void storeImage(String name, Image image) {
		images.put(name, image);
	}
	
	/** Get font for given font data */
	public Font getFont(FontData data) {
		final String dataString = data.toString();
		Font font = fonts.get(dataString);
		if ( font == null ) {
			font = new Font(display, data);
			fonts.put(dataString, font);
		}
		return font;
	}
	
	/** Get {@link Image} from system. */
	public Image getSystemImage(int image) {
		return display.getSystemImage(image);
	}
	
	/** @return the system's font */
	public Font getSystemFont() {
		return display.getSystemFont();
	}
	
	/** @return a system cursor */
	public Cursor getSystemCursor(int id) {
		return display.getSystemCursor(id);
	}
	
	/** @return image from an program extension */
	public Image getProgramIcon(String extension) {
		String id = "program://" + extension;
		Image image = images.get(id);
		if ( image == null ) {
			Program program = Program.findProgram(extension);
			if ( program != null ) {
				image = new Image(display, program.getImageData());
				images.put(id, image);
			}
		}
		return image;
	}
	
	/** Dispose all kept elements */
	protected void dispose() {
		for ( Color color : colors.values() ) {
			color.dispose();
		}
		colors.clear();
		for ( Image image : images.values() ) {
			image.dispose();
		}
		images.clear();
		for ( Font font : fonts.values() ) {
			font.dispose();
		}
		fonts.clear();
	}
}
