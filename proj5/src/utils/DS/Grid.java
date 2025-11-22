package utils.DS;

import tileengine.TETile;
import tileengine.Tileset;

import java.util.*;

public class Grid {
    int width;
    int height;
    int xOffset;
    int yOffset;
    TETile[][] tiles;

    public Grid(int width, int height) {
        validateDimension(width, height);

        this.width = width;
        this.height = height;

        tiles = new TETile[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    public Grid(Grid parent, Point bottomLeft, int width, int height) {
        validateDimension(width, height);
        validateContainment(bottomLeft, parent.width, parent.height, width, height);

        this.width = width;
        this.height = height;

        tiles = parent.tiles;

        xOffset = bottomLeft.x;
        yOffset = bottomLeft.y;
    }

    public void set(Tile tile) {
        int lx = tile.point().x ;
        int ly = tile.point().y;

        validateInBounds(lx, ly);

        tiles[lx + xOffset][ly + yOffset] = tile.tileType();
    }

    public TETile get(int x, int y) {
        validateInBounds(x, y);

        return tiles[x + xOffset][y + yOffset];
    }

    public TETile get(Point p) {
        return get(p.x, p.y);
    }

    public void add(Iterable<Tile> uddaTiles, boolean noOverwrite) {
        if (!noOverwrite) {
            for (Tile tile: uddaTiles) {
                set(tile);
            }
        } else {
            for (Tile tile: uddaTiles) {
                TETile nativeTile = get(tile.point().x, tile.point().y);

                if (nativeTile.equals(Tileset.NOTHING)) {
                    set(tile);
                } else {
                    throw new IllegalStateException(String.format(
                            "Cannot place %s at (%s) obstructed by %s",
                            tile.tileType(),
                            tile.point(),
                            nativeTile
                    ));
                }
            }
        }
    }

    public void add(TETile[][] uddaTiles) {
        if (
                uddaTiles.length == width
                && uddaTiles[0].length == height
        ) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    set(new Tile(new Point(x,y), uddaTiles[x][y]));
                }
            }
        } else {
            throw new IllegalArgumentException("Tile frames must match");
        }
    }

    public TETile[][] view(Point bottomLeft, int width, int height) {
        validateDimension(width, height);
        validateContainment(bottomLeft, this.width, this.height, width, height);

        TETile[][] result = new TETile[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                result[x][y] = get(x + bottomLeft.x, y + bottomLeft.y);
            }
        }

        return result;
    }

    public TETile[][] view() {
        TETile[][] result = new TETile[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                result[x][y] = get(x, y);
            }
        }

        return result;
    }

    public List<Point> astar(Point start, Point end) {
        final int TURN_COST = 5;

        final int STRAIGHT_COST_SHORT = 1;
        final int STRAIGHT_COST_LONG = 4;

        final int STRAIGHT_BURST = 3;

        validateInBounds(start.x, start.y);
        validateInBounds(end.x, end.y);
        if (start.equals(end)) {
            throw new IllegalArgumentException("Start cannot equal end");
        }

        record PQNode(double dist, Point loc) {}
        record adjNode(char dir, Point loc) {}
        PriorityQueue<PQNode> fringe = new PriorityQueue<>(new Comparator<PQNode>() {
            @Override
            public int compare(PQNode o1, PQNode o2) {
                return Double.compare(o1.dist, o2.dist);
            }
        });

        HashMap<Point, Integer> distTo = new HashMap<>();
        HashMap<Point, Point> edgeTo = new HashMap<>();
        HashMap<Point, Character> directionTo = new HashMap<>();
        HashMap<Point, Integer> straightStreak = new HashMap<>();
        HashSet<Point> visited = new HashSet<>();

        fringe.add(new PQNode(0, start));
        distTo.put(start, 0);
        edgeTo.put(start, null);
        directionTo.put(start, ' ');
        straightStreak.put(start, 0);

        int cost;
        PQNode node;
        boolean pathFound = false;

        while (!fringe.isEmpty()) {
            node = fringe.poll();
            visited.add(node.loc);

            if (node.loc.equals(end)) {
                pathFound = true;
                break;
            }

            List<adjNode> neighbors = new ArrayList<>();

            if (node.loc.x - 1 >= 0) {
                neighbors.add(new adjNode('W', node.loc.left()));
            }

            if (node.loc.x + 1 < width) {
                neighbors.add(new adjNode('E', node.loc.right()));
            }

            if (node.loc.y - 1 >= 0) {
                neighbors.add(new adjNode('S', node.loc.down()));
            }

            if (node.loc.y + 1 < height) {
                neighbors.add(new adjNode('N', node.loc.up()));
            }

            for (adjNode adj: neighbors) {
                Point point = adj.loc;
                char dir = adj.dir;

                if (visited.contains(point) || !isVisitable(point)) {
                    continue;
                }

                int ss = straightStreak.get(node.loc);

                if (isScraping(dir, point)) {
                    cost = 10;
                } else if (dir == directionTo.get(node.loc) && ss <= STRAIGHT_BURST) {
                    cost = STRAIGHT_COST_SHORT;
                    ss++;
                } else if (dir == directionTo.get(node.loc)) {
                    cost = STRAIGHT_COST_LONG;
                    ss++;
                } else {
                    cost = TURN_COST;
                    ss = 0;
                }

                if (!distTo.containsKey(point)) {
                    distTo.put(point, distTo.get(node.loc) + cost);
                    edgeTo.put(point, node.loc);
                    straightStreak.put(point, ss);
                    directionTo.put(point, dir);

                    fringe.add(new PQNode(
                            distTo.get(point) + point.eDist(end),
                            point
                    ));
                } else if (distTo.get(point) > distTo.get(node.loc) + cost) {
                    distTo.put(point, distTo.get(node.loc) + cost);
                    edgeTo.put(point, node.loc);
                    straightStreak.put(point, ss);
                    directionTo.put(point, dir);

                    Point finalPoint = point;
                    fringe.removeIf(pqNode -> pqNode.loc.equals(finalPoint));
                    fringe.add(new PQNode(
                            distTo.get(point) + point.eDist(end),
                            point
                    ));
                }
                }

        }

        if (!pathFound) {
            throw new RuntimeException("Path could not be found");
        }

        Point visitor = end;
        LinkedList<Point> path = new LinkedList<>();
        while (visitor != null) {
            path.addFirst(visitor);
            visitor = edgeTo.get(visitor);
        }

        return path;
    }

    private boolean isScraping(char dir, Point point) {
        int x = point.x;
        int y = point.y;

        if (dir == 'N') {
            return
                    (isInBounds(x - 2, y) && get(x - 2, y).equals(Tileset.WALL))
                    || (isInBounds(x - 2, y + 1) && get(x - 2, y + 1).equals(Tileset.WALL))
                    || (isInBounds(x + 2, y) && get(x + 2, y).equals(Tileset.WALL))
                    || (isInBounds(x + 2, y + 1) && get(x + 2, y + 1).equals(Tileset.WALL));
        } else if (dir == 'E') {
            return
                    (isInBounds(x, y - 2) && get(x, y - 2).equals(Tileset.WALL))
                    || (isInBounds(x + 1, y - 2) && get(x + 1, y - 2).equals(Tileset.WALL))
                    || (isInBounds(x, y + 2) && get(x, y + 2).equals(Tileset.WALL))
                    || (isInBounds(x + 1, y + 2) && get(x + 1, y + 2).equals(Tileset.WALL));
        } else if (dir == 'S') {
            return
                    (isInBounds(x - 2, y) && get(x - 2, y).equals(Tileset.WALL))
                    || (isInBounds(x - 2, y - 1) && get(x - 2, y - 1).equals(Tileset.WALL))
                    || (isInBounds(x + 2, y) && get(x + 2, y).equals(Tileset.WALL))
                    || (isInBounds(x + 2, y - 1) && get(x + 2, y - 1).equals(Tileset.WALL));
        } else if (dir == 'W') {
            return
                    (isInBounds(x, y - 2) && get(x, y - 2).equals(Tileset.WALL))
                    || (isInBounds(x - 1, y - 2) && get(x - 1, y - 2).equals(Tileset.WALL))
                    || (isInBounds(x, y + 2) && get(x, y + 2).equals(Tileset.WALL))
                    || (isInBounds(x - 1, y + 2) && get(x - 1, y + 2).equals(Tileset.WALL));
        }

        return false;
    }

    private boolean isVisitable(Point point) {
        int startX = point.x - 1;
        int stopX = point.x + 1;
        int startY = point.y - 1;
        int stopY = point.y + 1;

        for (int x = startX; x <= stopX; x++) {
            for (int y = startY; y <= stopY; y++) {
                if (
                        x == 0
                        || x == width - 1
                        || y == 0
                        || y == height - 1
                        || get(x, y).equals(Tileset.WALL)
                ) {
                    return false;
                }
            }
        }

        return true;
    }

    private static void validateDimension(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Dimensions must be greater than 0");
        }
    }

    private static void validateContainment(Point bottomLeft, int parentWidth, int parentHeight, int width, int height) {
        if (
                width + bottomLeft.x > parentWidth
                || height + bottomLeft.y > parentHeight
        ) {
            throw new ArrayIndexOutOfBoundsException("Sub grid clips out of grid");
        }
    }

    private void validateInBounds(int x, int y) {
        if (!isInBounds(x, y)) {
            throw new ArrayIndexOutOfBoundsException("grid index OOB");
        }
    }

    private boolean isInBounds(int x, int y) {
        return x >= 0
                && y >= 0
                && x < width
                && y < height;
    }
}
