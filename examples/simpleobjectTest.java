public class Square{
    int side;
    Square(){
      side = 0;
    }
    Square(int s){
        side = s;
    }
}
public class MyClass {
    public static void main() {
        Square square = new Square(5);
    }
}
