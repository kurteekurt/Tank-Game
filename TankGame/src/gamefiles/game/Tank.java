package gamefiles.game;

import gamefiles.Animation;
import gamefiles.GameConstants;
import gamefiles.ResourceManager;
import gamefiles.Sound;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author anthony-pc
 */
public class Tank extends GameObject{


    public float x;
    public float y;
    private float vx;
    private float vy;
    private float angle;
    private float speed;
    private int health;
    private int lives;
    private float screen_x;
    private float screen_y;

    private float R = 5;
    private float ROTATIONSPEED = 3.0f;
    private int rocketDamage = 20;
    private int normalDamage = 10;

    private BufferedImage img;
    private BufferedImage bulletImg;
    private BufferedImage shieldedImg;
    private boolean UpPressed;
    private boolean DownPressed;
    private boolean RightPressed;
    private boolean LeftPressed;
    private boolean ShootPressed;
    private boolean shielded;
    private boolean hasRockets;
    private boolean hasSpeed;
    private long shieldeduntil;
    private long rocketsUntil;
    private long bouncingBulletsUntil;

    List<Bullet> ammo = new ArrayList<>();
    List<Animation> animations = new ArrayList<>();


    private ArrayList<Bullet> bullets;
    Bullet b;
    private GameWorld gameWorld;
    private long speedUntil;

    Tank(float x, float y, float vx, float vy, float angle, BufferedImage img, GameWorld gameWorld) {
        super(x, y, img);
        this.screen_x = x;
        this.screen_y = y;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.img = img;
        this.bulletImg = bulletImg;
        this.angle = angle;
        this.bullets = new ArrayList<>();
        this.health = 100;
        this.lives = 3;
        this.shielded = false;
        this.hasRockets = false;
        this.hasSpeed = false;
        this.gameWorld = gameWorld;
    }

    void setX(float x){ this.x = x; }

    void setY(float y) { this. y = y;}

    void toggleUpPressed() {
        this.UpPressed = true;
    }

    void toggleDownPressed() {
        this.DownPressed = true;
    }

    void toggleRightPressed() {
        this.RightPressed = true;
    }

    void toggleLeftPressed() {
        this.LeftPressed = true;
    }

    void unToggleUpPressed() {
        this.UpPressed = false;
    }

    void unToggleDownPressed() {
        this.DownPressed = false;
    }

    void unToggleRightPressed() {
        this.RightPressed = false;
    }

    void unToggleLeftPressed() {
        this.LeftPressed = false;
    }

    void toggleShootPressed() {
        this.ShootPressed = true;
    }
    void unToggleShootPressed() {
        this.ShootPressed = false;
    }

    public void update() {
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < animations.size(); i++) {
            animations.get(i).update();

        }
        if (this.UpPressed) {
            this.moveForwards();
        }

        if (this.DownPressed) {
            this.moveBackwards();
        }

        if (this.LeftPressed) {
            this.rotateLeft();
        }

        if (this.RightPressed) {
            this.rotateRight();
        }

        if (this.ShootPressed) {
            ResourceManager.getSound("shoot").play();
            shoot();
            this.animations.add(new Animation(this.x, this.y, ResourceManager.getAnimation("bulletshoot")));
            this.ShootPressed = false;
        }

        for (int i = 0; i < this.ammo.size(); i++) {
            this.ammo.get(i).update();
        }

        if(b != null){
            b.update();
        }

        if (shielded && currentTime > shieldeduntil) {
            shielded = false;
        }

        if (hasSpeed && currentTime > speedUntil) {
            hasSpeed = false;
        }

        if (hasRockets && currentTime > rocketsUntil) {
            hasRockets = false;
            normalDamage = 10;
        }

