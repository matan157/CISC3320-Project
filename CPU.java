/*
    The CPU class encompasses two functions of our "CPU".
    The CPU will schedule and dispatch jobs accordingly.
    This class was created so that os's length could be made shorter.
*/
public class CPU {
    /*
     * CpuScheduler() -- Invoked from other interrupt handlers to select order of jobs to run.
	 * The scheduler accesses the smallest unblocked job by index. If such a job exists, it will 
	 * be dispatched(). 
	*/ 
    public static void Schedule(int a[], int p[]) {
        //Intialize two local variables to keep track of lowest job size and the index of that job. 
		int lowest = 0;
		int lowestIndex = 0;

		//Search the readyQueue for the job. 
		for(int i = 0; i < os.readyQueue.size(); i++) {
			// Finds smallest job out of unblocked jobs.  
			if(lowest == 0 && !os.readyQueue.get(i).isBlocked()) {
				//Get the currentJob from the readyQueue 
				os.currentJob = os.readyQueue.get(i);
				//Set lowest to currentJob Size 
				lowest = os.currentJob.getJobSize();
				//Set the lowestIndex to the iterator
				lowestIndex = i;
			}

			os.currentJob = os.readyQueue.get(i);
			// If the current job isn't blocked, or terminated and is the lowest job so far, set it as the lowest. 
			if(os.currentJob != null && !os.currentJob.isBlocked() && !os.currentJob.isTerminated() && os.currentJob.getJobSize() < lowest) {
				lowestIndex = i;
				lowest = os.currentJob.getJobSize();
			}
		}

		//If lowest is greater than 0
		if(lowest > 0) {
			//Get the lowest index of the job from the readyQueue
			os.currentJob = os.readyQueue.get(lowestIndex);
			//Call the dispatcher 
			Dispatcher(a, p);
			//While the readyQueue contains the current job remove it 
			while(os.readyQueue.contains(os.currentJob))
				os.readyQueue.remove(os.currentJob);
			return;
		}
		//a[0]= 1, "There are no jobs ready to run".
		os.lastRunningJob = null;
		a[0] = 1;
    }
    
    /* 
     * Dispatcher() -- "Sets CPU registers before context switches". 
	 * After taking in the address, and jobsize, this will run the 
	 * current job. Either the job will be run for the duration of 
	 * the round robin slice, or for the remainder of how much the 
	 * job has left. 
	*/
    public static void Dispatcher(int a[], int p[]) {
		//Set last running job to currentJob 
		os.lastRunningJob = os.currentJob;
		//Set the last time the job was running
		os.lastRunningJob.setLastTimeProcessing(p[5]);
		
		// a[0]= 2, "Set CPU to run mode, must set p values". Specifically, p[2,3,4]. 
		a[0] = 2;
		//Get the address of job 
		p[2] = os.lastRunningJob.getAddress();
		//Get the size of the job 
		p[3] = os.lastRunningJob.getJobSize();

		// If there's more time left than our time slice set it to the slice.
		if(os.lastRunningJob.getCpuTimeLeft() > os.ROUNDROBINSLICE) {
			p[4] = os.ROUNDROBINSLICE;
        // Otherwise, set it to how much time it has left.
		} else {
			p[4] = os.lastRunningJob.getCpuTimeLeft();
		}
	}
}