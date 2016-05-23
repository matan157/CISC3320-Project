import java.util.ArrayList;
import java.util.List;

public class os {

	private static JobTable jobTable;
	private static FreeSpaceTable freeSpaceTable;

	private static List<PCB> readyQueue = new ArrayList<PCB>();
	private static List<PCB> drumToMainQueue = new ArrayList<PCB>();
	private static List<PCB> mainToDrumQueue = new ArrayList<PCB>();
	private static List<PCB> ioQueue = new ArrayList<PCB>();

	private static PCB lastRunningJob;
	private static PCB lastJobToIo;
	private static PCB lastJobToDrum;
	private static PCB currentJob;

	private static int blockCount;
	private static boolean currentlyDoingIo;
	private static boolean doingSwap;
	
	private static final int MAX_NUM_JOBS = 50;
	private static final int MEMORY_SIZE = 100;
	private static final int ROUNDROBINSLICE = 9;
	private static final int BLOCKTHRESHOLD = 1;

	// startup is called at the beginning
	// of the simulation. Initializes all my
	// rad variables.
	public static void startup() {
		jobTable = new JobTable(MAX_NUM_JOBS);
		freeSpaceTable = new FreeSpaceTable(MEMORY_SIZE);

		lastRunningJob = null;
		lastJobToIo = null;
		lastJobToDrum = null;
		currentJob = null;

		currentlyDoingIo = false;
		doingSwap = false;

		blockCount = 0;

		sos.offtrace();
	}

	// Crint is called when a new job arrives on the drum.
	// When Crint is called, it's passed job information.
	// The new job will be 
	//	Given a PCB
	//	Placed on the job table
	//	Allocated some space
	//	Scheduled by the CPU
	//	Loved
	public static void Crint(int a[], int p[]) {
		if(lastRunningJob != null) {
			lastRunningJob.calculateTimeProcessed(p[5]);
			readyQueue.add(lastRunningJob);
		}

		currentJob = new PCB(p[1], p[2], p[3], p[4], p[5]);

		jobTable.addJob(currentJob);
		MemoryManager(currentJob);
		CpuScheduler(a, p);
	}

	// Dskint is called after a job finishes an IO operation(after siodisk is called)
	// The status of currentlyDoingIo is changed to false.
	// The job is removed from the ready queue, if it's not null.
	// If the job is in the job table:
	//	Its IO count is decremented;
	public static void Dskint(int a[], int p[]) {
		currentlyDoingIo = false;

		if(lastRunningJob != null) {
			lastRunningJob.calculateTimeProcessed(p[5]);
			readyQueue.add(lastRunningJob);
		}

		if(jobTable.contains(lastJobToIo.getPID())) {
			lastJobToIo.decIoCount();
			lastJobToIo.unlatchJob();

			if(lastJobToIo.getIoCount() == 0) {
				if(lastJobToIo.isTerminated()) {
					freeSpaceTable.addSpace(lastJobToIo);
					jobTable.removeJob(lastJobToIo);
				} else if(lastJobToIo.isBlocked()) {
					lastJobToIo.unblockJob();
					blockCount--;
					readyQueue.add(lastJobToIo);
				} else {
					readyQueue.add(lastJobToIo);
				}
				
			}
		}

		IOManager();
		CpuScheduler(a, p);
	}

	public static void Drmint(int a[], int p[]) {
		doingSwap = false;

		if(lastRunningJob != null) {
			lastRunningJob.calculateTimeProcessed(p[5]);
			readyQueue.add(lastRunningJob);
		}

		currentJob = lastJobToDrum;

		if(!currentJob.isInCore()) {
			currentJob.putInCore();
			readyQueue.add(currentJob);

			for(int i = 0; i < currentJob.getIoCount(); i++) {
				ioQueue.add(currentJob);
			}
		} else {
			currentJob.removeInCore();
			freeSpaceTable.addSpace(currentJob);
			drumToMainQueue.add(currentJob);
			mainToDrumQueue.remove(currentJob);
		}

		Swapper();
		CpuScheduler(a, p);
	}

	public static void Tro(int a[], int p[]) {
		lastRunningJob.calculateTimeProcessed(p[5]);

		if(lastRunningJob.getCpuTimeUsed() >= lastRunningJob.getMaxCpuTime()) {
			if(lastRunningJob.getIoCount() > 0) {
				lastRunningJob.terminateJob();
			} else {
				freeSpaceTable.addSpace(lastRunningJob);
				jobTable.removeJob(lastRunningJob);
			}

			Swapper();
			IOManager();
		} else {
			readyQueue.add(currentJob);
		}

		CpuScheduler(a, p);
	}

