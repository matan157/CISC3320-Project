DEP = JobTable.class PCB.class FST_2.class CPU.class
CLASSES = $(DEP) os.class

os.class: $(DEP) os.java
	javac os.java

JobTable.class: JobTable.java
	javac JobTable.java

PCB.class: PCB.java
	javac PCB.java

FST_2.class: FST_2.java
	javac FST_2.java
	
CPU.class: CPU.java
	javac CPU.java

clean: $(CLASSES)
	rm $(CLASSES)
