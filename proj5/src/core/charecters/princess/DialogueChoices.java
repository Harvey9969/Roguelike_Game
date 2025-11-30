package core.charecters.princess;

import java.util.function.Consumer;

// if action null just ignore it
public record DialogueChoices(String text, DialogueNode node, Consumer<DialogueContext> action) {
}
