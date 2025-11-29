package core.charecters;

import core.game.GameState;
import utils.DS.Grid;
import utils.DS.recordlike.Dir;

public class Player extends Combatant {
    public static final int MAX_HEALTH = 5;

    public Player(int x, int y, Grid grid, GameState manager) {
        super(x, y, grid, "player", manager, MAX_HEALTH, 1);
    }

    @Override
    public void _respond(char key) {
        if ((key == 'w' || key == 'W') && canMove(Dir.NORTH)) {
            move(Dir.NORTH);
        } else if ((key == 'a' || key == 'A') && canMove(Dir.WEST)) {
            move(Dir.WEST);
        } else if ((key == 's' || key == 'S') && canMove(Dir.SOUTH)) {
            move(Dir.SOUTH);
        } else if ((key == 'd' || key == 'D') && canMove(Dir.EAST)) {
            move(Dir.EAST);
        } else if (key == ' ') {
            relPos = Actions.ATTACK;
        }
    }

    @Override
    public void _act() {
        throw new UnsupportedOperationException();
    }

}
