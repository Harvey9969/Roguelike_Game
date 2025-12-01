package core.charecters;

import core.charecters.animation.Action;
import core.charecters.animation.State;
import core.screens.GameScreen;
import edu.princeton.cs.algs4.StdDraw;
import utils.DS.Grid;
import utils.DS.recordlike.Dir;

public abstract class GameCharacter {
    private static final int FRAME_DELAY = 10;

    public double x; // absolute pos
    public double y;

    public final Grid grid;

    public Actions Actions = new Actions();
    public class Actions {
        Idle IDLE;
        public Actions() {
            IDLE = new Idle();
        }

        class Idle extends Action {
            Idle() {
                super("IDLE");
            }
            @Override
            public void onFinish() {
                throw new UnsupportedOperationException();
            }
        }
    }

    public Dir facing;
    public Action relPos;

    private final State state;

    private int accruedTicks;
    private boolean locked;

    public String spriteFolder;

    public GameCharacter(double x, double y, boolean directional, Grid grid, String spriteFolder) {
        this.x = x + 0.5;
        this.y = y + 0.5;
        this.grid = grid;
        this.spriteFolder = spriteFolder;
        this.state = new State(spriteFolder);

        if (directional) {
            facing = Dir.NORTH;
        } else {
            facing = Dir.BLANK;
        }

        relPos = Actions.IDLE;
        state.loop(relPos.name, facing);
    }

    public void animate() {
        if (accruedTicks < FRAME_DELAY) {
            accruedTicks++;
        } else {
            if (relPos.uninterruptible) {
                locked = true;

                if (!state.playthrough(relPos.cycle, relPos.name, facing)) {
                    relPos.onFinish();

                    if (!relPos.isDeath) {
                        locked = false;
                        relPos = Actions.IDLE;
                    }
                }
            } else {
                state.loop(relPos.name, facing);

                relPos = Actions.IDLE;
            }

            accruedTicks = 0;
        }
    }

    public int snapX() {
        return snap(x);
    }

    public int snapY() {
        return snap(y);
    }

    private int snap (double v) {
        return (int) Math.floor(v);
    }

    public void draw(double cameraX, double cameraY) {
        int intX = snapX();
        int intY = snapY();

        int intCX = snap(cameraX);
        int intCY = snap(cameraY);

        if (!grid.isInBounds(intX - intCX, intY - intCY)) {
            // charecter out of view
            return;
        }

        StdDraw.picture(
                x - cameraX,
                y - cameraY,
                state.getSpriteFile()
        );
    }

    // princesses can interact w/ respond
    public void respond(char key) {
        if (locked) {
            return;
        }

        _respond(key);
    }

    public void act() {
        if (locked) {
            return;
        }

        _act();
    }

    public abstract void _respond(char key);

    public abstract void _act();
}
