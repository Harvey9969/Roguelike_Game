package core.charecters;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;
import utils.DS.Grid;
import utils.DS.RecordLike.Dir;

public abstract class Characters {
    private static final double d = .3;

    public double x; // absolute pos
    public double y;

    private final Grid grid;
    private final String sprite;
    private Dir facing;

    public Characters(double x, double y, Grid grid, String sprite) {
        this.x = x + 0.5;
        this.y = y + 0.5;
        this.grid = grid;
        this.sprite = "src/assets/sprites/" + sprite;
        this.facing = Dir.NORTH;
    }

    // TODO: animations

    public void moveUp() {
        if (!canMoveUp()) {
            throw new IllegalStateException("cannot move up");
        }

        facing = Dir.NORTH;
        y += d;
    }

    public void moveDown() {
        if (!canMoveDown()) {
            throw new IllegalStateException("cannot move down");
        }

        facing = Dir.SOUTH;
        y -= d;
    }

    public void moveLeft() {
        if (!canMoveLeft()) {
            throw new IllegalStateException("cannot move left");
        }

        facing = Dir.WEST;
        x -= d;
    }

    public void moveRight() {
        if (!canMoveRight()) {
            throw new IllegalStateException("cannot move right");
        }

        facing = Dir.EAST;
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
        int intX = snapX(x);
        int intY = snapY(y);

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

    private int snapX (double x) {
        return (int) Math.floor(x);
    }

    private int snapY (double y) {
        return (int) Math.floor(y);
    }

    public void draw(int cameraX, int cameraY) {
        int intX = (int) Math.floor(x);
        int intY = (int) Math.floor(y);

        if (!grid.isInBounds(intX - cameraX, intY - cameraY)) {
            // charecter out of view
            return;
        }

        StdDraw.picture(x - cameraX, y - cameraY, sprite);
    }

    public abstract void respond(char key);
    public abstract void tick();
}
