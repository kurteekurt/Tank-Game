package gamefiles.menus;

import gamefiles.Launcher;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class EndGamePanel extends JPanel {

    private BufferedImage menuBackground;
    private final Launcher lf;
    private String winner;

    public EndGamePanel(Launcher lf) {
        this.lf = lf;
        this.winner = "";
        this.setDoubleBuffered(true);

        try {
            menuBackground = ImageIO.read(this.getClass().getClassLoader().getResource("title.png"));
        } catch (IOException e) {
            System.out.println("Error cant read menu background");
            e.printStackTrace();
            System.exit(-3);
        }

        this.setBackground(Color.BLACK);
        this.setLayout(null);

        JButton start = new JButton("Restart Game");
        start.setFont(new Font("Courier New", Font.BOLD, 24));
        start.setBounds(150, 300, 250, 50);
        start.addActionListener((actionEvent -> this.lf.setFrame("game")));

        JButton exit = new JButton("Exit");
        exit.setFont(new Font("Courier New", Font.BOLD, 24));
        exit.setBounds(150, 400, 250, 50);
        exit.addActionListener((actionEvent -> this.lf.closeGame()));

        this.add(start);
        this.add(exit);
    }

    public void setWinner(String winner) {
        this.winner = winner;
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(this.menuBackground, 0, 0, null);

        // Adjusting the position of the winner text
        g2.setFont(new Font("Courier New", Font.BOLD, 36));
        g2.setColor(Color.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        int textX = (this.getWidth() - fm.stringWidth(winner)) / 2;
        int textY = (this.getHeight() - fm.getHeight()) / 2 + 50;
        g2.drawString(winner + " wins!", textX, textY);
    }
}
