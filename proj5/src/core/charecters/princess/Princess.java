package core.charecters.princess;

import core.charecters.GameCharacter;
import core.charecters.animation.Action;
import core.game.GameState;
import core.game.HUD;
import utils.DS.Grid;
import utils.DS.recordlike.Dir;

public class Princess extends GameCharacter {

    public Actions Actions = new Actions();
    public class Actions extends GameCharacter.Actions {
        public Give GIVE;
        public Cry CRY;

        Actions() {
            GIVE = new Give();
            CRY = new Cry();
        }

        class Give extends Action {
            Give() {
                super("GIVE", true, true);
            }

            @Override
            public void onFinish() {
                gameState.player.damage++;
            }
        }

        class Cry extends Action {
            Cry() {
                super("CRY", true, true);
            }

            @Override
            public void onFinish() {
                return;
            }
        }
    }

    private HUD hud;
    private GameState gameState;
    private DialogueNode tree;

    private boolean proximal;

    public boolean spokenTo;

    public Princess(double x, double y, Grid grid, GameState gameState, String spriteFolder, DialogueNode tree) {
        super(x, y, false, grid, spriteFolder);
        this.gameState = gameState;
        this.tree = tree;
    }

    public void setHud(HUD hud) {
        this.hud = hud;
    }

    @Override
    public void _respond(char key) {
        if (proximal && !spokenTo && key == 'i') {
            gameState.startConversation(this, tree, hud);
            spokenTo = true;
            hud.setTalkTip(false);
        }
    }

    @Override
    public void _act() {
        proximal = !gameState.proximal(gameState.player, this).equals(Dir.BLANK);

        if (!spokenTo) {
            hud.setTalkTip(proximal && !gameState.inConversation());
        }
    }
}
