import jeliot.io.*;

public class Rectangle{
    double width,heigth;
    Rectangle(double w, double h){
        width=w;
        heigth=h;
    }
    public double getArea(){
        return width*heigth;
    }
}
    
public class Square extends Rectangle{
    double side;
    Square(double s){
        super(s,s);
        side=s;
    }
    
    public double getArea(){
        return side*side;
    }
}

public class MyClass {
    public static void main() {
        Square square = new Square(3);
        double area = square.getArea();
    }
}
