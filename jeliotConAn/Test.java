public class Test1{

   private int field1;
   public  int field2;
   
   private Test1(){
   	  field1 = 0;
   	  field2 = 0;
   }
   
   protected Test1(int f1, double f2){
      super();
      this.field1 = f1;
      this.field2 = f2;
   }
}

public class Test2 extends Test1{

   private double field3;
   
   Test2(){
      field3 = 3.0;
   }
   public void main(String[] argv){
   
   		Test1 first = new Test1(4,5);
   		Test2 second = new Test2();
   		second.field1 = first.field2 + second.field3;
   }
}
   		
   		