        this.centerScreen();
    }

    private void centerScreen(){
        this.screen_x = this.x - GameConstants.GAME_SCREEN_WIDTH/4f;
        this.screen_y = this.y - GameConstants.GAME_SCREEN_HEIGHT/2f;

        if(this.screen_x < 0) this.screen_x = 0;
        if(this.screen_y < 0) this.screen_y = 0;

        if(this.screen_x > this.screen_x + GameConstants.GAME_WORLD_WIDTH - GameConstants.GAME_SCREEN_WIDTH/2f){
            this.screen_x = GameConstants.GAME_WORLD_WIDTH - GameConstants.GAME_SCREEN_WIDTH/2f;
        }

        if(this.screen_y > GameConstants.GAME_WORLD_HEIGHT - GameConstants.GAME_SCREEN_HEIGHT){
            this.screen_y = GameConstants.GAME_WORLD_HEIGHT - GameConstants.GAME_SCREEN_HEIGHT;
        }
    }

    private void rotateLeft() {
        this.angle -= this.ROTATIONSPEED;
    }

    private void rotateRight() {
        this.angle += this.ROTATIONSPEED;
    }

    private void moveBackwards() {
        vx =  Math.round(R * Math.cos(Math.toRadians(angle)));
        vy =  Math.round(R * Math.sin(Math.toRadians(angle)));
        x -= vx;
        y -= vy;
       checkBorder();

    }

    private void moveForwards() {
        vx = Math.round(R * Math.cos(Math.toRadians(angle)));
        vy = Math.round(R * Math.sin(Math.toRadians(angle)));
        x += vx;
        y += vy;
        checkBorder();

    }


    private void checkBorder() {
        if (x < 30) {
            x = 30;
        }
        if (x >= GameConstants.GAME_WORLD_WIDTH - 88) {
            x = GameConstants.GAME_WORLD_WIDTH - 88;
        }
        if (y < 40) {
            y = 40;
        }
        if (y >= GameConstants.GAME_WORLD_HEIGHT - 115) {
            y = GameConstants.GAME_WORLD_HEIGHT - 115;
        }
    }

    public void shoot() {
        BufferedImage bulletImage = ResourceManager.getSprite("bullet");
        int bulletX = (int) (x + Math.cos(Math.toRadians(angle)) * img.getWidth() / 2);
        int bulletY = (int) (y + Math.sin(Math.toRadians(angle)) * img.getHeight() / 2);

        this.ammo.add(
                new Bullet(bulletX, bulletY, angle, bulletImage, gameWorld, this));
    }

    public void takeDamage() {
        if(shielded) return;
        this.animations.add(new Animation(this.x, this.y, ResourceManager.getAnimation("puffsmoke")));
        if(hasRockets){
            this.health -= 10;
        }
        this.health -= 5;
        if (this.health <= 0) {
            this.animations.add(new Animation(this.x, this.y, ResourceManager.getAnimation("explosion_lg")));
            ResourceManager.getSound("tankexplode").play();
            gameWorld.respawnTank(this);
            lives--;
            if(lives > 0){
                this.health = 100;
            }
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
        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), this.img.getWidth() / 2.0, this.img.getHeight() / 2.0);
        g.drawImage(this.img, rotation, null);
        for (int i = 0; i < this.ammo.size(); i++) {
            this.ammo.get(i).drawImage(g);
        }
        for (int i = 0; i < animations.size(); i++) {
            this.animations.get(i).render(g);

        }

    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }


    public int getHealth() {
        return health;
    }



    public void revertToPreviousPosition() {
        x -= vx;
        y -= vy;
    }



    public void activateShield(){
        this.shielded = true;
        this.shieldeduntil = System.currentTimeMillis() + 10000;
    }

    public int getLives() {
        return lives;
    }

    public float getScreen_y() {
        return screen_y;
    }

    public float getScreen_x() {
        return screen_x;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void addLife() {
        this.lives++;
    }

    public void activateRockets() {
        this.hasRockets = true;
        this.rocketsUntil = System.currentTimeMillis() + 10000; // Rockets last for 10 seconds
        normalDamage = rocketDamage;
    }

    public void activateSpeed() {
        this.hasSpeed = true;
        this.R = 10;
        this.speedUntil = System.currentTimeMillis() + 10000; // Bouncing bullets last for 10 seconds
    }

    public void setAngle(int i) {
        this.angle = i;
    }
}
