package core;

import tileengine.TERenderer;

public class Main {
    private static final int WIDTH = 100;
    private static final int HEIGHT = 60;

    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // TODO: improve so no overlap with 4848747072185569509L
        WorldMap map = new WorldMap(977903862194668525L, WIDTH, HEIGHT);

        // draws the world to the screen
        ter.renderFrame(map.grid.view());
    }
}
