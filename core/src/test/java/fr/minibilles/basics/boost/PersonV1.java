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
 * A simple object serialized in a {@link Boost} stream.
 * @author Jean-Charles Roger
 */
public class PersonV1 implements BoostObject {

	private final String name;
	private String phoneNumber;
	private List<PersonV1> children = new ArrayList<PersonV1>();
	
	protected PersonV1(Boost boost) {
		boost.register(this);
		name = boost.readString();
		phoneNumber = boost.readString();
		children.addAll(BoostUtil.readObjectList(boost, PersonV1.class));
	}
	
	public PersonV1(String name) {
		this.name = name;
	}
	
	public void writeToBoost(Boost boost) {
		boost.writeString(name);
		boost.writeString(phoneNumber);
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
		final String brianFilename = "test/boost/brianV1.person";

		JBoost boost = new JBoost("Person", 1);
		boost.initializeWriting(new FileOutputStream(brianFilename));
		boost.writeObject(createBrian());
		boost.close();
		
		boost.initializeReading(new FileInputStream(brianFilename));
		PersonV1 brian = boost.readObject(PersonV1.class);
		boost.close();
		
		System.out.println("Read: " + brian);
	}
	
	
	public static PersonV1 createBrian() {
		PersonV1 brian = new PersonV1("Brian");
		brian.phoneNumber = "0000-00-000";
		
		PersonV1 terry = new PersonV1("Terry");
		brian.children.add(terry);
		
		PersonV1 wanda = new PersonV1("Wanda");
		brian.children.add(wanda);
		
		return brian;
	}
	

}
