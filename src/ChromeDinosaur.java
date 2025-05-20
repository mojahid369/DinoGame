import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

// use abstraction class
interface Drawable {
    void draw(Graphics g);
}

class Block implements Drawable {
    private int x, y, width, height;  // use encapsulation
    private Image img;

    public Block(int x, int y, int width, int height, Image img) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.img = img;
    }

    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {

        return height; }

    public Image getImg() {
        return img;
    }
    public void setImg(Image img) {
        this.img = img;
    }

    // use polymorphism
    @Override
    public void draw(Graphics g) {
        g.drawImage(img, x, y, width, height, null);
    }
}

    // Dinosaur class inherit Block class
class Dinosaur extends Block {
    public Dinosaur(int x, int y, int width, int height, Image img) {
        super(x, y, width, height, img);
    }

    // use polymorphism
    @Override
    public void draw(Graphics g) {
        super.draw(g);
    }
}

   // Cactus class inherit Block class
class Cactus extends Block {
    public Cactus(int x, int y, int width, int height, Image img) {
        super(x, y, width, height, img);
    }
}

public class ChromeDinosaur extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 720;
    int boardHeight = 250;

    // Images
    Image dinosaurImg;
    Image dinosaurDeadImg;
    Image dinosaurJumpImg;
    Image cactus1Img;
    Image cactus2Img;
    Image cactus3Img;

    // Dinosaur
    int dinosaurWidth = 88;
    int dinosaurHeight = 94;
    int dinosaurX = 50;
    int dinosaurY = boardHeight - dinosaurHeight;
    Dinosaur dinosaur;

    // Cactus
    int cactus1Width = 34;
    int cactus2Width = 69;
    int cactus3Width = 102;
    int cactusHeight = 70;
    int cactusX = 700;
    int cactusY = boardHeight - cactusHeight;
    ArrayList<Cactus> cactusArray;

    // Physics
    int velocityX = -12;
    int velocityY = 0;
    int gravity = 1;

    boolean gameOver = false;
    int score = 0;
    Timer gameLoop;
    Timer placeCactusTimer;

    public ChromeDinosaur() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.lightGray);
        setFocusable(true);
        addKeyListener(this);

        dinosaurImg = new ImageIcon(getClass().getResource("./img/dino-run.gif")).getImage();
        dinosaurDeadImg = new ImageIcon(getClass().getResource("./img/dino-dead.png")).getImage();
        dinosaurJumpImg = new ImageIcon(getClass().getResource("./img/dino-jump.png")).getImage();
        cactus1Img = new ImageIcon(getClass().getResource("./img/cactus1.png")).getImage();
        cactus2Img = new ImageIcon(getClass().getResource("./img/cactus2.png")).getImage();
        cactus3Img = new ImageIcon(getClass().getResource("./img/cactus3.png")).getImage();

        dinosaur = new Dinosaur(dinosaurX, dinosaurY, dinosaurWidth, dinosaurHeight, dinosaurImg);
        cactusArray = new ArrayList<>();

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();

        placeCactusTimer = new Timer(1500, e -> placeCactus());
        placeCactusTimer.start();
    }

    void placeCactus() {
        if (gameOver) return;

        double chance = Math.random();
        if (chance > 0.90) {
            cactusArray.add(new Cactus(cactusX, cactusY, cactus3Width, cactusHeight, cactus3Img));
        } else if (chance > 0.70) {
            cactusArray.add(new Cactus(cactusX, cactusY, cactus2Width, cactusHeight, cactus2Img));
        } else if (chance > 0.50) {
            cactusArray.add(new Cactus(cactusX, cactusY, cactus1Width, cactusHeight, cactus1Img));
        }

        if (cactusArray.size() > 10) {
            cactusArray.remove(0);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        dinosaur.draw(g);
        for (Cactus cactus : cactusArray) {
            cactus.draw(g);
        }

        g.setColor(Color.black);
        g.setFont(new Font("Courier", Font.PLAIN, 32));
        g.drawString((gameOver ? "Game Over: " : "") + score, 10, 35);
    }

    public void move() {
        velocityY += gravity;
        dinosaur.setY(dinosaur.getY() + velocityY);

        if (dinosaur.getY() > dinosaurY) {
            dinosaur.setY(dinosaurY);
            velocityY = 0;
            dinosaur.setImg(dinosaurImg);
        }

        for (Cactus cactus : cactusArray) {
            cactus.setX(cactus.getX() + velocityX);
            if (collision(dinosaur, cactus)) {
                gameOver = true;
                dinosaur.setImg(dinosaurDeadImg);
            }
        }

        score++;
    }

    boolean collision(Block a, Block b) {
        return a.getX() < b.getX() + b.getWidth() &&
                a.getX() + a.getWidth() > b.getX() &&
                a.getY() < b.getY() + b.getHeight() &&
                a.getY() + a.getHeight() > b.getY();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placeCactusTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (dinosaur.getY() == dinosaurY) {
                velocityY = -17;
                dinosaur.setImg(dinosaurJumpImg);
            }
            if (gameOver) {
                dinosaur.setY(dinosaurY);
                dinosaur.setImg(dinosaurImg);
                velocityY = 0;
                cactusArray.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placeCactusTimer.start();
            }
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}
