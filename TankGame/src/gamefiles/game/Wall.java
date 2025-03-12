package gamefiles.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Wall extends GameObject {

    public Wall(float x, float y, BufferedImage img) {
        super(x, y, img);
        this.x = x;
        this.y = y;
        this.img = img;
    }

    public void drawImage(Graphics2D g){
        g.drawImage(this.img, (int) this.x, (int) this.y, null);
    }
}
