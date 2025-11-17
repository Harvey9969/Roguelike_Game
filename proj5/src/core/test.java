package core;

import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;

/**
 *  Draws a world that is mostly empty except for a small region.
 */
public class test {

    private static final int WIDTH = 20;
    private static final int HEIGHT = 56;

    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        String prefix = "C:\\Users\\thest\\Downloads\\rem small pix";  // change this file path
        //"C:\Users\thest\Downloads\rem small pix\tile_10_20.png"
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = new TETile(
                        ' ',
                        Color.WHITE,
                        Color.BLACK,
                        "img " + x + "," + y,
                        prefix + "\\tile_" + x + "_" + (HEIGHT - 1 - y) +".png", 0
                );
            }
        }

        // draws the world to the screen
        ter.renderFrame(world);
    }


}