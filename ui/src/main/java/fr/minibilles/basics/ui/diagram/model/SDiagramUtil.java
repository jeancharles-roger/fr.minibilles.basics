package fr.minibilles.basics.ui.diagram.model;

import fr.minibilles.basics.serializer.Boost;
import fr.minibilles.basics.serializer.JBoost;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SDiagramUtil {

	private static JBoost createBoost() {
		return new JBoost("SDiagram", 1);
	}
	
	/** Read a {@link SDiagram} from a {@link File} */
	public static SDiagram loadDiagram(File file) throws IOException {
		JBoost boost = createBoost();
		boost.initializeReading(new FileInputStream(file));
		SDiagram diagram = boost.readObject(SDiagram.class);
		boost.close();
		return diagram;
	}
	

	/** Write a {@link SDiagram} to a {@link File} */
	public static void saveDiagram(SDiagram diagram, File file) throws IOException {
		JBoost boost = createBoost();
		boost.initializeWriting(new FileOutputStream(file));
		boost.writeObject(diagram);
		boost.close();
	}

	public static List<String> readStringList(Boost boost) {
		int size = boost.readInt();
		List<String> result = new ArrayList<String>(size);
		for ( int i=0; i<size; i++ ) {
			result.add(boost.readString());
		}
		return result;
	}
	
	public static void writeStringList(Boost boost, List<String> list) {
		if ( list == null ) {
			boost.writeInt(0);
		} else {
			boost.writeInt(list.size());
			for (String string : list ) {
				boost.writeString(string);
			}
		}
		
	}
}
