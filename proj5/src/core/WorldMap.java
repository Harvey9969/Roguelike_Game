package core;

import tileengine.Tileset;
import utils.DS.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldMap {
    public RoomGraph graph;
    public Grid grid;

    public int width;
    public int height;

    public WorldMap(long seed, int width, int height) {
        Random random = new Random(seed);
        grid = new Grid(width, height);

        this.width = width;
        this.height = height;

        List<Room> rooms = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();

        poissonRoomPlacement(rooms, 6, 10, 3, random);
        euclideanMST(rooms, edges);
        graph = new RoomGraph(rooms);

        for (Room room: rooms) {
            grid.add(room, true);
        }

        for (Edge edge: edges) {
            Room r1 = edge.r1();
            Room r2 = edge.r2();

            graph.connect(r1, r2);

            Point[] closest = r1.wallTiles.closestE(r2.wallTiles);
            Point r1C = closest[0];
            Point r2C = closest[1];

            for (Point point: List.of(r1C, r1C.left(), r1C.right(), r1C.down(), r1C.up())) {
                if (r1.wallTiles.remove(point)) {
                    grid.set(new Tile(point, Tileset.NOTHING));
                }
            }

            for (Point point: List.of(r2C, r2C.left(), r2C.right(), r2C.down(), r2C.up())) {
                if (r2.wallTiles.remove(point)) {
                    grid.set(new Tile(point, Tileset.NOTHING));
                }
            }

            // TODO: If this fails retry with new portal
            grid.add(
                    new Path(
                            r1,
                            grid.astar(r1C, r2C),
                            r2
                    ),
                    true
            );
        }
    }

    private void poissonRoomPlacement(List<Room> rooms, int minDim, int maxDim, int minCorridor, Random random) {
        PSet candidateCenters = new PSet();
        PSet activePoints = new PSet();

        int R = maxDim + minDim + minCorridor;
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
            localPoints = candidateCenters.filterM(p, (int) (1.5*R), true);

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
}
