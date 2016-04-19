
import java.util.ArrayList;
import java.util.List;

public class os{
	
	// The Naming Scheme for Variables, Objects, and Methods:
	//
	// Objects & Varible Names> objectsLookLikeThis, start with an underscase 
	// letter followed by capitals for every contiguous word in the object name  
	//
	// Method Names> MethodsLookLikeThis, each contiguous word is 
	// capitalizied. 
	//
	// Final Variables> AREINALLCAPS.
	
	private static JobTable jobTable; 
	private static FreeSpaceTable freeSpaceTable;
	private static PCB currentRunningJob, lastRunningJobPCB, lastJobToDrum;
	private static final int ROUNDROBINSLICE = 9; 
	private static boolean swap; // For the swapper() function.
	private static List<PCB> readyQueue = new ArrayList<PCB>();
	private static List<PCB> drumToMemoryQueue = new ArrayList<PCB>();
	private static List<PCB> memoryToDrumQueue = new ArrayList<PCB>();
	private static List<PCB> ioQueue = new ArrayList<PCB>();

	// Initialize variables to be used here! Initialize more objects as you go! 
	public static void startup() {
		
		jobTable = new JobTable(50); // Limit = 50. 
		freeSpaceTable = new FreeSpaceTable(100); // 100k bytes. 
		currentRunningJob = null;
		lastRunningJobPCB = null;
		lastJobToDrum = null;
	       	swap = false; 	
	}
	
	 
	 /* What all of points in the p[] array mean? Just for reference 
	  * p[1](job number), p[2](priority), p[3](job size -k bytes)
          * p[4](max CPU time for job, p[5](current time)
	  */
	 
	
	// Invoked when a new job has entered the system and is on the drum. 
	public static void Crint (int[] a, int[] p) {

		if( lastRunningJobPCB != null ){
			lastRunningJobPCB.calculateTimeProcessed(p[5]); 
			readyQueue.add(lastRunningJobPCB); 
		}
		currentRunningJob = new PCB(p[1], p[2], p[3], p[4], p[5]); 
		jobTable.addJob(currentRunningJob); 

		MemoryManager(currentRunningJob);
		CpuScheduler(a, p);

	}

	//TODO: The rest of these methods. 	
	public static void Dskint (int[] a, int[] p) {}
	
	public static void Drmint (int[] a, int[] p) {}

	public static void Tro  (int[] a, int[] p) {}

	public static void Svc (int[] a, int[] p) {}
	
	//TODO: We can probably leave this to the fine-tuning portion of our code, but the way 
	// the memory manager was implemented is different from what was described 
	// in Jones' specifications. Just a heads up if there's time. 	
        public static void MemoryManager(PCB job){
	
		drumToMemoryQueue.add(job);
		Swapper();
	}	
	
	// Swapper will be dealing with both the long-term & short-term schedulers. 
	// To swap-in, the this function will check the free-space table to see if 
	// there exists available memory. siodrum will then be called so that it is 
	// removed from the drumToMemoryQueue. 
	// What does siodrum do? It will take the job#, jobSize, startCoreAddr, transferID;
	// This is how a job will be placed into memory, and the lastJobToDrum pcb object 
	// will contain its information so that it can still be processed in all of the 
	// queues which are being utilizied.   
	public static void Swapper(){

		int tempAddress; 
	  	int lowest = 0;
		int lowestIndex = 0; 
		
		// Swapping-In step
		if(!swap){
			for( int i = 0; i < drumToMemoryQueue.size(); i++ ){
				currentRunningJob = drumToMemoryQueue.get(i); // From MemoryManager()
				tempAddress = freeSpaceTable.findSpaceForJob(currentRunningJob);
				if(currentRunningJob.getAddress() >= 0){
					swap = true; 
					sos.siodrum(currentRunningJob.getPID(), currentRunningJob.getJobSize(), currentRunningJob.getAddress(), 0);
					lastJobToDrum = currentRunningJob;
					drumToMemoryQueue.remove(i);
					break;	
				}	
			}
		}

		// Swapping-Out step
		if(!swap){
			for( int i = 0; i < memoryToDrumQueue.size(); i++ ){
				currentRunningJob = memoryToDrumQueue.get(i);
				if(currentRunningJob != null && !memoryToDrumQueue.get(i).isLatched() && currentRunningJob.getCpuTimeLeft() > lowest ){
					lowestIndex = i;
					lowest = currentRunningJob.getCpuTimeLeft();
				}
			}
			if( lowest > 0 ){
				swap = true;
				currentRunningJob = memoryToDrumQueue.get(lowestIndex);
				sos.siodrum(currentRunningJob.getPID(), currentRunningJob.getJobSize(), currentRunningJob.getAddress(), 1);
				lastJobToDrum = currentRunningJob;

				readyQueue.remove(lastJobToDrum);
				// First occurance of ioQueue!
				ioQueue.remove(lastJobToDrum);
				memoryToDrumQueue.remove(lastJobToDrum);
			}
		}
	}
	//TODO: More informed explanation. 	
	public static void CpuScheduler(int a[], int p[]){

		int lowest = 0;
	        int lowestIndex = 0; 

		for(int i = 0; i < readyQueue.size(); i++){

			if( lowest == 0 && !readyQueue.get(i).isBlocked() ){
				currentRunningJob = readyQueue.get(i); 
				lowest = currentRunningJob.getJobSize();
				lowestIndex = i;
			}
			currentRunningJob = readyQueue.get(i);
			if( currentRunningJob != null && !currentRunningJob.isBlocked() && !currentRunningJob.isTerminated() && currentRunningJob.getJobSize() < lowest ){
				lowestIndex = i; 
				lowest = currentRunningJob.getJobSize();
			}
		}

		if( lowest > 0 ){
			currentRunningJob = readyQueue.get(lowestIndex);
			Dispatcher(a, p);
			while(readyQueue.contains(currentRunningJob) ){
				readyQueue.remove(currentRunningJob); //Hmm
			}
			return;
		}
		// No jobs to run, therefore the CPU is idle. 
		lastRunningJobPCB = null;
		a[0] = 1;
	}
	//TODO: More informed explanation.
	// Called by the CpuScheduler(). 
	public static void Dispatcher(int a[], int p[]){
		lastRunningJobPCB = currentRunningJob; 
		lastRunningJobPCB.setLastTimeProcessing(p[5]);
		
		// First use of Round Robin, with time slice set to 9. 
		if(lastRunningJobPCB.getCpuTimeLeft() > ROUNDROBINSLICE){
			a[0] = 2; 
			p[2] = lastRunningJobPCB.getAddress();
			p[3] = lastRunningJobPCB.getJobSize();
			p[4] = ROUNDROBINSLICE;
		}
		else {
			a[0] = 2; 
			p[2] = lastRunningJobPCB.getAddress();
			p[3] = lastRunningJobPCB.getJobSize(); 
			p[4] = lastRunningJobPCB.getCpuTimeLeft();
		}
	}		


}

