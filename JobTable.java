import java.util.TreeMap;
import java.util.*;

public class JobTable{
	private static int MAX_SIZE;
	private static int jobsInTable;
	private static Map<Integer, PCB> jobTable;

	public JobTable(int MAX_SIZE) {
		this.MAX_SIZE = MAX_SIZE;
		jobsInTable = 0;
		jobTable = new TreeMap<>();
	}

	public void addJob(PCB pcb) {
		if(jobsInTable < MAX_SIZE) {
			jobTable.put(pcb.getPID(), pcb);
			jobsInTable++;
		} else {
			System.out.println("JOB TABLE FULL");
		}
	}

	public void removeJob(PCB pcb) {
		if(jobsInTable > 0) {
			jobTable.remove(pcb.getPID());
			jobsInTable--;
		}
	}

	public PCB getJob(int PID) {
		return jobTable.get(PID);
	}

	public boolean contains(int PID) {
		return jobTable.containsKey(PID);
	}

	public static void printJobTable() {
		PCB temp;

		System.out.println("#	Size Arrived		CPU TU  MAX CPUT  IOP  Priority  Blocked  Latched  INCORE  Terminated  Address");
		
		for(Map.Entry<Integer, PCB> entry : jobTable.entrySet()) {
			temp = entry.getValue();
			temp.printProcess();
		}
	}
}
