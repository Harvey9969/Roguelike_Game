package core.charecters;

import utils.DS.Grid;

public class Princess extends Characters{
    public Princess(int x, int y, Grid grid, String sprite) {
        super(x, y, grid, sprite);
    }

    @Override
    public void respond(char key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void tick() {
        return;
    }
}
