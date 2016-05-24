/*
	CISC 3320 Project - Spring 2016
	Operating Systems with Professor Jones
	
	Created by Matan Uchen, Nicholas Galarza, Shahrukh Jalil.
	
	This system simulates the functions of a basic Operating System using
	five main interrupts. 
*/
import java.util.ArrayList;
import java.util.List;

public class os {
	// Data Structures created by our team
	private static JobTable jobTable;
	private static FST_2 freeSpaceTable;
	private static CPU cpu;
	// Keeping a couple of queues
	public static List<PCB> readyQueue = new ArrayList<PCB>();
	public static List<PCB> drumToMainQueue = new ArrayList<PCB>();
	public static List<PCB> mainToDrumQueue = new ArrayList<PCB>();
	public static List<PCB> ioQueue = new ArrayList<PCB>();
	// PCB's for keeping track
	public static PCB lastRunningJob;
	public static PCB lastJobToIo;
	public static PCB lastJobToDrum;
	public static PCB currentJob;
	// Status variables
	private static int blockCount;
	private static boolean currentlyDoingIo;
	private static boolean doingSwap;
	// Constants
	public static final int MAX_NUM_JOBS = 50;
	public static final int MEMORY_SIZE = 100;
	public static final int ROUNDROBINSLICE = 9;
	public static final int BLOCKTHRESHOLD = 1;

	// startup is called at the beginning
	// of the simulation. Initializes all of my
	// rad variables.
	public static void startup() {
		jobTable = new JobTable(MAX_NUM_JOBS);
		freeSpaceTable = new FST_2();
		cpu = new CPU();

		lastRunningJob = null;
		lastJobToIo = null;
		lastJobToDrum = null;
		currentJob = null;

		currentlyDoingIo = false;
		doingSwap = false;

		blockCount = 0;

		sos.offtrace();
	}

	/* Crint is called when a new job arrives on the drum.
	 *	When Crint is called, it's passed job information.
	 *	The new job will be :
	 *		Given a PCB
	 *		Placed on the job table
	 *		Allocated some space
	 *		Scheduled by the CPU
	 *		Loved
	*/
	public static void Crint(int a[], int p[]) {
		// If there was a job before it
		if(lastRunningJob != null) {
			// See how much it was running for
			lastRunningJob.calculateTimeProcessed(p[5]);
			// Put it on the ready queue
			readyQueue.add(lastRunningJob);
		}
		
		currentJob = new PCB(p[1], p[2], p[3], p[4], p[5]);// Job that came in is created
		jobTable.addJob(currentJob); // Add the new job to the job table
		MemoryManager(currentJob); // Manage the new job
		// freeSpaceTable.printFST();
		cpu.Schedule(a, p); // Schedule it
	}

