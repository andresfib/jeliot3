public class Side {
    public int length;
    Side(){
        length=0;
    }
    Side(int l){
        length = l;

    }

}

public class Polygon {
    int sides;
}

public class Rectangle extends Polygon{
    Side width,heigth;
    int area;
    Rectangle(){
        this(0,0);
    }
    Rectangle(int w, int h){
        width= new Side(w);
        heigth=new Side(h);
        area = getArea();
    }

    public int getArea(){
        return width.length*heigth.length;
    }
}
    
public class Square extends Rectangle{
    Side side;
    Square(){
      side= new Side(0);
    }
    Square(int s){
       super(s,s);
        side= new Side(s);
    }
}
public class MyClass {
    public static void main() {
        Square square = new Square();
       int area = square.getArea();;
    }
}
