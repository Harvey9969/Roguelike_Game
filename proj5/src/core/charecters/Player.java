package core.charecters;

import utils.DS.Grid;

public class Player extends Characters {
    public Player(int x, int y, Grid grid, String sprite) {
        super(x, y, grid, sprite);
    }

    @Override
    public void respond(char key) {
        if (key == 'w' && canMoveUp()) {
            moveUp();
        } else if (key == 'a' && canMoveLeft()) {
            moveLeft();
        } else if (key == 's' && canMoveDown()) {
            moveDown();
        } else if (key == 'd' && canMoveRight()) {
            moveRight();
        }
    }

    @Override
    public void tick() {
        throw new UnsupportedOperationException();
    }
}
