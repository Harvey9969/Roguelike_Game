package utils.DS;

import tileengine.TETile;
import tileengine.Tileset;

public class Grid {
    int width;
    int height;
    int xOffset;
    int yOffset;
    TETile[][] tiles;

    public Grid(int width, int height) {
        validateDimension(width, height);

        this.width = width;
        this.height = height;

        tiles = new TETile[width][height];
    }

    public Grid(Grid parent, Point bottomLeft, int width, int height) {
        validateDimension(width, height);
        validateContainment(bottomLeft, parent.width, parent.height, width, height);

        this.width = width;
        this.height = height;

        tiles = parent.tiles;

        xOffset = bottomLeft.x;
        yOffset = bottomLeft.y;
    }

    public void set(Tile tile) {
        int lx = tile.point().x ;
        int ly = tile.point().y;

        validateInBounds(lx, ly);

        tiles[lx + xOffset][ly + yOffset] = tile.tileType();
    }

    public TETile get(int x, int y) {
        validateInBounds(x, y);

        return tiles[x + xOffset][y + yOffset];
    }

    public void add(TSet uddaTiles, boolean noOverwrite) {
        if (!noOverwrite) {
            for (Tile tile: uddaTiles) {
                set(tile);
            }
            return;
        }

        for (Tile tile: uddaTiles) {
            TETile nativeTile = get(tile.point().x, tile.point().y);

            if (
                    nativeTile == null
                    || nativeTile.equals(Tileset.NOTHING)
            ) {
                set(tile);
            } else {
                throw new IllegalStateException("Obscuring tile exists");
            }
        }
    }

    public void add(TETile[][] uddaTiles) {
        if (
                uddaTiles.length == width
                && uddaTiles[0].length == height
        ) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    set(new Tile(new Point(x,y), uddaTiles[x][y]));
                }
            }
        } else {
            throw new IllegalArgumentException("Tile frames must match");
        }
    }

    public TETile[][] view(Point bottomLeft, int width, int height) {
        validateDimension(width, height);
        validateContainment(bottomLeft, this.width, this.height, width, height);

        TETile[][] result = new TETile[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                result[x][y] = get(x + bottomLeft.x, y + bottomLeft.y);
            }
        }

        return result;
    }

    public TETile[][] view() {
        TETile[][] result = new TETile[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                result[x][y] = get(x, y);
            }
        }

        return result;
    }

    private static void validateDimension(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Dimensions must be greater than 0");
        }
    }

    private static void validateContainment(Point bottomLeft, int parentWidth, int parentHeight, int width, int height) {
        if (
                width + bottomLeft.x > parentWidth
                || height + bottomLeft.y > parentHeight
        ) {
            throw new ArrayIndexOutOfBoundsException("Sub grid clips out of grid");
        }
    }

    private void validateInBounds(int x, int y) {
        if (
                x < 0
                || y < 0
                || x >= width
                || y >= height
        ) {
            throw new ArrayIndexOutOfBoundsException("grid index OOB");
        }
    }
}
