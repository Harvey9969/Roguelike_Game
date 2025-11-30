package core.game;

import core.Controller;
import core.charecters.Combatant;
import core.charecters.GameCharacter;
import core.charecters.Player;
import core.charecters.princess.Conversation;
import core.charecters.princess.DialogueContext;
import core.charecters.princess.DialogueNode;
import core.charecters.princess.Princess;
import core.screens.GameScreen;
import edu.princeton.cs.algs4.StdDraw;
import utils.DS.Grid;
import utils.DS.recordlike.Dir;
import utils.DS.recordlike.Point;

import java.util.*;

public class GameState {
    public Grid grid;

    public Player player;

    public Princess p1;
    public Princess p2;
    public Princess p3;

    public final Set<GameCharacter> charactersSet;
    private final Set<Point> princessTiles;

    public Conversation conversation;

    private boolean hasWon;
    private final Controller controller;

    public GameState(Grid grid, Controller controller) {
        this.grid = grid;
        this.controller = controller;

        charactersSet = new HashSet<>();
        princessTiles = new HashSet<>();
    }

    public void setP1 (Princess p) {
        p1 = p;
        addCharacter(p);
    }

    public void setP2 (Princess p) {
        p2 = p;
        addCharacter(p);
    }

    public void setP3 (Princess p) {
        p3 = p;
        addCharacter(p);
    }

    public void addPlayer(Player player) {
        this.player = player;
        addCharacter(player);
    }

    public void addCharacter(GameCharacter character) {
        charactersSet.add(character);
    }

    public void addPrincessTile(Point point) {
        princessTiles.add(point);
    }

    public boolean isPrincessTile(Point point) {
        return princessTiles.contains(point);
    }

    public void tickAll() {
        for (GameCharacter c: new ArrayList<>(charactersSet)) {
            if (c != player) {
                c.act();
            }

            c.animate();
        }

        if (hasWon && !inConversation()) {
            controller.gotoWin();
        }
    }

    public void win() {
        hasWon = true;
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

    public boolean inConversation() {
        return
                conversation != null
                && !conversation.isFinished();
    }

    public void startConversation(Princess princess, DialogueNode node, HUD hud) {
        conversation = new Conversation(node, new DialogueContext(princess, this, controller), hud);
    }

    public void handleConversation(char key) {
        conversation.handle(key);
    }
}