	/* 	Dskint is called after a job finishes an IO operation(after siodisk is called)
	 * 	The status of currentlyDoingIo is changed to false.
	 * 	The job is removed from the ready queue, if it's not null.
	 * 	If the job is in the job table:
	 *		Its IO count is decremented
	*/
	public static void Dskint(int a[], int p[]) {
		currentlyDoingIo = false; // Just finished IO

		if(lastRunningJob != null) {
			// Figure out how much it's been running
			lastRunningJob.calculateTimeProcessed(p[5]);
			// Put the last job on the readyQueue
			readyQueue.add(lastRunningJob);
		}

		// If the last job that did IO is on the job table
		if(jobTable.contains(lastJobToIo.getPID())) {
			// Decrement the IoPending count, it just did IO
			lastJobToIo.decIoCount();
			// Unlatch
			lastJobToIo.unlatchJob();

			// If the job doesn't need to do IO anymore
			if(lastJobToIo.getIoCount() == 0) {
				// And it is going to terminate
				if(lastJobToIo.isTerminated()) {
					// Remove from the job table and free up space
					freeSpaceTable.addSpace(lastJobToIo);
					jobTable.removeJob(lastJobToIo);
				// If it's blocked
				} else if(lastJobToIo.isBlocked()) {
					// Unblock it and add it to the ready queue.
					lastJobToIo.unblockJob();
					blockCount--;
					readyQueue.add(lastJobToIo);
				// It really wants to be on the ready queue
				} else {
					readyQueue.add(lastJobToIo);
				}
				
			}
		}
		//freeSpaceTable.printFST();
		IOManager(); // Manage your IO
		cpu.Schedule(a, p); // Schedule
	}
	/* Drmint() -- called after sos.siodrum() is called. 
	 *
	 * If the last job on the drum isn't in core it will be placed in core. Then 
	 * it will run all of its IO jobs. 
	 *
	 * If not, this means the job is already in core so it will be removed from memory, 
	 * placed into the drum from the main memory queue. And it is removed from the 
	 * main memory to drum queue. 
	 *
	 * Subsequent calls to Swapper() and cpu.Schedule() are then made. 
	 */
	public static void Drmint(int a[], int p[]) {
		doingSwap = false;

		// If there was a job running
		if(lastRunningJob != null) {
			// Calculate how much it was running
			lastRunningJob.calculateTimeProcessed(p[5]);
			// Add it to the ready queue
			readyQueue.add(lastRunningJob);
		}

		// Working with the last job to drum
		currentJob = lastJobToDrum;
		
		// If the job isn't in core
		if(!currentJob.isInCore()) {
			// Put it in the core
			currentJob.putInCore();
			// Add it to the ready queue
			readyQueue.add(currentJob);

			for(int i = 0; i < currentJob.getIoCount(); i++) {
				// Put it on the ioQueue for how many times it needs IO
				ioQueue.add(currentJob);
			}
		// If it's already in core
		} else {
			// Remove it from the core
			currentJob.removeInCore();
			// Remove it from memory
			freeSpaceTable.addSpace(currentJob);
			// add to the drumToMain queue
			drumToMainQueue.add(currentJob);
			// remove it from drum queue
			mainToDrumQueue.remove(currentJob);
		}

		Swapper(); // Make all swaps
		
		// freeSpaceTable.printFST();
		cpu.Schedule(a, p);// Schedule it
	}
	/* Tro() -- Invoked when "running job has run out of time."
	 * If the job has used up its maximum time allocation or its time slice.  
	 *
	 * 	- Checks the io count, and if the counter is > 0 the job is terminated. 
	 * 	- If not, it will be removed from memory and the job table. 
	 * 	Subsequent calls to Swapper() & IOManager() are called to handle job. 
	 *
	 * If the first condition doesn't fall through, then the job is added to the ready 
	 * queue and the job is scheduled. 
	 */
	public static void Tro(int a[], int p[]) {
		// See how much it ran for
		lastRunningJob.calculateTimeProcessed(p[5]);

		// If the amount of CPU time it used is over the allowed time
		if(lastRunningJob.getCpuTimeUsed() >= lastRunningJob.getMaxCpuTime()) {
			// If it has IO pending
			if(lastRunningJob.getIoCount() > 0) {
				// Terminate it
				lastRunningJob.terminateJob();
			// If it has no IO
			} else {
				// Free up some space
				freeSpaceTable.addSpace(lastRunningJob);
				// Remove from the job table
				jobTable.removeJob(lastRunningJob);
			}
			// Swap and manage IO
			Swapper();
			IOManager();
		// If it can still run
		} else {
			// Put it on the ready queue
			readyQueue.add(currentJob);
		}
		// Schedule
		cpu.Schedule(a, p);
	}

	/*This function is invoked when a running job wants a service. The job can request termination, request disk I/O, or 
	  request to be blocked. The time in which the job makes a request is calculated and the job is added to the readyQueue. 
	  aInt acts a int holder for the value a[0]. 
		aInt = 5: (Request termination.)
			Remove the job from the readyQueue and mainToDrumQueue.
			Check the IO count, if greater than 0 and terminate the job, 
			else remove the job from memory if IO count is 0, update the 
			freeSpaceTable and jobTable accordingly.  
		aInt = 6: (Request disk I/O.) 
			Increment the jobs IO counter and add the job to the ioQueue.
			Call IOManager to deal with the request.  
		aInt = 7: (Request to be blocked.)
			Check to see if IO count is not 0, if 0 remove job from readyQueue, 
			block the job and increment blockCount. If blockCount is greater than
			BLOCKTHRESHOLD and the job is not latched, add the job to mainToDrumQueue
			and call Swapper();  
	*/
	public static void Svc(int a[], int p[]) {
		lastRunningJob.calculateTimeProcessed(p[5]);
		readyQueue.add(lastRunningJob);

		int aInt = a[0];

		// Request termination
		if(aInt == 5) {
			// Remove it from the ready queue
			while(readyQueue.contains(lastRunningJob))
				readyQueue.remove(lastRunningJob);
			// Remove it from the drum queue
			while(mainToDrumQueue.contains(lastRunningJob))
				mainToDrumQueue.remove(lastRunningJob);
			// If it didn't do IO
			if(lastRunningJob.getIoCount() > 0) {
				lastRunningJob.terminateJob();
			// If it did io
			} else {
				// Remove from core
				lastRunningJob.removeInCore();
				// Make space
				freeSpaceTable.addSpace(lastRunningJob);
				// Remove from job table
				jobTable.removeJob(lastRunningJob);
			}
		//Request disk I/O
		} else if(aInt == 6) {
			//Increment IO Count
			lastRunningJob.incIoCount();
			//Add to the IO Count
			ioQueue.add(lastRunningJob);
			//Call IOManager
			IOManager();
		//Request to be blocked
		} else if (aInt == 7) {
			//If IoCount doesn't equal 0
			if(lastRunningJob.getIoCount() != 0) {
				//Remove the job from the ready queue
				readyQueue.remove(lastRunningJob);
				//Block the job
				lastRunningJob.blockJob();
				//Increase the block count 
				blockCount++;
				
				//If the blockCount is greater than BLOCKTHRESHOLD and the job is not latched
				if(blockCount > BLOCKTHRESHOLD && !lastRunningJob.isLatched()) {
					//Put the job on the drum 
					mainToDrumQueue.add(lastRunningJob);
					//Call swapper to figure out which job goes where
					Swapper();
				}
			}
		}
		
		//Call cpu.Schedule to decide which job goes next
		cpu.Schedule(a, p);
	}

