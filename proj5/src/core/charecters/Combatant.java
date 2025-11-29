package core.charecters;

import core.charecters.animation.Action;
import core.game.GameState;
import tileengine.TETile;
import tileengine.Tileset;
import utils.DS.Grid;
import utils.DS.recordlike.Dir;

public abstract class Combatant extends GameCharacter {
    private static final double D = .3;

    private static final int FRAME_DELAY = 20;
    private int accruedTicks;

    public Actions Actions = new Actions();
    public class Actions extends GameCharacter.Actions {
        public Walk WALK;
        public Hurt HURT;
        public Attack ATTACK;
        public Death DEATH;

        Actions() {
            this.IDLE = new Idle();
            this.WALK = new Walk();
            this.HURT = new Hurt();
            this.ATTACK = new Attack();
            this.DEATH = new Death();
        }

        class Walk extends Action {
            Walk() {
                super("WALK");
            }

            @Override
            public void onFinish() {
                throw new UnsupportedOperationException();
            }
        }

        class Hurt extends Action {
            Hurt() {
                super("HURT", true, true);
            }

            @Override
            public void onFinish() {
                if (health <= 0) {
                    relPos = Actions.DEATH;
                }
            }
        }

        class Attack extends Action {
            Attack() {
                super("ATTACK", true, true);
            }

            @Override
            public void onFinish() {
                manager.attack(Combatant.this);
            }
        }

        class Death extends Action {
            Death() {
                super("DEATH", true, false, true);
            }

            @Override
            public void onFinish() {
                manager.kill(Combatant.this);
            }
        }
    }

    public GameState manager;

    public int health;
    public int damage;

    public Combatant(double x, double y, Grid grid, String spriteFolder, GameState manager, int health, int damage) {
        super(x, y, true, grid, spriteFolder);

        this.manager = manager;

        this.health = health;
        this.damage = damage;
    }

    public void hurt(Dir dir, int damage) {
        facing = dir;
        health -= damage;

        relPos = Actions.HURT;

        for (int _i = 0; _i < 3; _i++) {
            if (canMove(dir)) {
                moveNoAnim(dir);
            }
        }
    }

    public void move(Dir dir) {
        if (dir.equals(Dir.BLANK)) {
            throw new IllegalArgumentException("Cannot move in blank direction");
        } else if (!canMove(dir)) {
            throw new IllegalStateException("Cannot move " + dir);
        }

        facing = dir;
        relPos = Actions.WALK;

        moveNoAnim(dir);
    }

    private void moveNoAnim(Dir dir) {
        if (dir.isNorth()) {
            y += D;
        } else if (dir.isEast()) {
            x += D;
        } else if (dir.isSouth()) {
            y -= D;
        } else if (dir.isWest()) {
            x -= D;
        }
    }

    public boolean canMove(Dir dir) {
        if (dir.isNorth()) {
            return canMove(x, y + D);
        } else if (dir.isEast()) {
            return canMove(x + D, y);
        } else if (dir.isSouth()) {
            return canMove(x, y - D);
        } else if (dir.isWest()) {
            return canMove(x - D, y);
        } else {
            throw new IllegalArgumentException("Cannot check blank direction");
        }
    }

    private boolean canMove(double x, double y) {
        int intX = (int) Math.floor(x);
        int intY = (int) Math.floor(y);

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

    @Override
    public void act() {
        if (FRAME_DELAY > accruedTicks) {
            accruedTicks++;
            return;
        }

        accruedTicks = 0;
        super.act();
    }

}
