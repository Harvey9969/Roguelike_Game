package core.charecters;

import core.game.GameState;
import utils.DS.Grid;
import utils.DS.recordlike.Dir;
import utils.DS.recordlike.Point;

import java.util.List;

public class Enemy extends Combatant {

    public Enemy(double x, double y, Grid grid, String spriteFolder, GameState manager, int health, int damage) {
        super(x, y, grid, spriteFolder, manager, health, damage);
    }

    @Override
    public void _respond(char key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void _act() {
        Dir bestDir = Dir.BLANK;
        int bestDist = Integer.MAX_VALUE;

        Dir attackDir = manager.proximal(this, manager.player);
        if (!attackDir.equals(Dir.BLANK)) {
            facing = attackDir;
            relPos = Actions.ATTACK;
            return;
        }

        List<Dir> dirs = List.of(Dir.NORTH, Dir.EAST, Dir.SOUTH, Dir.WEST);
        for (Dir dir: dirs) {
            if (!canMove(dir)) {
                continue;
            }

            int currDist = getMyPos().move(dir).mDist(getTargetPos());
            if (bestDist > currDist) {
                bestDir = dir;
                bestDist = currDist;
            }
        }

        if (bestDir.equals(Dir.BLANK)) {
            throw new IllegalStateException("Stuck enemy");
        }

        move(bestDir);
    }

    private Point getMyPos() {
        return new Point(snapX(), snapY());
    }

    private Point getTargetPos() {
        return new Point(manager.player.snapX(), manager.player.snapY());
    }

    @Override
    public boolean canMove(Dir dir) {
        return
                !manager.isPrincessTile(new Point(snapX(), snapY()).move(dir))
                && super.canMove(dir);
    }
}
