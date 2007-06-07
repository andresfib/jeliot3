import jeliot.io.*;

public class Application {
    
	public static void main() {
    	char[] array1 = new char[] {'g', 'c', 't', 'c', 'g', 'g', 'c', 'a'};

    	char[] array2 = new char[] {'g', 'c', 'a'};
    	
    	if (task(array2, array1)) {
            System.out.println("True");
        } else {
            System.out.println("False");            
        }
	}
	
	static boolean task(char[] array2, char[] array1) { 
        int length1 = array1.length;
        int length2 = array2.length;
        int value = length2 - length1;
        int index1;
        int index2; 
  
        for (index1 = 0; index1 <= value; index1++) { 
            for (index2 = 0; index2 < length2; index2++) {
            	if (array2[index2] != array1[index1 + index2]) {
            		break;
            	}
            }
       
            if (index2 >= length2) { 
                return true;
            } 
        }
        return false;
    }
}
