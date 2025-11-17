package utils.DS;

public class Point {
    public int x;
    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public double eDist(Point uddaPoint) {
        return Math.sqrt(Math.pow(x - uddaPoint.x, 2) + Math.pow(y - uddaPoint.y, 2));
    }

    public int mDist(Point uddaPoint) {
        return Math.abs(x - uddaPoint.x) + Math.abs(y - uddaPoint.y);
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}
