package fr.minibilles.basics.ui.diagram.gc;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GCData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.graphics.Transform;

/**
 * GC implementation for SVG export.
 * @author Didier Simoneau
 */
public final class SWTGC implements GC {

	protected org.eclipse.swt.graphics.GC gc;
	
	protected boolean mustDisposeGc;

	public SWTGC(Drawable drawable) {
		this.gc = new org.eclipse.swt.graphics.GC(drawable);
		this.mustDisposeGc = true;
	}
	
	public SWTGC(Drawable drawable, int style) {
		this.gc = new org.eclipse.swt.graphics.GC(drawable, style);
		this.mustDisposeGc = true;
	}

	public SWTGC(org.eclipse.swt.graphics.GC gc) {
		this.gc = gc;
		this.mustDisposeGc = false; // because the GC what not created by this SWTGC, so it is not its responsibility to dispose it
	}
	
	public void copyArea(Image image, int x, int y) {
		gc.copyArea(image, x, y);
	}

	public void copyArea(int srcX, int srcY, int width, int height, int destX,
			int destY) {
		gc.copyArea(srcX, srcY, width, height, destX, destY);
	}

	public void copyArea(int srcX, int srcY, int width, int height, int destX,
			int destY, boolean paint) {
		gc.copyArea(srcX, srcY, width, height, destX, destY, paint);
	}

