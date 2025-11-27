package core.charecters;

import core.EntityManager;
import utils.DS.Grid;
import utils.DS.RecordLike.Dir;

public class Player extends Combatant {
    public Player(int x, int y, Grid grid, EntityManager manager) {
        super(x, y, grid, "player", manager, 5, 1);
    }

    @Override
    public void _respond(char key) {
        if (key == 'w' && canMove(Dir.NORTH)) {
            move(Dir.NORTH);
        } else if (key == 'a' && canMove(Dir.WEST)) {
            move(Dir.WEST);
        } else if (key == 's' && canMove(Dir.SOUTH)) {
            move(Dir.SOUTH);
        } else if (key == 'd' && canMove(Dir.EAST)) {
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
