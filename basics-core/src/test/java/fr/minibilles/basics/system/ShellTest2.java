package fr.minibilles.basics.system;

import fr.minibilles.basics.Basics;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;

public class ShellTest2 {

	public static void main(String[] args) {
		final PrintWriter out = new PrintWriter(System.out);
		BasicShell shell = new BasicShell(new File("."), out);
		shell.cd("test");
		
		final File archive = new File(shell.pwd(), "testZip.zip");
		if ( archive.exists() ) archive.delete();

		out.println("-----------");
		List<File> allFiles = shell.ls(null, Basics.RECURSIVE);
		out.println("All files: ");
		for ( File oneFile : allFiles ) {
			out.println("- " + oneFile);
		}
		
		Zip.zip(shell, archive, allFiles);
	}
	
}
