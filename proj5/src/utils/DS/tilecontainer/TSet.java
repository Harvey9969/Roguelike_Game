package utils.DS.tilecontainer;

import tileengine.Tileset;
import utils.DS.recordlike.Point;
import utils.DS.recordlike.Tile;

import java.util.Iterator;

public abstract class TSet implements Iterable<Tile> {
    public static final String prefix = "src/assets/";

    public abstract Iterable<Tile> getWallTiles();

    public abstract Iterable<Point> getFloorTiles();

    @Override
    public Iterator<Tile> iterator() {
        return new Iterator<Tile>() {
            final Iterator<Tile> wallIterator = getWallTiles().iterator();
            final Iterator<Point> floorIterator = getFloorTiles().iterator();

            @Override
            public boolean hasNext() {
                return wallIterator.hasNext() || floorIterator.hasNext();
            }

            @Override
            public Tile next() {
                if (wallIterator.hasNext()) {
                    return wallIterator.next();
                } else if (floorIterator.hasNext()) {
                    return new Tile(floorIterator.next(), Tileset.NORMAL_FLOOR);
                } else {
                    throw new IndexOutOfBoundsException("No next tile");
                }
            }
        };
    }
}