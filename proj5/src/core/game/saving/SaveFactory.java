package core.game.saving;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.charecters.Enemy;
import core.charecters.GameCharacter;
import core.charecters.Player;
import core.charecters.Slime;
import core.charecters.princess.Princess;
import core.game.GameState;
import tileengine.TETile;
import tileengine.Tileset;
import utils.DS.recordlike.Point;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveFactory {
    private static final String PATH = "src/save/0.json";

    public static void save(GameState gameState) {
        int WIDTH = gameState.grid.view().length;
        int HEIGHT = gameState.grid.view()[0].length;

        String[][] gridRepr = new String[WIDTH][HEIGHT];
        List<PointData> princessTiles = new ArrayList<>();
        List<CharacterData> characterData = new ArrayList<>();

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (gameState.isPrincessTile(new Point(x, y))) {
                    princessTiles.add(new PointData(x, y));
                }

                gridRepr[x][y] = gameState.grid.get(x, y).description();
            }
        }

        for (GameCharacter c: gameState.charactersSet) {
            if (c instanceof Player player) {
                characterData.add(new CharacterData(
                        "PLAYER",
                        player.snapX(),
                        player.snapY(),
                        null,
                        player.health,
                        player.damage,
                        Integer.MAX_VALUE,
                        false
                ));
            } else if (c instanceof Princess princess) {
                characterData.add(new CharacterData(
                        "PRINCESS",
                        princess.snapX(),
                        princess.snapY(),
                        princess.spriteFolder,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        princess.pID,
                        princess.spokenTo
                ));
            } else if (c instanceof Slime slime) {
                characterData.add(new CharacterData(
                        "SLIME",
                        slime.snapX(),
                        slime.snapY(),
                        null,
                        slime.health,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        false
                ));
            } else if (c instanceof Enemy enemy) {
                characterData.add(new CharacterData(
                        "ENEMY",
                        enemy.snapX(),
                        enemy.snapY(),
                        enemy.spriteFolder,
                        enemy.health,
                        enemy.damage,
                        Integer.MAX_VALUE,
                        false
                ));
            }
        }

        write(
                new SaveData(
                        WIDTH,
                        HEIGHT,
                        gridRepr,
                        characterData,
                        princessTiles
                )
        );
    }

    public static SaveData load() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();


        try (FileReader fileReader = new FileReader(PATH)) {
            return gson.fromJson(fileReader, SaveData.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static Map<String, TETile> getGridMapping() {
        Map<String, TETile> gridMap = new HashMap<>();

        gridMap.put(Tileset.WALL.description(), Tileset.WALL);
        gridMap.put(Tileset.UPPER_INNER_WALL.description(), Tileset.UPPER_INNER_WALL);
        gridMap.put(Tileset.UPPER_OUTER_WALL.description(), Tileset.UPPER_OUTER_WALL);
        gridMap.put(Tileset.BOTTOM_WALL.description(), Tileset.BOTTOM_WALL);
        gridMap.put(Tileset.LEFT_WALL.description(), Tileset.LEFT_WALL);
        gridMap.put(Tileset.RIGHT_WALL.description(), Tileset.RIGHT_WALL);
        gridMap.put(Tileset.LBC_WALL.description(), Tileset.LBC_WALL);
        gridMap.put(Tileset.RBC_WALL.description(), Tileset.RBC_WALL);
        gridMap.put(Tileset.LTC_WALL.description(), Tileset.LTC_WALL);
        gridMap.put(Tileset.RTC_WALL.description(), Tileset.RTC_WALL);
        gridMap.put(Tileset.LEFT_POLE.description(), Tileset.LEFT_POLE);
        gridMap.put(Tileset.RIGHT_POLE.description(), Tileset.RIGHT_POLE);
        gridMap.put(Tileset.FLOOR.description(), Tileset.FLOOR);
        gridMap.put(Tileset.NORMAL_FLOOR.description(), Tileset.NORMAL_FLOOR);
        gridMap.put(Tileset.NOTHING.description(), Tileset.NOTHING);
        gridMap.put(Tileset.FLOWER.description(), Tileset.FLOWER);

        return gridMap;
    }

    private static void write(SaveData save) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter fileWriter = new FileWriter(PATH)) {
            gson.toJson(save, fileWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
