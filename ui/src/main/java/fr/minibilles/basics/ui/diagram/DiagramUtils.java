package fr.minibilles.basics.ui.diagram;

import fr.minibilles.basics.ui.diagram.gc.GC;
import fr.minibilles.basics.ui.diagram.gc.SWTGC;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;

public class DiagramUtils {
	
	/**
	 * Exports a diagram to a bitmap file. Supported formats are PNG, Jpeg, GIF, bmp.
	 * If filename is null, does nothing.
	 * @param diagram
	 *            the diagram to be exported
	 * @param fileName
	 *            the destination file name
	 * @param scale
	 *            the scaling factor applied to diagram coordinates to get
	 *            bitmap coordinates (image pixels).
	 * @param format
	 *            one of {@link SWT#IMAGE_PNG},  {@link SWT#IMAGE_JPEG},  {@link SWT#IMAGE_BMP}
	 */
	public static void exportToImage(Diagram<?> diagram, String fileName, float scale, int format) {
		if (fileName == null) return;
		DiagramController diagramController = new DiagramController(diagram, DiagramContext.BITMAP_FILE, null);
		float[] diagramBounds = diagram.getElementsBounds(diagramController);
		int width = Math.round((diagramBounds[2] - diagramBounds[0]) * scale);
		int height = Math.round((diagramBounds[3] - diagramBounds[1]) * scale);
		Image image = new Image(Display.getCurrent(), width, height);
		diagramController.setWindowOrigin(diagramBounds);
		diagramController.refreshElements(true);
		GC gc = new SWTGC(image);
		diagramController.initializeGcTransform(gc);
		diagram.initializeGc(gc, diagramController);
		for (Element element : diagram.getElements()) {
			diagram.resetGc(gc, diagramController);
			element.display(gc, diagramController);
		}
		gc.dispose();
		diagramController.dispose();
		ImageLoader loader = new ImageLoader();
		loader.data = new ImageData[] { image.getImageData() };
		loader.save(fileName, format);
		image.dispose();
	}
	
}
