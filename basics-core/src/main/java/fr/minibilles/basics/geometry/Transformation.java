package fr.minibilles.basics.geometry;

/**
 * This class provides static functions for handling and applying 2D affine
 * transformations.
 * <p>
 * Transformations are represented by 6 elements float array {m11, m12, m21,
 * m22, dx, dy}, compatible with the SWT Transform class. The direct
 * transformation is defined by the equations:<br>
 * x' = m11*x + m21*y + dx<br>
 * y' = m12*x + m22*y + dy
 * 
 * @author dsimoneau
 */
public class Transformation {

	// ******************** Point transformations ********************
	
	/** Applies a transformation to a point; returns the x coordinate of the transformed point. */
	public static float transformX(float[] tr, float x, float y) {
		return x * tr[0] + y * tr[2] + tr[4];
	}
	
	/** Applies a transformation to a point; returns the y coordinate of the transformed point. */
	public static float transformY(float[] tr, float x, float y) {
		return x * tr[1] + y * tr[3] + tr[5];
	}
	
	/**
	 * Applies the inverse transformation of a given transformation to a
	 * point; returns the x coordinate of the transformed point.
	 */
	public static float inverseTransformX(float[] tr, float x, float y) {
		float d = tr[0] * tr[3] - tr[1] * tr[2]; // matrix determinant
		return ((x -  tr[4]) * tr[3] - (y -  tr[5]) * tr[2]) / d;
	}
	
	/**
	 * Applies the inverse transformation of a given transformation to a
	 * point; returns the y coordinate of the transformed point.
	 */
	public static float inverseTransformY(float[] tr, float x, float y) {
		float d = tr[0] * tr[3] - tr[1] * tr[2]; // matrix determinant
		return ((y -  tr[5]) * tr[0] - (x -  tr[4]) * tr[1]) / d;
	}
	
	/**
	 * Applies the inverse transformation of a given transformation to an array
	 * of points. The result is written back into the provided array.
	 */
	public static void transform(float[] tr, float[] points) {
		for (int i = 0, j = 1; j < points.length; i+=2, j+=2) {
			float x = points[i];
			float y = points[j];
			points[i] = transformX(tr, x, y);
			points[j] = transformY(tr, x, y);
		}
	}
	
	/** Applies a transformation to an array of points. The result is written back into the provided array. */
	public static void inverseTransform(float[] tr, float[] points) {
		float d = tr[0] * tr[3] - tr[1] * tr[2]; // matrix determinant
		for (int i = 0, j = 1; j < points.length; i+=2, j+=2) {
			float x = points[i];
			float y = points[j];
			points[i] = ((x -  tr[4]) * tr[3] - (y -  tr[5]) * tr[2]) / d;
			points[j] = ((y -  tr[5]) * tr[0] - (x -  tr[4]) * tr[1]) / d;
		}
	}
	
	// ******************** Vector transformations ********************
	
	public static float vectorTransformX(float[] tr, float x, float y) {
		return x * tr[0] + y * tr[2];
	}
	
	public static float vectorTransformY(float[] tr, float x, float y) {
		return x * tr[1] + y * tr[3];
	}
	
	public static float vectorInverseTransformX(float[] tr, float x, float y) {
		float d = tr[0] * tr[3] - tr[1] * tr[2]; // matrix determinant
		return (x * tr[3] - y * tr[2]) / d;
	}
	
	public static float vectorInverseTransformY(float[] tr, float x, float y) {
		float d = tr[0] * tr[3] - tr[1] * tr[2]; // matrix determinant
		return (y * tr[0] - x * tr[1]) / d;
	}
	
	// ******************** Transformations creation ********************
	
	/** Creates an identity transformation. */
	public static float[] newIdentityTransformation() {
		return new float[] {1f, 0f, 0f, 1f, 0f, 0f};
	}
	
	/**
	 * Creates a transformation, combining a scale and a translation, that,
	 * given two scale factors for x and y, transform point1 into point2.
	 */
	public static float[] newTransformation(float xScale, float yScale, float[] point1, float[] point2) {
		float xOffset = point2[0] - (point1[0] * xScale);
		float yOffset = point2[1] - (point1[1] * yScale);
		return new float[]{xScale, 0f, 0f, yScale, xOffset, yOffset};
	}
	
