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
        int jobSize = job.getJobSize();
        Collections.sort(FreeSpaceTable);
        for(FreeSpace fs : FreeSpaceTable) {
            int chunkSize = fs.getSize();
            int chunkAddress = fs.getAddress();
            if(chunkSize >= jobSize) {
                fs.setAddress(chunkAddress + jobSize);
                fs.setSize(chunkSize - jobSize);
                job.setAddress(chunkAddress);
                return chunkAddress;
            }
        }

        return -1;
    }
    
    
    public void addSpace(PCB job) {
        int jobAddress = job.getAddress();
        int jobSize = job.getJobSize();
        for(FreeSpace fs : FreeSpaceTable) {
            int chunkAddress = fs.getAddress();
            int chunkSize = fs.getSize();
            
            if(chunkAddress + chunkSize == jobAddress) {
                fs.setSize(chunkSize + jobSize);
                job.setAddress(-1);
                return;
            } else if(jobSize + jobAddress == chunkAddress) {
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