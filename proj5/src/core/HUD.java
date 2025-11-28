package core;

import core.charecters.Player;
import edu.princeton.cs.algs4.StdDraw;
import utils.DS.Grid;
import utils.DS.RecordLike.Point;

import java.awt.*;

public class HUD {
    private static final double X_HEART_PADDING = 0.5;
    private static final double X_TEXT_PADDING = 3;
    private static final double Y_PADDING = 0.5;

    private static final int FRAME_DELAY = 20;

    private static final String full_heart = "src/assets/misc/hearts/full.png";
    private static final String empty_heart = "src/assets/misc/hearts/empty.png";
    private static final double HEART_SPACING = 1;

    private static final Font FONT = new Font("Arial", Font.BOLD, 20);

    private int accrued_ticks;
    private String tileName;

    private int DISPLAY_WIDTH;
    private int DISPLAY_HEIGHT;
    private Player player;
    private Grid grid;

    public HUD(int DISPLAY_WIDTH, int DISPLAY_HEIGHT, Grid grid, Player player) {
        this.DISPLAY_WIDTH = DISPLAY_WIDTH;
        this.DISPLAY_HEIGHT = DISPLAY_HEIGHT;
        this.player = player;
        this.grid = grid;

        tileName = "";
    }

    public void draw() {
        drawPlayerHealth();
        drawTileName();
    }

    public void pollMouse(Point camera) {
        if (FRAME_DELAY > accrued_ticks) {
            accrued_ticks++;
            return;
        }

        int x = (int) Math.floor(StdDraw.mouseX());
        int y = (int) Math.floor(StdDraw.mouseY());

        tileName = grid.get(x + camera.x, y + camera.y).description();

        accrued_ticks = 0;
    }

    private void drawPlayerHealth() {
        int numHearts = Player.MAX_HEALTH;

        double x_offset = X_HEART_PADDING;

        int i;
        for (i = 0; i < player.health; i++) {
            StdDraw.picture(x_offset, DISPLAY_HEIGHT - Y_PADDING, full_heart);
            x_offset += HEART_SPACING;
        }

        for (; i < Player.MAX_HEALTH; i++) {
            StdDraw.picture(x_offset, DISPLAY_HEIGHT - Y_PADDING, empty_heart);
            x_offset += HEART_SPACING;
        }
    }

    private void drawTileName() {
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(FONT);

        StdDraw.text(DISPLAY_WIDTH - X_TEXT_PADDING, DISPLAY_HEIGHT - Y_PADDING, tileName);
    }
}
