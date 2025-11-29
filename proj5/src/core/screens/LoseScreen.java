package core.screens;

import core.Controller;
import edu.princeton.cs.algs4.StdDraw;
import utils.DS.recordlike.Point;

import java.awt.*;
import java.util.List;

public class LoseScreen implements Screen{
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 50);
    private static final Font KEY_FONT = new Font("Arial", Font.BOLD, 30);

    private static final int TITLE_OFFSET = 100;

    private static final int KEY_OFFSET = 260;
    private static final int KEY_SPACE = 100;

    private static final int SCREEN_WIDTH = 700;
    private static final int SCREEN_HEIGHT = 700;

    private final Controller controller;

    public LoseScreen(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void handleKeyPress(char key) {
        if (key == 'm' || key == 'M') {
            controller.gotoMenu();
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
                "GAME OVER"
        );

        List<String> keys = List.of("(M) Menu", "(Q) Quit Game");
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
