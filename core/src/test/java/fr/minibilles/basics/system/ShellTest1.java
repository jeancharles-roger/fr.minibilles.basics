package fr.minibilles.basics.system;

import fr.minibilles.basics.Basics;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;

public class ShellTest1 {

	public static void main(String[] args) {
		final PrintWriter out = new PrintWriter(System.out);
		BasicShell shell = new BasicShell(new File("."), out);
		shell.cd("test");

		out.println("-----------");
		List<File> allFiles = shell.ls(null, Basics.RECURSIVE);
		out.println("All files: ");
		for ( File oneFile : allFiles ) {
			out.println("- " + oneFile);
		}

		out.println("-----------");
		List<File> allHiddenFiles = shell.ls(null, Basics.RECURSIVE | Basics.HIDDEN);
		out.println("All files (with hidden files): ");
		for ( File oneFile : allHiddenFiles ) {
			out.println("- " + oneFile);
		}
		
		out.println("-----------");
		shell.cd("c");
		List<File> cFiles = shell.ls("\\.c$", Basics.NONE);
		out.println("C files: ");
		for ( File oneFile : cFiles ) {
			out.println("- " + oneFile);
		}
		
		out.println("-----------");
		shell.cd("..");
		shell.cp("boost", "copy", "\\.person$", Basics.RECURSIVE | Basics.MKDIR);
		
		out.println("-----------");
		shell.rm("copy", "\\.person$", Basics.NONE);
		out.println("-----------");
		shell.rm("copy", null, Basics.NONE);
		out.close();
	}
	
}
