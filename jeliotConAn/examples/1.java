import jeliot.io.*;

class Operation {

	public static void main() {
		int n = 2;
	
		int value = 1;
		while (n > 1) {
			value = value * n;
			n--;
		}
		System.out.println("Value = "+value);
	}		
}