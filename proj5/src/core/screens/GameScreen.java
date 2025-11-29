package core.screens;

import core.Controller;
import core.charecters.GameCharacter;
import core.charecters.Player;
import core.game.GameState;
import core.game.HUD;
import tileengine.TETile;
import utils.DS.recordlike.Point;

import java.util.List;

public class GameScreen implements Screen {
    public static final int TILE_SIZE = 32;

    private final int WORLD_WIDTH;
    private final int WORLD_HEIGHT;

    private static final int SCREEN_WIDTH_TILES = 50;
    private static final int SCREEN_HEIGHT_TILES = 30;

    private static final int SCREEN_WIDTH_PIXELS = SCREEN_WIDTH_TILES * TILE_SIZE;
    private static final int SCREEN_HEIGHT_PIXELS = SCREEN_HEIGHT_TILES * TILE_SIZE;

    private Point camera;

    private final Controller controller;
    private GameState gameState;

    private HUD hud;

    private static final int LOSE_DELAY = 240;
    private int loseTicks;

    private boolean selectorKey;

    public GameScreen(int worldWidth, int worldHeight, Controller controller, GameState gameState) {
        WORLD_WIDTH = worldWidth;
        WORLD_HEIGHT = worldHeight;

        this.controller = controller;
        this.gameState = gameState;

        camera = positionCamera(gameState.player);
        hud = new HUD(SCREEN_WIDTH_TILES, SCREEN_HEIGHT_TILES, gameState.grid, gameState.player);

        gameState.p1.setHud(hud);
    }

    @Override
    public void handleKeyPress(char key) {
        if (key == ':') {
            selectorKey = true;
        } else if (selectorKey && (key == '1')) {
            gameState.player.x = gameState.p1.x;
            gameState.player.y = gameState.p1.y;
        } else if (selectorKey && (key == '2')) {
            gameState.player.x = gameState.p2.x;
            gameState.player.y = gameState.p2.y;
        } else if (selectorKey && (key == '3')) {
            gameState.player.x = gameState.p3.x;
            gameState.player.y = gameState.p3.y;
        } else if (selectorKey && (key == 'q' || key == 'Q')) {
            System.exit(0);
        } else {
            gameState.player.respond(key);
            gameState.p1.respond(key);
            selectorKey = false;
        }
    }

    @Override
    public void pollMouse(Point mouse) {
        hud.pollMouse(mouse, camera);
    }

    @Override
    public void update() {
        gameState.tickAll();

        if (gameState.player.health <= 0 && loseTicks >= LOSE_DELAY) {
            controller.gotoLose();
        } else if (gameState.player.health <= 0) {
            loseTicks++;
        }
    }

    @Override
    public void render() {
        TETile[][] view = gameState.grid.view(camera, SCREEN_WIDTH_TILES, SCREEN_HEIGHT_TILES);
        camera = positionCamera(gameState.player);

        for (String phase: List.of("front", "characters", "back")) {

            if (phase.equals("front")) {

                // draw front tiles
                for (int x = 0; x < SCREEN_WIDTH_TILES; x++) {
                    for (int y = 0; y < SCREEN_HEIGHT_TILES; y++) {
                        if (view[x][y] == null) {
                            throw new IllegalArgumentException("Tile at position x=" + x + ", y=" + y
                                    + " is null.");
                        }

                        if (view[x][y].description().equals("upper outer wall")) {
                            continue;
                        }

                        view[x][y].draw(x, y);
                    }
                }

            } else if (phase.equals("characters")) {
                for (GameCharacter c: gameState.charactersSet) {
                    c.draw(camera.x, camera.y);
                }
            } else if (phase.equals("back")) {

                // draw back tiles
                for (int x = 0; x < SCREEN_WIDTH_TILES; x++) {
                    for (int y = 0; y < SCREEN_HEIGHT_TILES; y++) {
                        if (!view[x][y].description().equals("upper outer wall")) {
                            continue;
                        }

                        view[x][y].draw(x, y);
                    }
                }

            }

        }

        hud.draw();

    }

    @Override
    public int width() {
        return SCREEN_WIDTH_PIXELS;
    }

    @Override
    public int height() {
        return SCREEN_HEIGHT_PIXELS;
    }

    @Override
    public int xScale() {
        return SCREEN_WIDTH_TILES;
    }

    @Override
    public int yScale() {
        return SCREEN_HEIGHT_TILES;
    }

    private Point positionCamera(Player player) {
        int desiredCX = player.snapX() - (SCREEN_WIDTH_TILES / 2);
        int desiredCY = player.snapY() - (SCREEN_HEIGHT_TILES / 2);

        int CX = Math.min(Math.max(desiredCX, 0), WORLD_WIDTH - SCREEN_WIDTH_TILES);
        int CY = Math.min(Math.max(desiredCY, 0), WORLD_HEIGHT - SCREEN_HEIGHT_TILES);

        return new Point(CX, CY);
    }
}
