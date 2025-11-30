package core.charecters.princess;

import core.Controller;
import core.game.GameState;

public record DialogueContext(Princess p, GameState gameState, Controller controller) {
}
