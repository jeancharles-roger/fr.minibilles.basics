package fr.minibilles.basics.tools.ant;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Test;

public class ModelGeneratorTests {

	private final Path destination = Paths.get("src-generated");

	@Before
	public void setUp() throws IOException {
		if (!Files.exists(destination)) {
			Files.createDirectory(destination);
		}
	}

	@Test
	public void ecoreTest() {
		ModelGeneratorTask task = new ModelGeneratorTask();
		task.setSource("models/ecore/Ecore.ecore");
		task.setDestination(destination.toString());
		task.setVisitor("simple");
		task.setWalkerAndCloner(true);
		task.setReplacer(true);
		task.setModel(false);
		task.setBoost(false);
		//task.setXml("simple");
		task.setSexp("simple");
		task.execute();
	}

	@Test
	public void fiacreTest() {
		ModelGeneratorTask task = new ModelGeneratorTask();
		task.setSource("models/fiacre/fiacre.ecore");
		task.setDestination(destination.toString());
		task.setBasepackage("obp");
		task.setBoost(true);
		
		task.execute();
	}
	
	@Test
	public void obpTest() {
		ModelGeneratorTask task = new ModelGeneratorTask();
		task.setSource("models/obp/obp.ecore");
		task.setDestination(destination.toString());
		task.setBasepackage("obp");
		task.setBoost(true);
        //task.setVisitor("recursive");
        //task.setSexp("recursive");

		task.execute();
	}

    @Test
    public void diagramTest() {
        ModelGeneratorTask task = new ModelGeneratorTask();
        task.setSource("models/diagram/Diagram.ecore");
		task.setDestination(destination.toString());
		task.setVisitor("none");
        task.execute();
    }


}
