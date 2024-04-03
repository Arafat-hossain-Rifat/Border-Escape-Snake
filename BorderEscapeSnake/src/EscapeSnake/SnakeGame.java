package EscapeSnake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;

public class SnakeGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Snake Game");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(440, 470);
                frame.setLocationRelativeTo(null);

                GamePanel gamePanel = new GamePanel();
                frame.add(gamePanel);

                frame.setVisible(true);
            }
        });
    }

    static class GamePanel extends JPanel implements ActionListener {
        private final int SCREEN_WIDTH = 400;
        private final int SCREEN_HEIGHT = 400;
        private final int UNIT_PIXEL_SIZE = 20;
        private final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_PIXEL_SIZE;
        private int initialSnakeLength = 3;
        private char direction = 'R';
        private int score = 0;
        private int delay = 200;
        private int[] snakeX = new int[GAME_UNITS];
        private int[] snakeY = new int[GAME_UNITS];
        private int foodX;
        private int foodY;
        private Color foodColor;
        private boolean inGame = true;
        private Timer timer;

        public GamePanel() {
            this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
            this.setBackground(Color.white);
            this.setFocusable(true);
            this.addKeyListener(new MyKeyAdapter());
            startGame();
        }

        public void startGame() {
            for (int i = 0; i < initialSnakeLength; i++) {
                snakeX[i] = 100 - i * UNIT_PIXEL_SIZE;
                snakeY[i] = 100;
            }
            placeFood();
            timer = new Timer(delay, this);
            timer.start();
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(Color.black);
            g.drawRect(0, 0, SCREEN_WIDTH - 1, SCREEN_HEIGHT - 1);
            g.drawRect(1, 1, SCREEN_WIDTH - 3, SCREEN_HEIGHT - 3);
            if (inGame) {
                draw(g);
                drawScore(g);
            } else {
                gameOver(g);
            }
        }

        public void draw(Graphics g) {
            g.setColor(foodColor);
            g.fillRect(foodX, foodY, UNIT_PIXEL_SIZE, UNIT_PIXEL_SIZE);

            for (int i = 0; i < initialSnakeLength; i++) {
                if (i == 0) {
                    g.setColor(new Color(0, 100, 0));
                } else {
                    g.setColor(Color.green);
                }
                g.fillRect(snakeX[i], snakeY[i], UNIT_PIXEL_SIZE, UNIT_PIXEL_SIZE);
            }
        }

        public void drawScore(Graphics g) {
            g.setColor(Color.black);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + score, 10, 20);
        }

        public void move() {
            for (int i = initialSnakeLength; i > 0; i--) {
                snakeX[i] = snakeX[i - 1];
                snakeY[i] = snakeY[i - 1];
            }
            switch (direction) {
                case 'U':
                    snakeY[0] -= UNIT_PIXEL_SIZE;
                    break;
                case 'D':
                    snakeY[0] += UNIT_PIXEL_SIZE;
                    break;
                case 'L':
                    snakeX[0] -= UNIT_PIXEL_SIZE;
                    break;
                case 'R':
                    snakeX[0] += UNIT_PIXEL_SIZE;
                    break;
            }
            checkBorderCollision();
            checkFoodCollision();
        }

        public void placeFood() {
            int cols = SCREEN_WIDTH / UNIT_PIXEL_SIZE;
            int rows = SCREEN_HEIGHT / UNIT_PIXEL_SIZE;
            if (score < 100) {
                foodColor = Color.yellow;
            } else if (score >= 100 && score < 300) {
                foodColor = Color.blue;
            } else {
                foodColor = Color.red;
            }
            foodX = (int) (Math.random() * cols) * UNIT_PIXEL_SIZE;
            foodY = (int) (Math.random() * rows) * UNIT_PIXEL_SIZE;
        }

        public void checkBorderCollision() {
            if (snakeX[0] < 0 || snakeX[0] >= SCREEN_WIDTH || snakeY[0] < 0 || snakeY[0] >= SCREEN_HEIGHT) {
                inGame = false;
            }
        }

        public void checkFoodCollision() {
            if (snakeX[0] == foodX && snakeY[0] == foodY) {
                initialSnakeLength++;
                if (foodColor.equals(Color.yellow)) {
                    score += 10;
                } else if (foodColor.equals(Color.blue)) {
                    score += 20;
                } else {
                    score += 50;
                }
                updateDelay();
                placeFood();
            }
        }

        public void updateDelay() {
            if (score >= 100 && score < 300) {
                delay = 160;
            } else if (score >= 300) {
                delay = 120;
            }
            timer.setDelay(delay);
        }

        public void actionPerformed(ActionEvent e) {
            if (inGame) {
                move();
                repaint();
            }
        }

        private void gameOver(Graphics g) {
            String msg = "Game Over";
            Font font = new Font("Arial", Font.BOLD, 20);
            FontMetrics metrics = getFontMetrics(font);

            g.setColor(Color.black);
            g.setFont(font);
            g.drawString(msg, (SCREEN_WIDTH - metrics.stringWidth(msg)) / 2, SCREEN_HEIGHT / 2);
            String scoreMsg = "Score: " + score;
            g.drawString(scoreMsg, (SCREEN_WIDTH - metrics.stringWidth(scoreMsg)) / 2, SCREEN_HEIGHT / 2 + 30);
        }

        private class MyKeyAdapter extends KeyAdapter {
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (direction != 'R')
                            direction = 'L';
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (direction != 'L')
                            direction = 'R';
                        break;
                    case KeyEvent.VK_UP:
                        if (direction != 'D')
                            direction = 'U';
                        break;
                    case KeyEvent.VK_DOWN:
                        if (direction != 'U')
                            direction = 'D';
                        break;
                }
            }
        }
    }
}
