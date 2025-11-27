package core.charecters.animation;

import utils.DS.RecordLike.Dir;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class State {
    private final String spritePath;
    private final Map<String, Integer> frameNums;

    private String relPos;
    private Dir facing;
    private int frameInd;

    public State(String spriteFolder) {
        spritePath = "src/assets/sprites/" + spriteFolder;
        File spriteDirectory = new File(spritePath);

        frameNums = new HashMap<>();

        if (spriteDirectory.isDirectory()) {
            for (
                    File file0 : // iterating over NORTH, SOUTH, ...
                    Objects.requireNonNull(spriteDirectory.listFiles())
            ) {
                if (!file0.isDirectory()) {
                    continue;
                }

                for (
                        File file1: // iterating over WALK, ATTACK, ...
                        Objects.requireNonNull(file0.listFiles())
                ) {

                    if (!file1.isDirectory()) {
                        continue;
                    }

                    for (
                            File file2: // iterating over 0.png, 1.png, ...
                            Objects.requireNonNull(file1.listFiles())
                    ) {
                        if (!file2.getName().endsWith(".png")) {
                            continue;
                        }

                        String key = file0.getName() + "_" + file1.getName();
                        frameNums.put(
                                key,
                                frameNums.getOrDefault(key, 0) + 1
                        );
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Sprite folder could not be found");
        }
    }

    public void loop(String relPos, Dir facing) {
        checkState(relPos, facing);

        if (!facing.equals(this.facing) || !relPos.equals(this.relPos)) {
            frameInd = 0;
        } else {
            frameInd = (frameInd + 1) % frameNums.get(key());
        }

        this.relPos = relPos;
        this.facing = facing;
    }

    public boolean playthrough(boolean cycle, String relPos, Dir facing) {
        checkState(relPos, facing);

        boolean unfinished = true;

        if (!facing.equals(this.facing) || !relPos.equals(this.relPos)) {
            frameInd = 0;
        } else {
            frameInd = (frameInd + 1) % frameNums.get(key());

            if (
                    (frameInd == 0 && cycle)
                    || (frameInd == frameNums.get(key()) - 1 && !cycle)
            ) {
                unfinished = false;
            }
        }

        this.relPos = relPos;
        this.facing = facing;

        return unfinished;
    }

    public String getSpriteFile() {
        return
                spritePath + "/" +
                String.format("%s/%s/%d.png", facing, relPos, frameInd);
    }

    private String key() {
        return facing + "_" + relPos;
    }

    private void checkState(String relPos, Dir facing) {
        if (!frameNums.containsKey(facing.toString() + "_" + relPos)) {
            throw new IllegalArgumentException(
                    "Positions must correspond to sprite folders, "
                    + facing.toString() + "_" + relPos
                    + " does not for " + spritePath
            );
        }
    }
}
