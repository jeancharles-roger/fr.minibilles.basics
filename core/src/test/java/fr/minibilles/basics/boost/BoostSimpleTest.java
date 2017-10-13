package fr.minibilles.basics.boost;

import fr.minibilles.basics.serializer.BoostUtil;
import fr.minibilles.basics.serializer.JBoost;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/** Simple write of primitive objects in a file and read it. */
public class BoostSimpleTest {

	public static void main(String[] args) throws IOException {
		File file = File.createTempFile("simple", "txt");
		//File file = new File("test/boost/simple.txt");
		//InputStream stream = BoostSimpleTest.class.getResourceAsStream("boost/simple.txt");
		
		// create boost
		JBoost boost = new JBoost("SimpleTest", 1);

		// writes data to file.
		boost.initializeWriting(new FileOutputStream(file));
		boost.writeBoolean(true);
		boost.writeDouble(Math.PI);
		boost.writeString("Here is a ù string with \" strange things \' inside.");
		BoostUtil.writeIntArray(boost, new int[] { 0, 1, 2, 3, 4 });
		boost.close();
		
		// reads data from file
		boost.initializeReading(new FileInputStream(file));
		System.out.println("Reading :");
		System.out.println("- " + boost.readBoolean());
		System.out.println("- " + boost.readDouble());
		System.out.println("- " + boost.readString());
		System.out.println("- " + Arrays.toString(BoostUtil.readIntArray(boost)));
		boost.close();
	}

}
