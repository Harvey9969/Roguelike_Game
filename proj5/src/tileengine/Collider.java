package tileengine;

import java.util.Collections;
import java.util.List;

public class Collider {
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;

    /**
     * Enter the values as pixel values based on a TILE_SIZE x TILE_SIZE
     * grid with the bottom left pixel being (0,0) and the top right being
     * (TILE_SIZE - 1, TILE_SIZE - 1)
     */
    public Collider(double minX, double maxX, double minY, double maxY) {
        if (minX >= maxX || minY >= maxY) {
            throw new IllegalArgumentException("Misplaced points don't form valid rectangle");
        }

        double nMinX = minX / (TERenderer.TILE_SIZE - 1);
        double nMaxX = maxX / (TERenderer.TILE_SIZE - 1);
        double nMinY = minY / (TERenderer.TILE_SIZE - 1);
        double nMaxY = maxY / (TERenderer.TILE_SIZE - 1);

        List<Double> checkList = List.of(nMinX, nMaxX, nMinY, nMaxY);

        if (
                Collections.max(checkList) > 1
                || Collections.min(checkList) < 0
        ) {
            throw new IllegalArgumentException(
                    "Cords must be between 0 and "
                    + (TERenderer.TILE_SIZE - 1)
            );
        }

        this.minX = nMinX;
        this.maxX = nMaxX;
        this.minY = nMinY;
        this.maxY = nMaxY;
    }

    public boolean collide(double x, double y) {
        return
                x >= minX
                && x <= maxX
                && y >= minY
                && y <= maxY;
    }
}
