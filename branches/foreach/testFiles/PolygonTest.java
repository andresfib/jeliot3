//Class:MyClass

import jeliot.io.*;

public class Side {
    int sides;

    Side(int s) {
        sides = s;
    }
}

public class Polygon {
    int sides;

    Polygon() {
    }

    Polygon(int s) {
        sides = s;
    }

    public int getSides() {
        return sides;
    }

}

public class MyClass {
    public static void main() {
        Polygon polygon;
        int sides = 3;
        polygon = new Polygon(sides);
    }
}