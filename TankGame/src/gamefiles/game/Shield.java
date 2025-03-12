package gamefiles.game;

import java.awt.image.BufferedImage;

public class Shield extends PowerUp {

    public Shield(float x, float y, BufferedImage img) {
        super(x, y, img, "shield");
    }

    @Override
    public void applyTo(Tank tank) {
        tank.activateShield();
    }
}