	/*This function is responsible for managing IO requests. 
		If not currentlyDoingIO and the ioQueue is not empty, currentJob is removed from the ioQueue, 
		currentlyDoingIO is set to true and the job is latched. Lastly siodisk is called on currentJob.  
		This is done because siodisk 
	*/
	public static void IOManager() {
		// If the job isn't doing IO, and this io queue size isn't empty then 
		// then we must take action. 
		if(!currentlyDoingIo && ioQueue.size() != 0) {
			
		   	// For each IO job in the queue ...	
			for(int i = 0; i < ioQueue.size(); i++) {
				// We add its state to current job, 
				currentJob = ioQueue.get(i);
				// remove it from the IOqueue because it will be handled. 
				ioQueue.remove(i);
				// LastJobToIo is set to current job.  
				lastJobToIo = currentJob;
				// Set the doingIO flag to true. 
				currentlyDoingIo = true;
				// The job is latched to prevent other jobs from from doing IO. 
				currentJob.latchJob();
				// Siodisk will take the job and its PID so its number of the job whose I/O is to be done. 
				sos.siodisk(currentJob.getPID());
				break;
			}
		}
	}
	
	/*This function is inovked in accordance with Crint() when a new job needs to be added to memory. 
		The new job is added to the drumToMainQueue and swapper is invoked. 
	*/
	public static void MemoryManager(PCB pcb) {
		// Job wants to be in main memory
		drumToMainQueue.add(pcb);
		// Let the swapper figure things out		
		Swapper();
	}
	
	/*This function is responsible for determining whichever job is currently not in memory should be placed in memory (long-term scheduler). 
		It finds space in memory using *best fit method. Once space is found, OS must call siodrum() to swap the job in. If needed the
		swaps jobs out to make room for ones want to be swapped in. 

		Swap in: Examine every job on the drumToMainQueue. currentJob is placed into memory if space is available. 
			 siodrum is called and the job is removed from memory.  
		Swap out: Examine every job on the mainToDrumQueue. If currentJob is not null, is not latched, and has the 
			 the most amount of CPU time left call siodrum on that job. Then remove the job from all three queues.  
	*/
	public static void Swapper() {

		// Take on the temporary address. 
		int tempAddress;
		int lowest = 0;
		int lowestIndex = 0;

		// Swap in step. 
		if(!doingSwap) {
			for(int i = 0; i < drumToMainQueue.size(); i++) {
				// currentJob is taking the job which came in from Memory Manager(). 
				currentJob = drumToMainQueue.get(i);
				// the temporary address will take in the current job and will
				// hold the chunk address. 
				tempAddress = freeSpaceTable.findSpaceForJob(currentJob);
				// If space wasn't found, then a -1 is placed for current job. 
				// Otherwise, the result will be anything within 100k from 0-99. 
				if(currentJob.getAddress() >= 0) {
					// set flag to true. 
					doingSwap = true;
					// write the info to siodrum() for swapping in step, therefore, the last parameter is 
					// set to 0 because its moving from drum to memory. And we specify the job#, its size, 
					// and its address. 
					sos.siodrum(currentJob.getPID(), currentJob.getJobSize(), currentJob.getAddress(), 0);
					// set current job to lastJobToDrum because Drmint() may have to
					// use this info. 
					lastJobToDrum = currentJob;
					// remove the current job from the backing store to main memory 
					// queue. 
					drumToMainQueue.remove(i);
					break;
				}
			}
		}

		// Swap out step. 
		if(!doingSwap) {
			// While there exists jobs from main memory to backing store. 
			for(int i = 0; i < mainToDrumQueue.size(); i++) {
				// Update current job to take job currently the queue. 
				currentJob = mainToDrumQueue.get(i);
				// Ensure the job isn't null, the job from main memory to backingstore 
				// isn't latched, and the current job is greater than the lowest, as in 
				// the job with the lowest size.  
				if(currentJob != null && !mainToDrumQueue.get(i).isLatched() && currentJob.getCpuTimeLeft() > lowest) {
					// Index of the job. 
					lowestIndex = i;
					// Assign job size to lowest. 
					lowest = currentJob.getCpuTimeLeft();
				}
			}
			// If the lowest job has time to run. 
			if(lowest > 0) {
				// Update flag. 
				doingSwap = true;
				// Assign state of the lowest job size to the current job. 
				currentJob = mainToDrumQueue.get(lowestIndex);
				// Specify job to be swapped out.  
				sos.siodrum(currentJob.getPID(), currentJob.getJobSize(), currentJob.getAddress(), 1);
				// Update last job to drum since it was just swapped out.  
				lastJobToDrum = currentJob;

				// remove the lastJobToDrum from respective queues because it was swapped out.  
				readyQueue.remove(lastJobToDrum);
				ioQueue.remove(lastJobToDrum);
				mainToDrumQueue.remove(lastJobToDrum);
			}
		}
	}
}
