package core.game.saving;

import java.util.List;

public record SaveData(int width, int height, String[][] gridData, boolean playerChoice, List<CharacterData> characterData, List<PointData> princessTiles) {
}
