package fr.minibilles.basics.progress;


/**
 * Defines a set of call-backs to monitors an action progress.
 * @author Jean-Charles Roger
 */
public interface ActionMonitor {

	/** If used as remaining time, it expresses that there is no indication
	 * about remaining time.
	 */
	public static final int UNKNOWN = -1;
	
	/** Set the current task name. */
	void setTaskName(String name);
	
	/** The action execution begins */
	void begin(int remaining);
	
	/** The action had executed some work and need to continue. */
	void worked(int workIncrement, int remaining);
	
	/** The action execution is finished. */
	void done();
	
	/** The action has been cancelled. */
	void canceled();
	
	/** Empty progress monitor, it does nothing. */
	public final static ActionMonitor empty = new Stub();
	
	/** Stub for {@link ActionMonitor}. */
	public static class Stub implements ActionMonitor {
	
		public void setTaskName(String name) { }
		public void begin(int remaining) { }
		public void worked(int workIncrement, int remaining) { }
		public void done() { }
		public void canceled() { }
		
	}

	/** {@link ActionMonitor} that act as a part of another. */
	public static class Sub implements ActionMonitor {

		private final ActionMonitor parent;
		private final int start;
		private final int length;
		
		/**
		 * Sub {@link ActionMonitor} creation.
		 * @param parent parent's {@link ActionMonitor}
		 * @param start from where it's starts.
		 * @param length the length of this sub action from the parent's one.
		 */
		public Sub(ActionMonitor parent, int start, int length) {
			this.parent = parent;
			this.start = start;
			this.length = length;
		}
		
		public void setTaskName(String name) {
			parent.setTaskName(name);
		}

		public void begin(int remaining) {
			parent.worked(start, length);
		}

		public void worked(int workIncrement, int remaining) {
			int parentWorked = (int) (workIncrement / (workIncrement+remaining)) * length;
			parent.worked(parentWorked, length - parentWorked);
		}
		
		public void done() {
			parent.worked(start + length, 0);
		}
		
		public void canceled() {
			parent.canceled();
		}

	}
}
