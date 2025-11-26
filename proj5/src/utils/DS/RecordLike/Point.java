package utils.DS.RecordLike;

public class Point {
    public int x;
    public int y;

    public Point(int x, int y) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Negative points aren't allowed");
        }

        this.x = x;
        this.y = y;
    }

    public double eDist(Point uddaPoint) {
        return Math.sqrt(Math.pow(x - uddaPoint.x, 2) + Math.pow(y - uddaPoint.y, 2));
    }

    public int mDist(Point uddaPoint) {
        return Math.abs(x - uddaPoint.x) + Math.abs(y - uddaPoint.y);
    }

    public Point left() {
        return new Point(x - 1, y);
    }

    public Point right() {
        return new Point(x + 1, y);
    }

    public Point up() {
        return new Point(x, y + 1);
    }

    public Point down() {
        return new Point(x, y - 1);
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Point uddaPoint) {
            return (uddaPoint.x == x) && (uddaPoint.y == y);
        }

        return false;
    }

    @Override
    public String toString() {
        return x + "," + y;
    }
}
