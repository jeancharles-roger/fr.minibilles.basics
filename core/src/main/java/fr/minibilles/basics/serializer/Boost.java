package fr.minibilles.basics.serializer;

import fr.minibilles.basics.error.ErrorHandler;

/**
 * <p>
 * A class that implements {@link Boost} is able to serialize and de-serialize {@link BoostObject} to
 * text files in UTF-8 encoding.
 * </p>
 * 
 * <b>Writing example using {@link JBoost} implementation</b>
 * 
 * <pre>
 * Boost boost = new JBoost(&quot;Cob&quot;, 1);
 * FileOutputStream fileStream = new FileOutputStream(file);
 * boost.initializeWritin(fileStream);
 * boost.writeObject(this);
 * boost.close();
 * </pre>
 * 
 * <b>Reading example using {@link JBoost} implementation</b>
 * 
 * <pre>
 * Boost boost = new JBoost(&quot;Cob&quot;, 1);
 * FileInputStream fileStream = new FileInputStream(file);
 * boost.initializeReading(fileStream);
 * Model result = (Model) boost.readObject();
 * boost.close();
 * </pre>
 * 
 * @author Jean-Charles Roger
 * 
 */
public interface Boost {

	/** @return the error handler. */ 
	public ErrorHandler getErrorHandler();

	/** Sets the error handler. */ 
	public void setErrorHandler(ErrorHandler errorHandler);

	/**
	 * <p>
	 * The current file version that is currently loading..
	 * </p>
	 * 
	 * @return the current file version.
	 */
	public int getFileVersion();

	/**
	 * <p> Registers a read object from the constructor.</p>
	 * @param object the referred object.
	 */
	public void register(BoostObject object);
	
	public void writeBoolean(boolean b);
	public boolean readBoolean();

	public void writeDouble(double d);
	public double readDouble();

	public void writeFloat(float f);
	public float readFloat();

	public void writeByte(byte b);
	public byte readByte();
	
	public void writeShort(short s);
	public short readShort();
	
	public void writeInt(int i);
	public int readInt();
	
	public void writeLong(long l);
	public long readLong();

	public void writeString(String stringValue);
	public String readString();

	public <T extends Enum<T>> void writeEnum(T enumValue);
	public <T extends Enum<T>> T readEnum(Class<T> enumClass);

	public void writeObject(BoostObject obj);

	public <T extends BoostObject> T readObject(Class<T> objectClass);
}
