package core.charecters;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.Tileset;
import utils.DS.Grid;

public abstract class Characters {
    public int x;
    public int y;

    private Grid grid;
    private String sprite;

    public Characters(int x, int y, Grid grid, String sprite) {
        this.x = x;
        this.y = y;
        this.grid = grid;
        this.sprite = "src/assets/sprites/" + sprite;
    }

    public boolean canMoveUp() {
        return canMove(x, y + 1);
    }

    public boolean canMoveDown() {
        return canMove(x, y - 1);
    }

    public boolean canMoveLeft() {
        return canMove(x - 1, y);
    }

    public boolean canMoveRight() {
        return canMove(x + 1, y);
    }

    public void moveUp() {
        if (!canMoveUp()) {
            throw new IllegalStateException("Cannot move up in position");
        }

        y++;
    }

    public void moveDown() {
        if (!canMoveDown()) {
            throw new IllegalStateException("Cannot move down in position");
        }

        y--;
    }

    public void moveLeft() {
        if (!canMoveLeft()) {
            throw new IllegalStateException("Cannot move left in position");
        }

        x--;
    }

    public void moveRight() {
        if (!canMoveRight()) {
            throw new IllegalStateException("Cannot move right in position");
        }

        x++;
    }

    public abstract void respond(char key);
    public abstract void tick();

    public void draw(int cameraX, int cameraY) {
        if (!grid.isInBounds(x - cameraX, y - cameraY)) {
            // charecter out of view
            return;
        }

        double adjustedX = (((double) x) - cameraX) + 0.5;
        double adjustedY = (((double) y) - cameraY) + 0.5;
        StdDraw.picture(adjustedX, adjustedY, sprite);
    }

    private boolean canMove(int x, int y) {
        return
                grid.isInBounds(x, y)
                && grid.get(x, y).equals(Tileset.FLOOR);
    }
}
