package demo;

import core.WorldMap;
import tileengine.TERenderer;

public class WorldGenTest {
//    private static final int WIDTH = 50;
//    private static final int HEIGHT = 30;

    private static final int WIDTH = 150;
    private static final int HEIGHT = 150;

    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        WorldMap map = new WorldMap(977903862194668525L, WIDTH, HEIGHT);

        // draws the world to the screen
        ter.renderFrame(map.grid.view());
    }
}