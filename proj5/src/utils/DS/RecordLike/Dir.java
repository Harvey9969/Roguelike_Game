package utils.DS.RecordLike;

import java.util.List;
import java.util.Random;

public class Dir {
    public static final Dir NORTH = new Dir('N');
    public static final Dir EAST = new Dir('E');
    public static final Dir SOUTH = new Dir('S');
    public static final Dir WEST = new Dir('W');
    public static final Dir BLANK = new Dir(' ');

    private final char val;

    public Dir(char dir) {
        if (!List.of('N', 'E', 'S', 'W', ' ').contains(dir)) {
            throw new IllegalArgumentException("Must be blank or valid cardinal direction");
        }

        val = dir;
    }

    public boolean isNorth() {
        return val == 'N';
    }

    public boolean isEast() {
        return val == 'E';
    }

    public boolean isSouth() {
        return val == 'S';
    }

    public boolean isWest() {
        return val == 'W';
    }
    
    public static Dir choose(Random random) {
        return switch (random.nextInt(4)) {
            case 0 -> Dir.NORTH;
            case 1 -> Dir.EAST;
            case 2 -> Dir.SOUTH;
            case 3 -> Dir.WEST;
            default -> throw new IllegalStateException("Unexpected value");
        };
    }

    public Dir opposite() {
        return switch (val) {
            case 'N' -> Dir.SOUTH;
            case 'E' -> Dir.WEST;
            case 'S' -> Dir.NORTH;
            case 'W' -> Dir.EAST;
            case ' ' -> Dir.BLANK;
            default -> throw new IllegalStateException("Unexpected value: " + val);
        };
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Dir uddaDir) {
            return
                    (isNorth() == uddaDir.isNorth())
                    && (isEast() == uddaDir.isEast())
                    && (isSouth() == uddaDir.isSouth())
                    && (isWest() == uddaDir.isWest());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return switch (val) {
            case 'N' -> 0;
            case 'E' -> 1;
            case 'S' -> 2;
            case 'W' -> 3;
            case ' ' -> 4;
            default -> throw new IllegalStateException("Unexpected value: " + val);
        };
    }

    @Override
    public String toString() {
        return switch (val) {
            case 'N' -> "NORTH";
            case 'E' -> "EAST";
            case 'S' -> "SOUTH";
            case 'W' -> "WEST";
            case ' ' -> "BLANK";
            default -> throw new IllegalStateException("Unexpected value: " + val);
        };
    }
}
