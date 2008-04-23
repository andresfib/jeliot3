//Class:MyClass

class Polygon {
    static int polygons = 0;
    int sides;
    Polygon() {
        polygons++;
    }
    Polygon(int s){
        this();
        sides=s;
    }
}

class Rectangle extends Polygon {
    int width,heigth;
    Rectangle(){
        super(4);
        width=0;
        heigth=0;
    }
    Rectangle(int w, int h) {
        super(4);
        width=w;
        heigth=h;
    }
    public int getArea() {
        return width*heigth;
    }
}
    
class Square extends Rectangle {
    int side;
    Square(){
        side=0;
    }
    Square(int s){
        super(s,s);
        side=s;
    }
}

public class MyClass {
    public static void main() {
        Square square;
        square = new Square(3);
        int area = square.getArea();
        System.out.println(Polygon.polygons);
    }
}
