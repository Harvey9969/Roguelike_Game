package core.game;

import core.Controller;
import core.charecters.*;
import core.charecters.princess.DialogueChoices;
import core.charecters.princess.DialogueNode;
import core.charecters.princess.Princess;
import core.game.saving.CharacterData;
import core.game.saving.PointData;
import core.game.saving.SaveData;
import core.game.saving.SaveFactory;
import tileengine.TETile;
import utils.DS.Grid;
import utils.DS.PSet;
import utils.DS.RoomGraph;
import utils.DS.recordlike.Edge;
import utils.DS.recordlike.Point;
import utils.DS.recordlike.Tile;
import utils.DS.tilecontainer.Path;
import utils.DS.tilecontainer.Room;
import utils.RandomUtils;

import java.util.*;

public class GameStateFactory {

    private static final Map<String, TETile> GRID_MAPPING = SaveFactory.getGridMapping();


    // START OF DIALOGUE CREATION

    private static final String prefix = "src/assets/";

    // P1 DIALOGUE

    private static final DialogueNode p10cTree = new DialogueNode(
            "Ayaka",
            prefix + "p1/5.png",
            prefix + "audio/p1/5.wav",
            "please don't just leave me here",
            List.of()
    );

    private static final DialogueNode p10bTree = new DialogueNode(
            "Ayaka",
            prefix + "p1/3.png",
            prefix + "audio/p1/3.wav",
            "Are you going to take me with you",
            List.of(
                    new DialogueChoices("You'll just be extra baggage", p10cTree, null),
                    new DialogueChoices("Like I said I'm here for Rem", p10cTree, null)
            )
    );

    private static final DialogueNode p10aTree = new DialogueNode(
            "Ayaka",
            prefix + "p1/1.png",
            prefix + "audio/p1/1.wav",
            "Really !?! But I'm her sister... Well this sword should help you save her",
            List.of(
                    new DialogueChoices("*Wordlessly take it*", p10bTree, ctx -> {
                        ctx.p().relPos = ctx.p().Actions.GIVE;
                        ctx.gameState().player.damage++;
                    }),
                    new DialogueChoices("I appreciate your understanding", p10bTree, ctx -> {
                        ctx.p().relPos = ctx.p().Actions.GIVE;
                        ctx.gameState().player.damage++;
                    })
            )
    );

    private static final DialogueNode p11cTree = new DialogueNode(
            "Ayaka",
            prefix + "p1/6.png",
            prefix + "audio/p1/6.wav",
            "Will you be back ??",
            List.of(
                    new DialogueChoices("*Walk away without a word*", null, null),
                    new DialogueChoices("I'll be back for you", null, null)
            )
    );

    private static final DialogueNode p11bTree = new DialogueNode(
            "Ayaka",
            prefix + "p1/4.png",
            prefix + "audio/p1/4.wav",
            "Don't leave yet!! Please take me with you",
            List.of(
                    new DialogueChoices("It safer for you to stay here, enemies can't enter these rooms", p11cTree, null)
            )
    );

    private static final DialogueNode p11aTree = new DialogueNode(
            "Ayaka",
            prefix + "p1/2.png",
            prefix + "audio/p1/2.wav",
            "Sure",
            List.of(
                    new DialogueChoices("Thank you!", p11bTree, null)
            )
    );

    private static final DialogueNode p1Tree = new DialogueNode(
            "Ayaka",
            prefix + "p1/0.png",
            prefix + "audio/p1/0.wav",
            "Are you here to save me?",
            List.of(
                    new DialogueChoices("No I'm here for Princess Rem", p10aTree,
                            ctx -> {
                                ctx.gameState().good = false;
                            }
                            ),
                    new DialogueChoices("Yes I am a knight duty bound to your father the king. The weapon you have will help me save your other sisters. Can I have it?", p11aTree, ctx -> {
                        ctx.p().relPos = ctx.p().Actions.GIVE;
                        ctx.gameState().player.damage++;
                        ctx.gameState().good = true;
                    })
            )
    );

