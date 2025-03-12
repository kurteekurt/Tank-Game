package gamefiles;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound {
    private Clip clip;
    private int loopCount;

    public Sound(Clip c){
        this.clip = c;
        this.loopCount = 0;
    }

    public Sound(Clip c, int loopCount){
        this.clip = c;
        this.loopCount = loopCount;
        this.clip.loop(loopCount);
    }

    public void play(){
        clip.setFramePosition(0);
        clip.start();
    }

    public void stop(){
        this.clip.stop();
    }

    public void loop(){

    }

    public void loopContinuously(){
        this.clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void setVolume(float level){
        FloatControl volume = (FloatControl) this.clip.getControl(FloatControl.Type.MASTER_GAIN);
        volume.setValue(20.0f * (float) Math.log10(level));
    }
}
