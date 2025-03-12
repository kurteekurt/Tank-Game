package gamefiles.game;

import gamefiles.GameConstants;
import gamefiles.ResourceManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Map {
    private boolean[][] breakableWalls;
    private boolean[][] unbreakableWalls;
    private List<Rectangle> walls;
    private List<Rectangle> obstacles;
    private List<PowerUp> powerUps;

    public Map(String mapFile) {
        this.unbreakableWalls = new boolean[GameConstants.GAME_WORLD_WIDTH][GameConstants.GAME_WORLD_HEIGHT];
        this.breakableWalls = new boolean[GameConstants.GAME_WORLD_WIDTH][GameConstants.GAME_WORLD_HEIGHT];
        this.walls = new ArrayList<>();
        this.obstacles = new ArrayList<>();
        this.powerUps = new ArrayList<>();

        loadMapFromCSV(mapFile);
    }

    private void loadMapFromCSV(String mapFile) {
        int row = 0;
        InputStreamReader isr = new InputStreamReader(Objects.requireNonNull(ResourceManager.class.getClassLoader().getResourceAsStream(mapFile)));

        try(BufferedReader mapReader = new BufferedReader(isr)){
            while(mapReader.ready()){
                String line = mapReader.readLine();
                String objs[] = line.split(",");
                for(int col = 0; col < objs.length; col++){
                    String gameItem =  objs[col];
                    int x = col * 32;
                    int y = row * 32;
                    if(gameItem.equals("9") || gameItem.equals("3")){
                        unbreakableWalls[x][y] = true;
                        walls.add(new Rectangle(x, y, 32, 32));
                    } else if(gameItem.equals("2")){
                        breakableWalls[x][y] = true;
                        obstacles.add(new Rectangle(x, y, 32, 32));
                    }
                }
                row++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void draw(Graphics2D g) {
        for (int i = 0; i < GameConstants.GAME_SCREEN_WIDTH; i += 50) {
            for (int j = 0; j < GameConstants.GAME_SCREEN_HEIGHT; j += 50) {
                if (unbreakableWalls[i][j]) {
                    g.drawImage(ResourceManager.getSprite("unbreakablewall"), i, j, null);
                }
                if (breakableWalls[i][j]) {
                    g.drawImage(ResourceManager.getSprite("breakablewall"), i, j, null);
                }
            }
        }

        // Draw power-ups
        for (PowerUp powerUp : powerUps) {
            powerUp.drawImage(g);
        }
    }

    public boolean checkCollision(Rectangle bounds) {
        for (int i = bounds.x; i < bounds.x + bounds.width; i++) {
            for (int j = bounds.y; j < bounds.y + bounds.height; j++) {
                if (i < 0 || j < 0 || i >= GameConstants.GAME_SCREEN_WIDTH || j >= GameConstants.GAME_SCREEN_HEIGHT) {
                    return false;
                }
                if (unbreakableWalls[i][j] || breakableWalls[i][j]) {
                    return true;
                }
            }
        }
        return false;
    }
}
