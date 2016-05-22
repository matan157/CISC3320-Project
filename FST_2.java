import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class FST_2 {
    private static List<FreeSpace> FreeSpaceTable;
    private static final int MEMORY_SIZE = 100;
    
    public FST_2() {
        FreeSpaceTable = new ArrayList<>();
        FreeSpaceTable.add(new FreeSpace(0, 100));
    }
    
    // Implementing BEST FIT
    public int findSpaceForJob(PCB job) {
        int jobSize = job.getJobSize(); // Save state of current job size. 
        Collections.sort(FreeSpaceTable); 
	// Iterate through list in acsending order, as soon as the first 
	// smallest job that is found which fits inside of the free space 
	// table, the address to the space is returned. 
        for(FreeSpace fs : FreeSpaceTable) {
	    // Save the state of the chunksize and chunkaddress since they will update. 
	    // upon iteration. 
            int chunkSize = fs.getSize();
            int chunkAddress = fs.getAddress();
            if(chunkSize >= jobSize) {
		// Computations to determine where to place job in the currently free chunk. 
                fs.setAddress(chunkAddress + jobSize);
                fs.setSize(chunkSize - jobSize);
		// If a portion in the free space table is zero, remove the free space chunk. 
                if(fs.getSize() == 0) 
                    FreeSpaceTable.remove(fs);
		// Place job in the smallest chunk it can fit. 
                job.setAddress(chunkAddress); 
                return chunkAddress;
            }
        }

        return -1;
    }
    
   /* Get a job out of memory. */ 
    public void addSpace(PCB job) {
	// Save state of job address and its size. 
        int jobAddress = job.getAddress();
        int jobSize = job.getJobSize();
	// If job is next to another freespace chunk, combine them. 
        for(FreeSpace fs : FreeSpaceTable) {
            int chunkAddress = fs.getAddress();
            int chunkSize = fs.getSize();
            
            if(chunkAddress + chunkSize == jobAddress) { // Job After 
                fs.setSize(chunkSize + jobSize);
                job.setAddress(-1);
                return;
            } else if(jobSize + jobAddress == chunkAddress) { // Job Before
                fs.setAddress(jobAddress);
                fs.setSize(jobSize + chunkSize);
                job.setAddress(-1);
                return;
            }
        }
        FreeSpaceTable.add(new FreeSpace(jobAddress, jobSize));
        job.setAddress(-1);
    }
    
    
    
    public void printFST() {
        System.out.println("\t\t FREE SPACE TABLE");
        System.out.println("Address" + "\t" + "Size");
        for(FreeSpace fs : FreeSpaceTable) 
            System.out.println(fs.getAddress() + "\t" + fs.getSize());
    }
}
