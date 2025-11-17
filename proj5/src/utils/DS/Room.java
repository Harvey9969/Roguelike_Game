package utils.DS;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Room extends TSet{
    public Point center;
    public PSet wallTiles;
    public PSet floorTiles;

    public Room(Point center, int min, int max, Random random) {
        this.center = center;
        wallTiles = new PSet();
        floorTiles = new PSet();

        if (min >= max) {
            throw new IllegalArgumentException("Min must be less than max");
        } else if (min <= 0) {
            throw new IllegalArgumentException("dimensions must be greater than 0");
        }

        int lw = random.nextInt(min/2, max/2);
        int rw = random.nextInt(min/2, max/2);
        int bh = random.nextInt(min/2, max/2);
        int th = random.nextInt(min/2, max/2);

        int xStart = center.x - lw;
        int xStop = center.x + rw;
        int yStart = center.y - bh;
        int yStop = center.y + th;

        Set<Integer> xCorners = new HashSet<>(List.of(xStart, xStop));
        Set<Integer> yCorners = new HashSet<>(List.of(yStart, yStop));

        if (
                xStart < 0
                || xStop < 0
                || yStart < 0
                || yStop < 0
        ) {
            throw new RuntimeException("OOB room generated");
        }

        for (int y = yStart; y <= yStop; y++) {
            for (int x = xStart; x <= xStop; x++) {
                if (xCorners.contains(x) && yCorners.contains(y)) {
                    continue;
                } else if (xCorners.contains(x) || yCorners.contains(y)) {
                    wallTiles.add(new Point(x, y));
                } else {
                    floorTiles.add(new Point(x, y));
                }
            }
        }
    }

    @Override
    public Iterable<Point> getWallTiles() {
        return wallTiles;
    }

    @Override
    public Iterable<Point> getFloorTiles() {
        return floorTiles;
    }
}
