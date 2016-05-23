public class PCB {

	private int PID;
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

	// Accessors
	public int getPID() { return PID; }
	public int getJobSize() { return jobSize; }
	public int getAddress() { return address; }
	public int getTimesBlocked() { return timesBlocked; }
	public int getTimeArrived() { return timeArrived; }
	public int getMaxCpuTime() { return maxCpuTime; }
	public int getIoCount() { return ioPending; }
	public int getCpuTimeUsed() { return cpuTimeUsed; }
	public int getCpuTimeLeft() { return maxCpuTime - cpuTimeUsed; }
	public int getPriority() { return priority; }
	public boolean isBlocked() { return blocked; }
	public boolean isInCore() { return inCore; }
	public boolean isLatched() { return latched; }
	public boolean isTerminated() { return terminated; }

	// Mutators
	public void setAddress(int address) { this.address = address; }
	public void setLastTimeProcessing(int lastTimeProcessing) {
		this.lastTimeProcessing = lastTimeProcessing; 
	}
	public void calculateTimeProcessed(int currentTime) {
		cpuTimeUsed = cpuTimeUsed + currentTime - lastTimeProcessing; 
	}

	public void incTimesBlocked() { timesBlocked++; }
	public void incIoCount() { ioPending++; }
	public void decIoCount() { ioPending--; }
	public void blockJob() { blocked = true; }
	public void unblockJob() { blocked = false; }
	public void latchJob() { latched = true; }
	public void unlatchJob() { latched = false; }
	public void putInCore() { inCore = true; }
	public void removeInCore() { inCore = false; }
	public void terminateJob() { terminated = true; }

	public void printProcess() {
		System.out.println("PCB stuff");
	//	System.out.println(PID + " " + jobSize + " " + timeArrived + " " cpuTimeUsed + " " + maxCpuTime + " " + ioPending + " " + priority + " " + blocked + " " + latched + " " + inCore + " " + terminated + " " + address + " " + timesBlocked);
	}
}
