public class CPU {
    public static void Schedule(int a[], int p[]) {
        //Intialize two local variables to keep track of lowest job size and the index of that job. 
		int lowest = 0;
		int lowestIndex = 0;

		//Search the readyQueue for the job. 
		for(int i = 0; i < readyQueue.size(); i++) {
			// Finds smallest job out of unblocked jobs.  
			if(lowest == 0 && !readyQueue.get(i).isBlocked()) {
				//Get the currentJob from the readyQueue 
				currentJob = readyQueue.get(i);
				//Set lowest to currentJob Size 
				lowest = currentJob.getJobSize();
				//Set the lowestIndex to the iterator
				lowestIndex = i;
			}

			currentJob = readyQueue.get(i);
			// If every job is blocked, null, terminated, then it means that there is no process to run. 
			if(currentJob != null && !currentJob.isBlocked() && !currentJob.isTerminated() && currentJob.getJobSize() < lowest) {
				lowestIndex = i;
				lowest = currentJob.getJobSize();
			}
		}

		//If lowest is greater than 0
		if(lowest > 0) {
			//Get the lowest index of the job from the readyQueue
			currentJob = readyQueue.get(lowestIndex);
			//Call the dispatcher 
			Dispatcher(a, p);
			//While the readyQueue contains the current job remove it 
			while(readyQueue.contains(currentJob))
				readyQueue.remove(currentJob);
			return;
		}
		//a[0]= 1, "There are no jobs ready to run".
		lastRunningJob = null;
		a[0] = 1;
    }
}