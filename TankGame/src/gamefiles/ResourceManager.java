package gamefiles;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.Objects;

public class ResourceManager {
    private final static Map<String, BufferedImage> sprites = new HashMap<>();
    private final static Map<String, Sound> sounds = new HashMap<>();
    private final static Map<String, List<BufferedImage>> animations = new HashMap<>();
    private final static Map<String, Integer> animInfo = new HashMap<>(){{
        put("explosion_sm", 6);
        put("explosion_lg", 6);
        put("puffsmoke", 31);
        put("powerpick", 31);
        put("bulletshoot", 23);
    }};

    private static BufferedImage loadSprite(String path) throws IOException {
        BufferedImage img = ImageIO.read(Objects.requireNonNull(ResourceManager.class.getClassLoader().getResource(path),
                "Resource " + path + " not found")
        );
        if (img == null) {
            System.out.println("Resource " + path + " could not be loaded.");
        }
        return img;
    }

    private static Sound loadSound(String path) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream ais = AudioSystem.getAudioInputStream(
                Objects.requireNonNull(
                        ResourceManager.class.getClassLoader().getResource(path), "Sound %s not found".formatted(path))
        );
        Clip c = AudioSystem.getClip();
        c.open(ais);
        Sound s = new Sound(c);

        return s;
    }

    public static void loadSounds(){
        try {
            ResourceManager.sounds.put("bg", loadSound("Music.mid"));
            ResourceManager.sounds.put("largeboom", loadSound("Explosion_large.wav"));
            ResourceManager.sounds.put("smallboom", loadSound("Explosion_small.wav"));
            ResourceManager.sounds.put("shoot", loadSound("laser-gun-short.wav"));
            ResourceManager.sounds.put("tankmove", loadSound("tankmove.wav"));
            ResourceManager.sounds.put("bump", loadSound("bump.wav"));
            ResourceManager.sounds.put("tankexplode", loadSound("tankexplode.wav"));
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            throw new RuntimeException(e);
            }
    }

    public static void loadAnims(){
        String baseFormat = "animations/%s/%s_%04d.png";
        ResourceManager.animInfo.forEach((animationName, frameCount) -> {
            List<BufferedImage> f = new ArrayList<>(frameCount);
            try{
                for (int i = 1; i < frameCount; i++) {
                    String spritePath = String.format(baseFormat, animationName, animationName ,i);
                    f.add(loadSprite(spritePath));
                }
                ResourceManager.animations.put(animationName, f);
            } catch (IOException e){
                throw new RuntimeException(e);
            }
        });
    }


    private static void initSprites() throws IOException {
        ResourceManager.sprites.put("t1", loadSprite("tank1.png"));
        ResourceManager.sprites.put("t2", loadSprite("tank2.png"));
        ResourceManager.sprites.put("rocket", loadSprite("missile.png"));
        ResourceManager.sprites.put("shield", loadSprite("protection.png"));
        ResourceManager.sprites.put("bullet", loadSprite("Shell.png"));
        ResourceManager.sprites.put("speed", loadSprite("speed.png"));
        ResourceManager.sprites.put("breakablewall", loadSprite("wall2.png"));
        ResourceManager.sprites.put("unbreakablewall", loadSprite("wall1.png"));
        ResourceManager.sprites.put("floor", loadSprite("Background.bmp"));
        ResourceManager.sprites.put("health", loadSprite("heart.png"));
        ResourceManager.sprites.put("HUD", loadSprite("copper_hud.png"));
    }

    public static void loadAssets(){
        try{
            initSprites();
            loadSounds();
            loadAnims();
        } catch (IOException e) {
            throw new RuntimeException("Loading assests failed", e);
        }

    }

    public static BufferedImage getSprite(String name) {
        BufferedImage sprite = ResourceManager.sprites.get(name);
        if (sprite == null) {
            System.out.println("Sprite " + name + " not found.");
        }
        return sprite;
    }

    public static Sound getSound(String key){
        if(!ResourceManager.sounds.containsKey(key)){
            throw new IllegalArgumentException("Sprite %s does not exist in map".formatted(key));
        }
        return ResourceManager.sounds.get(key);

    }

    public static List<BufferedImage> getAnimation(String key){
        if(!ResourceManager.animations.containsKey(key)){
            throw new IllegalArgumentException("Sprite %s does not exist in map".formatted(key));
        }
        return ResourceManager.animations.get(key);
    }
}
