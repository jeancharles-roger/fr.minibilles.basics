package fr.minibilles.basics.serializer;

import fr.minibilles.basics.error.Diagnostic;
import fr.minibilles.basics.error.DiagnosticUtil;
import fr.minibilles.basics.error.ErrorHandler;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * A {@link JBoost} is able to serialize and de-serialize {@link BoostObject} to
 * text files in UTF-8 encoding.
 * <p>
 * <b>Writing example</b>
 * 
 * <pre>
 * JBoost boost = new JBoost(&quot;Cob&quot;, 1);
 * FileOutputStream fileStream = new FileOutputStream(file);
 * boost.initializeWritin(fileStream);
 * boost.writeObject(this);
 * boost.close();
 * </pre>
 *
 * <p>
 * <b>Reading example</b>
 * 
 * <pre>
 * JBoost boost = new JBoost(&quot;Cob&quot;, 1);
 * FileInputStream fileStream = new FileInputStream(file);
 * boost.initializeReading(fileStream);
 * Model result = (Model) boost.readObject();
 * boost.close();
 * </pre>
 * 
 * @author Jean-Charles Roger.
 * 
 */
public class JBoost implements Boost {

	/**
	 * Token separators.
	 */
	protected static final String Separators = "[]{} :";

	/**
	 * Writing target.
	 */
	protected Writer writer;

	/**
	 * Reading source.
	 */
	protected Reader reader;

	/**
	 * This {@link ArrayList} stores the object indexes through file while
	 * <b>reading</b>.
	 */
	protected final ArrayList<BoostObject> readObjetIndex = new ArrayList<BoostObject>();

	/**
	 * This {@link Map} store the object indexes through file while
	 * <b>writing</b>.
	 */
	protected final LinkedHashMap<BoostObject, Integer> writeObjetIndex = new LinkedHashMap<BoostObject, Integer>();

	/**
	 * This {@link Map} store the class indexes through file.
	 */
	protected final HashMap<Class<? extends BoostObject>, Integer> classIndex = new HashMap<Class<? extends BoostObject>, Integer>();

	/**
	 * This {@link String} will be printed in the file header as serial Type.
	 */
	protected final String type;

	/**
	 * This integer will be printed in the file header as serial Version.
	 */
	protected final int version;

	/**
	 * Stores the version of file that is currently loading.
	 */
	protected int fileVersion;

	/**
	 * Handles errors.
	 */
	protected ErrorHandler errorHandler = ErrorHandler.simple;

	/**
	 * Stores names of classes that have changed. If {@link JBoost} reads
	 * a file with a reference to a class contained in this map it will create
	 * a class the value of the entry.
	 */
	protected Map<String, String> boostNameToClassName = null;
	
	/**
	 *  {@link Boost} class loader.
	 */
	private final ClassLoader classLoader;
	
	/**
	 * Creates a {@link JBoost} instance for a special type and version.
	 *
	 * @param type
	 *            type of file to read.
	 * @param version
	 *            version reference.
	 * @param classLoader
	 * 			  {@link ClassLoader} to use to load classes.
	 */
	public JBoost(String type, int version, ClassLoader classLoader) {
		this.type = type;
		this.version = version;
		this.classLoader = classLoader;
	}
	
	/**
	 * Creates a {@link JBoost} instance for a special type and version.
	 *
	 * @param type
	 *            type of file to read.
	 * @param version
	 *            version reference.
	 */
	public JBoost(String type, int version) {
		this(type, version, JBoost.class.getClassLoader());
	}

	/**
	 * The current file version that is currently loading. Accessible only after
	 * {@link #initializeReading(InputStream)}.
	 *
	 * @return the current file version.
	 */
	public int getFileVersion() {
		return fileVersion;
	}

	/** @return current {@link Boost} file's version. */
	public int getVersion() {
		return version;
	}

