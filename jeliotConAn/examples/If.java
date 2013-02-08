 class Ware {
int i = 0;
static int z = 0;
Ware (int i) {
this.i  = i;
z++;
}
}    
 
     public class MeinProgramm { 
       public static void main (String [] args) {
         int [] f = {1,2,3};
Ware [] waren = new Ware [3];
for (int j = 0; j < f.length; j++){
waren[j] = new Ware (f[j]);
}
for (int j = 0; j < f.length; j++){
System.out.println (waren [j].i);
}
 

       } // Ende main
       }