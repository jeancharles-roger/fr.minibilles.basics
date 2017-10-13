package fr.minibilles.basics.boost;

import fr.minibilles.basics.serializer.Boost;
import fr.minibilles.basics.serializer.BoostObject;
import fr.minibilles.basics.serializer.BoostUtil;
import fr.minibilles.basics.serializer.JBoost;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>This is an example for file version handling with Boost.</p>
 * <p>This class is a new version of PersonV1 (and it changed it's name
 * to PersonV2. It shows how to handle old file with new version.</p>
 * @author Jean-Charles Roger
 */
public class PersonV2 implements BoostObject {

	private final String name;
	private String phoneNumber;
	private String favoriteColor;
	private List<PersonV2> children = new ArrayList<PersonV2>();
	
	public PersonV2(String name) {
		this.name = name;
	}
	
	protected PersonV2(Boost boost) {
		boost.register(this);
		name = boost.readString();
		phoneNumber = boost.readString();
		if ( boost.getFileVersion() >= 2 ) {
			favoriteColor = boost.readString();
		}
		children.addAll(BoostUtil.readObjectList(boost, PersonV2.class));
	}

	public void writeToBoost(Boost boost) {
		boost.writeString(name);
		boost.writeString(phoneNumber);
		if ( boost.getFileVersion() >= 2 ) {
			boost.writeString(favoriteColor);
		}
		BoostUtil.writeObjectCollection(boost, children);
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append(name);
		if ( phoneNumber != null ) { 	
			string.append("(");
			string.append(phoneNumber);
			string.append(")");
		}
		string.append(" children ");
		string.append(children);
		return string.toString();
	}

	public static void main(String[] args) throws FileNotFoundException {

		// read a file save with PersonV1.
		JBoost boostV1 = new JBoost("Person", 1);
		// register PersonV1 class to be a PersonV2 class
		boostV1.addBoostNameToClassName(PersonV1.class.getCanonicalName(), PersonV2.class.getCanonicalName());
		boostV1.initializeReading(new FileInputStream("test/boost/brianV1.person"));
		PersonV2 brian = boostV1.readObject(PersonV2.class);
		boostV1.close();
		
		System.out.println("Read: " + brian);
		
		// write a Person file with version 2, you can compare the results.
		JBoost boostV2 = new JBoost("Person", 2);
		boostV2.initializeWriting(new FileOutputStream("test/boost/brianV2.person"));
		boostV2.writeObject(createBrian());
		boostV2.close();
		System.out.println("Wrote file: test/boost/brianV2.person");
		

		
	}
	
	
	public static PersonV2 createBrian() {
		PersonV2 brian = new PersonV2("Brian");
		brian.phoneNumber = "0000-00-000";
		brian.favoriteColor= "blue";
		
		PersonV2 terry = new PersonV2("Terry");
		brian.children.add(terry);
		
		PersonV2 wanda = new PersonV2("Wanda");
		brian.children.add(wanda);
		
		return brian;
	}
	

}