	public void dispose() {
		if (mustDisposeGc) gc.dispose();
	}

	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		gc.drawArc(x, y, width, height, startAngle, arcAngle);
	}

	public void drawFocus(int x, int y, int width, int height) {
		gc.drawFocus(x, y, width, height);
	}

	public void drawImage(Image image, int x, int y) {
		gc.drawImage(image, x, y);
	}

	public void drawImage(Image image, int srcX, int srcY, int srcWidth,
			int srcHeight, int destX, int destY, int destWidth, int destHeight) {
		gc.drawImage(image, srcX, srcY, srcWidth, srcHeight, destX, destY, destWidth, destHeight);
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		gc.drawLine(x1, y1, x2, y2);
	}

	public void drawOval(int x, int y, int width, int height) {
		gc.drawOval(x, y, width, height);
	}

	public void drawPath(Path path) {
		gc.drawPath(path);
	}

	public void drawPoint(int x, int y) {
		gc.drawPoint(x, y);
	}

	public void drawPolygon(int[] pointArray) {
		gc.drawPolygon(pointArray);
	}

	public void drawPolyline(int[] pointArray) {
		gc.drawPolyline(pointArray);
	}

	public void drawRectangle(int x, int y, int width, int height) {
		gc.drawRectangle(x, y, width, height);
	}

	public void drawRectangle(Rectangle rect) {
		gc.drawRectangle(rect);
	}

	public void drawRoundRectangle(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		gc.drawRoundRectangle(x, y, width, height, arcWidth, arcHeight);
	}

	
	public void drawString(String string, int x, int y) {
		gc.drawString(string, x, y);
	}

	
	public void drawString(String string, int x, int y, boolean isTransparent) {
		gc.drawString(string, x, y, isTransparent);
	}

	
	public void drawText(String string, int x, int y) {
		gc.drawText(string, x, y);
	}

	
	public void drawText(String string, int x, int y, boolean isTransparent) {
		gc.drawText(string, x, y, isTransparent);
	}

	
	public void drawText(String string, int x, int y, int flags) {
		gc.drawText(string, x, y, flags);
	}

	
	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		gc.fillArc(x, y, width, height, startAngle, arcAngle);
	}

	
	public void fillGradientRectangle(int x, int y, int width, int height,
			boolean vertical) {
		gc.fillGradientRectangle(x, y, width, height, vertical);
	}

	
	public void fillOval(int x, int y, int width, int height) {
		gc.fillOval(x, y, width, height);
	}

	
	public void fillPath(Path path) {
		gc.fillPath(path);
	}

	
	public void fillPolygon(int[] pointArray) {
		gc.fillPolygon(pointArray);
	}

	
	public void fillRectangle(int x, int y, int width, int height) {
		gc.fillRectangle(x, y, width, height);
	}

	
	public void fillRectangle(Rectangle rect) {
		gc.fillRectangle(rect);
	}

	
	public void fillRoundRectangle(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		gc.fillRoundRectangle(x, y, width, height, arcWidth, arcHeight);
	}

	
	public int getAdvanceWidth(char ch) {
		return gc.getAdvanceWidth(ch);
	}

	
	public boolean getAdvanced() {
		return gc.getAdvanced();
	}

	
	public int getAlpha() {
		return gc.getAlpha();
	}

	
	public int getAntialias() {
		return gc.getAntialias();
	}

	
	public Color getBackground() {
		return gc.getBackground();
	}

	
	public Pattern getBackgroundPattern() {
		return gc.getBackgroundPattern();
	}

	
	public int getCharWidth(char ch) {
		return gc.getCharWidth(ch);
	}

	
	public Rectangle getClipping() {
		return gc.getClipping();
	}

	
	public void getClipping(Region region) {
		gc.getClipping(region);
	}

	
	public Device getDevice() {
		return gc.getDevice();
	}
	
	
	public int getFillRule() {
		return gc.getFillRule();
	}

	
	public Font getFont() {
		return gc.getFont();
	}

	
	public FontMetrics getFontMetrics() {
		return gc.getFontMetrics();
	}

	
	public Color getForeground() {
		return gc.getForeground();
	}

	
	public Pattern getForegroundPattern() {
		return gc.getForegroundPattern();
	}

	
	public GCData getGCData() {
		return gc.getGCData();
	}

	
	public int getInterpolation() {
		return gc.getInterpolation();
	}

	
	public LineAttributes getLineAttributes() {
		return gc.getLineAttributes();
	}

	
	public int getLineCap() {
		return gc.getLineCap();
	}

	
	public int[] getLineDash() {
		return gc.getLineDash();
	}

	
	public int getLineJoin() {
		return gc.getLineJoin();
	}

	
	public int getLineStyle() {
		return gc.getLineStyle();
	}

	
	public int getLineWidth() {
		return gc.getLineWidth();
	}

	
	public int getStyle() {
		return gc.getStyle();
	}

	
	public int getTextAntialias() {
		return gc.getAntialias();
	}

	
	public void getTransform(Transform transform) {
		gc.getTransform(transform);
	}

	
	public boolean getXORMode() {
		return gc.getXORMode();
	}

	
	public boolean isClipped() {
		return gc.isClipped();
	}

	
	public boolean isDisposed() {
		return gc.isDisposed();
	}

	
	public void setAdvanced(boolean advanced) {
		gc.setAdvanced(advanced);
	}

	
	public void setAlpha(int alpha) {
		gc.setAlpha(alpha);
	}

	
	public void setAntialias(int antialias) {
		gc.setAntialias(antialias);
	}

	
	public void setBackground(Color color) {
		gc.setBackground(color);
	}

	
	public void setBackgroundPattern(Pattern pattern) {
		gc.setBackgroundPattern(pattern);
	}

	
	public void setClipping(int x, int y, int width, int height) {
		gc.setClipping(x, y, width, height);
	}

	
	public void setClipping(Path path) {
		gc.setClipping(path);
	}

	
	public void setClipping(Rectangle rect) {
		gc.setClipping(rect);
	}

	
	public void setClipping(Region region) {
		gc.setClipping(region);
	}

	
	public void setFillRule(int rule) {
		gc.setFillRule(rule);
	}

	
	public void setFont(Font font) {
		gc.setFont(font);
	}

	
	public void setForeground(Color color) {
		gc.setForeground(color);
	}

	
	public void setForegroundPattern(Pattern pattern) {
		gc.setForegroundPattern(pattern);
	}

	
	public void setInterpolation(int interpolation) {
		gc.setInterpolation(interpolation);
	}

	
	public void setLineAttributes(LineAttributes attributes) {
		gc.setLineAttributes(attributes);
	}

	
	public void setLineCap(int cap) {
		gc.setLineCap(cap);
	}

	
	public void setLineDash(int[] dashes) {
		gc.setLineDash(dashes);
	}

	
	public void setLineJoin(int join) {
		gc.setLineJoin(join);
	}

	
	public void setLineStyle(int lineStyle) {
		gc.setLineStyle(lineStyle);
	}

	
	public void setLineWidth(int lineWidth) {
		gc.setLineWidth(lineWidth);
	}

	
	public void setTextAntialias(int antialias) {
		gc.setTextAntialias(antialias);
	}

	
	public void setTransform(Transform transform) {
		gc.setTransform(transform);
	}

	 @Deprecated
	public void setXORMode(boolean xor) {
		gc.setXORMode(xor);
	}

	
	public Point stringExtent(String string) {
		return gc.stringExtent(string);
	}

	
	public Point textExtent(String string) {
		return gc.textExtent(string);
	}

	
	public Point textExtent(String string, int flags) {
		return gc.textExtent(string, flags);
	}
	
}
