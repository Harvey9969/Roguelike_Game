package core.game.saving;

import java.util.List;

public record SaveData(int width, int height, String[][] gridData, List<CharacterData> characterData, List<PointData> princessTiles) {
}
