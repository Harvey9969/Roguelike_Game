package core.charecters;

import utils.DS.Grid;

public class Player extends Characters {
    public Player(int x, int y, Grid grid, String playerFolder) {
        super(x, y, grid, playerFolder);
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
    public void act() {
        throw new UnsupportedOperationException();
    }

}
