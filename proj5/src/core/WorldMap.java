package core;

import core.charecters.Characters;
import core.charecters.Player;
import core.charecters.Princess;
import tileengine.Tileset;
import utils.DS.*;

import java.util.*;

public class WorldMap {
    public RoomGraph graph;
    public Grid grid;

    public int width;
    public int height;

    public Player player;
    public Set<Characters> charactersSet;

    public WorldMap(long seed, int width, int height) {
        Random random = new Random(seed);
        charactersSet = new HashSet<>();
        grid = new Grid(width, height);

        this.width = width;
        this.height = height;

        List<Room> rooms = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();

        poissonRoomPlacement(rooms, 6, 10, 10, random);
        euclideanMST(rooms, edges);
        graph = new RoomGraph(rooms, random);

        for (Room room: rooms) {
            grid.add(room, false);
        }

        for (Edge edge: edges) {
            Room r1 = edge.r1();
            Room r2 = edge.r2();

            graph.connect(r1, r2);

            double cost = Double.POSITIVE_INFINITY;
            Point r1d = null;
            Point r2d = null;

            for (Point r1dC: r1.getDoorCands()) {
                for (Point r2dC: r2.getDoorCands()) {
                    if (cost > doorCost(r1dC, r2dC, r1.doors, r2.doors)) {
                        r1d = r1dC;
                        r2d = r2dC;
                        cost = doorCost(r1dC, r2dC, r1.doors, r2.doors);
                    }
                }
            }

            // This throws if r1d / r2d is null
            r1.placeDoor(grid, r1d);
            r2.placeDoor(grid, r2d);

            grid.add(
                    new Path(
                            r1,
                            grid.astar(r1d, r2d),
                            r2,
                            random
                    ),
                    true
            );
        }

        graph.genDungeon(grid);
        Room startRoom = graph.indToRoom.get(graph.start);
        Room p1Room = graph.indToRoom.get(graph.p1);

        player = new Player(startRoom.center.x, startRoom.center.y, grid, "player.png");
        charactersSet.add(player);

        Princess p1 = new Princess(p1Room.center.x, p1Room.center.y, grid, "p1.png");
        charactersSet.add(p1);
    }

    private void poissonRoomPlacement(List<Room> rooms, int minDim, int maxDim, int minCorridor, Random random) {
        PSet candidateCenters = new PSet();
        PSet activePoints = new PSet();

        int R = maxDim + minCorridor;
        int bufferDist = (maxDim / 2) + 1;

        for (int y = bufferDist; y < height - bufferDist; y++) {
            for (int x = bufferDist; x < width - bufferDist; x++) {
                candidateCenters.add(new Point(x, y));
            }
        }

        Point p = candidateCenters.choose(random);
        Point v;
        PSet localPoints;

        candidateCenters = candidateCenters.filterM(p, R, false);
        activePoints.add(p);
        rooms.add(new Room(p, minDim, maxDim, random));

        while (!activePoints.isEmpty()) {
            p = activePoints.choose(random);
            localPoints = candidateCenters.filterM(p, (int) (1.5 * R), true);

            if (localPoints.isEmpty()) {
                activePoints.remove(p);
                continue;
            } else {
                v = localPoints.choose(random);
            }

            candidateCenters = candidateCenters.filterM(v, R, false);
            activePoints.add(v);
            rooms.add(new Room(v, minDim, maxDim, random));
        }
    }

    private void euclideanMST(List<Room> rooms, List<Edge> edges) {
        Double[] distanceTo = new Double[rooms.size()];
        Integer[] edgeTo = new Integer[rooms.size()];
        Boolean[] inMST = new Boolean[rooms.size()];

        distanceTo[0] = 0.0;
        edgeTo[0] = -1;
        inMST[0] = true;
        for (int i = 1; i < rooms.size(); i++) {
            distanceTo[i] = Double.POSITIVE_INFINITY;
            edgeTo[i] = -1;
            inMST[i] = false;
        }

        int room = 0;
        int vertices = 1;

        while (vertices < rooms.size()) {
            double minDist = Double.POSITIVE_INFINITY;
            int closest = Integer.MIN_VALUE;

            for (int i = 0; i < rooms.size(); i++) {
                if (inMST[i]) {
                    continue;
                }

                double roomToIDist = rooms.get(room).center.eDist(rooms.get(i).center);
                if (distanceTo[i] > roomToIDist) {
                    distanceTo[i] = roomToIDist;
                    edgeTo[i] = room;
                }

                if (minDist > distanceTo[i]) {
                    minDist = distanceTo[i];
                    closest = i;
                }
            }

            room = closest;
            inMST[closest] = true;
            vertices++;
        }

        for (int i = 0; i < rooms.size(); i++) {
            if (edgeTo[i] != -1) {
                edges.add(new Edge(rooms.get(i), rooms.get(edgeTo[i])));
            }
        }
    }

    private double doorCost(Point p1, Point p2, PSet doors1, PSet doors2) {
        double LAMBDA = 1;

        int spacing1 = 0;
        int spacing2 = 0;

        if (!doors1.isEmpty()) {
            spacing1 = doors1.closestMDist(p1);
        }

        if (!doors2.isEmpty()) {
            spacing2 = doors2.closestMDist(p2);
        }

        return p1.eDist(p2) - LAMBDA * (spacing1 + spacing2);
    }
}
