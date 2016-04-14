DEP = JobTable.class PCB.class FreeSpaceTable.class
CLASSES = $(DEP) os.class

os.class: $(DEP) 
	javac os.java

JobTable.class: 
	javac JobTable.java

PCB.class:
	javac PCB.java

FreeSpaceTable.class:
	javac FreeSpaceTable.java

clean:
	rm $(CLASSES)
