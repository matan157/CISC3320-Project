
import java.util.ArrayList;
import java.util.List;

public class os{
	
	// Naming scheme will be to start off all objects 
	// with an underscase letter followed by capitals
	// for every contiguous word in the object name
	// ex> objectNameIsLikeThis.  
	
	private static JobTable jobTable; 
	private static FreeSpaceTable freeSpaceTable;
	private static PCB currentRunningJob, lastRunningJobPCB;

	private static List<PCB> readyQueue = new ArrayList<PCB>();

	// Initialize variables to be used here! Create more objects as you go. 
	public static void Startup() {
		
		jobTable = new JobTable(50); // Limit = 50. 
		freeSpaceTable = new FreeSpaceTable(100); // 100k bytes. 
		currentRunningJob = null;
		lastRunningJobPCB = null; 
	}
	
	 
	 /* What all of points in the p[] array mean. Just for reference 
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

		//TODO: Memory-Manager(currentRunningJob);
		//TODO: CPU-Scheduler(a, p);



	}

	
	public static void Dskint (int[] a, int[] p) {}
	
	public static void Drmint (int[] a, int[] p) {}

	public static void Tro  (int[] a, int[] p) {}

	public static void Svc (int[] a, int[] p) {}

	// Currently considering making all of the data managing tasks
	// into a separate class of its own to handle the memory-manager, 
	// longTermScheduler, shortTermScheduler(dispatcher), Swapper. If we put in 
	// our own uniqueness, then going into to the interview will be much 
	// easier. 

	 

}

