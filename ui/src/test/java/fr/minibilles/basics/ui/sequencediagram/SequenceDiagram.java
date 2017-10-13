package fr.minibilles.basics.ui.sequencediagram;

import fr.minibilles.basics.progress.ActionMonitor;
import fr.minibilles.basics.ui.action.Action;
import fr.minibilles.basics.ui.diagram.Diagram;
import fr.minibilles.basics.ui.diagram.DiagramContext;
import fr.minibilles.basics.ui.diagram.DiagramController;
import fr.minibilles.basics.ui.diagram.DiagramUtils;
import fr.minibilles.basics.ui.diagram.Element;
import fr.minibilles.basics.ui.sequencediagram.model.HorizontalLine;
import fr.minibilles.basics.ui.sequencediagram.model.LifeLine;
import fr.minibilles.basics.ui.sequencediagram.model.Message;
import fr.minibilles.basics.ui.sequencediagram.model.Pause;
import fr.minibilles.basics.ui.sequencediagram.model.Sequence;
import fr.minibilles.basics.ui.sequencediagram.model.SequenceItem;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.FileDialog;

public class SequenceDiagram extends Diagram<Sequence> {

	public static final RGB defaultBackGroundRgb = new RGB(255, 251, 246);

	public SequenceDiagram() {
	}
	
	public SequenceDiagram(Sequence model) {
		setModel(model);
	}
	
	
	@Override
	public void build() {
		clearElements();
		if ( model != null ) {
			if ( model.getLineCount() == 0 ) return;
			
			// calc minX and maxX
			float minX = Float.MAX_VALUE;
			float maxX = Float.MIN_VALUE;
			for ( LifeLine line : model.getLifeLineList() ) {
				final float x = line.getX();
				if ( x > maxX ) maxX = x;
				if ( x < minX ) minX = x;
				
			}
			
			float y = 100f;
			for ( SequenceItem item : model.getItemList() ) {
				
				float delta = 30f;
				if ( item instanceof Message ) {
					Message message = (Message) item;
					float[] sourcePoint = new float[] { message.getSource().getX(), y};
					float[] targetPoint = new float[] { message.getTarget().getX(), y};
					
					MessageElement element = new MessageElement(message, sourcePoint, targetPoint);
					addElement(element);
					
				} else if ( item instanceof HorizontalLine ) {
					float[] sourcePoint = new float[] { minX - 50f, y};
					float[] targetPoint = new float[] { maxX + 50f, y};
					HorizontalLineElement element = new HorizontalLineElement((HorizontalLine) item, sourcePoint, targetPoint);
					addElement(element);
				
				} else if ( item instanceof Pause ) {
					float[] sourcePoint = new float[] { minX - 50f, y};
					float[] targetPoint = new float[] { maxX + 50f, y};
					PauseElement element = new PauseElement((Pause) item, sourcePoint, targetPoint);
					addElement(element);
					
					delta = 50f;
				}
				
				y+= delta;
				
			}
			
			for ( LifeLine line : model.getLifeLineList() ) {
				LifeLineElement element = new LifeLineElement(line, new float[] { line.getX(), 50f }, 100f, y);
				addElement(0,element);
			}
		}
		invalidateBounds();
	}

	public void updateAll(DiagramContext context) {

		// calc minX and maxX
		float minX = Float.MAX_VALUE;
		float maxX = Float.MIN_VALUE;
		for ( LifeLine line : model.getLifeLineList() ) {
			LifeLineElement element = findElement(LifeLineElement.class, line);
			final float x = element.getX();
			if ( x > maxX ) maxX = x;
			if ( x < minX ) minX = x;
		}

		for ( Element element : getElements() ) {
			if ( element.getClass() == MessageElement.class ) {
				((MessageElement) element).update(context);
			}
			if ( element.getClass() == HorizontalLineElement.class ) {
				((HorizontalLineElement) element).update(context, minX, maxX);
			}
		}
	}
	
	@Override
	public Color getBackground(DiagramContext context) {
		//return context.getResources().getColor(defaultBackGroundRgb);
		return context.getResources().getSystemColor(SWT.COLOR_WHITE);
	}
	
	@Override
	public void computeActions(final List<Action> result, final DiagramContext context) {
		DiagramController controller = (DiagramController) context;
		result.add(controller.createIncreaseZoomAction());
		result.add(controller.createDecreaseZoomAction());
		result.add(new Action.Stub("Export to PNG\u2026") {
			@Override
			public int run(ActionMonitor monitor) {
				FileDialog dialog = new FileDialog(context.getControl().getShell(), SWT.SAVE);
				dialog.setFilterExtensions(new String[] { "*.png" });
				dialog.setFilterNames(new String[] { "PNG image" });
				String filename = dialog.open();
				if ( filename == null ) return STATUS_CANCEL;
				if ( !filename.endsWith(".png") ) filename += ".png";
				DiagramUtils.exportToImage(SequenceDiagram.this, filename, 1f, SWT.IMAGE_PNG);
				System.out.println("Exported to " + filename);
				return STATUS_OK;
			}
		});
	}

}
