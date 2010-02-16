public class Side {
    public int length;
    Side(){
        length=0;
    }
    Side(int l){
        length = l;

    }

}
    
public class Square{
    Side w,h;
    Square(){
      w = new Side();
      h = new Side();
    }
    Square(int s,int t){
      w = new Side(s);
      h = new Side(t);
    }
}
public class MyClass {
    public static void main() {
        Square square = new Square(5,5);
    }
}
