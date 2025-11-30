package core.charecters.princess;

import core.game.HUD;

public class Conversation {
    public DialogueNode node;
    DialogueContext context;
    HUD hud;

    public Conversation(DialogueNode node, DialogueContext context, HUD hud) {
        this.node = node;
        this.context = context;
        this.hud = hud;
    }

    public void handle(char key) {
        if (node.choices().isEmpty()) {
            node = null;
            return;
        }

        if (
                Character.isDigit(key)
                && Character.getNumericValue(key) < node.choices().size()
        ) {
            DialogueChoices choice = node.choices().get(Character.getNumericValue(key));

            if (choice.action() != null) {
                choice.action().accept(context);
            }

            node = choice.node();
        }
    }

    public boolean isFinished() {
        return node == null;
    }
}
