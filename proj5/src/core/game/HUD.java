package core.game;

import core.charecters.Player;
import core.charecters.princess.AudioManager;
import core.charecters.princess.DialogueChoices;
import core.charecters.princess.DialogueNode;
import edu.princeton.cs.algs4.StdDraw;
import utils.DS.Grid;
import utils.DS.recordlike.Point;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HUD {
    private static final double X_DMG = 7;

    private static final double X_HEART_PADDING = 0.5;
    private static final double X_TEXT_PADDING = 3;

    private static final double Y_PADDING = 0.5;

    private static final double TEXT_BOX_Y = 4.5;
    private static final double TEXT_BOX_WIDTH = 30;
    private static final double TEXT_BOX_HEIGHT = 8;

    private static final double SPOKEN_SPACE = 1.5;
    private static final double SPOKEN_INDENT = 1;

    private static final double NEWLINE_SPACE = 0.8;
    private static final double OPTION_SPACE = 1;

    private static final double TEXT_BOX_PADDING = 0.5;

    private static final String full_heart = "src/assets/misc/hearts/full.png";
    private static final String empty_heart = "src/assets/misc/hearts/empty.png";
    private static final double HEART_SPACING = 1;

    private static final Font FONT = new Font("Arial", Font.BOLD, 20);
    private static final Font SPOKEN_FONT = new Font("Times New Roman", Font.PLAIN, 22);

    private final int DISPLAY_WIDTH;
    private final int DISPLAY_HEIGHT;

    private final Player player;
    private final Grid grid;

    private String tileName;

    private boolean talkTip;

    private String speaker;

    private String picPath;
    private String wavPath;

    private String oldDialogueText;
    private String dialogueText;
    private List<String> dialogueChoices;

    AudioManager audio;

    private boolean isChoices;
    private boolean showDialogue;

    public HUD(int DISPLAY_WIDTH, int DISPLAY_HEIGHT, Grid grid, Player player) {
        this.DISPLAY_WIDTH = DISPLAY_WIDTH;
        this.DISPLAY_HEIGHT = DISPLAY_HEIGHT;
        this.player = player;
        this.grid = grid;

        audio = new AudioManager();

        oldDialogueText = "";
        tileName = "";
    }

    public void draw() {
        StdDraw.setPenColor(StdDraw.WHITE);

        drawPlayerHealth();
        drawPlayerDMG();
        drawTileName();

        if (showDialogue) {
            drawDialogue();

            if (!oldDialogueText.equals(dialogueText)) {

                if (wavPath != null) {
                    audio.playAudio(wavPath);
                    oldDialogueText = dialogueText;
                } else {
                    audio.stopAudio();
                }

            }
        } else  {
            audio.stopAudio();
        }

        if (talkTip) {
            StdDraw.setPenColor(StdDraw.WHITE);
            drawTip();
        }
    }

    public void drawPlayerDMG() {
        StdDraw.textLeft(X_DMG, DISPLAY_HEIGHT - Y_PADDING, "DMG: " + player.damage);
    }

    public void pollMouse(Point mouse, Point camera) {
        int absX = mouse.x + camera.x;
        int absY = mouse.y + camera.y;

        if (grid.isInBounds(absX, absY)) {
            tileName = grid.get(absX, absY).description();
        }
    }

    private void drawPlayerHealth() {
        int numHearts = Player.MAX_HEALTH;

        double x_offset = X_HEART_PADDING;

        int i;
        for (i = 0; i < player.health; i++) {
            StdDraw.picture(x_offset, DISPLAY_HEIGHT - Y_PADDING, full_heart);
            x_offset += HEART_SPACING;
        }

        for (; i < Player.MAX_HEALTH; i++) {
            StdDraw.picture(x_offset, DISPLAY_HEIGHT - Y_PADDING, empty_heart);
            x_offset += HEART_SPACING;
        }
    }

    private void drawTileName() {
        StdDraw.setFont(FONT);

        StdDraw.text(DISPLAY_WIDTH - X_TEXT_PADDING, DISPLAY_HEIGHT - Y_PADDING, tileName);
    }

    private void drawTip() {
        StdDraw.setFont(FONT);

        StdDraw.text(DISPLAY_WIDTH / 2, DISPLAY_HEIGHT - Y_PADDING, "press i to talk");
    }

    public void drawDialogue() {
        StdDraw.setFont(FONT);

        StdDraw.setPenColor(224, 197, 109);

        StdDraw.picture(7, 10, picPath);

        // takes half width and half height
        StdDraw.filledRectangle(DISPLAY_WIDTH / 2, TEXT_BOX_Y, TEXT_BOX_WIDTH / 2, TEXT_BOX_HEIGHT / 2);

        double textX = (DISPLAY_WIDTH / 2) - (TEXT_BOX_WIDTH / 2) + TEXT_BOX_PADDING;
        double textY = TEXT_BOX_Y + (TEXT_BOX_HEIGHT / 2) - TEXT_BOX_PADDING;

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.textLeft(textX, textY, speaker);

        StdDraw.setFont(SPOKEN_FONT);

        textY -= SPOKEN_SPACE;
        textY = drawText(textX, textY, dialogueText, 100) - OPTION_SPACE;

        textX += SPOKEN_INDENT;

        for (int i = 0; i < dialogueChoices.size(); i++) {
            textY = drawText(textX, textY, i + ") " + dialogueChoices.get(i), 94);
            textY -= OPTION_SPACE;
        }

    }

    private double drawText(double textX, double textY, String text, int charLength) {
        List<String> toWrite = new ArrayList<>();

        int ind = 0;
        for (int pos = 0; pos < text.length(); pos++) {
            if (ind == toWrite.size()) {
                toWrite.add("");
            }

            toWrite.set(ind, toWrite.get(ind) + text.charAt(pos));

            if (pos != 0 && pos % charLength == 0) {
                ind++;
            }
        }

        for (String write: toWrite) {
            StdDraw.textLeft(textX, textY, write);
            textY -= NEWLINE_SPACE;
        }

        return textY + NEWLINE_SPACE;
    }

    public void setTalkTip(boolean talkTip) {
        this.talkTip = talkTip;
    }

    public void setDialogue(DialogueNode node) {
        speaker = node.speaker();
        dialogueText = node.text();

        picPath = node.imgpath();
        wavPath = node.wavpath();

        dialogueChoices = new ArrayList<>();
        for (DialogueChoices choice : node.choices()) {
            dialogueChoices.add(choice.text());
        }
    }

    public void setShowDialogue(boolean bool) {
        showDialogue = bool;
    }
}
