package gamefiles.game;


import gamefiles.*;
import gamefiles.menus.EndGamePanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @author anthony-pc
 */
public class GameWorld extends JPanel implements Runnable {

    private BufferedImage world;
    private Tank t1;
    private Tank t2;
    private final Launcher lf;
    private long tick = 0;
    private BufferedImage backgroundImage;
    private final int MINI_MAP_SCALE = 4;
    private List<PowerUp> powerUps;
    ArrayList<GameObject> gObjs = new ArrayList<>(2000);
    private boolean gameOver = false;
    private Launcher launcher;
    List<Animation> animations = new ArrayList<>();

    /**
     *
     */
    public GameWorld(Launcher lf) {
        this.lf = lf;
    }

    @Override
    public void run() {
        try {
            while (true) {
                this.tick++;
                this.t1.update();
                this.t2.update();
                this.checkCollisions();
                for (int i = 0; i < this.animations.size(); i++) {
                    this.animations.get(i).update();
                }
                this.checkGameOver();
                this.renderFrame();
                this.repaint();
                /*
                 * Sleep for 1000/144 ms (~6.9ms). This is done to have our 
                 * loop run at a fixed rate per/sec. 
                */
                Thread.sleep(1000 / 144);
            }
        } catch (InterruptedException ignored) {
            System.out.println(ignored);
        }
    }

    private void checkGameOver() {
        if (t1.getLives() <= 0 || t2.getLives() <= 0) {
            this.tick = 0;

            String winner = t1.getLives() <= 0 ? "Player 2" : "Player 1";

            // Get the EndGamePanel and set the winner
            lf.getEndGamePanel().setWinner(winner);

            // Switch to the end screen
            this.tick = 0;
            lf.setFrame("end");
        }
    }


    private void checkCollisions() {

        handleWallCollisions();
        handlePowerUpCollisions();
    }


    private void handleWallCollisions() {
        handleTankWallCollision(t1);
        handleTankWallCollision(t2);
    }

    private void handleTankWallCollision(Tank tank) {
        // Check for collision with all walls
        for (GameObject obj : gObjs) {
            if (obj instanceof Wall) {
                Wall wall = (Wall) obj;
                if (tank.getBounds().intersects(wall.getBounds())) {
                    tank.revertToPreviousPosition();
                    this.animations.add(new Animation(tank.getX(), tank.getY(), ResourceManager.getAnimation("explosion_sm")));
                    ResourceManager.getSound("bump").play();
                }
            }

            if (obj instanceof BreakableWall) {
                BreakableWall bwall = (BreakableWall) obj;
                if (tank.getBounds().intersects(bwall.getBounds())) {
                    tank.revertToPreviousPosition();
                    this.animations.add(new Animation(tank.getX(), tank.getY(), ResourceManager.getAnimation("explosion_sm")));
                    ResourceManager.getSound("bump").play();
                }
            }
        }
    }

