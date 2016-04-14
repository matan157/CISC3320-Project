import java.util.TreeMap;
import java.util.*;

public class JobTable {
	private static TreeMap jobTable;
	private static int jobsInTable;
	private static int SIZE;
   
	// Takes a SIZE variable and initializes a jobtable of that size. 
    public JobTable(int SIZE) {
		jobTable = new TreeMap();
		jobsInTable = 0;
		this.SIZE = SIZE;
    }
    
	// constructs table and adds one job
    public JobTable(PCB pcb) {
		jobTable = new TreeMap();
		jobTable.put(pcb.getPID(), pcb);
		jobsInTable = 1;
    }
    
    // Adding a job to the job table
	public static void addJob(PCB pcb) {
		if(jobsInTable < SIZE) {
			jobTable.put(pcb.getPID(), pcb);
			jobsInTable++;
		} else {
			System.out.println("Cannot add job. Table full.");
		}
		return;
    }

	// Removes a job from the job table
	public static void removeJob(PCB pcb) {
		if(jobsInTable > 0) {
			jobTable.remove(pcb.getPID());
			jobsInTable--;
		} else {
			System.out.println("Cannot remove job. Table is empty.");
		}
		return;
	}
	
	// Returns a job
	public PCB getJob(int PID) {
		return (PCB)jobTable.get(PID);
	}

	// Returns a boolean based on whether the job exists
	public boolean contains(int PID) {
		return jobTable.containsKey(PID);
	}
}
