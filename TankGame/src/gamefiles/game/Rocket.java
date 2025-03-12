package gamefiles.game;

import java.awt.image.BufferedImage;

public class Rocket extends PowerUp {

    public Rocket(float x, float y, BufferedImage img) {
        super(x, y, img, "rocket");
    }

    @Override
    public void applyTo(Tank tank) {
        tank.activateRockets();
    }
}