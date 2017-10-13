package fr.minibilles.basics.compile;

import fr.minibilles.basics.progress.ActionMonitor;
import fr.minibilles.basics.system.BatchCommand;
import fr.minibilles.basics.system.Make;
import fr.minibilles.basics.system.Rule;
import java.io.PrintWriter;

public class CompileMultiCCodeFiles {

	public static void testCMultiFile() {
		
		Make make = new Make();
		make.cd("test/c");
		
		make.addRule(new Rule.Stub("(.*)\\.o", "\\1.c") {
			@Override
			public int execute(String effectiveTarget, String[] effectiveDependencies, Make make, PrintWriter out, PrintWriter err,  ActionMonitor progress) {
				StringBuilder commandBuilder = new StringBuilder();
				commandBuilder.append("gcc -v -c -o ");
				commandBuilder.append(effectiveTarget);
				for ( String oneDep : effectiveDependencies ) {
					commandBuilder.append(" ");
					commandBuilder.append(oneDep);
				}
				System.out.println("Command: " + commandBuilder.toString());
				BatchCommand.execute(commandBuilder.toString(), make.getWorkingDir(), out, err, progress);
				return 0;
			}
		});
		
		make.addRule(new Rule.Stub("helloworld", "hello.o", "message.o", "toto.o") {
			@Override
			public int execute(String effectiveTarget, String[] effectiveDependencies, Make make, PrintWriter out, PrintWriter err, ActionMonitor progress) {
				String command = "gcc -o helloword hello.o message.o toto.o";
				System.out.println("Command: " + command);
				BatchCommand.execute(command, make.getWorkingDir(), new PrintWriter(System.out), new PrintWriter(System.err));
				return 0;
			}
		});
		
		make.build("helloworld", new PrintWriter(System.out), new PrintWriter(System.err), null);
		
		BatchCommand.execute("./helloword", make.getWorkingDir(), new PrintWriter(System.out), new PrintWriter(System.err));
	}
	
}
