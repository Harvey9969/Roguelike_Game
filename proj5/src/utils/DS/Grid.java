package utils.DS;

import tileengine.TETile;
import tileengine.Tileset;
import utils.DS.recordlike.Dir;
import utils.DS.recordlike.Point;
import utils.DS.recordlike.Tile;

import java.util.*;

public class Grid {
    private final int SCRAPING_COST = 10;
    private final int TURN_COST = 5;

    private final int STRAIGHT_COST_SHORT = 1;
    private final int STRAIGHT_COST_LONG = 2;

    private final int STRAIGHT_BURST = 4;

    int width;
    int height;
    TETile[][] tiles;

    private class State implements Comparable<State> {
        Dir dir;
        Point loc;
        double priority;
        int streak;

        State(Dir dir, Point loc, double priority, int streak) {
            this.dir = dir;
            this.loc = loc;
            this.priority = priority;
            this.streak = Math.min(streak, STRAIGHT_BURST);
        }

        public void setPriority(double priority) {
            this.priority = priority;
        }

        public void setStreak(int streak) {
            this.streak = Math.min(streak, STRAIGHT_BURST);
        }

        @Override
        public int hashCode() {
           return
                   2 * dir.hashCode()
                   + 3 * loc.hashCode()
                   + 5 * streak;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof State uddaState) {
                return
                        uddaState.dir.equals(dir)
                        && uddaState.loc.equals(loc)
                        && uddaState.streak == streak;
            }

            return false;
        }

