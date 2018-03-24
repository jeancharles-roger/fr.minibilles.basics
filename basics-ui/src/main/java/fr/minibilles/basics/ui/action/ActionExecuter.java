/**
 * 
 */
package fr.minibilles.basics.ui.action;

import fr.minibilles.basics.action.ActionRunnable;
import fr.minibilles.basics.progress.ActionMonitor;



/**
 * An {@link ActionExecuter} executes {@link Action}.
 * @author Jean-Charles Roger
 *
 */
public interface ActionExecuter {
	
	/** Default executer */
	public static final Stub DEFAULT_EXECUTER = new ActionExecuter.Stub();

	/** Basic stub for {@link ActionExecuter}. */
	public static class Stub implements ActionExecuter {
		
		public void executeAction(Action action) {
			switch ( action.run(ActionMonitor.empty) ) {
			case ActionRunnable.STATUS_ERROR:
				error(action);
				break;
			case ActionRunnable.STATUS_CANCEL:
				canceled(action);
				break;
			case ActionRunnable.STATUS_OK:
				// do nothing
				break;
			}		
		}

		public void error(Action action) { }
		public void canceled(Action action) { }
	}

	/** Executes given {@link Action} */
	public void executeAction(Action action);
}
