import jeliot.io.*;

public class StringMatching {
    
	public static void main() {
    	char[] jono = new char[8];
    	char[] merkki = new char[3];
    	
    	jono[0] = 'g';
    	jono[1] = 'c';
    	jono[2] = 't';
    	jono[3] = 'c';
    	jono[4] = 'g';
    	jono[5] = 'g';
    	jono[6] = 'c';
    	jono[7] = 'a';
    	
    	merkki[0] = 'g';
    	merkki[1] = 'c';
    	merkki[2] = 'a';
    	
    	if (etsi(merkki, jono)) {
            Output.println("Löytyi");
        } else {
            Output.println("Ei löytynyt");            
        }
	}
	
	static boolean etsi(char[] etsi, char[] jono) { 
        int pituusE = etsi.length;
        int pituusJ = jono.length;
        int pituusErotus = pituusJ - pituusE;
        int indexE;
        int indexJ; 
  
        for (indexJ = 0; indexJ <= pituusErotus; ++indexJ) { 
            for (indexE = 0; indexE < pituusE; ++indexE) {
            	if (etsi[indexE] != /* == */ jono[indexE + indexJ]) {
            		break;
            	}
            }
       
            if (indexE >= pituusE) { 
                return true;
            } 
        }
        return false;
    }
}
