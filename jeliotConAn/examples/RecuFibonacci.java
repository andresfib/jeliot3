import jeliot.io.*;

public class Side {
    int  sides;
    Side(int s){
        sides=s;
    }
}
public class Polygon {
    int sides;
    Polygon(){}
    Polygon(int s){
        sides=s;
    }
    public int getSides(){
        return sides;
    }
    
}
public class MyClass {
    public static void main() {
        Polygon polygon;
//        Integer sides = new Integer (3);
//        polygon = new Polygon(sides);
    }
}
