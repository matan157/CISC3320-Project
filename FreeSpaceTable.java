import java.util.*;

public class FreeSpaceTable {
	
	private static int worstSpaceAddress;
	private static int worstSpaceSize;
	private static int SIZE_OF_MEMORY;
	private int[] FST;

	public FreeSpaceTable(int SIZE) {
		FST = new int[SIZE];
		SIZE_OF_MEMORY = SIZE;

		for(int i = 0; i < SIZE; i++)
			FST[i] = 0;

		worstSpaceSize = SIZE;
		worstSpaceAddress = 0;
	}

	public int findSpaceForJob(PCB pcb) {
		int tempAddress;

		if(pcb.getJobSize() <= worstSpaceSize) {
			for(int i = worstSpaceAddress; i < worstSpaceAddress + pcb.getJobSize(); i++) 
				FST[i] = 1;

			tempAddress = worstSpaceAddress;
			pcb.setAddress(tempAddress);

			calculateWorstSpaceAddressAndSize();

			return tempAddress;
		} else {
			calculateWorstSpaceAddressAndSize();
			return -1;
		}
	}

	public void addSpace(PCB pcb) {
		for(int i = pcb.getAddress(); i < pcb.getAddress() + pcb.getJobSize(); i++) {
			FST[i] = 0;
		}

		pcb.setAddress(-1);
		calculateWorstSpaceAddressAndSize();
	}

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
				tempAddress = i + 1;
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

	public int getWorstSpaceAddress() { return worstSpaceAddress; }
	public int getWorstSpaceSize() { return worstSpaceSize; }

	public void printFST() {
		for(int i = 0; i < SIZE_OF_MEMORY; i++) {
			if(i%6 == 0)
				System.out.println("");
			System.out.println(i + " " + FST[i] + "\t");
		}
	}
}