    // P2 DIALOGUE

    private static final DialogueNode p20bremerge001Tree = new DialogueNode(
            "Makima",
            prefix + "p2/12.png",
            prefix + "audio/p2/12.wav",
            "May I go with you now? You're not just going to leave me behind... right?",
            List.of(
                    new DialogueChoices("*Walk away without a word*", null, null)
            )
    );

    private static final DialogueNode p20bremerge0000Tree = new DialogueNode(
            "Makima",
            prefix + "p2/13.png",
            prefix + "audio/p2/13.wav",
            "I’ll find a way out -- and when I do, I’ll report everything about you.",
            List.of(
                    new DialogueChoices("* keep going *", null, null)
            )
    );

    private static final DialogueNode p20bremerge0001Tree = new DialogueNode(
            "Makima",
            prefix + "p2/14.png",
            prefix + "audio/p2/14.wav",
            "...",
            List.of()
    );

    private static final DialogueNode p20bremerge000Tree = new DialogueNode(
            "Makima",
            prefix + "p2/11.png",
            prefix + "audio/p2/11.wav",
            "I’m going to tell my father. You’ll regret even thinking you could get away with this.",
            List.of(
                    new DialogueChoices("Where are you going", p20bremerge0000Tree, null),
                    new DialogueChoices("You'll never get out of this maze", p20bremerge0001Tree, null)
            )
    );

    private static final DialogueNode p20bremerge00Tree = new DialogueNode(
            "Makima",
            prefix + "p2/10.png",
            prefix + "audio/p2/10.wav",
            "No, uhh- its not a sword but a decorative family heirloom so don’t you dare try to take it.",
            List.of(
                    new DialogueChoices("[take the sword]", p20bremerge000Tree, ctx -> { ctx.gameState().player.damage++; }),
                    new DialogueChoices("[don't take the sword]", p20bremerge001Tree, null)
            )
    );

    private static final DialogueNode p20bremerge01Tree = new DialogueNode(
            "Makima",
            prefix + "p2/9.png",
            prefix + "audio/p2/9.wav",
            "Please... don’t take it... It’s mine. Come on, just be reasonable, you know its not yours",
            List.of(
                    new DialogueChoices("[take the sword]", p20bremerge000Tree, ctx -> { ctx.gameState().player.damage++; }),
                    new DialogueChoices("[don't take the sword]", p20bremerge001Tree, null)
            )
    );

    private static final DialogueNode p20bremerge0Tree = new DialogueNode(
            "Makima",
            prefix + "p2/7.png",
            prefix + "audio/p2/7.wav",
            "No, that’s not allowed. Know your place, and don’t forget who’s giving the orders.",
            List.of(
                    new DialogueChoices("What is this -- is it a sword", p20bremerge00Tree, null),
                    new DialogueChoices("Is this a sword", p20bremerge01Tree, null)
            )
    );

    private static final DialogueNode p20bremerge1Tree = new DialogueNode(
            "Makima",
            prefix + "p2/8.png",
            prefix + "audio/p2/8.wav",
            "...",
            List.of(
                    new DialogueChoices("What is this -- is it a sword", p20bremerge00Tree, null),
                    new DialogueChoices("Is this a sword", p20bremerge01Tree, null)
            )
    );

    private static final DialogueNode p20aremergeTree = new DialogueNode(
            "Makima",
            prefix + "p2/5.png",
            prefix + "audio/p2/5.wav",
            "Be reasonable and save me. I have something for you if you do",
            List.of(
                    new DialogueChoices("You've piqued my interest -- I'll take that something", p20bremerge0Tree, null),
                    new DialogueChoices("* push her back *", p20bremerge1Tree, null)
            )
    );


