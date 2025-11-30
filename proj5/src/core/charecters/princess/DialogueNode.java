package core.charecters.princess;

import java.util.List;

// if choices is null, give one option -- leave convo
public record DialogueNode(String speaker, String filepath, String text, List<DialogueChoices> choices) {
}
