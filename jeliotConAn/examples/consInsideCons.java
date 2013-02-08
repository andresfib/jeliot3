public class Side {
    public int length;
    Side(){
        length=0;
    }
    Side(int l){
        length = l;

    }

}
    

public class Square {
    Side side;
    Square(){
      this(new Side(0));
    }
    Square(Side s){       
        side= s;
    }
}
public class MyClass {
    public static void main() {
        Square square = new Square();
       //int area = square.getArea();;
    }
}
