package utils.DS;

import utils.DS.recordlike.Point;

import java.util.ArrayList;
import java.util.Random;

public class PSet extends ArrayList<Point> {
    public Point choose(Random random) {
        return get(
                random.nextInt(0, size())
        );
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

    public int closestMDist(Point comparison) {
        if (size() == 0) {
            throw new IllegalStateException("Cannot find closest on empty PSet");
        }

        int dist = Integer.MAX_VALUE;

        for (Point point: this) {
            if (dist > point.mDist(comparison)) {
                dist = point.mDist(comparison);
            }
        }

        return dist;
    }
}
