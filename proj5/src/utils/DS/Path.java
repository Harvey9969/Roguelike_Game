package utils.DS;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Path extends TSet {
    public PSet wallTiles;
    public PSet floorTiles;

    public Path(Room start, List<Point> path, Room stop, Random random) {
        super(random);
        wallTiles = new PSet();
        floorTiles = new PSet();

        for (Point pathPoint: path) {
            floorTiles.add(pathPoint);

            Set<Point> startFloorTiles = new HashSet<>();
            Set<Point> startWallTiles = new HashSet<>();
            Set<Point> stopFloorTiles = new HashSet<>();
            Set<Point> stopWallTiles = new HashSet<>();

            for (Point point: start.getFloorTiles()) {
                startFloorTiles.add(point);
            }

            for (Point point: start.getWallTiles()) {
                startWallTiles.add(point);
            }

            for (Point point: stop.getFloorTiles()) {
                stopFloorTiles.add(point);
            }

            for (Point point: stop.getWallTiles()) {
                stopWallTiles.add(point);
            }

            for (Point adjPoint : getAdj(pathPoint)) {

                if (
                        !startFloorTiles.contains(adjPoint)
                        && !startWallTiles.contains(adjPoint)
                        && !stopFloorTiles.contains(adjPoint)
                        && !stopWallTiles.contains(adjPoint)
                        && !path.contains(adjPoint)
                        && !wallTiles.contains(adjPoint)
                ) {
                    wallTiles.add(adjPoint);
                }
            }
        }
    }

    private PSet getAdj(Point point) {
        PSet result = new PSet();

        for (int x = point.x - 1; x <= point.x + 1; x++) {
            for (int y = point.y - 1; y <= point.y + 1; y++) {
                result.add(new Point(x, y));
            }
        }

        return result;
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
