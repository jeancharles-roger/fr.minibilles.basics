package fr.minibilles.basics.compile;

import fr.minibilles.basics.progress.ActionMonitor;
import fr.minibilles.basics.system.BatchCommand;
import fr.minibilles.basics.system.Make;
import fr.minibilles.basics.system.Rule;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class GenerateCompileExecuteCCode {

	public static void test(String filename, String message) throws IOException {
		final String java_io_tmpdir = "java.io.tmpdir";
		File tmpFolder = new File(System.getProperty(java_io_tmpdir));
		
		final String sourceName = filename + ".c";
		final String executableName = filename;
		
		generate(tmpFolder, sourceName, message);
		compile(tmpFolder, sourceName, executableName);
		execute(tmpFolder, executableName);
	}

	private static void generate(File tmpFolder, final String sourceName, String message) throws FileNotFoundException {
		PrintWriter sourceWriter = new PrintWriter(new File(tmpFolder,sourceName));
		sourceWriter.println("#include \"stdio.h\"");
		sourceWriter.println();
		sourceWriter.println("int main(char ** argv, int argc) {");
		sourceWriter.println("\tprintf(\"" + message + "\\n\");");
		sourceWriter.println("\treturn 0;");
		sourceWriter.println("}");
		sourceWriter.println();
		sourceWriter.close();
	}

	private static void compile(File tmpFolder, final String sourceName, final String executableName) {
		Make make = new Make();
		make.cd(tmpFolder.getAbsolutePath());
		final PrintWriter outWriter = new PrintWriter(System.out);
		final PrintWriter errWriter = new PrintWriter(System.err);
	
		make.addRule(new Rule.Stub(executableName, sourceName) {
			@Override
			public int execute(String effectiveTarget, String[] effectiveDependencies, Make make, PrintWriter out, PrintWriter err, ActionMonitor progress) {
				String command = "gcc -v -o " + executableName + " " + sourceName;
				System.out.println("Command: " + command);
				BatchCommand.execute(command, make.getWorkingDir(), outWriter, errWriter);
				return 0;
			}
		});
		make.build(executableName, outWriter, errWriter, null);
	}

	private static void execute(File workingDir, final String executableName) {
		System.out.println("[Starting '" + executableName + "']");
		BatchCommand.execute("./" + executableName, workingDir, new PrintWriter(System.out), new PrintWriter(System.err));
		System.out.println("[Ended '" + executableName + "']");
	}

	public static void main(String[] args) throws IOException {
		test("test", "Bonne chance jeune Padawane.");
	}

}
