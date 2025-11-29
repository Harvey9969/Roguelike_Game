package core.charecters;

import core.game.GameState;
import utils.DS.Grid;
import utils.DS.recordlike.Dir;

import java.util.Random;

public class Slime extends Combatant{
    Random random;

    public Slime(double x, double y, Grid grid, GameState manager, Random random) {
        super(x, y, grid, "slime", manager, 2, 0);
        this.random = random;
    }

    @Override
    public void _respond(char key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void _act() {
        Dir dir = Dir.choose(random);

        if (random.nextBoolean() && canMove(dir)) {
            move(dir);
        }
    }
}