    private void handlePowerUpCollisions() {
        Iterator<GameObject> iterator = gObjs.iterator();
        while (iterator.hasNext()) {
            GameObject obj = iterator.next();
            if (obj instanceof PowerUp) {
                PowerUp powerUp = (PowerUp) obj;
                if (powerUp.getBounds().intersects(t1.getBounds())) {
                    powerUp.applyTo(t1);
                    iterator.remove();
                } else if (powerUp.getBounds().intersects(t2.getBounds())) {
                    powerUp.applyTo(t2);
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Reset game to its initial state.
     */
    public void respawnTank(Tank tank) {
        if(tank.equals(t1)){
            tank.setX(300);
            tank.setY(300);
            tank.setAngle(0);
        }

        if(tank.equals(t2)){
            tank.setX(1080);
            tank.setY(1080);
            tank.setAngle(180);
        }


    }

    /**
     * Load all resources for Tank Wars Game. Set all Game Objects to their
     * initial state as well.
     */
    public void InitializeGame() {
        this.world = new BufferedImage(GameConstants.GAME_WORLD_WIDTH,
                GameConstants.GAME_WORLD_HEIGHT,
                BufferedImage.TYPE_INT_RGB);
        int row = 0;
        InputStreamReader isr = new InputStreamReader(Objects.requireNonNull(ResourceManager.class.getClassLoader().getResourceAsStream("map.csv")));

        try(BufferedReader mapReader = new BufferedReader(isr)){
            while(mapReader.ready()){
                String line = mapReader.readLine();
                String[] objs = line.split(",");
                for(int col = 0; col < objs.length; col++){
                    String gameItem =  objs[col];
                    if("0".equals(gameItem)) continue;
                    if("7".equals(gameItem)) continue;
                    this.gObjs.add(GameObject.newInstance(gameItem, col * 32, row * 32));
                }
                row++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        t1 = new Tank(300, 300, 0, 0, (short) 0, ResourceManager.getSprite("t1"), this);
        TankControl tc1 = new TankControl(t1, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE);
        this.lf.getJf().addKeyListener(tc1);

        t2 = new Tank(1080, 1080, 0, 0, (short) 180, ResourceManager.getSprite("t2"), this);
        TankControl tc2 = new TankControl(t2, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_ENTER);
        this.lf.getJf().addKeyListener(tc2);
        gObjs.add(t1);
        gObjs.add(t2);
    }

    private void renderFrame(){
        Graphics2D buffer = world.createGraphics();
        buffer.drawImage(backgroundImage, 0, 0, GameConstants.GAME_WORLD_WIDTH, GameConstants.GAME_WORLD_HEIGHT, null);
        renderFloor(buffer);
        this.gObjs.forEach(go -> go.drawImage(buffer));
        this.t1.drawImage(buffer);
        this.t2.drawImage(buffer);
        for (int i = 0; i < this.animations.size(); i++) {
            this.animations.get(i).render(buffer);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;


        this.renderSplitScreen(g2);

        this.displayMiniMap(g2);
    }

    private void renderFloor(Graphics buffer){
        BufferedImage floor = ResourceManager.getSprite("floor");
        for(int i = 0; i < GameConstants.GAME_WORLD_WIDTH; i+=320){
            for(int j = 0; j < GameConstants.GAME_WORLD_HEIGHT; j+=240){
                buffer.drawImage(floor, i, j, null);
            }
        }
    }

    private void displayMiniMap(Graphics2D onScreenPanel){
        BufferedImage mm = this.world.getSubimage(0,0,GameConstants.GAME_WORLD_WIDTH, GameConstants.GAME_WORLD_HEIGHT);
        double mmx = GameConstants.GAME_SCREEN_WIDTH/2. - (GameConstants.GAME_WORLD_WIDTH*.15)/2;
        double mmy = GameConstants.GAME_SCREEN_HEIGHT - (GameConstants.GAME_WORLD_HEIGHT*.15);
        AffineTransform scaler =AffineTransform.getTranslateInstance(mmx, mmy);
        scaler.scale(.15,.15);
        onScreenPanel.drawImage(mm,scaler,null);
    }

    private void renderSplitScreen(Graphics2D onScreenPanel) {
        int subimageWidth = GameConstants.GAME_SCREEN_WIDTH / 2;
        int subimageHeight = GameConstants.GAME_SCREEN_HEIGHT - 300;

        int t1ScreenX = Math.max(0, Math.min((int) t1.getScreen_x(), GameConstants.GAME_WORLD_WIDTH - subimageWidth));
        int t1ScreenY = Math.max(0, Math.min((int) t1.getScreen_y(), GameConstants.GAME_WORLD_HEIGHT - subimageHeight));
        int t2ScreenX = Math.max(0, Math.min((int) t2.getScreen_x(), GameConstants.GAME_WORLD_WIDTH - subimageWidth));
        int t2ScreenY = Math.max(0, Math.min((int) t2.getScreen_y(), GameConstants.GAME_WORLD_HEIGHT - subimageHeight));

        BufferedImage lh = this.world.getSubimage(t1ScreenX, t1ScreenY, subimageWidth, subimageHeight);
        BufferedImage rh = this.world.getSubimage(t2ScreenX, t2ScreenY, subimageWidth, subimageHeight);
        onScreenPanel.drawImage(lh, 0, 0, null);
        onScreenPanel.drawImage(rh, GameConstants.GAME_SCREEN_WIDTH / 2 + 4, 0, null);

        renderHUD(onScreenPanel, t1, 0);
        renderHUD(onScreenPanel, t2, GameConstants.GAME_SCREEN_WIDTH / 2 + 4);
    }


    private void renderHUD(Graphics2D g, Tank tank, int offsetX) {
        BufferedImage hudBackground = ResourceManager.getSprite("HUD");
        int hudWidth = GameConstants.GAME_SCREEN_WIDTH / 2;
        int originalHudWidth = 258;
        int originalHudHeight = 132;
        float aspectRatio = (float) originalHudHeight / originalHudWidth;
        int hudHeight = (int) (hudWidth * aspectRatio);
        int hudY = GameConstants.GAME_SCREEN_HEIGHT - hudHeight;

        if (hudBackground != null) {
            g.drawImage(hudBackground, offsetX, hudY, hudWidth, hudHeight, null);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(offsetX, hudY, hudWidth, hudHeight);
        }

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        Font font = new Font("Arial", Font.BOLD, 16);
        g.setFont(font);
        g.setColor(Color.WHITE);

        BufferedImage heartIcon = ResourceManager.getSprite("health");
        int heartIconWidth = heartIcon.getWidth();
        int heartIconHeight = heartIcon.getHeight();
        int totalHeartsWidth = heartIconWidth * tank.getLives();
        int heartsStartX = offsetX + (hudWidth - totalHeartsWidth) / 2;

        for (int i = 0; i < tank.getLives(); i++) {
            g.drawImage(heartIcon, heartsStartX + i * heartIconWidth, hudY + 120, null);
        }

        // Draw health bar
        int healthBarWidth = 200;
        int healthBarHeight = 20;
        int healthBarX = offsetX + (hudWidth - healthBarWidth) / 2;
        int healthBarY = hudY + 50;
        drawHealthBar(g, healthBarX, healthBarY, healthBarWidth, healthBarHeight, tank.getHealth());

        // Calculate and draw centered text for active power-up
//        String powerUpText = "Power-Up: " + (tank.getActivePowerUp() != null ? tank.getActivePowerUp().getType() : "None");
//        int powerUpTextWidth = g.getFontMetrics().stringWidth(powerUpText);
//        int powerUpTextX = offsetX + (hudWidth - powerUpTextWidth) / 2;
//        g.drawString(powerUpText, powerUpTextX, hudY + 90);
    }

    private void drawHealthBar(Graphics2D g, int x, int y, int width, int height, int health) {
        g.setColor(Color.GRAY);
        g.fillRect(x, y, width, height);

        int filledWidth = (int) ((health / 100.0) * width);

        g.setColor(Color.GREEN);
        g.fillRect(x, y, filledWidth, height);

        g.setColor(Color.WHITE);
        g.drawRect(x, y, width, height);
    }



    public List<GameObject> getGameObjects() {
        return gObjs;
    }

    public void removeGameObject(GameObject obj) {
        gObjs.remove(obj);
    }
}
