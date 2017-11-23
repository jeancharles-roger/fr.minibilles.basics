/**
 * 
 */
package fr.minibilles.basics.serializer;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Set of utilities methods for {@link Boost}.
 * @author Jean-Charles Roger
 *
 */
public class BoostUtil {

	public static void writeBooleanArray(Boost boost, boolean[] array) {
		if (array == null) {
			boost.writeInt(0);
			return;
		}
		boost.writeInt(array.length);
		for (int i = 0; i < array.length; i++) {
			boost.writeBoolean(array[i]);
		}
	}

	public static boolean[] readBooleanArray(Boost boost) {
		int size = boost.readInt();
		boolean[] result = new boolean[size];
		for (int i = 0; i < size; i++) {
			result[i] = boost.readBoolean();
		}
		return result;
	}

	public static void writeDoubleArray(Boost boost, double[] array) {
		if (array == null) {
			boost.writeInt(0);
			return;
		}
		boost.writeInt(array.length);
		for (int i = 0; i < array.length; i++) {
			boost.writeDouble(array[i]);
		}
	}

	public static double[] readDoubleArray(Boost boost) {
		int size = boost.readInt();
		double[] result = new double[size];
		for (int i = 0; i < size; i++) {
			result[i] = boost.readInt();
		}
		return result;

	}

	public static void writeFloatArray(Boost boost, float[] array) {
		if (array == null) {
			boost.writeInt(0);
			return;
		}
		boost.writeInt(array.length);
		for (int i = 0; i < array.length; i++) {
			boost.writeFloat(array[i]);
		}
	}

	public static float[] readFloatArray(Boost boost) {
		int size = boost.readInt();
		float[] result = new float[size];
		for (int i = 0; i < size; i++) {
			result[i] = boost.readFloat();
		}
		return result;

	}
	
	public static void writeShortArray(Boost boost, short[] array) {
		if (array == null) {
			boost.writeInt(0);
			return;
		}
		boost.writeInt(array.length);
		for (int i = 0; i < array.length; i++) {
			boost.writeShort(array[i]);
		}
	}
	
	public static short[] readShortArray(Boost boost) {
		int size = boost.readInt();
		short[] result = new short[size];
		for (int i = 0; i < size; i++) {
			result[i] = boost.readShort();
		}
		return result;
		
	}

	public static void writeIntArray(Boost boost, int[] array) {
		if (array == null) {
			boost.writeInt(0);
			return;
		}
		boost.writeInt(array.length);
		for (int i = 0; i < array.length; i++) {
			boost.writeInt(array[i]);
		}
	}

	public static int[] readIntArray(Boost boost) {
		int size = boost.readInt();
		int[] result = new int[size];
		for (int i = 0; i < size; i++) {
			result[i] = boost.readInt();
		}
		return result;

	}

	public static void writeLongArray(Boost boost, long[] array) {
		if (array == null) {
			boost.writeInt(0);
			return;
		}
		boost.writeInt(array.length);
		for (int i = 0; i < array.length; i++) {
			boost.writeLong(array[i]);
		}
	}

	public static long[] readLongArray(Boost boost) {
		int size = boost.readInt();
		long[] result = new long[size];
		for (int i = 0; i < size; i++) {
			result[i] = boost.readLong();
		}
		return result;
	}
	

	public static void writeStringArray(Boost boost, String [] array) {
		if (array == null) {
			boost.writeInt(0);
			return;
		}
		boost.writeInt(array.length);
		for (int i = 0; i < array.length; i++) {
			boost.writeString(array[i]);
		}
	}
	
	public static void writeStringCollection(Boost boost, Collection<String> value) {
		if (value == null) {
			boost.writeInt(0);
			return;
		}
		boost.writeInt(value.size());
		for (String oneString : value) {
			boost.writeString(oneString);
		}
	}
	
	public static String[] readStringArray(Boost boost) {
		int size = boost.readInt();
		String[] result = new String[size];
		for (int i = 0; i < size; i++) {
			result[i] = boost.readString();
		}
		return result;
	}
	
	public static List<String> readStringList(Boost boost) {
		int size = boost.readInt();
		List<String> result = new ArrayList<String>(size);
		for (int i = 0; i < size; i++) {
			result.add(boost.readString());
		}
		return result;
	}

	public static <T extends Enum<T>> void writeEnumArray(Boost boost,T [] array) {
		if (array == null) {
			boost.writeInt(0);
			return;
		}
		boost.writeInt(array.length);
		for (int i = 0; i < array.length; i++) {
			boost.writeEnum(array[i]);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Enum<T>> T[] readEnumArray(Boost boost, Class<T> enumClass) {
		int size = boost.readInt();
		T[] result = (T[]) Array.newInstance(enumClass, size);
		for (int i = 0; i < size; i++) {
			result[i] = boost.readEnum(enumClass);
		}
		return result;
	}
	
	public static void writeObjectArray(Boost boost, BoostObject [] array) {
		if (array == null) {
			boost.writeInt(0);
			return;
		}
		boost.writeInt(array.length);
		for (int i = 0; i < array.length; i++) {
			boost.writeObject(array[i]);
		}
	}
	

	@SuppressWarnings("unchecked")
	public static <T extends BoostObject> T[] readObjectArray(Boost boost, Class<T> type) {
		int size = boost.readInt();
		T[] result = (T[]) Array.newInstance(type, size);
		for (int i = 0; i < size; i++) {
			result[i] = boost.readObject(type);
		}
		return result;
	}


	/**
	 * Writes a collection of {@link BoostObject} in the writer.
	 *
	 * @param collection
	 *            {@link Collection} to write.
	 */
	public static <T extends BoostObject> void writeObjectCollection(Boost boost, Collection<T> collection) {
		if (collection == null) {
			boost.writeInt(0);
			return;
		}
		boost.writeInt(collection.size());
		for (T oneObject : collection) {
			boost.writeObject(oneObject);
		}
	}

	public static <T extends BoostObject> List<T> readObjectList(Boost boost, Class<T> objectClass) {
		int size = boost.readInt();
		List<T> result = new ArrayList<T>(size);
		for (int i = 0; i < size; i++) {
			result.add(boost.readObject(objectClass));
		}
		return result;
	}
	
	public static void writeFile(Boost boost, File file) {
		if (file == null) {
			boost.writeString(null);
		} else {
			boost.writeString(file.getPath());
		}
	}

	public static File readFile(Boost boost) {
		String path = boost.readString();
		return path == null ? null : new File(path);
	}
	
	
	public static void writeFileCollection(Boost boost, Collection<File> value) {
		if (value == null) {
			boost.writeInt(0);
			return;
		}
		boost.writeInt(value.size());
		for (File oneString : value) {
			writeFile(boost, oneString);
		}
	}
	
	
	public static List<File> readFileList(Boost boost) {
		int size = boost.readInt();
		List<File> result = new ArrayList<File>(size);
		for (int i = 0; i < size; i++) {
			result.add(readFile(boost));
		}
		return result;
	}


}
