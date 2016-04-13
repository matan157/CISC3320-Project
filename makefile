CLASSES = JobTable.class PCB.class os.class

os.class: JobTable.class PCB.class
	javac os.java

JobTable.class: 
	javac JobTable.java

PCB.class:
	javac PCB.java

clean:
	rm $(CLASSES)
