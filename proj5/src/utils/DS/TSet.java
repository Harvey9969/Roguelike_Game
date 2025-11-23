package utils.DS;

import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;
import java.util.Iterator;
import java.util.Random;

public abstract class TSet implements Iterable<Tile> {
    Random random;
    public static final String prefix = "src/assets/";

    public TSet(Random random) {
        this.random = random;
    }

    public abstract Iterable<Point> getWallTiles();

    public abstract Iterable<Point> getFloorTiles();

    @Override
    public Iterator<Tile> iterator() {
        return new Iterator<Tile>() {
            final Iterator<Point> wallIterator = getWallTiles().iterator();
            final Iterator<Point> floorIterator = getFloorTiles().iterator();

            @Override
            public boolean hasNext() {
                return wallIterator.hasNext() || floorIterator.hasNext();
            }

            @Override
            public Tile next() {
                if (wallIterator.hasNext()) {
                    return new Tile(
                            wallIterator.next(),
                            new TETile(
                                    ' ',
                                    Color.WHITE,
                                    Color.BLACK,
                                    "wall",
                                    prefix + "wall/wall" + random.nextInt(8) + ".png",
                                    1
                            )
                    );
                } else if (floorIterator.hasNext()) {
                    return new Tile(
                            floorIterator.next(),
                            new TETile(
                                    ' ',
                                    Color.WHITE,
                                    Color.BLACK,
                                    "wall",
                                    prefix + "floor/floor" + random.nextInt(8) + ".png",
                                    2
                            )
                    );
                } else {
                    throw new IndexOutOfBoundsException("No next tile");
                }
            }
        };
    }
}