    private static final DialogueNode p201Tree = new DialogueNode(
            "Makima",
            prefix + "p2/3.png",
            prefix + "audio/p2/3.wav",
            " Please... help me as well... I'm begging you",
            List.of(
                    new DialogueChoices("I'm only here for Rem -- nothing personal", p20aremergeTree, null)
                    )
    );
    private static final DialogueNode p200Tree = new DialogueNode(
            "Makima",
            prefix + "p2/2.png",
            prefix + "audio/p2/2.wav",
            "What is that supposed to mean? I hardly understand, what you mean by that",
            List.of(
                    new DialogueChoices("My words were perfectly clear, if you don't understand that's on you", p20aremergeTree, null)
            )
    );

    private static final DialogueNode p20Tree = new DialogueNode(
            "Makima",
            prefix + "p2/0.png",
            prefix + "audio/p2/0.wav",
            "I'm Princess Makima, daughter of the king and famed for my beauty. Don't lie, everyone knows me. Now come on, get me out of here.",
            List.of(
                    new DialogueChoices("I'm not going to save you", p200Tree, null),
                    new DialogueChoices("So you are not Princess Rem then...", p201Tree, null)
            )
    );

    private static final DialogueNode p21c1Tree = new DialogueNode(
            "Makima",
            prefix + "p2/16.png",
            prefix + "audio/p2/16.wav",
            "Then.. t-take me with you... If you want this weapon, you’ll have to take me too.",
            List.of(
                    new DialogueChoices("It's safer for you here, believe me. Stay here and I'll be back for you", null, null)
            )
    );

    private static final DialogueNode p21c0Tree = new DialogueNode(
            "Makima",
            prefix + "p2/15.png",
            prefix + "audio/p2/15.wav",
            "Then give it back, you're not allowed to just take things",
            List.of(
                    new DialogueChoices("As you insist. Stay here and I'll be back for you", null, null)
            )
    );

    private static final DialogueNode p21cTree = new DialogueNode(
            "Makima",
            prefix + "p2/6.png",
            prefix + "audio/p2/6.wav",
            "Here you go. Don't break it.",
            List.of(
                    new DialogueChoices("I think my weapon is better", p21c0Tree, null),
                    new DialogueChoices("I like this new sword", p21c1Tree,
                            ctx -> {
                        ctx.gameState().player.damage++;
                    })
            )
    );

    private static final DialogueNode p21bTree = new DialogueNode(
            "Makima",
            prefix + "p2/4.png",
            prefix + "audio/p2/4.wav",
            "Well are you going to talk or just stay silent",
            List.of(
                    new DialogueChoices("The weapon you have with you can help me save your sisters... may I see it", p21cTree,
                            ctx -> {
                        ctx.p().relPos = ctx.p().Actions.GIVE;
                    })
            )
    );

    private static final DialogueNode p21aTree = new DialogueNode(
            "Makima",
            prefix + "p2/1.png",
            prefix + "audio/p2/1.wav",
            "Took you long enough. I haven't had dinner yet and I need to get ready for bed. Honestly, you should be grateful I'm still waiting",
            List.of(
                    new DialogueChoices("...", p21bTree, null)
            )
    );

    private static final DialogueNode p2TreeGood = new DialogueNode(
            "Makima",
            prefix + "p2/0.png",
            null,
            "...",
            List.of(
                    new DialogueChoices("You must be the other princess", p21aTree, null)
            )
    );

    private static final DialogueNode p2TreeBad = new DialogueNode(
            "Makima",
            prefix + "p2/0.png",
            null,
            "...",
            List.of(
                    new DialogueChoices("Who are you?", p20Tree, null)
            )
    );

    // P3 DIALOGUE

    private static final DialogueNode p3011remergeTree = new DialogueNode(
            "Rem",
            prefix + "p3/smiling.png",
            prefix + "audio/p3/9.wav",
            "Who do you think built this maze??? There is only one reason I do anything. Your words mean nothing, I will persuade you... even if it takes forever ",
            List.of()
    );

