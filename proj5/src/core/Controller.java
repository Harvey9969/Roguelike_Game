package core;

import core.game.GameState;
import core.game.GameStateFactory;
import core.screens.*;
import edu.princeton.cs.algs4.StdDraw;
import utils.DS.recordlike.Point;

import java.awt.*;

public class Controller {
    private static class Frame {
        private static final int MILLISEC_PER_FRAME = 1000 / 60;

        private long start;

        void startFrame() {
            start = System.currentTimeMillis();
        }

        void stopFrame() {
            long elapsed = System.currentTimeMillis() - start;
            int sleepTime = MILLISEC_PER_FRAME - (int) elapsed;

            if (sleepTime > 0) {
                StdDraw.pause(sleepTime);
            }
        }

    }

    private static final int WORLD_WIDTH = 150;
    private static final int WORLD_HEIGHT = 150;

    private static final Frame frameManager = new Frame();

    private static final int MOUSE_POLL_FREQUENCY = 20;
    private int mouseTicks;

    private Screen screen;
    private Screen oldScreen;

    private final Screen MENU;
    private final Screen SEED;
    private final Screen LOSE;
    // add other screens

    private boolean running;
    private GameState gameState;

    public Controller() {
        running = true;

        MENU = new MenuScreen(this);
        SEED = new SeedScreen(this);
        LOSE = new LoseScreen(this);

        oldScreen = null;
        screen = MENU;
    }

    public void run() {
        while (running) {

            if (screen != oldScreen) {
                StdDraw.setCanvasSize(screen.width(), screen.height());
                StdDraw.enableDoubleBuffering();

                StdDraw.setXscale(0, screen.xScale());
                StdDraw.setYscale(0, screen.yScale());

                StdDraw.setPenColor(StdDraw.WHITE);

                oldScreen = screen;
            }

            frameManager.startFrame();

            while (StdDraw.hasNextKeyTyped()) {
                screen.handleKeyPress(StdDraw.nextKeyTyped());
            }

            if (MOUSE_POLL_FREQUENCY > mouseTicks) {
                mouseTicks++;
            } else {
                int mouseX = (int) Math.floor(StdDraw.mouseX());
                int mouseY = (int) Math.floor(StdDraw.mouseY());

                screen.pollMouse(new Point(mouseX, mouseY));
            }

            StdDraw.clear(Color.BLACK);

            screen.update();
            screen.render();

            StdDraw.show();

            frameManager.stopFrame();
        }
    }

    public void gotoSeed() {
        screen = SEED;
    }

    public void startNewGame(long seed) {
        GameState gameState = GameStateFactory.createNew(WORLD_WIDTH, WORLD_HEIGHT, seed);

        screen = new GameScreen(WORLD_WIDTH, WORLD_HEIGHT, this, gameState);
    }

    public void loadGame() {
        // somehow gets a GameState
        // uses GameState to start GameScreen
    }

    public void gotoWin() {
        // moves to win screen
    }

    public void gotoLose() {
        screen = LOSE;
    }

    public void gotoMenu() {
        screen = MENU;
    }

    public void quit() {
        System.exit(0);
    }
}
