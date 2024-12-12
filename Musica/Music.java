import java.io.InputStream;
import javax.sound.sampled.*;

public class Music extends Thread {
    private Clip clip;
    private boolean isLoop;
    private int delayMilliseconds;
    private boolean isPaused;
    private long clipTimePosition;

    public Music(InputStream stream, boolean isLoop) {
        try {
            this.isLoop = isLoop;
            this.delayMilliseconds = 0; 
            this.isPaused = false;

            if (stream == null) {
                throw new IllegalArgumentException("Audio input stream cannot be null");
            }

            try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(stream)) {
                clip = AudioSystem.getClip();
                clip.open(audioStream);
            }

            setVolume(0.6f); 

        } catch (Exception e) {
            System.out.println("Error loading audio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setVolume(float volume) {
        if (clip != null) {
            try {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float gain = gainControl.getMinimum() + (gainControl.getMaximum() - gainControl.getMinimum()) * volume;
                gainControl.setValue(gain);
            } catch (IllegalArgumentException e) {
                System.out.println("Error setting volume: " + e.getMessage());
            }
        }
    }

    // Method to start playback
    public void startPlayback() {
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        }
    }

    public void pausePlayback() {
        if (clip.isRunning()) {
            clipTimePosition = clip.getMicrosecondPosition();
            clip.stop();
            isPaused = true;
        }
    }

    public void resumePlayback() {
        if (clip != null && isPaused) {
            clip.setMicrosecondPosition(clipTimePosition);
            clip.start();
            isPaused = false;
        }
    }

    public void close() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }

    @Override
    public void run() {
        try {
            if (delayMilliseconds > 0) {
                Thread.sleep(delayMilliseconds);
            }

            if (clip != null) {
                clip.setFramePosition(0);
                clip.loop(isLoop ? Clip.LOOP_CONTINUOUSLY : 0);
            }
        } catch (Exception e) {
            System.out.println("Error during playback: " + e.getMessage());
        }
    }

    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }

    public boolean hasFinished() {
        return clip != null && clip.getMicrosecondPosition() == clip.getMicrosecondLength(); 
    }

    public void setMicrosecondPosition(long clipPosition) {
        if (clip != null) {
            clip.setMicrosecondPosition(clipPosition);
        }
    }

    public double getMicrosecondLength() {
        if (clip != null) {
            return clip.getMicrosecondLength(); 
        } else {
            return 0; 
        }
    }
    
    public void updatePositionFromSlider(long clipPosition) {
        if (clip != null) {
            clip.setMicrosecondPosition(clipPosition); 
            if (!clip.isRunning()) {
                clip.start();
            }
        }
    }
    
    public long getMicrosecondPosition() {
        if (clip != null) {
            return clip.getMicrosecondPosition();
        } else {
            return 0;
        }
    }
}