    private static final DialogueNode p30111Tree = new DialogueNode(
            "Rem",
            prefix + "p3/food.png",
            prefix + "audio/p3/8.wav",
            "How about we don't... we're not going anywhere. We have all we need here",
            List.of(
                    new DialogueChoices("What do you mean", p3011remergeTree, ctx -> {ctx.gameState().win();})
            )
    );

    private static final DialogueNode p30110Tree = new DialogueNode(
            "Rem",
            prefix + "p3/worried.png",
            prefix + "audio/p3/7.wav",
            "That kind of moralizing makes me worried. Forget about them or forget about me... your choice",
            List.of(
                    new DialogueChoices("You are the one making me worried", p3011remergeTree, ctx -> {ctx.gameState().win();})
            )
    );

    private static final DialogueNode p3011Tree = new DialogueNode(
            "Rem",
            prefix + "p3/smiling.png",
            prefix + "audio/p3/6.wav",
            "It’s fine. We don’t have to return for them. It’ll be just the two of us here forever",
            List.of(
                    new DialogueChoices("But Rem, they are your sisters", p30110Tree, null),
                    new DialogueChoices("I like the way you think -- lets get out of this place", p30111Tree, null)
            )
    );

    private static final DialogueNode p3010Tree = new DialogueNode(
            "Rem",
            prefix + "p3/salute.png",
            prefix + "audio/p3/5.wav",
            "Go back and get them, go and get us our happily ever after. I salute you soldier -- GODSPEED",
            List.of(
                    new DialogueChoices("Unfortunately, I found Makima dead at the hands of a slime by the time I got there. I will save the others though", null, ctx -> {ctx.gameState().win();}),
                    new DialogueChoices("You got it", null, ctx -> {ctx.gameState().win();})
            )
    );

    private static final DialogueNode p301Tree = new DialogueNode(
            "...",
            prefix + "p3/worried.png",
            null,
            "Choose Rem's response",
            List.of(
                    new DialogueChoices("Rem: <Go back and save them>", p3010Tree, null),
                    new DialogueChoices("Rem: <Abandon them>", p3011Tree, null)
            )
    );

    private static final DialogueNode p300Tree = new DialogueNode(
            "Rem",
            prefix + "p3/smiling.png",
            prefix + "audio/p3/4.wav",
            "It’s fine! We don't need them, we have everything we need here. We can stay here forever... isn’t that lovely?",
            List.of()
    );

    private static final DialogueNode p30bTree = new DialogueNode(
            "Rem",
            prefix + "p3/sniffling.png",
            prefix + "audio/p3/2.wav",
            "Did you see my other sisters? I... I can’t stop thinking about them...",
            List.of(
                    new DialogueChoices("Ye- I mean, no... I'm sorry, I didn't", p300Tree, ctx -> {ctx.gameState().win();}),
                    new DialogueChoices("Yes I ran into the other 2 on the way here", p301Tree, null)
            )
    );

    private static final DialogueNode p30aTree = new DialogueNode(
            "Rem",
            prefix + "p3/nervousSmile.png",
            prefix + "audio/p3/0.wav",
            "You are finally here... I felt so alone without you",
            List.of(
                    new DialogueChoices("I finally found you, lets go home", p30bTree, null)
            )
    );

    private static final DialogueNode p31bTree = new DialogueNode(
            "Rem",
            prefix + "p3/nervousSmile.png",
            prefix + "audio/p3/3.wav",
            "Thank you... really. I—I don’t know what I would’ve done without you",
            List.of()
    );

    private static final DialogueNode p31aTree = new DialogueNode(
            "Rem",
            prefix + "p3/sniffling.png",
            prefix + "audio/p3/1.wav",
            "Are you here to save me? I’ve been so scared",
            List.of(
                    new DialogueChoices("I am a knight of your father, the king. He will be glad to have you back safely", p31bTree, ctx -> {ctx.gameState().win();})
            )
    );

