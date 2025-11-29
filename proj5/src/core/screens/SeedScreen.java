package core.screens;

import core.Controller;
import edu.princeton.cs.algs4.StdDraw;
import utils.DS.recordlike.Point;

import java.awt.*;

public class SeedScreen implements Screen {
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 50);
    private static final int TITLE_OFFSET = 60;

    private static final Font KEY_FONT = new Font("Arial", Font.BOLD, 30);
    private static final int KEY_OFFSET = 120;

    private static final int SCREEN_WIDTH = 700;
    private static final int SCREEN_HEIGHT = 700;

    private Long seed;

    private final Controller controller;

    public SeedScreen(Controller controller) {
        this.controller = controller;
        seed = null;
    }

    @Override
    public void handleKeyPress(char key) {
        if (Character.isDigit(key)) {
            long digit = ((long) Integer.parseInt(String.valueOf(key)));

            if (seed == null) {
                seed = digit;
                return;
            }

            try {
                seed = Math.addExact(Math.multiplyExact(seed, 10), digit);
            } catch (ArithmeticException e) {
                // cannot overflow
            }
        } else if (key == 'b' || key == 'B') {
            controller.gotoMenu();
        } else if (key == 'd' || key == 'D') {
            seed /= 10;
        } else if (key == 's' || key == 'S') {
            controller.startNewGame(seed);
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
                "TYPE SEED"
        );

        StdDraw.setFont(KEY_FONT);
        StdDraw.text(
                SCREEN_WIDTH / 2,
                SCREEN_HEIGHT - KEY_OFFSET,
                "(B) back -- (D) delete -- (S) enter"
        );

        StdDraw.setFont(TITLE_FONT);
        StdDraw.text(
                SCREEN_WIDTH / 2,
                SCREEN_HEIGHT / 2,
                seed()
        );
    }

    @Override
    public int width() {
        return SCREEN_WIDTH;
    }

    @Override
    public int height() {
        return SCREEN_HEIGHT;
    }

    private String seed() {
        if (seed == null) {
            return "";
        }

        return Long.toString(seed);
    }
}