	/** @return the error handler. */ 
	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}

	/** Sets the error handler. */ 
	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	public void register(BoostObject object) {
		readObjetIndex.add(object);
	}
	
	public void addBoostNameToClassName(String boostName, String className) {
		if ( boostNameToClassName == null ) {
			// lazy creation, there is no need to create a map
			// if there are no match.
			boostNameToClassName = new HashMap<String, String>();
		}
		boostNameToClassName.put(boostName, className);
	}
	
	/** Initializes current {@link Boost} to write in given zipped stream. */
	public void initializeZippedWriting(ZipOutputStream stream, String entryName) {
		ZipEntry entry = new ZipEntry(entryName);
		try {
			stream.putNextEntry(entry);
		} catch (IOException e) {
			getErrorHandler().handleError(Diagnostic.ERROR, DiagnosticUtil.createMessage(e));
			return;
		}
		initializeWriting(stream);
	}
	
	/** Initializes current {@link Boost} to read in given stream. */
	public void initializeZippedReading(ZipInputStream stream, String entryName) {

		ZipEntry entry = null;
		try {
			entry = stream.getNextEntry();
			while ( entry != null && !entryName.equals(entry.getName()) ) {
				entry = stream.getNextEntry();
			}
		} catch (IOException e) {
			getErrorHandler().handleError(Diagnostic.ERROR, DiagnosticUtil.createMessage(e));
			return;
		}
		if ( entry == null ) {
			getErrorHandler().handleError(Diagnostic.ERROR, "No entry named '"+ entryName + "' in zipped stream.");
			return;
		}
		initializeReading(stream);
	}
	
	/** Initializes current {@link Boost} to write in given stream. */
	public void initializeWriting(OutputStream stream) {
		writeObjetIndex.clear();
		classIndex.clear();
		try {
			this.writer = new BufferedWriter(new OutputStreamWriter(stream, getEncoding()));
		} catch (UnsupportedEncodingException e) {
			return;
		}
		writeHeader();
	}

	/** Initializes current {@link Boost} to read in given stream. */
	public void initializeReading(InputStream stream) {
		readObjetIndex.clear();
		classIndex.clear();
		try {
			this.reader = new BufferedReader(new InputStreamReader(stream, getEncoding()));
		} catch (UnsupportedEncodingException e) {
			return;
		}
		// init token reader
		basicReadChar();
		readHeader();
	}

	/** Closes {@link Boost} read or write session. */
	public void close() {
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				errorHandler.handleError(Diagnostic.ERROR, "I/O exception: " + e.getMessage());
			} finally {
				writer = null;
			}
		}

		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				errorHandler.handleError(Diagnostic.ERROR, "I/O exception: " + e.getMessage());
			}
			reader = null;
		}
	}

	/**
	 * @return the {@link Boost} encoding
	 */
	protected String getEncoding() {
		return "UTF-8";
	}

	/**
	 * @return the {@link Boost} locale
	 */
	protected Locale getLocale() {
		return Locale.ENGLISH;
	}

	/**
	 * @return This {@link String} is printed in all JBoost files.
	 */
	protected String getFormatMagic() {
		return "XidFile";
	}

	/**
	 * @return This integer identifies JBoost version.
	 */
	protected int getFormatVersion() {
		return 1;
	}

	/** Writes header in stream */
	protected void writeHeader() {
		basicWriteString(getFormatMagic() + " " + getFormatVersion() + " ");
		writeString(type);
		writeInt(version);
		fileVersion = version;
	}

	/** Reads header from stream */
	protected void readHeader() {
		String magic = nextToken();
		if (!getFormatMagic().equals(magic)) {
			errorHandler.handleError(Diagnostic.ERROR, "Wrong format type: " + magic + ", it should be " + getFormatMagic());
			return;
		}
		readToken(" ");

		fileVersion = readInt();
		if (getFormatVersion() < fileVersion ) {
			errorHandler.handleError(Diagnostic.ERROR, "Can't read version " + fileVersion + " file with version " + getFormatVersion() + " reader.");
			return;
		}

		String currentType = readString();
		if (!type.equals(currentType)) {
			errorHandler.handleError(Diagnostic.ERROR, "Wrong file type: " + currentType + ", it should be " + type);
			return;
		}
		fileVersion = readInt();
	}

	public void writeBoolean(boolean b) {
		basicWriteString(b ? "t " : "f ");
	}

	public boolean readBoolean() {
		boolean result = nextToken().equals("t");
		readToken(" ");
		return result;
	}

	public void writeDouble(double d) {
		basicWriteString(String.valueOf(d));
		basicWriteString(" ");
	}

	public double readDouble() {
		double result = Double.valueOf(nextToken());
		readToken(" ");
		return result;
	}

	public void writeFloat(float f) {
		basicWriteString(String.valueOf(f));
		basicWriteString(" ");
	}

	public float readFloat() {
		float result = Float.valueOf(nextToken());
		readToken(" ");
		return result;
	}
	
	public void writeByte(byte b) {
		basicWriteString(String.valueOf(b));
		basicWriteString(" ");
	}
	
	public byte readByte() {
		byte result = Byte.valueOf(nextToken());
		readToken(" ");
		return result;
	}

	public void writeShort(short s) {
		basicWriteString(String.valueOf(s));
		basicWriteString(" ");
	}
	
	public short readShort() {
		short result = Short.valueOf(nextToken());
		readToken(" ");
		return result;
	}
	
	public void writeInt(int i) {
		basicWriteString(String.valueOf(i));
		basicWriteString(" ");
	}

	public int readInt() {
		int result = Integer.valueOf(nextToken());
		readToken(" ");
		return result;
	}

	public void writeLong(long l) {
		basicWriteString(String.valueOf(l));
		basicWriteString(" ");
	}

	public long readLong() {
		long result = Long.valueOf(nextToken());
		readToken(" ");
		return result;
	}

	public void writeObject(BoostObject obj) {

		if (obj == null) {
			basicWriteString("n ");
			return;
		}

		basicWriteString("{");
		if (writeObjetIndex.containsKey(obj)) {
			// object already been serialized
			basicWriteString(writeObjetIndex.get(obj).toString());
		} else {

			// first serialization for object
			// use the objectIndex size as new object index
			int index = writeObjetIndex.size();
			writeObjetIndex.put(obj, index);

			basicWriteString(String.valueOf(index));
			basicWriteString(" ");
			writeClass(obj.getClass());
			obj.writeToBoost(this);

		}
		basicWriteString("} ");
	}

	public <T extends BoostObject> T readObject(Class<T> objectClass) {
		String token = nextToken();

		// case of null.
		if (token.equals("n")) {
			readToken(" ");
			return null;
		}

		// case of a reference 
		if ( token.charAt(0) == '!' ) {
			
			// get size from token: '![size]'
			int size = Integer.valueOf(token.substring(1));

			// consume next token: ':'
			readToken(":");

			String result = readNCharacters(size);
			
			// consume space after string.
			readToken(" ");
			
			return resolveReference(result, objectClass);
		}
		
		
		// lastDelimiter must be must be equals to "{".

		token = nextToken();
		int objectId = Integer.valueOf(token);

		token = nextToken();

		T result = null;
		if (token.equals(" ")) {
			Class<? extends T> objectRealClass = readClass(objectClass);
			try {
				Constructor<? extends T> constructor = findConstructor(objectRealClass);
				constructor.setAccessible(true);
				result = (T) constructor.newInstance(this);
			
			} catch (Exception e) {
				errorHandler.handleError(Diagnostic.ERROR, DiagnosticUtil.createMessage(e));
				return result;
			}

			readToken("}");

		} else /* token must be '}' */{
			// searches for object.
			
			if ( objectId < readObjetIndex.size() ) {
				result = objectClass.cast(readObjetIndex.get(objectId));
			}

			if (result == null) {
				// object already been encounters, the objectIndex must contain
				// it
				errorHandler.handleError(Diagnostic.ERROR, "Object id " + objectId + " doesn't exist");
				return null;
			}
		}
		readToken(" ");
		return result;
	}

	protected <T extends BoostObject> Constructor<T> findConstructor(Class<T> objectClass) throws NoSuchMethodException {
		for (Constructor<?> constructor : objectClass.getDeclaredConstructors()) {
			Class<?>[] types = constructor.getParameterTypes();
			if (types.length == 1 && Boost.class.isAssignableFrom(types[0])) {
				return (Constructor<T>) constructor;
			}
		}
		throw new NoSuchMethodError("Can't find a Boost constructor for '" + objectClass.getName() + "'");
	}

	protected void writeClass(Class<? extends BoostObject> oneClass) {
		if (classIndex.containsKey(oneClass)) {
			// class already been serialized
			basicWriteString("[");
			basicWriteString(classIndex.get(oneClass).toString());
			basicWriteString("] ");
			return;
		}
		// first serialization for class
		// use the classIndex size as new class index
		int index = classIndex.size();
		classIndex.put(oneClass, index);

		// searches in mapping for class name, if none uses the simple name.
		String className = getClassBoostNameMap().get(oneClass);
		if (className == null) {
			className = createClassName(oneClass);
		}

		basicWriteString("[");
		basicWriteString(String.valueOf(index));
		basicWriteString(" ");
		basicWriteString(className);
		basicWriteString("] ");
	}
	
	private String createClassName(Class<?> clazz) {
		Class<?> declaringClass = clazz.getDeclaringClass();
		if ( declaringClass != null ) {
			StringBuilder className = new StringBuilder();
			className.append(createClassName(declaringClass));
			className.append("$");
			className.append(clazz.getSimpleName());
			return className.toString();
		} else {
			return clazz.getCanonicalName();
		}
	}

	@SuppressWarnings("unchecked")
	protected <T extends BoostObject> Class<? extends T> readClass(Class<T> parentClass) {
		Class<? extends BoostObject> result = null;
		readToken("[");
		int classId = Integer.valueOf(nextToken());
		if (classIndex.containsValue(classId)) {
			// searches for class.
			for (Entry<Class<? extends BoostObject>, Integer> entry : classIndex.entrySet()) {
				if (entry.getValue() == classId) {
					result = entry.getKey();
					break;
				}
			}
		} else {
			readToken(" ");

			String boostClassName = nextToken();
			result = findClass(boostClassName);
			classIndex.put(result, classIndex.size());
		}

		if (!parentClass.isAssignableFrom(result)) {
			errorHandler.handleError(Diagnostic.ERROR, "Excepting for class " + parentClass.getSimpleName()
					+ " but read incompatible class " + result.getSimpleName() + ".");
			return null;
		}

		readToken("]");
		readToken(" ");
		return (Class<? extends T>) result;
	}

	public void writeString(String stringValue) {
		if (stringValue == null) {
			basicWriteString("n ");
			return;
		}

		basicWriteString("s");
		basicWriteString(String.valueOf(stringValue.length()));
		basicWriteString(":");
		basicWriteString(stringValue);
		basicWriteString(" ");
	}

	public String readString() {
		// reads token: 's[size]'
		String token = nextToken();
		
		if (token.equals("n")) {
			readToken(" ");
			return null;
		}
		
		int size = Integer.valueOf(token.substring(1));

		// consume next token: ':'
		readToken(":");

		String result = readNCharacters(size);
		
		// consume space after string.
		readToken(" ");
		return result;
	}

	/**
	 * Reads exactly the given number of characters.
	 *
	 * @param size number of character to read
	 * @return the read {@link String}.
	 */
	protected String readNCharacters(int size) {
		// consume all characters of the string, and build it
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < size; i++) {
			buffer.append(lookAheadChar);
			basicReadChar();
		}
		return buffer.toString();
	}

	public <T extends Enum<T>> void writeEnum(T enumValue) {
		if (enumValue == null) {
			basicWriteString("n ");
			return;
		}

		String enumLiteral = enumValue.name();
		basicWriteString("#");
		basicWriteString(String.valueOf(enumLiteral.length()));
		basicWriteString(":");
		basicWriteString(enumLiteral);
		basicWriteString(" ");
	}

	public <T extends Enum<T>> T readEnum(Class<T> enumClass) {
		final String readString = readString();
		if ( readString == null ) return null;
		return Enum.valueOf(enumClass, readString);
	}
	
	protected void basicWriteString(String value) {
		try {
			writer.write(value);
		} catch (IOException e) {
			errorHandler.handleError(Diagnostic.ERROR, "I/O error: " + e.getMessage());
		}
	}

	protected void readToken(String exceptedToken) {
		String token = nextToken();
		if (!token.equals(exceptedToken)) {
			errorHandler.handleError(Diagnostic.ERROR, "Expecting '" + exceptedToken + "' but read '" + token + "'");
		}
	}

	protected boolean eof = false;
	protected char lookAheadChar = '\0';

	/**
	 * <p>
	 * Reads the next token from stream. It considers the tokens separated by
	 * {@value #Separators}. One separator is one token, nothing is lost,
	 * everything in the buffer will appear as tokens.
	 * <p>
	 * For example, the stream <code>0.001 [1 AClass] s3:token</code>, will
	 * gives the next tokens:
	 * <ul>
	 * <li>'<code>0.001</code>', '<code> </code>', '<code>[</code>', '
	 * <code>1</code>', '<code> </code>', '<code>AClass</code>', '<code>]</code>
	 * ', '<code> </code>', '<code>s3</code>', '<code>:</code>', '
	 * <code>token</code>'
	 * </ul>
	 * 
	 * @return the next token in the stream, null if eof is reached
	 */
	protected String nextToken() {
		if ( eof ) return null;
		StringBuilder tokenBuffer = new StringBuilder();
		while (true) {
			switch (lookAheadChar) {
			case '[': case ']': case '{': case '}': case ' ': case ':':
				if (tokenBuffer.length() == 0) {
					tokenBuffer.append(lookAheadChar);
					basicReadChar();
				}
				return tokenBuffer.toString();
			default: 
				tokenBuffer.append(lookAheadChar);
				basicReadChar();
			}
		}
	}
	
	protected void basicReadChar() {
		try {
			final int read = reader.read();
			eof = read == -1;
			lookAheadChar = (char) read;
		} catch (IOException e) {
			errorHandler.handleError(Diagnostic.ERROR, "I/O error: " + e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	protected Class<? extends BoostObject> findClass(String boostName) {
		String className = boostName;
		if (getClassBoostNameMap().containsKey(boostName)) {
			className = getClassBoostNameMap().get(boostName);
		}
		try {
			return (Class<? extends BoostObject>) classLoader.loadClass(className);
		} catch (ClassNotFoundException e) {
			errorHandler.handleError(Diagnostic.ERROR, "Class not found error: " + e.getMessage());
		}
		return null;
	}

	protected Map<String, String> getClassBoostNameMap() {
		if ( boostNameToClassName != null ) return boostNameToClassName;
		return Collections.emptyMap();
	}

	protected <T extends BoostObject> T  resolveReference(String reference, Class<T> objectClass) {
		errorHandler.handleError(Diagnostic.ERROR, "This Boost implementation doesn't handle references (ref: " + reference + ").");
		return null;
	}
	
}

