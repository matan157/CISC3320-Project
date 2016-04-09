# CISC3320-Project
Simulating an OS in Java.

**The following are Jones' classes:**
```
    - sos.class
    - jobtype.class
    - Format.class
```

**Instructions for running the Java program:**

Since Java is a totally object-oriented language, all method calls whose definitions are outside the calling class must be preceded by the object name, or the class name if the method is static. In this case, all methods you will need to call are static. Since the class in 'sos.class' is called sos (the public class must be the same as the file name), you will have to call my methods in the following way:
```
    sos.ontrace();
    sos.offtrace();
    sos.siodisk(...);
    sos.siodrum(...);
```
My main calls some of your methods in a similar way. Therefore, you must call your public class os and therefore your file must be called 'os.java'. You should use the following headers:
```
    public static void Crint (int []a, int []p)
    public static void Dskint (int []a, int []p)
    public static void Drmint (int []a, int []p)
    public static void Tro (int []a, int []p)
    public static void Svc (int []a, int []p)
```
The first variable, 'a' is declared as an array with only one element, since basic types may not be changed within a method. Access this value as 'a\[0\]'. Any other methods you write that are called from within these will also have to be static.

Since sos does not create an object of type os and call its constructor, you must call your constructor inside startup, so that everything can be initialized. If you would like to write an interface, it should be called here, too.

To run the program from the command line:

1. Copy sos.class, jobtype.class and Format.class into your directory.
2. javac \*.java	(compile all your necessary Java files)
3. java sos	(run my program)

The 'sos.class' file and other .class files may be used in DOS, Windows, UNIX and MacOS, since the Java Virtual Machine will interpret the bytecode in the correct way for that machine. Java's type sizes are the same for all machines (i.e. an int is the same size on the PC as on a Sun), so you don't have to change everything to long if you will be doing it on a PC.

Nicholas Galarza
Shahrukh Jalil
