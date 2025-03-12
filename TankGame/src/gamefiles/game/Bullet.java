package gamefiles.game;

import gamefiles.Animation;
import gamefiles.GameConstants;
import gamefiles.ResourceManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author anthony-pc
 */
public class Bullet extends GameObject{

    public float x;
    public float y;
    private float vx;
    private float vy;
    private float angle;


    private float R = 10;

    private BufferedImage img;
    private BufferedImage bulletImg;
    private BufferedImage shieldedImg;
    private float speed;
    private boolean active;
    private GameWorld gameWorld;
    private int bounces;
    private boolean bouncingBullets;
    List<Animation> animations = new ArrayList<>();
    private Tank firingTank;

    Bullet(float x, float y, float angle, BufferedImage img, GameWorld gameWorld, Tank firingTank) {
        super(x, y, img);
        this.x = x;
        this.y = y;
        this.vx = 0;
        this.vy = 0;
        this.img = img;
        this.angle = angle;
        this.active = true;
        this.gameWorld =gameWorld;
        this.bounces = 0;
        this.firingTank = firingTank;

    }


    void update() {
        if (!active) return;
        for (int i = 0; i < this.animations.size(); i++) {
            this.animations.get(i).update();
        }


        vx = Math.round(R * Math.cos(Math.toRadians(angle)));
        vy = Math.round(R * Math.sin(Math.toRadians(angle)));
        x += vx;
        y += vy;
        checkCollisions();
        checkBorder();
    }

    private void checkCollisions() {
        List<GameObject> gameObjects = gameWorld.getGameObjects();

        for (GameObject obj : gameObjects) {
            if (obj == firingTank) continue;

            if (getBounds().intersects(obj.getBounds())) {
                if (obj instanceof BreakableWall) {
                    this.animations.add(new Animation(this.x, this.y, ResourceManager.getAnimation("explosion_lg")));
                    gameWorld.removeGameObject(obj);
                    active = false;
                    return;
                } else if (obj instanceof Tank) {
                    ((Tank) obj).takeDamage();
                    this.animations.add(new Animation(this.x, this.y, ResourceManager.getAnimation("explosion_lg")));
                    active = false;
                    return;
                } else if (obj instanceof Wall) {
                    active = false;
                    this.animations.add(new Animation(this.x, this.y, ResourceManager.getAnimation("explosion_lg")));
                    return;
                }
            }
        }
    }


    private void checkBorder() {
        if (x < 30 || x >= GameConstants.GAME_WORLD_WIDTH - 88 || y < 40 || y >= GameConstants.GAME_WORLD_HEIGHT - 115) {
            this.active = false;
        }
    }

    public Rectangle getNextPosition() {
        int nextX = (int) (x + Math.cos(Math.toRadians(angle)) * speed);
        int nextY = (int) (y + Math.sin(Math.toRadians(angle)) * speed);
        return new Rectangle(nextX, nextY, img.getWidth(), img.getHeight());
    }

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, img.getWidth(), img.getHeight());
    }

    @Override
    public String toString() {
        return "x=" + x + ", y=" + y + ", angle=" + angle;
    }


    public void drawImage(Graphics2D g) {
        if (!active) return;

        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), this.img.getWidth() / 2.0, this.img.getHeight() / 2.0);
        g.drawImage(this.img, rotation, null);
        for (int i = 0; i < animations.size(); i++) {
            this.animations.get(i).render(g);

        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
