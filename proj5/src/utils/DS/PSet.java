package utils.DS;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class PSet extends HashSet<Point> {
    public Point choose(Random random) {
        int rIndex = random.nextInt(0, size() - 1);
        Iterator<Point> iterator = iterator();

        while(rIndex > 0) {
            iterator.next();
            rIndex--;
        }

        return iterator.next();
    }

    public PSet filterM(Point point, int radius, boolean in) {
        if (radius < 0) {
            throw new IllegalArgumentException("Negative radius not allowed");
        }

        PSet filtered = new PSet();

        for (Point PSetPoints : this) {
            if ((point.mDist(PSetPoints) <= radius) == in) {
                filtered.add(PSetPoints);
            }
        }

        return filtered;
    }

    public Point[] closestE(PSet uddaPSet) {
        double minDist = Double.POSITIVE_INFINITY;
        Point[] result = new Point[2];

        for (Point point: this) {
            for (Point uddaPoint: uddaPSet) {
                double dist = point.eDist(uddaPoint);

                if (minDist > dist) {
                    minDist = dist;

                    result[0] = point;
                    result[1] = uddaPoint;
                }
            }
        }

        return result;
    }
}
