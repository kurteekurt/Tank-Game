package gamefiles.game;

import java.awt.image.BufferedImage;

public class Speed extends PowerUp {

    public Speed(float x, float y, BufferedImage img) {
        super(x, y, img, "bouncingBullet");
    }

    @Override
    public void applyTo(Tank tank) {
        tank.activateSpeed();
    }
}