	public static void Svc(int a[], int p[]) {
		lastRunningJob.calculateTimeProcessed(p[5]);
		readyQueue.add(lastRunningJob);

		int aInt = a[0];

		if(aInt == 5) {
			while(readyQueue.contains(lastRunningJob))
				readyQueue.remove(lastRunningJob);

			while(mainToDrumQueue.contains(lastRunningJob))
				mainToDrumQueue.remove(lastRunningJob);

			if(lastRunningJob.getIoCount() > 0) {
				lastRunningJob.terminateJob();
			} else {
				lastRunningJob.removeInCore();
				freeSpaceTable.addSpace(lastRunningJob);
				jobTable.removeJob(lastRunningJob);
			}
		} else if(aInt == 6) {
			lastRunningJob.incIoCount();
			ioQueue.add(lastRunningJob);
			IOManager();
		} else if (aInt == 7) {
			if(lastRunningJob.getIoCount() != 0) {
				readyQueue.remove(lastRunningJob);
				lastRunningJob.blockJob();
				blockCount++;
				
				if(blockCount > BLOCKTHRESHOLD && !lastRunningJob.isLatched()) {
					mainToDrumQueue.add(lastRunningJob);
					Swapper();
				}
			}
		}

		CpuScheduler(a, p);
	}

	public static void IOManager() {
		if(!currentlyDoingIo && ioQueue.size() != 0) {
			for(int i = 0; i < ioQueue.size(); i++) {
				currentJob = ioQueue.get(i);
				ioQueue.remove(i);

				lastJobToIo = currentJob;
				currentlyDoingIo = true;
				currentJob.latchJob();

				sos.siodisk(currentJob.getPID());
				break;
			}
		}
	}

	public static void MemoryManager(PCB pcb) {
		drumToMainQueue.add(pcb);
		Swapper();
	}

	public static void Swapper() {
		int tempAddress;
		int lowest = 0;
		int lowestIndex = 0;

		// Swap in
		if(!doingSwap) {
			for(int i = 0; i < drumToMainQueue.size(); i++) {
				currentJob = drumToMainQueue.get(i);
				tempAddress = freeSpaceTable.findSpaceForJob(currentJob);
				if(currentJob.getAddress() >= 0) {
					doingSwap = true;
					sos.siodrum(currentJob.getPID(), currentJob.getJobSize(), currentJob.getAddress(), 0);
					lastJobToDrum = currentJob;
					drumToMainQueue.remove(i);
					break;
				}
			}
		}

		// Swap out
		if(!doingSwap) {
			for(int i = 0; i < mainToDrumQueue.size(); i++) {
				currentJob = mainToDrumQueue.get(i);

				if(currentJob != null && !mainToDrumQueue.get(i).isLatched() && currentJob.getCpuTimeLeft() > lowest) {
					lowestIndex = i;
					lowest = currentJob.getCpuTimeLeft();
				}
			}

			if(lowest > 0) {
				doingSwap = true;
				currentJob = mainToDrumQueue.get(lowestIndex);
				sos.siodrum(currentJob.getPID(), currentJob.getJobSize(), currentJob.getAddress(), 1);
				lastJobToDrum = currentJob;

				readyQueue.remove(lastJobToDrum);
				ioQueue.remove(lastJobToDrum);
				mainToDrumQueue.remove(lastJobToDrum);
			}
		}
	}

	public static void CpuScheduler(int a[], int p[]) {
		int lowest = 0;
		int lowestIndex = 0;

		for(int i = 0; i < readyQueue.size(); i++) {
			if(lowest == 0 && !readyQueue.get(i).isBlocked()) {
				currentJob = readyQueue.get(i);
				lowest = currentJob.getJobSize();
				lowestIndex = i;
			}

			currentJob = readyQueue.get(i);

			if(currentJob != null && !currentJob.isBlocked() && !currentJob.isTerminated() && currentJob.getJobSize() < lowest) {
				lowestIndex = i;
				lowest = currentJob.getJobSize();
			}
		}

		if(lowest > 0) {
			currentJob = readyQueue.get(lowestIndex);
			Dispatcher(a, p);
			while(readyQueue.contains(currentJob))
				readyQueue.remove(currentJob);
			return;
		}

		lastRunningJob = null;
		a[0] = 1;
	}

	public static void Dispatcher(int a[], int p[]) {
		lastRunningJob = currentJob;
		lastRunningJob.setLastTimeProcessing(p[5]);

		a[0] = 2;
		p[2] = lastRunningJob.getAddress();
		p[3] = lastRunningJob.getJobSize();

		if(lastRunningJob.getCpuTimeLeft() > ROUNDROBINSLICE) {
			p[4] = ROUNDROBINSLICE;
		} else {
			p[4] = lastRunningJob.getCpuTimeLeft();
		}
	}
}
