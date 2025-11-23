package utils;

import tileengine.TETile;
import utils.DS.Point;
import utils.DS.Tile;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TiledImage implements Iterable<Tile> {
    private final List<Tile> tiles;

    public TiledImage(String filename, int width, int height) {
        String prefix = "src/assets/ES small pix";
        tiles = new ArrayList<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles.add(new Tile(
                        new Point(x, y),
                        new TETile(
                                ' ',
                                Color.WHITE,
                                Color.BLACK,
                                "img " + x + "," + y,
                                prefix + "/tile_" + x + "_" + (height - 1 - y) +".png",
                                999
                        )
                ));
            }
        }
    }

    @Override
    public Iterator<Tile> iterator() {
        return tiles.iterator();
    }
}
