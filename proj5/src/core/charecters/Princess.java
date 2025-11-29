package core.charecters;

import core.game.GameState;
import core.game.HUD;
import utils.DS.Grid;
import utils.DS.recordlike.Dir;

public class Princess extends GameCharacter {
    private HUD hud;
    private GameState gameState;

    private boolean proximal;
    private boolean inConvo;

    public Princess(double x, double y, Grid grid, GameState gameState, String spriteFolder) {
        super(x, y, false, grid, spriteFolder);
        this.gameState = gameState;
    }

    public void setHud(HUD hud) {
        this.hud = hud;
    }

    @Override
    public void _respond(char key) {
        if (proximal && !inConvo && key == 'i') {
            inConvo = true;
        } else if (proximal && inConvo) {
            // talk
        } else {
            inConvo = false;
        }
    }

    @Override
    public void _act() {
        proximal = !gameState.proximal(gameState.player, this).equals(Dir.BLANK);

        hud.setTalkTip(proximal);
    }
}
