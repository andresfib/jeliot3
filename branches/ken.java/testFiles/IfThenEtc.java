
class IfThenEtc {                                    
    static void main() {
        int i = 0;
        long a = 3;
		long b = 20;
		long tmp;
		boolean less = false;

		do {
			if (less) {
				if (a > b) {
					tmp = a;
					a = b;
					b = tmp;
				}
			}
			else if (a < b) {
					tmp = a;
					a = b;
					b = tmp;
			} 			
			less = !less;
			i++;
		}
		while (i<10);
    }                         
}                          
