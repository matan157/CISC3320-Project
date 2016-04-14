import java.util.*;

public class FreeSpaceTable {

	private static int worstSpaceAddress;
	private static int worstSpaceSize;
	private static int SIZE_OF_MEMORY;
	private int[] FST;

	// Constructor takes memory size
	public FreeSpaceTable(int SIZE_OF_MEMORY) {
		FST = new int[SIZE_OF_MEMORY];
		this.SIZE_OF_MEMORY = SIZE_OF_MEMORY;
		worstSpaceSize = SIZE_OF_MEMORY; // All of memory is empty
		worstSpaceAddress = 0; // Memory starts at 0

		// Memory is empty
		for (int i = 0; i < SIZE_OF_MEMORY; i++)
			FST[i] = 0;		
	}

	// Takes a job and finds space
	// IF space is found, starting address of the space is returned
	// Otherwise it returns -1
	public int findSpaceForJob(PCB job) {
		int tempAddress;
		
		if(job.getJobSize() <= worstSpaceSize) {
			for(int i = worstSpaceAddress; i < worstSpaceAddress + job.getJobSize(); i++)
				FST[i] = 1; // Putting job in memory

			tempAddress = worstSpaceAddress;
			job.setAddress(tempAddress); // PCB stores where in memory

			calculateWorstSpaceAddressAndSize();
			return tempAddress;
		} else {
			calculateWorstSpaceAddressAndSize();
			return -1;
		}
	}

	// Takes a job and adds space according to the job's
	// starting address and size
	// it recalculates the largest size and returns void
	public void addSpace(PCB job) {
		for(int i = job.getAddress(); i < job.getAddress() + job.getJobSize(); i++)
			FST[i] = 0; // Free me seymour, FREE ME

		job.setAddress(-1); // Job no longer in memory

		calculateWorstSpaceAddressAndSize();

		return;
	}
		
	// Meat and potatoes
	// Calculates the largest free space and returns void
	public void calculateWorstSpaceAddressAndSize() {
		int tempAddress = 0;
		int tempSize = 0;

		int largestSize = 0;
		int largestSizeAddress = 0;

		for(int i = 0; i < SIZE_OF_MEMORY; i++) {
			if(FST[i] == 0) {
				tempSize++;
			} else {
				if(tempSize > largestSize) {
					largestSize = tempSize;
					largestSizeAddress = tempAddress;
				}
				tempAddress = i+1;
				tempSize = 0;
			}
		}

		if(largestSize == 0 && tempSize != 0) {
			largestSize = tempSize;
			largestSizeAddress = tempAddress;
		} else if(tempSize > largestSize) {
			largestSize = tempSize;
			largestSizeAddress = tempAddress;
		}

		worstSpaceAddress = largestSizeAddress;
		worstSpaceSize = largestSize;
	}

	// Getters

	public int getWorstSpaceAddress() { return worstSpaceAddress;}
	public int getWorstSpaceSize() { return worstSpaceSize; }

	// Print method
	public void printFST() {
		for(int i = 0; i < SIZE_OF_MEMORY;i++) {
			if(i%6 == 0)
				System.out.println("");
			System.out.print(i + " " + FST[i] + "\t");
		}
	}
	
}