	/**
	 * Creates a transformation, combining a scale and a translation, that transform rectangle1 into rectangle2.
	 */
	public static float[] newTransformation(float[] rectangle1, float[] rectangle2) {
		float xScale = (rectangle2[2] - rectangle2[0]) / (rectangle1[2] - rectangle1[0]);
		float yScale = (rectangle2[3] - rectangle2[1]) / (rectangle1[3] - rectangle1[1]);
		float xOffset = rectangle2[0] - (rectangle1[0] * xScale);
		float yOffset = rectangle2[1] - (rectangle1[1] * yScale);
		return new float[]{xScale, 0f, 0f, yScale, xOffset, yOffset};
	}
	
	/**
	 * Creates a translate transformation.
	 * @param dx a delta for x
	 * @param dy a delta for y
	 */
	public static float[] newTranslateTransformation(float dx, float dy) {
		return new float[]{1f, 0f, 0f, 1f, dx, dy};
	}
	
	/**
	 * Creates a scale transformation.
	 * @param sx a scale factor in x
	 * @param sy a scale factor in y
	 */
	public static float[] newScaleTransformation(float sx, float sy) {
		return new float[]{sx, 0f, 0f, sy, 0f, 0f };
	}
	
	/**
	 * Creates a rotation transformation.
	 * @param angle an angle, in radians
	 */
	public static float[] newRotationTransformation(double angle) {
		float c = (float)Math.cos(angle);
		float s = (float)Math.sin(angle);
		return new float[]{c, s, -s, c, 0f, 0f};
	}
	
	// ******************** Transformations operations ********************

	/**
	 * Returns a transformation that is the inverse of the given transformation.
	 */
	public static float[] getInverseTransformation(float[] tr) {
		float d = tr[0] * tr[3] - tr[1] * tr[2]; // matrix determinant
		return new float[]{
				tr[3]/d, 
				-tr[1]/d,
				-tr[2]/d,
				tr[0]/d,
				(tr[2]*tr[5] - tr[3]*tr[4]) / d,
				(tr[1]*tr[4] - tr[0]*tr[5]) / d
		};
	}
	
	/**
	 * Returns a transformation that is the composition (tr2 o tr1) of the given
	 * transformations tr2 and tr1.
	 * <p>
	 * For given point (x, y), the composed transformation is defined by the
	 * equation:<br>
	 * (tr2 o tr1)(x, y) = tr2(tr1(x, y))
	 */
	public static float[] getComposedTransformation(float[] tr2, float[]tr1) {
		return new float[]{
				tr2[0]*tr1[0] + tr2[2]*tr1[1],
				tr2[1]*tr1[0] + tr2[3]*tr1[1],
				tr2[0]*tr1[2] + tr2[2]*tr1[3],
				tr2[1]*tr1[2] + tr2[3]*tr1[3],
				tr2[0]*tr1[4] + tr2[2]*tr1[5] + tr2[4],
				tr2[1]*tr1[4] + tr2[3]*tr1[5] + tr2[5]
		};
	}
	
	// ******************** Testing ********************
	
	/**
	 * Tests if a two transformation are equal. A tolerance is applied when
	 * comparing matrix elements.
	 * @see Transformation#fequals(float, float)
	 */
	public static boolean areEqual(float[] tr1, float[] tr2) {
		for (int i = 0; i < 6; i++) {
			if (!fequals(tr1[i], tr2[i])) return false;
		}
		return true;
	}
	
	/**
	 * Tests if a transformation is the identity transformation. A tolerance
	 * is applied when comparing matrix elements.
	 * @see Transformation#fequals(float, float)
	 */
	public static boolean isIdentity(float[] tr) {
		return fequals(tr[4], 0f) && fequals(tr[5], 0f) && isTranslation(tr);
	}
	
	/**
	 * Tests if a transformation is is a pure translation. A tolerance
	 * is applied when comparing matrix elements.
	 * @see Transformation#fequals(float, float)
	 */
	public static boolean isTranslation(float[] tr) {
		return fequals(tr[0], 1f) && fequals(tr[1], 0f) && fequals(tr[2], 0f) && fequals(tr[3], 1f);
	}
	
	/**
	 * Compares two floats.
	 * @param float1
	 * @param float2
	 * @return true if the distance between the two floats is less than 1e-6, false otherwise
	 */
	public static boolean fequals(float float1, float float2) {
		return Math.abs(float1 - float2) <= 1e-6f;
	}
	
}
