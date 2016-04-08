import java.util.LinkedList;

public class JobTable {
    private LinkedList<PCB> jobtable; // List of jobs
    private static final int LIMIT = 50; // Max size of jobtable
    
    // Construct empty list
    public JobTable() {
        this.jobtable = new LinkedList<PCB>();
    }
    
    // Construct list with one job
    // Might not be used
    public JobTable(PCB pcb) {
        this.jobtable = new LinkedList<PCB>();
        jobtable.add(pcb);
    }
    
    // Adding a job to the job table
    public void addJob(PCB pcb) {
        // Check if @ the limit
        if(jobtable.size() > LIMIT) {
            System.out.println("Cannot add job. Job table is full.");
            return;
        }
        jobtable.add(pcb);
        return;
    }
}