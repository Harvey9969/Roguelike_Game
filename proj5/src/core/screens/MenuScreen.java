package core.screens;

import core.Controller;
import edu.princeton.cs.algs4.StdDraw;
import utils.DS.recordlike.Point;

import java.awt.*;
import java.util.List;

public class MenuScreen implements Screen {
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 50);
    private static final Font KEY_FONT = new Font("Arial", Font.BOLD, 30);

    private static final int TITLE_OFFSET = 60;

    private static final int KEY_OFFSET = 220;
    private static final int KEY_SPACE = 100;

    private static final int SCREEN_WIDTH = 700;
    private static final int SCREEN_HEIGHT = 700;

    private Controller controller;

    public MenuScreen(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void handleKeyPress(char key) {
        if (key == 'n' || key == 'N') {
            controller.gotoSeed();
        } else if (key == 'l' || key == 'L') {
            controller.loadGame();
        } else if (key == 'q' || key == 'Q') {
            controller.quit();
        }
    }

    @Override
    public void pollMouse(Point mouse) {
        return;
    }

    @Override
    public void update() {
        return;
    }

    @Override
    public void render() {

        StdDraw.setFont(TITLE_FONT);
        StdDraw.text(
                SCREEN_WIDTH / 2,
                SCREEN_HEIGHT - TITLE_OFFSET,
                "SORIA MORIA DUNGEON"
        );

        List<String> keys = List.of("(N) New Game", "(L) Load Game", "(Q) Quit Game");
        int key_ypos = SCREEN_HEIGHT - KEY_OFFSET;

        StdDraw.setFont(KEY_FONT);
        for (String key: keys) {
            StdDraw.text(
                    SCREEN_WIDTH / 2,
                    key_ypos,
                    key
            );

            key_ypos -= KEY_SPACE;
        }
    }

    @Override
    public int width() {
        return SCREEN_WIDTH;
    }

    @Override
    public int height() {
        return SCREEN_HEIGHT;
    }
}
