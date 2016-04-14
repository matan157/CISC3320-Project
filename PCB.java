/*/
	PCB Class is a Process Control Block for jobs
	It contains all the information possibly needed.
	Maybe even more.
/*/
public class PCB {
    
    // State constants
    private static int PID; // Process ID
	private int jobSize;
	private int timeArrived;
	private int lastTimeProcessing;
	private int cpuTimeUsed;
	private int maxCpuTime;
	private int ioPending;
	private int priority;
	private int address;
	private int timesBlocked;
	private boolean blocked;
	private boolean latched;
	private boolean inCore;
	private boolean terminated;
	 
	// Constructor   
	// When the job is created it is not latched, incore, or blocked.
	// It has no address until it's put into memory
	public PCB(int PID, int priority, int jobSize, int maxCpuTime, int timeArrived) {
		this.PID = PID;
		this.priority = priority;
		this.jobSize = jobSize;
		this.maxCpuTime = maxCpuTime;
		this.timeArrived = timeArrived;
		cpuTimeUsed = 0;
		ioPending = 0;
		blocked = false; 
		latched = false;
		inCore = false;
		terminated = false;
		timesBlocked = 0;
		address = -1;
	}
	
	// GETTERS 
	public int getPID() { return PID; } 
	public int getTimesBlocked() { return timesBlocked; }
	public int getTimeArrived() { return timeArrived; }
	public int getJobSize() { return jobSize; }
	public int getAddress() { return address; }
	public int getMaxCpuTime() { return maxCpuTime; }
	public int getIoCount() { return ioPending; }
	public int getCpuTimeUsed() { return cpuTimeUsed; }
	public int getCpuTimeLeft() { return maxCpuTime - cpuTimeUsed; }
	public int getPriority() { return priority; }
	public boolean isBlocked() { return blocked; }
	public boolean isInCore() { return inCore; }
	public boolean isTerminated() { return terminated; }
	public boolean isLatched() { return latched; }
    
	// SETTERS
	public void setAddress(int address) {
		this.address = address; 
	}
	
	public void setLastTimeProcessing(int lastTimeProcessing) {
		this.lastTimeProcessing = lastTimeProcessing;
	}
	// How much time it has been processed
	public void calculateTimeProcessed(int currentTime) {
		cpuTimeUsed += currentTime - lastTimeProcessing;
	}
	// Increment and Decrement
	public void incTimeBlocked() { timesBlocked++; }
	public void incIoCount() { ioPending++; }
	public void decIoCount() { ioPending--; }
	public void blockJob() {
		blocked = true;
	}
	public void unblockJob() {
		blocked = false;
	}
	public void latchJob() {
		latched = true;
	}
	public void unlatchJob() {
		latched = false;
	}
	public void removeInCore() {
		inCore = false;
	}
	public void terminateJob() {
		terminated = true;
	}

	// TODO add print method
}
