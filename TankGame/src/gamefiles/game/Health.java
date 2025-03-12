package gamefiles.game;

import java.awt.image.BufferedImage;

public class Health extends PowerUp {

    public Health(float x, float y, BufferedImage img) {
        super(x, y, img, "life");
    }

    @Override
    public void applyTo(Tank tank) {
        tank.addLife();
    }
}