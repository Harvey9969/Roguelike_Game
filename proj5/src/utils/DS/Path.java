package utils.DS;

import java.util.List;

public class Path extends TSet {
    public PSet wallTiles;
    public PSet floorTiles;

    public Path(Room start, List<Point> path, Room stop) {
        wallTiles = new PSet();
        floorTiles = new PSet();

        for (Point pathPoint: path) {
            floorTiles.add(pathPoint);

            for (Point adjPoint : getAdj(pathPoint)) {
                if (
                        !start.floorTiles.contains(adjPoint)
                        && !start.wallTiles.contains(adjPoint)
                        && !stop.floorTiles.contains(adjPoint)
                        && !stop.wallTiles.contains(adjPoint)
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
