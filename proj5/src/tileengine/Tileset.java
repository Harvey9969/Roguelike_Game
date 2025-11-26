package tileengine;

import java.awt.Color;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {
    private static final String prefix = "src/assets/tiles";
    public static final TETile AVATAR = new TETile('@', Color.white, Color.black, "you", 0);

    public static final TETile WALL = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "wall", 1);

    public static final TETile BOTTOM_WALL = new TETile(' ', Color.black, Color.black, "bottom wall", prefix + "/wall/bottomWall.png", 1);
    public static final TETile UPPER_WALL = new TETile(' ', Color.black, Color.black, "upper wall", prefix + "/wall/upperWall.png", 1);
    public static final TETile LEFT_WALL = new TETile(' ', Color.black, Color.black, "left wall", prefix + "/wall/leftWall.png", 1);
    public static final TETile RIGHT_WALL = new TETile(' ', Color.black, Color.black, "right wall", prefix + "/wall/rightWall.png", 1);

    public static final TETile LBC_WALL = new TETile(' ', Color.black, Color.black, "lbc wall", prefix + "/wall/leftBottomCorner.png", 1);
    public static final TETile RBC_WALL = new TETile(' ', Color.black, Color.black, "rbc wall", prefix + "/wall/rightBottomCorner.png", 1);
    public static final TETile LTC_WALL = new TETile(' ', Color.black, Color.black, "ltc wall", prefix + "/wall/upperLeftCorner.png", 1);
    public static final TETile RTC_WALL = new TETile(' ', Color.black, Color.black, "rtc wall", prefix + "/wall/upperRightCorner.png", 1);

    public static final TETile OUTER_PATH_RBC_WALL = new TETile(' ', Color.black, Color.black, "rbc outer path wall", prefix + "/wall/outerPathRBC.png", 1);

    public static final TETile INNER_PATH_RBC_WALL = new TETile(' ', Color.black, Color.black, "rbc inner path wall", prefix + "/wall/innerPathRBC.png", 1);
    public static final TETile INNER_PATH_LBC_WALL = new TETile(' ', Color.black, Color.black, "lbc inner path wall", prefix + "/wall/innerPathLBC.png", 1);
    public static final TETile INNER_PATH_LTC_WALL = new TETile(' ', Color.black, Color.black, "ltc inner path wall", prefix + "/wall/innerPathLTC.png", 1);

    public static final TETile CORNER_PATH_LTC_WALL = new TETile(' ', Color.black, Color.black, "ltc inner path wall", prefix + "/wall/cornerPathLTC.png", 1);

    public static final TETile FLOOR = new TETile('·', new Color(128, 192, 128), Color.black, "floor", 2);

    public static final TETile NORMAL_FLOOR = new TETile(' ', Color.black, Color.black, "normal floor", prefix + "/floor/floor.png", 2);

    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "nothing", 3);
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass", 4);
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water", 5);
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower", 6);
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door", 7);
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "unlocked door", 8);
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand", 9);
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain", 10);
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree", 11);

    public static final TETile CELL = new TETile('█', Color.white, Color.black, "cell", 12);
}


