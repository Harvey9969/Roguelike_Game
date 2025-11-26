package utils.DS.TileContainers;

import tileengine.Tileset;
import utils.DS.PSet;
import utils.DS.RecordLike.Point;
import utils.DS.RecordLike.Tile;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Path extends TSet {
    public Set<Point> floorTiles;
    public Set<Tile> wallTiles;

    private final Set<Point> wallPoints;

    private final Set<Point> wallPointsToPartition;
    private final Set<Point> outerWalls;
    private final Set<Point> innerWalls;
    private final Set<Point> cornerWalls;

    record Adj(boolean left, boolean right, boolean up, boolean down) {};

    public Path(Room start, List<Point> path, Room stop) {
        wallPoints = new HashSet<>();
        wallPointsToPartition = new HashSet<>();

        outerWalls = new HashSet<>();
        innerWalls = new HashSet<>();
        cornerWalls = new HashSet<>();

        wallTiles = new HashSet<>();
        floorTiles = new HashSet<>();

        Set<Point> blocked = new HashSet<>();

        for (Point point: start.getFloorTiles()) {
            blocked.add(point);
        }
        for (Tile tile: start.getWallTiles()) {
            blocked.add(tile.point());
            wallPoints.add(tile.point());
        }
        for (Point point: stop.getFloorTiles()) {
            blocked.add(point);
        }
        for (Tile tile: stop.getWallTiles()) {
            blocked.add(tile.point());
            wallPoints.add(tile.point());
        }

        for (Point pathPoint: path) {
            floorTiles.add(pathPoint);

            for (Point adjPoint : getAdj(pathPoint)) {
                if (
                        !blocked.contains(adjPoint)
                        && !path.contains(adjPoint)
                ) {
                    wallPoints.add(adjPoint);
                    wallPointsToPartition.add(adjPoint);
                }
            }
        }

        for (Point wallPoint: wallPointsToPartition) {
            boolean SW = path.contains(wallPoint.down())
                    || path.contains(wallPoint.left())
                    || path.contains(wallPoint.down().left());

            boolean NE = path.contains(wallPoint.up())
                    || path.contains(wallPoint.right())
                    || path.contains(wallPoint.up().right());

            if (SW && !NE) {
                innerWalls.add(wallPoint);
            } else if (!SW && NE) {
                outerWalls.add(wallPoint);
            } else {
                cornerWalls.add(wallPoint);
            }
        }

        for (Point wallPoint: outerWalls) {
            Adj surr = surroundingWalls(wallPoints, wallPoint);

            if (surr.up && surr.left && !surr.down && !surr.right) {
                wallTiles.add(new Tile(wallPoint, Tileset.LEFT_POLE));
            } else if (surr.up && surr.right && !surr.down && !surr.left) {
                wallTiles.add(new Tile(wallPoint, Tileset.LBC_WALL));
            } else if (surr.down && surr.left && !surr.up && !surr.right) {
                wallTiles.add(new Tile(wallPoint, Tileset.LEFT_WALL));
            } else if (surr.down && surr.right && !surr.up && !surr.left) {
                wallTiles.add(new Tile(wallPoint, Tileset.RIGHT_WALL));
            } else if (surr.left && surr.right) {
                wallTiles.add(new Tile(wallPoint, Tileset.UPPER_OUTER_WALL));
            } else if (surr.up && surr.down) {
                wallTiles.add(new Tile(wallPoint, Tileset.LEFT_WALL));
            }
        }

        for (Point wallPoint: innerWalls) {
            Adj surr = surroundingWalls(wallPoints, wallPoint);

//            wallTiles.add(new Tile(wallPoint, Tileset.FLOWER));

            if (surr.up && surr.left && !surr.down && !surr.right) {
                wallTiles.add(new Tile(wallPoint, Tileset.LEFT_POLE));
            } else if (surr.up && surr.right && !surr.down && !surr.left) {
                wallTiles.add(new Tile(wallPoint, Tileset.RIGHT_POLE));
            } else if (surr.down && surr.left && !surr.up && !surr.right) {
                wallTiles.add(new Tile(wallPoint, Tileset.RTC_WALL));
            } else if (surr.down && surr.right && !surr.up && !surr.left) {
                wallTiles.add(new Tile(wallPoint, Tileset.RIGHT_WALL));
            } else if (surr.down && surr.right && surr.left) {
                wallTiles.add(new Tile(wallPoint, Tileset.RTC_WALL));
            } else if (surr.up && surr.down) {
                wallTiles.add(new Tile(wallPoint, Tileset.RIGHT_WALL));
            } else if (surr.left && surr.right) {
                wallTiles.add(new Tile(wallPoint, Tileset.BOTTOM_WALL));
            } else if (surr.left) {
                wallTiles.add(new Tile(wallPoint, Tileset.LEFT_POLE));
            }
        }

        for (Point wallPoint: cornerWalls) {
            Adj surr = surroundingWalls(wallPoints, wallPoint);

//            wallTiles.add(new Tile(wallPoint, Tileset.FLOWER));

            if (surr.up && surr.left) {
                if (path.contains(wallPoint.right())) {
                    wallTiles.add(new Tile(wallPoint, Tileset.LEFT_POLE));
                } else {
                    wallTiles.add(new Tile(wallPoint, Tileset.RBC_WALL));
                }
            } else if (surr.down && surr.right) {
                if (path.contains(wallPoint.down().right())) {
                    wallTiles.add(new Tile(wallPoint, Tileset.LTC_WALL));
                } else {
                    wallTiles.add(new Tile(wallPoint, Tileset.RIGHT_WALL));
                }
            } else if (surr.left) {
                wallTiles.add(new Tile(wallPoint, Tileset.BOTTOM_WALL));
            } else {
                wallTiles.add(new Tile(wallPoint, Tileset.FLOWER));
            }
        }
    }

    private Adj surroundingWalls(Collection<Point> wallPoints, Point point) {
        boolean left = wallPoints.contains(point.left());
        boolean right = wallPoints.contains(point.right());
        boolean up = wallPoints.contains(point.up());
        boolean down = wallPoints.contains(point.down());

        return new Adj(left, right, up, down);
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
    public Iterable<Tile> getWallTiles() {
        return wallTiles;
    }

    @Override
    public Iterable<Point> getFloorTiles() {
        return floorTiles;
    }
}