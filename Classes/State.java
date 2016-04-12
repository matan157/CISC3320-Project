public class State {
	public static final int NEW = 0;
	public static final int READY = 1;
	public static final int RUNNING = 2;
	public static final int WAITING = 3;
	public static final int HALTED = 4;

	private int state;

	public State() {
		this.state = NEW;
	}

	public State(int state) {
		this.state = state;
	}
	
	public static int getState() {
		return this.state;
	}

	public static void setState(int state) {
		this.state = state;	
	}
		
}
