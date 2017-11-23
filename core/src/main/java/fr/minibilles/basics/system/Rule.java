package fr.minibilles.basics.system;

import fr.minibilles.basics.progress.ActionMonitor;
import java.io.PrintWriter;



/**
 * A {@link Rule} is able to construct a target using it's
 * dependencies. {@link Make}, before calling to execute the rule
 * for an identified target, it calls all dependencies.
 * @author Jean-Charles Roger
 */
public interface Rule {
	
	public static class Stub implements Rule {

		private final String target;
		private final String [] dependencies;

		public Stub(String target, String ... dependencies) {
			this.target = target;
			this.dependencies = dependencies == null ? new String[0] : dependencies;
		}
		
		public String getTarget(Make make) {
			return target;
		}
		
		public String[] getDependencies(Make make) {
			return dependencies;
		}
		
		public int execute(String effectiveTarget, String[] effectiveDependencies, Make make, PrintWriter out, PrintWriter err,  ActionMonitor progress) {
			return 0;
		}
	}
	/**
	 * @return the target regex for which this rule is able to construct.
	 */
	String getTarget(Make make);
	
	/**
	 * @return the list of regex dependencies of this target. 
	 */
	String [] getDependencies(Make make);

	
	int execute(String effectiveTarget, String [] effectiveDependencies, Make make, PrintWriter out, PrintWriter err, ActionMonitor prograess);
}
