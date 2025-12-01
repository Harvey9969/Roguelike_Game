package core.game.saving;

// String type gives type
// int x, y and String spritePath are for all
// (technically not for player or slime -- not needed)
// int health, damage are for combatants
// int pNum, boolean spokenTo are for Princess

public record CharacterData(String type, int x, int y, String spritePath, int health, int damage, int pNum, boolean spokenTo) {
}
