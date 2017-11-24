package fr.minibilles.basics.action;

import fr.minibilles.basics.error.Diagnostic;
import fr.minibilles.basics.progress.ActionMonitor;

/**
 * A user action.
 * @author Jean-Charles Roger
 *
 */
public interface ActionRunnable {
	
	/** The action is enable */
	int VISIBILITY_ENABLE = 1;
	
	/** The action is disable */
	int VISIBILITY_DISABLE = 2;
	
	/** The action is hidden */
	int VISIBILITY_HIDDEN = 3;
	
	/** The action is running */
	int VISIBILITY_RUNNING = 4;
	
	/** The action's code ends normally */
	int STATUS_OK = 1;
	
	/** The action's code was canceled */
	int STATUS_CANCEL = 2;
	
	/** The action's code gets in error */
	int STATUS_ERROR = 3;

	/** Action's visibility */
	int getVisibility();

	/** 
	 * Code to execute for action 
	 * @return the final status. 
	 * It can be {@link #STATUS_OK}, {@link #STATUS_CANCEL}, {@link #STATUS_ERROR}.
	 */
	int run(ActionMonitor monitor);
	
	/** Action execution {@link Diagnostic} if something wrong happened. */
	Diagnostic getDiagnostic();
	
	boolean getState();
	
	class Stub implements ActionRunnable {

		protected Diagnostic diagnostic;
		
		public int getVisibility() {
			return VISIBILITY_ENABLE;
		}

		public int run(ActionMonitor monitor) {
			return STATUS_OK;
		}

		public Diagnostic getDiagnostic() {
			return diagnostic;
		}
		
		/** Sets the diagnostic if something went wrong */
		protected void setDiagnostic(Diagnostic diagnostic) {
			this.diagnostic = diagnostic;
		}
		
		public boolean getState() {
			return false;
		}
		
	}
}
