package com.jsyryczynski;

/**
 *
 */
public class Point {

    private int x;
    private int y;

    public static Point of(int x, int y) {
        return new Point(x, y);
    }

    private Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Postion))
            return false;
        Postion other = (Postion) o;
        return this.x == other.x && this.y == other.y;
    }
}
