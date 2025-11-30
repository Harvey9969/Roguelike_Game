package core.charecters.princess;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioManager {

    private Clip clip;

    public AudioManager() {
        clip = null;
    }

    public void playAudio(String path) {
        stopAudio();

        loadAudio(path);

        clip.setFramePosition(0);
        clip.start();
    }

    public void stopAudio() {
        if (clip != null) {
            clip.stop();
            clip = null;
        }
    }

    public boolean isPlaying() {
        return clip != null;
    }

    private void loadAudio(String path) {
        File file = new File(path);

        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);

            clip = AudioSystem.getClip();
            clip.open(audioInputStream);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }
}
