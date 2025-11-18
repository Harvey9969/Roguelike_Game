package utils.DS;

import tileengine.Tileset;

import java.util.Iterator;

public class TSet implements Iterable<Tile> {
    public Iterable<Point> getWallTiles() {
        return null;
    }

    public Iterable<Point> getFloorTiles() {
        return null;
    }

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
                    return new Tile(wallIterator.next(), Tileset.WALL);
                } else if (floorIterator.hasNext()) {
                    return new Tile(floorIterator.next(), Tileset.FLOOR);
                } else {
                    throw new IndexOutOfBoundsException("No next tile");
                }
            }
        };
    }
}
