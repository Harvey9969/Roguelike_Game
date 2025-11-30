package core.game;

import core.Controller;
import core.charecters.*;
import core.charecters.princess.DialogueChoices;
import core.charecters.princess.DialogueNode;
import core.charecters.princess.Princess;
import edu.princeton.cs.algs4.StdDraw;
import utils.DS.Grid;
import utils.DS.PSet;
import utils.DS.RoomGraph;
import utils.DS.recordlike.Edge;
import utils.DS.recordlike.Point;
import utils.DS.tilecontainer.Path;
import utils.DS.tilecontainer.Room;
import utils.RandomUtils;

import java.util.*;

public class GameStateFactory {

    // START OF LOADING MAPS

    // END OF LOADING MAPS

    // START OF DIALOGUE CREATION

    private static final String prefix = "src/assets/";

    private static final DialogueNode p101Tree = new DialogueNode(
            "Ayaka",
            prefix + "p1/sobbing.png",
            prefix + "audio/p1/5.wav",
            "please don't just leave me here",
            List.of()
    );

    private static final DialogueNode p100Tree = new DialogueNode(
            "Ayaka",
            prefix + "p1/sniffle.png",
            prefix + "audio/p1/4.wav",
            "Are you going to take me with you",
            List.of(
                    new DialogueChoices("You'll just be extra baggage", p101Tree, null),
                    new DialogueChoices("Like I said I'm here for Rem", p101Tree, null)
            )
    );

    private static final DialogueNode p10Tree = new DialogueNode(
            "Ayaka",
            prefix + "p1/sniffle.png",
            prefix + "audio/p1/1.wav",
            "Really !?! But I'm her sister... Well this sword should help you save her",
            List.of(
                    new DialogueChoices("*Wordlessly take it*", p100Tree, ctx -> {ctx.gameState().player.damage++;}),
                    new DialogueChoices("I appreciate your understanding", p100Tree, ctx -> {ctx.gameState().player.damage++;})
            )
    );

    private static final DialogueNode p111Tree = new DialogueNode(
            "Ayaka",
            prefix + "p1/shy.png",
            prefix + "audio/p1/6.wav",
            "Will you be back ??",
            List.of(
                    new DialogueChoices("*Walk away without a word*", null, null),
                    new DialogueChoices("I'll be back for you", null, null)
            )
    );

    private static final DialogueNode p110Tree = new DialogueNode(
            "Ayaka",
            prefix + "p1/sniffle.png",
            prefix + "audio/p1/3.wav",
            "Don't leave yet!! Please take me with you",
            List.of(
                    new DialogueChoices("It safer for you to stay here, enemies can't enter these rooms", p111Tree, null)
            )
    );

    private static final DialogueNode p11Tree = new DialogueNode(
            "Ayaka",
            prefix + "p1/give.png",
            prefix + "audio/p1/2.wav",
            "Sure",
            List.of(
                    new DialogueChoices("Thank you!", p110Tree, null)
            )
    );

    private static final DialogueNode p1Tree = new DialogueNode(
            "Ayaka",
            prefix + "p1/beaming.png",
            prefix + "audio/p1/0.wav",
            "Are you here to save me?",
            List.of(
                    new DialogueChoices("No I'm here for Princess Rem", p10Tree, null),
                    new DialogueChoices("Yes I am a knight duty bound to your father the king. The weapon you have will help me save your other sisters. Can I have it?", p11Tree, ctx -> {ctx.gameState().player.damage++;})
            )
    );

    // END OF DIALOGUE CREATION

    private static record GG(Grid grid, RoomGraph graph, Random random) {};

    private static final int ROOM_MIN_DIM = 6;
    private static final int ROOM_MAX_DIM = 10;

    private static final int CORRIDOR_PADDING = 10;

