import jeliot.io.*;

public class MyClass {
    public static void main() {
		double sum = 0;
		int count = 0;
		int n;
		double avg;

		n = Input.readInt();
		while (count < n) {
        	sum = sum + Input.readDouble();
			count++;
		}
		avg = sum / n;
		Output.println(avg);
    }
}