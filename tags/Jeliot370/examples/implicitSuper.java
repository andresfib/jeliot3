   
public class Rectangle {
    int width,heigth;
    int area;
    Rectangle(){
        this(0,0);
    }
    Rectangle(int w, int h){
        width= w;
        heigth=h;
        area = getArea();
    }

    public int getArea(){
        return width*heigth;
    }
}
    
public class Square extends Rectangle{
    int side;
    Square(){
      side= 0;
    }
    Square(int s){
       super(s,s);
        side= s;
    }
}
public class MyClass {
    public static void main() {
        Square square = new Square();
       int area = square.getArea();;
    }
}