    public static GameState createNew(int width, int height, long seed, Controller controller) {
        GG world = worldGen(width, height, seed);
        Grid grid = world.grid;
        RoomGraph graph = world.graph;
        Random random = world.random;

        GameState gameState = new GameState(grid, controller);

        graph.genDungeon(grid);

        Room startRoom = graph.indToRoom.get(graph.start);

        Room p1Room = graph.indToRoom.get(graph.p1);
        Room p2Room = graph.indToRoom.get(graph.p2);
        Room p3Room = graph.indToRoom.get(graph.end);

        for (Point p: p1Room.getFloorTiles()) {
            gameState.addPrincessTile(p);
        }

        for (Point p: p2Room.getFloorTiles()) {
            gameState.addPrincessTile(p);
        }

        for (Point p: p3Room.getFloorTiles()) {
            gameState.addPrincessTile(p);
        }

        gameState.addPlayer(new Player(startRoom.center.x, startRoom.center.y, grid, gameState));

        gameState.setP1(new Princess(p1Room.center.x, p1Room.center.y, grid, gameState, "p1", p1Tree));

        // enemy placement
        List<Room> beginning = new ArrayList<>();
        List<Room> middle = new ArrayList<>();
        List<Room> end = new ArrayList<>();

        for (RoomGraph.PartitionNode roomNode: graph.princessPartition()) {
            switch (roomNode.place()) {
                case RoomGraph.Place.START -> beginning.add(graph.indToRoom.get(roomNode.room()));
                case RoomGraph.Place.MIDDLE -> middle.add(graph.indToRoom.get(roomNode.room()));
                case RoomGraph.Place.END -> end.add(graph.indToRoom.get(roomNode.room()));
            }
        }

        int slimes = beginning.size() / 2;
        for (Room room: RandomUtils.chooseRooms(random, beginning, slimes)) {
            gameState.addCharacter(
                    new Slime(room.center.x, room.center.y, grid, gameState, random)
            );
        }

        int skeletons = middle.size() / 2;
        for (Room room: RandomUtils.chooseRooms(random, middle, skeletons)) {
            gameState.addCharacter(
                    new Enemy(room.center.x, room.center.y, grid, "skeleton", gameState, 3, 1)
            );
        }

        int aliens = end.size() / 2;
        for (Room room: RandomUtils.chooseRooms(random, end, aliens)) {
            gameState.addCharacter(
                    new Enemy(room.center.x, room.center.y, grid, "alien", gameState, 5, 2)
            );
        }

        return gameState;
    }

    private static GG worldGen(int width, int height, long seed) {
        boolean found = false;
        int attempts = 0;

        Grid grid = null;
        RoomGraph graph = null;

        Random random = new Random(seed);

        while (!found) {
            int MIN_ROOMS = (width * height) / (attempts < 250 ? 550 : 600);

            try {
                grid = new Grid(width, height);

                List<Room> rooms = new ArrayList<>();
                List<Edge> edges = new ArrayList<>();

                poissonRoomPlacement(width, height, rooms, random);

                if (rooms.size() < MIN_ROOMS && attempts < 500) {
                    throw new RuntimeException("Insufficient rooms");
                }

                euclideanMST(rooms, edges);
                graph = new RoomGraph(rooms, random);

                for (Room room: rooms) {
                    grid.add(room, true);
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
                                    r2
                            ),
                            false
                    );
                }

                found = true;

            } catch (RuntimeException e) {
                seed = random.nextLong();
                random = new Random(seed);
                attempts++;
            }
        }

        return new GG(grid, graph, random);
    }

    private static void poissonRoomPlacement(int width, int height, List<Room> rooms, Random random) {
        PSet candidateCenters = new PSet();
        PSet activePoints = new PSet();

        int R = ROOM_MAX_DIM + CORRIDOR_PADDING;
        int bufferDist = (ROOM_MAX_DIM / 2) + 1;

        for (int y = bufferDist; y < height - bufferDist; y++) {
            for (int x = bufferDist; x < width - bufferDist; x++) {
                candidateCenters.add(new Point(x, y));
            }
        }

        Point p = candidateCenters.choose(random);
        Point v;
        PSet localPoints;

        candidateCenters = candidateCenters.filterM(p, (int) (1.3 * R), false);
        activePoints.add(p);
        rooms.add(new Room(p, ROOM_MIN_DIM, ROOM_MAX_DIM, random));

        while (!activePoints.isEmpty()) {
            p = activePoints.choose(random);
            localPoints = candidateCenters.filterM(p, (int) (2.4 * R), true);

            if (localPoints.isEmpty()) {
                activePoints.remove(p);
                continue;
            } else {
                v = localPoints.choose(random);
            }

            candidateCenters = candidateCenters.filterM(v, (int) (1.3 * R), false);
            activePoints.add(v);
            rooms.add(new Room(v, ROOM_MIN_DIM, ROOM_MAX_DIM, random));
        }
    }

    private static void euclideanMST(List<Room> rooms, List<Edge> edges) {
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

    private static double doorCost(Point p1, Point p2, PSet doors1, PSet doors2) {
        double LAMBDA = 2;

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
