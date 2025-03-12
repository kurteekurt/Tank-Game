package gamefiles.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PowerUp extends GameObject {

    private float x;
    private float y;
    private BufferedImage img;
    private boolean active;
    private String type;

    public PowerUp(float x, float y, BufferedImage img, String type) {
        super(x, y, img);
        this.x = x;
        this.y = y;
        this.img = img;
        this.type = type;
        this.active = true;
    }

    public void drawImage(Graphics2D g) {
        g.drawImage(img, (int) x, (int) y, null);
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, img.getWidth(), img.getHeight());
    }

    public boolean isActive() {
        return active;
    }

    public String getType() {
        return type;
    }

    public void deactivate(){
        this.active = false;
    }

    public void applyTo(Tank tank) {
    }

    public void setActive(boolean b) {
        this.active = b;
    }
}