        @Override
        public int compareTo(State uddaState) {
            return Double.compare(priority, uddaState.priority);
        }
    }

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

    public void set(Tile tile) {
        int lx = tile.point().x ;
        int ly = tile.point().y;

        validateInBounds(lx, ly);

        tiles[lx][ly] = tile.tileType();
    }

    public TETile get(int x, int y) {
        validateInBounds(x, y);

        return tiles[x][y];
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

        if (start.equals(end)) {
            throw new IllegalArgumentException("Start cannot equal end");
        }

        State startState = new State(Dir.BLANK, start, 0, 0);

        PriorityQueue<State> fringe = new PriorityQueue<>();
        HashMap<State, Integer> distTo = new HashMap<>(); // no heuristic
        HashMap<State, State> edgeTo = new HashMap<>();

        fringe.add(startState);
        distTo.put(startState, 0);
        edgeTo.put(startState, null);

        HashSet<State> visited = new HashSet<>();

        State node = null;
        boolean pathFound = false;

        while (!fringe.isEmpty()) {
            node = fringe.poll();

            if (distTo.get(node) == null) {
                continue;
            }

            visited.add(node);

            if (node.loc.equals(end)) {
                pathFound = true;
                break;
            }

            for (
                    State adj: // dummy priorities & streaks
                    neighbors(node)
            ) {
                if (visited.contains(adj) || !isVisitable(adj.loc, start, end)) {
                    continue;
                }

                boolean straight = node.dir.equals(adj.dir);
                int stepCost = cost(adj, node.streak, straight);
                adj.setStreak(straight ? node.streak + 1 : 0);

                if (
                        !distTo.containsKey(adj)
                        || distTo.get(adj) > distTo.get(node) + stepCost
                ) {
                    distTo.put(adj, distTo.get(node) + stepCost);
                    edgeTo.put(adj, node);

                    fringe.removeIf(pqNode -> pqNode.equals(adj));

                    adj.setPriority(distTo.get(node) + stepCost + adj.loc.eDist(end));
                    fringe.add(adj);
                }
            }

        }

        if (!pathFound) {
            throw new RuntimeException("Path could not be found");
        }
        
        // node is now the end state
        LinkedList<Point> path = new LinkedList<>();
        while (node != null) {
            path.addFirst(node.loc);
            node = edgeTo.get(node);
        }

        return path;
    }

    private Iterable<State> neighbors(State node) {
        List<State> neighbors = new ArrayList<>();

        if (node.loc.x - 1 >= 0) {
            neighbors.add(new State(
                    Dir.WEST,
                    node.loc.left(),
                    Double.POSITIVE_INFINITY,
                    Integer.MAX_VALUE
            ));
        }

        if (node.loc.x + 1 < width) {
            neighbors.add(new State(
                    Dir.EAST,
                    node.loc.right(),
                    Double.POSITIVE_INFINITY,
                    Integer.MAX_VALUE
            ));
        }

        if (node.loc.y - 1 >= 0) {
            neighbors.add(new State(
                    Dir.SOUTH,
                    node.loc.down(),
                    Double.POSITIVE_INFINITY,
                    Integer.MAX_VALUE
            ));
        }

        if (node.loc.y + 1 < height) {
            neighbors.add(new State(
                    Dir.NORTH,
                    node.loc.up(),
                    Double.POSITIVE_INFINITY,
                    Integer.MAX_VALUE
            ));
        }

        return neighbors;
    }

    private int cost(State node, int parentSS, boolean straight) {
        if (isScraping(node.dir, node.loc)) {
            return SCRAPING_COST;
        } else if (straight && parentSS < STRAIGHT_BURST) {
            return STRAIGHT_COST_SHORT;
        } else if (straight) {
            return STRAIGHT_COST_LONG;
        } else {
            return TURN_COST;
        }
    }

    private boolean isScraping(Dir dir, Point point) {
        int x = point.x;
        int y = point.y;

        if (dir.isNorth()) {
            return
                    (isInBounds(x - 2, y) && get(x - 2, y).equals(Tileset.WALL))
                    || (isInBounds(x - 2, y + 1) && get(x - 2, y + 1).equals(Tileset.WALL))
                    || (isInBounds(x + 2, y) && get(x + 2, y).equals(Tileset.WALL))
                    || (isInBounds(x + 2, y + 1) && get(x + 2, y + 1).equals(Tileset.WALL));
        } else if (dir.isEast()) {
            return
                    (isInBounds(x, y - 2) && get(x, y - 2).equals(Tileset.WALL))
                    || (isInBounds(x + 1, y - 2) && get(x + 1, y - 2).equals(Tileset.WALL))
                    || (isInBounds(x, y + 2) && get(x, y + 2).equals(Tileset.WALL))
                    || (isInBounds(x + 1, y + 2) && get(x + 1, y + 2).equals(Tileset.WALL));
        } else if (dir.isSouth()) {
            return
                    (isInBounds(x - 2, y) && get(x - 2, y).equals(Tileset.WALL))
                    || (isInBounds(x - 2, y - 1) && get(x - 2, y - 1).equals(Tileset.WALL))
                    || (isInBounds(x + 2, y) && get(x + 2, y).equals(Tileset.WALL))
                    || (isInBounds(x + 2, y - 1) && get(x + 2, y - 1).equals(Tileset.WALL));
        } else if (dir.isWest()) {
            return
                    (isInBounds(x, y - 2) && get(x, y - 2).equals(Tileset.WALL))
                    || (isInBounds(x - 1, y - 2) && get(x - 1, y - 2).equals(Tileset.WALL))
                    || (isInBounds(x, y + 2) && get(x, y + 2).equals(Tileset.WALL))
                    || (isInBounds(x - 1, y + 2) && get(x - 1, y + 2).equals(Tileset.WALL));
        }

        return false;
    }

    private boolean isVisitable(Point point, Point start, Point end) {
        int startX = point.x - 1;
        int stopX = point.x + 1;
        int startY = point.y - 1;
        int stopY = point.y + 1;

        boolean liminal = point.equals(start) || point.equals(end);

        for (int x = startX; x <= stopX; x++) {
            for (int y = startY; y <= stopY; y++) {
                if (!isInBounds(x, y)) {
                    return false;
                }

                if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
                    return false;
                }

                if (!get(x, y).equals(Tileset.NOTHING)) {
                    if (liminal && get(x, y).equals(Tileset.FLOOR)) {
                        continue;
                    }
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

    public boolean isInBounds(int x, int y) {
        return x >= 0
                && y >= 0
                && x < width
                && y < height;
    }

    public boolean isInBounds(Point point) {
        return isInBounds(point.x, point.y);
    }
}
