package test;

import org.python.util.jython;
 

public class PhytonTest {
	
	public static void main(String[] args)
	{
		String fileName = "C:\\My Documents\\eclipseWorkspace\\Jython\\src\\test\\helloWorld.py";
		String[] newArgs = new String[1];
		newArgs[0] = fileName;
		jython.main(newArgs);// = new jython();
		
	}	
}
