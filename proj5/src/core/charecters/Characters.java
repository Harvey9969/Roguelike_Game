package core.charecters;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.State;
import tileengine.TETile;
import tileengine.Tileset;
import utils.DS.Grid;
import utils.DS.RecordLike.Dir;

public abstract class Characters {
    private static final double d = .3;
    private static final int FRAME_DELAY = 10;

    public double x; // absolute pos
    public double y;

    private final Grid grid;

    private final State state;
    private Dir facing;
    private boolean walking;
    private int accruedTicks;

    public Characters(double x, double y, Grid grid, String spriteFolder) {
        this.x = x + 0.5;
        this.y = y + 0.5;
        this.grid = grid;
        this.state = new State(spriteFolder);

        facing = Dir.NORTH;
        walking = false;

        state.setState("IDLE", facing);
    }

    public void tick(boolean isNPC) {
        if (isNPC) {
            act();
        }
        animate();
    }

    public void animate() {
        if (accruedTicks < FRAME_DELAY) {
            accruedTicks++;
        } else {
            if (walking) {
                state.setState("WALK", facing);
            } else {
                state.setState("IDLE", facing);
            }

            accruedTicks = 0;
        }

        // set flags false
        walking = false;
    }

    public abstract void respond(char key);

    public abstract void act();

    public void moveUp() {
        if (!canMoveUp()) {
            throw new IllegalStateException("cannot move up");
        }

        facing = Dir.NORTH;
        walking = true;
        y += d;
    }

    public void moveDown() {
        if (!canMoveDown()) {
            throw new IllegalStateException("cannot move down");
        }

        facing = Dir.SOUTH;
        walking = true;
        y -= d;
    }

    public void moveLeft() {
        if (!canMoveLeft()) {
            throw new IllegalStateException("cannot move left");
        }

        facing = Dir.WEST;
        walking = true;
        x -= d;
    }

    public void moveRight() {
        if (!canMoveRight()) {
            throw new IllegalStateException("cannot move right");
        }

        facing = Dir.EAST;
        walking = true;
        x += d;
    }

    public boolean canMoveUp() {
        return canMove(x, y + d);
    }

    public boolean canMoveDown() {
        return canMove(x, y - d);
    }

    public boolean canMoveLeft() {
        return canMove(x - d, y);
    }

    public boolean canMoveRight() {
        return canMove(x + d, y);
    }

    private boolean canMove(double x, double y) {
        int intX = snap(x);
        int intY = snap(y);

        double relativeX = x - intX;
        double relativeY = y - intY;

        if (!grid.isInBounds(intX, intY)) {
            return false;
        }

        TETile tile = grid.get(intX, intY);
        if (tile.equals(Tileset.FLOOR)) {
            return true;
        }

        return !tile.getCollider().collide(relativeX, relativeY);
    }

    public int snapX() {
        return (int) Math.floor(x);
    }

    public int snapY() {
        return (int) Math.floor(y);
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

        StdDraw.picture(x - cameraX, y - cameraY, state.getSpriteFile());
    }
}
