public class Selection {

   public static void main() {
      int [] data  = new int [9];
      for (int i=0; i<data.length; i++) {
        data[i] = (int) (20 * Math.random());
      }
      select (data, 0, data.length);
   }
    
   public static void select(int [] data, int first, int n) {
      for (int i=0; i<n-1; i++) {
         int small = i;
         for (int j=i+1; j<n; j++) {
            if (data[first+j] < data[first+small]) small=j;
         }
         swap (data, first+i, first+small);
      }
   }

   private static void swap (int [] data, int i, int j) {
      final int temp = data[i];
      data[i]=data[j];
      data[j]=temp;
   }

}
