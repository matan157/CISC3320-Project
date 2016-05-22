import java.util.TreeMap;
import java.util.NavigableMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class FST {
    private static TreeMap<Integer, Integer> FreeSpaceTable;
    private static final int MEMORY_SIZE = 100;
    
    public FST() {
        FreeSpaceTable = new TreeMap<>();
        FreeSpaceTable.put(0, MEMORY_SIZE);
    }
    
    public int findSpaceForJob(PCB job) {
        int jobSize = job.getJobSize();
        
        int smallestDiff = MEMORY_SIZE;
        int smallestKey = -1;
        int smallestSize = 0;
        
        for(Map.Entry<Integer, Integer> entry : FreeSpaceTable.entrySet()) {
            int chunkAddress = entry.getKey();
            int chunkSize = entry.getValue();
            
            if(chunkSize - jobSize < smallestDiff) {
                smallestDiff = chunkSize - jobSize;
                smallestKey = chunkAddress;
                smallestSize = chunkSize;
            }
        }
        
        if(smallestDiff < MEMORY_SIZE) {
            job.setAddress(smallestKey);
            FreeSpaceTable.put(smallestKey + jobSize - 1, smallestSize - jobSize);
            FreeSpaceTable.remove(smallestKey);
            return smallestKey;
        }   
        // If the program doesn't return anything earlier, it doesn't have space for it.
        return -1;
    }
    
    public void addSpace(PCB job) {
        int jobAddress = job.getAddress();
        int jobSize = job.getJobSize();
        
        defrag(jobAddress, jobSize);
        job.setAddress(-1);
    }
    
    public void defrag(int jobAddress, int jobSize) {
        for(Map.Entry<Integer, Integer> entry : FreeSpaceTable.entrySet()) {
            int key = entry.getKey();
            int value = entry.getValue();
            if(key + value == jobAddress) {
                FreeSpaceTable.replace(key, value + jobSize);
            }
        }
    }
    
    public void printFST() {
        System.out.println("\t\t FREE SPACE TABLE");
        System.out.println("Address" + "\t" + "Size");
        for(Map.Entry<Integer, Integer> entry : FreeSpaceTable.entrySet()) {
            int key = entry.getKey();
            int value = entry.getValue();
            System.out.println(key + "\t" + value);
        }
    }
    
}

/* Traversing a TreeMap:
    
*/