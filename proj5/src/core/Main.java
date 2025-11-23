package core;

import core.charecters.Characters;
import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import utils.DS.Point;

public class Main {
    private static final int WIDTH = 200;
    private static final int HEIGHT = 200;

    private static final int VIEW_WIDTH = 25;
    private static final int VIEW_HEIGHT = 15;

    private static final int MILLISEC_PER_FRAME = 1000 / 60;

    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size VIEW_WIDTH x VIEW_HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(VIEW_WIDTH, VIEW_HEIGHT);

        WorldMap map = new WorldMap(977903862194668525L, WIDTH, HEIGHT);

        // draws the world to the screen, C is camera
        int desiredCX;
        int desiredCY;

        int actualCX;
        int actualCY;

        while (true) {
            long start = System.currentTimeMillis();

            while (StdDraw.hasNextKeyTyped()) {
                map.player.respond(StdDraw.nextKeyTyped());
            }

            for (Characters c: map.charactersSet) {
                if (c != map.player) {
                    c.tick();
                }
            }

            desiredCX = map.player.x - (VIEW_WIDTH / 2);
            desiredCY = map.player.y - (VIEW_HEIGHT / 2);

            actualCX = Math.min(Math.max(desiredCX, 0), WIDTH - VIEW_WIDTH);
            actualCY = Math.min(Math.max(desiredCY, 0), HEIGHT - VIEW_HEIGHT);

            ter.renderFrame(
                    map.grid.view(
                            new Point(actualCX, actualCY),
                            VIEW_WIDTH,
                            VIEW_HEIGHT
                    ),
                    map.charactersSet,
                    actualCX,
                    actualCY
            );

            long elapsed = System.currentTimeMillis() - start;
            int sleepTime = MILLISEC_PER_FRAME - (int) elapsed;

            if (sleepTime > 0) {
                StdDraw.pause(sleepTime);
            }
        }
    }
}
