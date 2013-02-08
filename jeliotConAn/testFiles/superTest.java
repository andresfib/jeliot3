//Class:MyClass

public class Rectangle {
    int width, heigth;

    Rectangle() {
        this(0, 0);
        width = 0;
        heigth = 0;
    }

    Rectangle(int w, int h) {
        width = w;
        heigth = h;
    }

    public int getArea() {
        return width * heigth;
    }
}

public class Square extends Rectangle {
    int side;

    Square() {
        //this(0);
    }

    Square(int s) {
        super(s, s);
        side = s;
    }
}

public class MyClass {
    public static void main() {
        Square square = new Square();
        int area = square.getArea();
    }
}