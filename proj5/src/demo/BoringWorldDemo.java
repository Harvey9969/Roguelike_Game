package demo;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.*;

public class BoringWorldDemo {

    private static final int WIDTH = 60;
    private static final int HEIGHT = 30;

    public static void main(String[] args) {

        // Expand canvas for HUD space


        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // build world
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.WALL;
            }
        }
        TETile[][] remTiles = loadImageTiles("resources/tiles", WIDTH, HEIGHT);

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < tilesY; y++) {
                world[startX + x][startY + y] = remTiles[x][y];
            }
        }

        ter.renderFrame(world);


    }
}
