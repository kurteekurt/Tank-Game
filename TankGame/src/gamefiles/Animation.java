package gamefiles;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Animation {
    private float x, y;
    private List<BufferedImage> frames;
    private long delay = 30;
    private long timeSinceLastFrameUpdate = 0;
    private int currentFrame;
    private boolean running = false;

    public Animation(float x, float y, List<BufferedImage> frames) {
        this.x = x;
        this.y = y;
        this.frames = frames;
        this.running = true;
        this.currentFrame = 0;
    }

    public void update() {
        long currentTime = System.currentTimeMillis();

        if (this.timeSinceLastFrameUpdate + delay < currentTime) {
            this.currentFrame++;
            if (this.currentFrame >= this.frames.size()) {
                this.running = false;
                this.currentFrame = this.frames.size() - 1;
            }
            this.timeSinceLastFrameUpdate = currentTime;
        }
    }

    public void render(Graphics g){
        if(this.running){
            g.drawImage(this.frames.get(currentFrame), (int) x, (int) y, null);
        }
    }
}
