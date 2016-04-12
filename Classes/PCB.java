public class PCB {
    
    // State constants
    private static final int NEW = 0;
    private static final int READY = 1;
    private static final int RUNNING = 2;
    private static final int WAITING = 3;
    private static final int HALTED = 4;
    
    private static int PID; // Process ID
    private State state; // 0 - 4 will represent states
    private int PC; // Program Counter
    
    // TODO: Add CPU scheduling
    // TODO: Add Memory Management
    
    public PCB(int PID, State state, int PC) {
        // Check if state is valid.
        if (state > 4 || state < 0) {
            throw new RuntimeException("State is invalid.");
        }
        this.PID = PID;
        this.state = state;
        this.PC = PC;
    }
    
    //////// Getters and Setters ////////////
    //////// Possibly setters for everything.
    
    // Returns PID as INT
    public int getPID() {
        return this.PID;
    }
    
    // Returns state as INT
    public State getState() {
        return this.state;
    }
    
    // Returns PC as INT
    public int getPC() {
        return this.PC;
    }
    
    // Set PC
    public void setPC(int address) {
        this.PC = address;
    }
}
