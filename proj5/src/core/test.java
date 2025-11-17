package core;

import tileengine.*;
import edu.princeton.cs.algs4.StdDraw;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Fully self-contained program:
 * - loads image
 * - slices into tiles
 * - creates TETiles for each tile
 * - renders inside TERenderer
 */
public class ShowImageInTiles {

    private static final int WORLD_WIDTH = 80;
    private static final int WORLD_HEIGHT = 50;

    private static final int TILE_SIZE = 32;  // Or 16 or 64
    private static final String INPUT_IMAGE = "resources/rem.png";
    private static final String OUTPUT_FOLDER = "resources/remtiles";

    public static void main(String[] args) throws Exception {

        // 1. Split image into tiles
        int[] dims = splitImage(INPUT_IMAGE, TILE_SIZE, OUTPUT_FOLDER);
        int tilesX = dims[0];
        int tilesY = dims[1];

        // 2. Load tiles as TETile objects
        TETile[][] remTiles = loadTiles(OUTPUT_FOLDER, tilesX, tilesY);

        // 3. Build empty world
        TETile[][] world = new TETile[WORLD_WIDTH][WORLD_HEIGHT];
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        // 4. Place the mosaic into the world
        int startX = 10;
        int startY = 10;
        for (int x = 0; x < tilesX; x++) {
            for (int y = 0; y < tilesY; y++) {
                world[startX + x][startY + y] = remTiles[x][y];
            }
        }

        // 5. Render using TERenderer
        TERenderer ter = new TERenderer();
        ter.initialize(WORLD_WIDTH, WORLD_HEIGHT);
        ter.renderFrame(world);
    }

    // ---------------------------------------------------------
    // IMAGE SPLITTING FUNCTION
    // ---------------------------------------------------------

    /**
     * Splits an image into TILE_SIZE × TILE_SIZE squares.
     * Saves them as tile_x_y.png.
     * Returns {tilesX, tilesY}.
     */
    public static int[] splitImage(String path, int tileSize, String outputFolder) throws Exception {
        BufferedImage img = ImageIO.read(new File(path));

        int width = img.getWidth();
        int height = img.getHeight();

        int tilesX = width / tileSize;
        int tilesY = height / tileSize;

        new File(outputFolder).mkdirs();

        for (int x = 0; x < tilesX; x++) {
            for (int y = 0; y < tilesY; y++) {
                BufferedImage tile = img.getSubimage(
                        x * tileSize,
                        y * tileSize,
                        tileSize,
                        tileSize
                );

                File out = new File(outputFolder + "/tile_" + x + "_" + y + ".png");
                ImageIO.write(tile, "png", out);
            }
        }

        System.out.println("Split into " + tilesX + " × " + tilesY + " tiles.");
        return new int[]{tilesX, tilesY};
    }

    // ---------------------------------------------------------
    // TILE LOADING FUNCTION
    // ---------------------------------------------------------

    /**
     * Loads tile images from folder into TETile[][].
     */
    public static TETile[][] loadTiles(String folder, int tilesX, int tilesY) {
        TETile[][] tiles = new TETile[tilesX][tilesY];

        for (int x = 0; x < tilesX; x++) {
            for (int y = 0; y < tilesY; y++) {
                String file = folder + "/tile_" + x + "_" + y + ".png";

                tiles[x][y] = new TETile(
                        ' ',               // ASCII char (unused)
                        Color.black,       // text color
                        Color.black,       // background color
                        "img " + x + "," + y,
                        file               // tile image path
                );
            }
        }

        return tiles;
    }
}
