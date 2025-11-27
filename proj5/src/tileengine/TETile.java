package tileengine;

import java.awt.Color;

import edu.princeton.cs.algs4.StdDraw;

/**
 * The TETile object is used to represent a single tile in your world. A 2D array of tiles make up a
 * board, and can be drawn to the screen using the TERenderer class.
 *
 * All TETile objects must have a character, textcolor, and background color to be used to represent
 * the tile when drawn to the screen. You can also optionally provide a path to an image file of an
 * appropriate size (16x16) to be drawn in place of the unicode representation. If the image path
 * provided cannot be found, draw will fallback to using the provided character and color
 * representation, so you are free to use image tiles on your own computer.
 *
 * The provided TETile is immutable, i.e. none of its instance variables can change. You are welcome
 * to make your TETile class mutable, if you prefer.
 */

public class TETile {
    private final char character; // Do not rename character or the autograder will break.
    private final Color textColor;
    private final Color backgroundColor;
    private final String description;
    private final String filepath;
    private final Collider collider;
    private final int id;

    /**
     * Full constructor for TETile objects.
     * @param character The character displayed on the screen.
     * @param textColor The color of the character itself.
     * @param backgroundColor The color drawn behind the character.
     * @param description The description of the tile, shown in the GUI on hovering over the tile.
     * @param filepath Full path to image to be used for this tile. Must be correct size (16x16)
     */
    public TETile(String description, String filepath, Collider collider, int id) {
        this.character = ' ';
        this.textColor = Color.BLACK;
        this.backgroundColor = Color.BLACK;
        this.description = description;
        this.filepath = filepath;
        this.collider = collider;
        this.id = id;
    }

    public TETile(String description, String filepath, int id) {
        this.character = ' ';
        this.textColor = Color.BLACK;
        this.backgroundColor = Color.BLACK;
        this.description = description;
        this.filepath = filepath;
        this.collider = new Collider(0, TERenderer.TILE_SIZE - 1, 0, TERenderer.TILE_SIZE - 1);
        this.id = id;
    }

    /**
     * Constructor without filepath. In this case, filepath will be null, so when drawing, we
     * will not even try to draw an image, and will instead use the provided character and colors.
     * @param character The character displayed on the screen.
     * @param textColor The color of the character itself.
     * @param backgroundColor The color drawn behind the character.
     * @param description The description of the tile, shown in the GUI on hovering over the tile.
     */
    public TETile(char character, Color textColor, Color backgroundColor, String description, int id) {
        this.character = character;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.description = description;
        this.filepath = null;
        this.collider = new Collider(0, TERenderer.TILE_SIZE - 1, 0, TERenderer.TILE_SIZE - 1);
        this.id = id;
    }

    /**
     * Draws the tile to the screen at location x, y. If a valid filepath is provided,
     * we draw the image located at that filepath to the screen. Otherwise, we fall
     * back to the character and color representation for the tile.
     *
     * Note that the image provided must be of the right size (16x16). It will not be
     * automatically resized or truncated.
     * @param x x coordinate
     * @param y y coordinate
     */
    public void draw(double x, double y) {
        if (filepath != null) {
            try {
                StdDraw.picture(x + 0.5, y + 0.5, filepath);
                return;
            } catch (IllegalArgumentException e) {
                // Exception happens because the file can't be found. In this case, fail silently
                // and just use the character and background color for the tile.
            }
        }

        StdDraw.setPenColor(backgroundColor);
        StdDraw.filledSquare(x + 0.5, y + 0.5, 0.5);
        StdDraw.setPenColor(textColor);
        StdDraw.text(x + 0.5, y + 0.5, Character.toString(character()));
    }

    /** Character representation of the tile. Used for drawing in text mode.
     * @return character representation
     */
    public char character() {
        return character;
    }

    /**
     * Description of the tile. Useful for displaying mouseover text or
     * testing that two tiles represent the same type of thing.
     * @return description of the tile
     */
    public String description() {
        return description;
    }

    /**
     * ID number of the tile. Used for equality comparisons.
     * @return id of the tile
     */
    public int id() {
        return id;
    }

    /**
     * Checks if two tiles are equal by comparing their IDs.
     * @param o object to compare with
     * @return boolean representing equality
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        return (o instanceof TETile otherTile && otherTile.id == this.id);
    }

    @Override
    public String toString() {
        return description;
    }

    public Collider getCollider() {
        return collider;
    }
}
