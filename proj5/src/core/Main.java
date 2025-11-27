package core;

import core.charecters.Player;
import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import utils.DS.RecordLike.Point;

import java.util.Random;

public class Main {
    private static final int WIDTH = 150;
    private static final int HEIGHT = 150;

    private static final int VIEW_WIDTH = 50;
    private static final int VIEW_HEIGHT = 30;

    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size VIEW_WIDTH x VIEW_HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(VIEW_WIDTH, VIEW_HEIGHT);

        WorldMap map = new WorldMap(977903862194668525L, WIDTH, HEIGHT);
        EntityManager manager = new EntityManager(map.graph, map.grid, new Random(977903862194668525L));

        Player player = manager.player;

        Frame frame = new Frame();
        while (true) {
            frame.startFrame();

            while (StdDraw.hasNextKeyTyped()) {
                player.respond(StdDraw.nextKeyTyped());
            }
            manager.tickAll();

            Point camera = positionCamera(player);
            ter.renderFrame(
                    map.grid.view(
                            camera,
                            VIEW_WIDTH,
                            VIEW_HEIGHT
                    ),
                    manager.charactersSet,
                    camera
            );

            frame.stopFrame();
        }
    }

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

    private static Point positionCamera(Player player) {
        int desiredCX = player.snapX() - (VIEW_WIDTH / 2);
        int desiredCY = player.snapY() - (VIEW_HEIGHT / 2);

        int CX = Math.min(Math.max(desiredCX, 0), WIDTH - VIEW_WIDTH);
        int CY = Math.min(Math.max(desiredCY, 0), HEIGHT - VIEW_HEIGHT);

        return new Point(CX, CY);
    }
}
