//Class:Rectange
//Call-Method:main(new String[0])

import java.io.*;

public class Point {
    public int x;
    public int y;

    public Point(int a, int b) {
        x = a;
        y = b;
    }

    public boolean isAbove(Point p) {
        if (y < p.y)
            return true;
        else
            return false;
    }

    public boolean isBelow(Point p) {
        if (y > p.y)
            return true;
        else
            return false;
    }

    public boolean isLeftTo(Point p) {
        if (x < p.x)
            return true;
        else
            return false;
    }

    public boolean isRightTo(Point p) {
        if (x > p.x)
            return true;
        else
            return false;
    }
}


public class Rectangle {
    private Point top_left;
    private Point bottom_right;

    public Rectangle(Point tl, Point br) {
        top_left = tl;
        bottom_right = br;
    }

    public boolean contains(Point p) {
        if (top_left.isAbove(p) && top_left.isLeftTo(p) &&
            bottom_right.isBelow(p) && bottom_right.isRightTo(p))
            return true;
        else
            return false;
    }

    public static void main(String args[]) {
        Point myPoint = new Point(12,6);
        Point topLeft = new Point(6,10);
        Point bottomRight = new Point(16,4);
        Rectangle myRectangle = new Rectangle(topLeft, bottomRight);

        if (myRectangle.contains(myPoint))
            System.out.println("The point is withing the rectangle");
        else
            System.out.println("The point is outside the rectangle");
    }
}
