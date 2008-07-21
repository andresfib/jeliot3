//Class:MyClass

import jeliot.io.*;

public class Polygon {
    int sides;
    Polygon(){}
    Polygon(int s){
        sides=s;
    }
	public int getSides() {
		return sides;
	}
}

public class Rectangle extends Polygon{
    int width,heigth;
    Rectangle(){
        super(4);
        width=0;
        heigth=0;
    }
    Rectangle(int w, int h){
        super(4);
        width=w;
        heigth=h;
    }
    public int getArea(){
        return width*heigth;
    }
}
    
public class Square extends Rectangle{
    int side;
    Square(){
        side=0;
    }
    Square(int s){
        super(s,s);
        side=s;
    }
}

public class Test {
	Polygon p;
	public Test() {
	}
	public void setPolygon(Polygon p) {
		this.p = p;
	}
	public int getSides() {
		return p.getSides();
	}
}

public class MyClass {
    public static void main() {
        Square square;
		Test t = new Test();
        square = new Square(3);
		t.setPolygon(square);
		System.out.println(t.getSides());
        int area;
        area = square.getArea();
    }
}
