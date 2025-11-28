package core;

import core.charecters.*;
import utils.DS.Grid;
import utils.DS.RecordLike.Dir;
import utils.DS.RecordLike.Point;
import utils.DS.RoomGraph;
import utils.DS.TileContainers.Room;
import utils.RandomUtils;

import java.util.*;

public class EntityManager {
    private RoomGraph graph;
    private Grid grid;
    private Random random;

    private Set<Point> princessTiles;

    public Set<GameCharacter> charactersSet;
    public Player player;

    public EntityManager(RoomGraph graph, Grid grid, Random random) {
        this.graph = graph;
        this.grid = grid;
        this.random = random;

        princessTiles = new HashSet<>();
        charactersSet = new HashSet<>();

        graph.genDungeon(grid);

        Room startRoom = graph.indToRoom.get(graph.start);
        Room p1Room = graph.indToRoom.get(graph.p1);
        Room p2Room = graph.indToRoom.get(graph.p2);
        Room p3Room = graph.indToRoom.get(graph.end);

        for (Point p: p1Room.getFloorTiles()) {
            princessTiles.add(p);
        }

        for (Point p: p2Room.getFloorTiles()) {
            princessTiles.add(p);
        }

        for (Point p: p3Room.getFloorTiles()) {
            princessTiles.add(p);
        }

        player = new Player(startRoom.center.x, startRoom.center.y, grid,  this);
        charactersSet.add(player);

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
            charactersSet.add(
                    new Slime(room.center.x, room.center.y, grid, this, random)
            );
        }

        int skeletons = middle.size() / 2;
        for (Room room: RandomUtils.chooseRooms(random, middle, skeletons)) {
            charactersSet.add(
                    new Enemy(room.center.x, room.center.y, grid, "skeleton", this, 3, 1)
            );
        }

        int aliens = end.size() / 2;
        for (Room room: RandomUtils.chooseRooms(random, end, aliens)) {
            charactersSet.add(
                    new Enemy(room.center.x, room.center.y, grid, "alien", this, 5, 2)
            );
        }

    }

    public boolean isPrincessTile(Point point) {
        return princessTiles.contains(point);
    }

    public void tickAll() {
        for (GameCharacter c: new ArrayList<>(charactersSet)) {
            if (c instanceof Combatant combatant && combatant != player) {
                combatant.act();
            }

            c.animate();
        }
    }

    public Dir proximal(GameCharacter c1, GameCharacter c2) {
        Point c1Pos = new Point(c1.snapX(), c1.snapY());
        Point c2Pos = new Point(c2.snapX(), c2.snapY());

        List<Dir> dirList = List.of(Dir.NORTH, Dir.EAST, Dir.SOUTH, Dir.WEST);

        for (Dir dir: dirList) {
            if (c1Pos.move(dir).equals(c2Pos)) {
                return dir;
            }
        }

        return Dir.BLANK;
    }

    public void attack(Combatant attacker) {
        Point attackerPos = new Point(attacker.snapX(), attacker.snapY());
        Point attackedPos = attackerPos.move(attacker.facing);

        // hurt box -- in double coordinates integers are bottom-left tile corners
        int minX = Math.min(attackerPos.x, attackedPos.x);
        int maxX = Math.max(attackerPos.x, attackedPos.x) + 1;

        int minY = Math.min(attackerPos.y, attackedPos.y);
        int maxY = Math.max(attackerPos.y, attackedPos.y) + 1;

        for (GameCharacter c: charactersSet) {
            if (
                    c instanceof Combatant combatant
                    && combatant != attacker
                    && minX <= combatant.x && maxX >= combatant.x
                    && minY <= combatant.y && maxY >= combatant.y
            ) {
                combatant.hurt(attacker.facing, attacker.damage);
            }
        }
    }

    public void kill(GameCharacter character) {
        if (!charactersSet.contains(character)) {
            throw new IllegalArgumentException("Can only kill managed entities");
        }

        charactersSet.remove(character);
    }
}
