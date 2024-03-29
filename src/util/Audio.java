package util;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
  
public class Audio {
  
    private static final byte STOP              = 0;
    private static final byte PAUSE             = 1;
    private static final byte PLAY              = 100;
    private Clip clip                           = null;
    private byte status                         = STOP;
    private AudioInputStream audioInputStream   = null;
    private boolean ready                       = false;
    private long microsecondPosition            = 0;
    private float oldValue                      = 80f; 
  
    /**
     * Audio class
     * @param filePath
     * @param loop
     */
    public Audio(String filePath, int loop) {
        try {
            audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (audioInputStream != null) {
            try {
                clip = AudioSystem.getClip();    
            } catch (Exception e) {}

            try {
                clip.open(audioInputStream);
                this.ready = true;
            } catch (Exception e) {
                this.ready = false;
            }
        }
    }

    public void playContinuously() {
        play(-1);
    }

    public void play() {
        play(0);
    }

    public void addVolume(float volume) {
        FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
        float value = control.getValue() + volume;
        if (value < control.getMaximum()) {
            control.setValue(value);
        }
    }

    public void decVolume(float volume) {
        FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
        float value = control.getValue() - volume;
        control.setValue(value);
    }

    public void play(int loop) {
        if (clip.isRunning()) {
            clip.stop();
        }
        clip.setMicrosecondPosition(this.microsecondPosition);
        try {
            Thread.sleep(1);    
        } catch (Exception e) {
        }
        clip.loop(loop);
        clip.start();
        status = PLAY;
    }

    public void pause() {
        this.microsecondPosition = clip.getMicrosecondPosition();
        clip.stop();
        status = PAUSE;
    }

    public void stop() {
        clip.stop();
        this.microsecondPosition = 0;
        clip.setMicrosecondPosition(this.microsecondPosition);
        clip.setFramePosition(0);
        status = STOP;
    }

    public boolean isReady() {
        return (this.ready);
    }

    public byte getStatus() {
        return (this.status);
    }

    public void muteVolume() {
        FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
        this.oldValue = control.getValue();
        control.setValue(-80);
    }

    public void unmuteVolume() {
        FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
        control.setValue(this.oldValue);
    }

    public void setVolume(byte volume) {
        FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
        switch (volume) {
            case 5:
                control.setValue(0);
                break;
            case 4:
                control.setValue(-5);
                break;
            case 3:
                control.setValue(-10);
                break;
            case 2:
                control.setValue(-15);
                break;
            case 1:
                control.setValue(-20);
                break;
            case 0:
                control.setValue(-25);
                break;
        }
    }
}