    private static final DialogueNode p3TreeGood = new DialogueNode(
            "Rem",
            prefix + "p3/shy.png",
            null,
            "...",
            List.of(
                    new DialogueChoices("You must be the last princess", p31aTree, null)
            )
    );

    private static final DialogueNode p3TreeBad = new DialogueNode(
            "Rem",
            prefix + "p3/shy.png",
            null,
            "...",
            List.of(
                    new DialogueChoices("MY REM!!!", p30aTree, null)
            )
    );

    // END OF DIALOGUE CREATION

    // SERVING DIALOGUE

    public static DialogueNode getDialogue(int pID, boolean good) {
        if (pID == 0) {
            return p1Tree;
        } else if (pID == 1 && good) {
            return p2TreeGood;
        } else if (pID == 1) {
            return p2TreeBad;
        } else if (pID == 2 && good) {
            return p3TreeGood;
        } else if (pID == 2) {
            return p3TreeBad;
        }

        throw new IllegalArgumentException("Invalid pID");
    }


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

        gameState.addPlayer(new Player(startRoom.center.x, startRoom.center.y, 5, 1, grid, gameState));

        gameState.addP1(new Princess(p1Room.center.x, p1Room.center.y, grid, gameState, "p1", 0));
        gameState.addP2(new Princess(p2Room.center.x, p2Room.center.y, grid, gameState, "p2", 1));
        gameState.addP3(new Princess(p3Room.center.x, p3Room.center.y, grid, gameState, "p3", 2));

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
                    new Slime(room.center.x, room.center.y, 2, grid, gameState, random)
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

    public static GameState loadNew(SaveData save, Controller controller) {
        int width = save.width();
        int height = save.height();

        Grid grid = new Grid(width, height);
        GameState gameState = new GameState(grid, controller);

        String[][] gridRepr = save.gridData();

        // fill grid
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid.set(new Tile(
                        new Point(x, y),
                        GRID_MAPPING.get(gridRepr[x][y])
                ));
            }
        }

        // add princess tiles
        for (PointData point: save.princessTiles()) {
            gameState.addPrincessTile(new Point(point.x(), point.y()));
        }

        // add charecters
        for (CharacterData cData: save.characterData()) {
            if (cData.type().equals("PLAYER")) {
                gameState.addPlayer(new Player(
                        cData.x(),
                        cData.y(),
                        cData.health(),
                        cData.damage(),
                        grid,
                        gameState
                ));
            } else if (cData.type().equals("PRINCESS") && cData.pNum() == 0) {
                gameState.addP1(new Princess(
                        cData.x(),
                        cData.y(),
                        grid,
                        gameState,
                        cData.spritePath(),
                        0
                ));
            } else if (cData.type().equals("PRINCESS") && cData.pNum() == 1) {
                gameState.addP2(new Princess(
                        cData.x(),
                        cData.y(),
                        grid,
                        gameState,
                        cData.spritePath(),
                        1
                ));
            } else if (cData.type().equals("PRINCESS") && cData.pNum() == 2) {
                gameState.addP3(new Princess(
                        cData.x(),
                        cData.y(),
                        grid,
                        gameState,
                        cData.spritePath(),
                        2
                ));
            } else if (cData.type().equals("SLIME")) {
                gameState.addCharacter(new Slime(
                        cData.x(),
                        cData.y(),
                        cData.health(),
                        grid,
                        gameState,
                        new Random(0)
                ));
            } else if (cData.type().equals("ENEMY")) {
                gameState.addCharacter(new Enemy(
                        cData.x(),
                        cData.y(),
                        grid,
                        cData.spritePath(),
                        gameState,
                        cData.health(),
                        cData.damage()
                ));
            }
        }

        // set choice
        gameState.good = save.playerChoice();

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
