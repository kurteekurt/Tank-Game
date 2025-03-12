package gamefiles.game;

import gamefiles.ResourceManager;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class GameObject {
    protected float x, y;
    protected BufferedImage img;

    public GameObject(float x, float y, BufferedImage img) {
        if (img == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }
        this.x = x;
        this.y = y;
        this.img = img;
    }


    public static GameObject newInstance(String type, float x, float y){
        return switch (type){
            case "3", "9" -> new Wall(x, y, ResourceManager.getSprite("unbreakablewall"));
            case "2" -> new BreakableWall(x, y, ResourceManager.getSprite("breakablewall"));
            case "5" -> new Shield(x, y, ResourceManager.getSprite("shield"));
            case "6" -> new Health(x, y, ResourceManager.getSprite("health"));
            case "4" -> new Rocket(x, y, ResourceManager.getSprite("rocket"));
            case "8" -> new Speed(x, y, ResourceManager.getSprite("speed"));
            default -> throw new IllegalArgumentException("Invalid type -> %s\n".formatted(type));
        };

    }

    public abstract void drawImage(Graphics2D g);

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, img.getWidth(), img.getHeight());
    }

}