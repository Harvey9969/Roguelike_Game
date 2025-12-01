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

        Actions() {
            GIVE = new Give();
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
    }

    private HUD hud;
    private GameState gameState;
    private DialogueNode tree;

    private boolean proximal;

    private boolean oldTip;
    private boolean tip;

    public int pID;
    public boolean spokenTo;

    public Princess(double x, double y, Grid grid, GameState gameState, String spriteFolder, int pID, DialogueNode tree) {
        super(x, y, false, grid, spriteFolder);
        this.gameState = gameState;
        this.tree = tree;
        this.pID = pID;
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
        tip = !spokenTo && proximal && !gameState.inConversation();

        if (tip != oldTip) {
            hud.setTalkTip(tip);
            oldTip = tip;
        }
    }
}
