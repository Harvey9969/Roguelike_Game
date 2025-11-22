package utils.DS;

import tileengine.Tileset;

import java.util.*;

public class Room extends TSet {
    private class WallNode {
        public WallNode prev;
        public Point point;
        public WallNode next;

        WallNode(WallNode prev, Point point, WallNode next) {
            this.prev = prev;
            this.point = point;
            this.next = next;
        }

        WallNode(Point point) {
            this.point = point;
            this.prev = this;
            this.next = this;
        }

        WallNode add(Point point) {
            WallNode newNode = new WallNode(this, point, this.next);
            this.next.prev = newNode;
            this.next = newNode;

            return newNode;
        }
    }

    public Point center;

    private int xStart;
    private int xStop;
    private int yStart;
    private int yStop;

    private final HashSet<Integer> xCorners;
    private final HashSet<Integer> yCorners;

    private final PSet floorTiles;
    private WallNode wallTiles;
    private int wallTileSize;

    public final PSet doors;

    public Room(Point center, int min, int max, Random random) {
        this.center = center;

        genStarts(min, max, random);

        xCorners = new HashSet<>(List.of(xStart, xStop));
        yCorners = new HashSet<>(List.of(yStart, yStop));

        floorTiles = new PSet();
        doors = new PSet();

        for (int y = yStart; y <= yStop; y++) {
            for (int x = xStart; x <= xStop; x++) {
                if (!xCorners.contains(x) && !yCorners.contains(y)) {
                    floorTiles.add(new Point(x, y));
                }
            }
        }

        wallTiles = new WallNode(new Point(xStart, yStart));
        wallTileSize++;

        for (int x = xStart + 1; x <= xStop; x++) {
            wallTiles = wallTiles.add(new Point(x, yStart));
            wallTileSize++;
        }

        for (int y = yStart + 1; y <= yStop; y++) {
            wallTiles = wallTiles.add(new Point(xStop, y));
            wallTileSize++;
        }

        for (int x = xStop - 1; x >= xStart; x--) {
            wallTiles = wallTiles.add(new Point(x, yStop));
            wallTileSize++;
        }

        for (int y = yStop - 1; y > yStart; y--) {
            wallTiles = wallTiles.add(new Point(xStart, y));
            wallTileSize++;
        }
    }

    private void genStarts(int min, int max, Random random) {
        if (min >= max) {
            throw new IllegalArgumentException("Min must be less than max");
        } else if (min <= 0) {
            throw new IllegalArgumentException("dimensions must be greater than 0");
        }

        int lw = random.nextInt(min/2, max/2);
        int rw = random.nextInt(min/2, max/2);
        int bh = random.nextInt(min/2, max/2);
        int th = random.nextInt(min/2, max/2);

        xStart = center.x - lw;
        xStop = center.x + rw;
        yStart = center.y - bh;
        yStop = center.y + th;

        if (
                xStart < 0
                        || xStop < 0
                        || yStart < 0
                        || yStop < 0
        ) {
            throw new RuntimeException("OOB room generated");
        }
    }

    private Iterable<WallNode> wallIter() {
        return new Iterable<WallNode>() {
            @Override
            public Iterator<WallNode> iterator() {
                return new Iterator<WallNode>() {
                    int index = 0;
                    WallNode curr = wallTiles;

                    @Override
                    public boolean hasNext() {
                        return index < wallTileSize;
                    }

                    @Override
                    public WallNode next() {
                        if (!hasNext()) {
                            throw new IndexOutOfBoundsException("No next");
                        }

                        WallNode temp = curr;
                        curr = curr.next;
                        index++;
                        return temp;
                    }
                };
            }
        };
    }

    public List<Point> getDoorCands() {
        List<Point> result = new ArrayList<>();

        for (WallNode node: wallIter()) {
            if (
                    (xCorners.contains(node.point.x) && yCorners.contains(node.point.y))
                    || (xCorners.contains(node.prev.point.x) && yCorners.contains(node.prev.point.y))
                    || (xCorners.contains(node.next.point.x) && yCorners.contains(node.next.point.y))
            ) {
                continue;
            }

            result.add(node.point);
        }

        return result;
    }

    public void placeDoor(Grid grid, Point door) {
        if (door == null || grid == null) {
            throw new IllegalArgumentException("Null arguments not permitted");
        } else if (wallTileSize <= 4) {
            throw new IllegalStateException("Room too degraded for door");
        } else if (!getDoorCands().contains(door)) {
            throw new IllegalArgumentException("Only valid door candidates can be placed");
        }

        while (!wallTiles.next.next.point.equals(door)) {
            wallTiles = wallTiles.next;
        }

        grid.set(new Tile(wallTiles.next.point, Tileset.NOTHING));

        grid.set(new Tile(wallTiles.next.next.point, Tileset.NOTHING));
        doors.add(wallTiles.next.next.point);

        grid.set(new Tile(wallTiles.next.next.next.point, Tileset.NOTHING));

        wallTiles.next = wallTiles.next.next.next.next;
        wallTiles.next.prev = wallTiles;

        wallTileSize -= 3;
    }

    @Override
    public Iterable<Point> getWallTiles() {
        return new Iterable<Point>() {
            @Override
            public Iterator<Point> iterator() {
                return new Iterator<Point>() {
                    final Iterator<WallNode> nodeIterator = wallIter().iterator();

                    @Override
                    public boolean hasNext() {
                        return nodeIterator.hasNext();
                    }

                    @Override
                    public Point next() {
                        return nodeIterator.next().point;
                    }
                };
            }
        };
    }

    @Override
    public Iterable<Point> getFloorTiles() {
        return floorTiles;
    }

}
