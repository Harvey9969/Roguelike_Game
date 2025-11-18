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

    public void add(TSet uddaTiles, boolean noOverwrite) {
        if (!noOverwrite) {
            for (Tile tile: uddaTiles) {
                set(tile);
            }
            return;
        }

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
        validateInBounds(start.x, start.y);
        validateInBounds(end.x, end.y);

        if (start == end) {
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
        HashSet<Point> visited = new HashSet<>();

        fringe.add(new PQNode(0, start));
        distTo.put(start, 0);
        edgeTo.put(start, null);
        directionTo.put(start, ' ');

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
                int cost = 2;

                if (dir == directionTo.get(node.loc)) {
                    cost = 1;
                }

                if (
                        visited.contains(point)
                        || !get(point).equals(Tileset.NOTHING)
                ) {
                    continue;
                }

                if (!distTo.containsKey(point)) {
                    distTo.put(point, distTo.get(node.loc) + cost);
                    edgeTo.put(point, node.loc);
                    directionTo.put(point, dir);

                    fringe.add(new PQNode(
                            distTo.get(point) + point.eDist(end),
                            point
                    ));
                } else if (distTo.get(point) > distTo.get(node.loc) + cost) {
                    distTo.put(point, distTo.get(node.loc) + cost);
                    edgeTo.put(point, node.loc);
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
        if (
                x < 0
                || y < 0
                || x >= width
                || y >= height
        ) {
            throw new ArrayIndexOutOfBoundsException("grid index OOB");
        }
    }
}
