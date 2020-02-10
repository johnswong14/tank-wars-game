package tankwars;

import tankwars.GameObject.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import static javax.imageio.ImageIO.read;

public class GameWorld extends JPanel {
    public static final String GAME_TITLE = "Tank Wars";
    public static final int WORLD_WIDTH = 1280;
    public static final int WORLD_HEIGHT = 1280;
    public static final int SCREEN_WIDTH = 1000;
    public static final int SCREEN_HEIGHT = 600;

    private JFrame jf;

    private boolean gameOver = false;

    private Audio audio;

    private BufferedImage emptyBg;
    private BufferedImage world;
    private BufferedImage bgImg;
    private BufferedImage gameOverImg;
    private BufferedImage unbWallsImg, bWallsImg;
    private BufferedImage t1Img, t2Img;
    private BufferedImage bulletImg;
    private BufferedImage powerUpImg;
    private BufferedImage[] smallExpImg;
    private BufferedImage[] largeExpImg;

    private Graphics2D worldBuffer;

    private FileInputStream mapTextFile;
    private BufferedReader map;

    private Controller ctrl1;
    private Controller ctrl2;
    private Tank t1, t2;
    private PowerUp powerUp;

    private ArrayList<Collidable> collidables;
    private ArrayList<Drawable> drawables;

    public static void main(String[] args) {
        GameWorld gw = new GameWorld();
        gw.init();
        try {
            while (!gw.gameOver) {
                gw.checkTankFire();
                gw.updateCollidables();
                gw.checkCollision();
                gw.updateExplosion();
                gw.repaint();
                //System.out.println("Drawables : " + gw.drawables.size());
                Thread.sleep(1000 / 144);
            }
        }
        catch (InterruptedException ignored) {
        }
    }

    private void init() {
        System.out.println(System.getProperty("user.dir"));

        this.jf = new JFrame(GAME_TITLE);

        emptyBg = new BufferedImage(GameWorld.WORLD_WIDTH, GameWorld.WORLD_HEIGHT, BufferedImage.TYPE_INT_RGB);
        world = new BufferedImage(GameWorld.WORLD_WIDTH, GameWorld.WORLD_HEIGHT, BufferedImage.TYPE_INT_RGB);

        loadAudio();
        loadList();
        loadImg();
        loadTanks();
        loadMap();
        loadCtrl();
        loadPowerUp();

        this.jf.setLayout(new BorderLayout());
        this.jf.add(this);
        this.jf.setSize(GameWorld.SCREEN_WIDTH, GameWorld.SCREEN_HEIGHT + 22);
        this.jf.setResizable(false);
        this.jf.setLocationRelativeTo(null);
        this.jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.jf.setVisible(true);
    }

    private void loadAudio() {
        audio = new Audio();
    }

    private void loadList() {
        collidables = new ArrayList<>();
        drawables = new ArrayList<>();
    }

    private void loadImg() {
        try {
            bgImg = read(new File("Resources/sprite/Background.bmp"));
            unbWallsImg = read(new File("Resources/sprite/Wall1.gif"));
            bWallsImg = read(new File("Resources/sprite/Wall2.gif"));
            t1Img = read(new File("Resources/sprite/Tank1.gif"));
            t2Img = read(new File("Resources/sprite/Tank2.gif"));
            bulletImg = read(new File("Resources/sprite/Shell.png"));
            powerUpImg = read(new File("Resources/sprite/pixel_heart.png"));
            gameOverImg = read(new File("Resources/sprite/gameover.png"));

            smallExpImg = new BufferedImage[6];
            for (int i = 0; i < 6; i++) {
                smallExpImg[i] = read(new File("Resources/sprite/small_exp/SMALL_EXPLOSION_" + (i+1) + ".png"));
            }

            largeExpImg = new BufferedImage[7];
            for (int i = 0; i < 7; i++) {
                largeExpImg[i] = read(new File("Resources/sprite/large_exp/LARGE_EXPLOSION_" + (i+1) + ".png"));
            }
        } catch (Exception e ) {
            e.printStackTrace();
        }
    }

    private void loadTanks() {
        t1 = new Tank(230, 220, 90, 1, t1Img, this);
        t2 = new Tank(1000, 220, 90, 2, t2Img, this);
        collidables.add(t1);
        drawables.add(t1);
        collidables.add(t2);
        drawables.add(t2);
    }

    private void loadMap() {
        try {
            mapTextFile = new FileInputStream("Resources/map/map.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        map = new BufferedReader(new InputStreamReader(mapTextFile));

        try {
            String mapTextLine;
            int position = 0;
            mapTextLine = map.readLine();
            while(mapTextLine != null){
                for (int i = 0; i < mapTextLine.length(); i++){
                    // unbreakable walls
                    if (mapTextLine.charAt(i) == '1'){
                        Wall wall = new Wall(i * unbWallsImg.getWidth(), position * unbWallsImg.getHeight(), 1, unbWallsImg);
                        collidables.add(wall);
                        drawables.add(wall);
                    }
                    // breakable walls
                    else if (mapTextLine.charAt(i) == '2'){
                        Wall wall = new Wall(i * bWallsImg.getWidth(), position * bWallsImg.getHeight(), 2, bWallsImg);
                        collidables.add(wall);
                        drawables.add(wall);
                    }
                }
                position++;
                mapTextLine = map.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCtrl() {
        ctrl1 = new Controller(t1, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE);
        ctrl2 = new Controller(t2, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_ENTER);
        jf.addKeyListener(ctrl1);
        jf.addKeyListener(ctrl2);
    }

    private void loadPowerUp() {
        powerUp = new PowerUp(436, 480, powerUpImg);
        collidables.add(powerUp);
        drawables.add(powerUp);
        powerUp = new PowerUp(816, 480, powerUpImg);
        collidables.add(powerUp);
        drawables.add(powerUp);
        powerUp = new PowerUp(436, 768, powerUpImg);
        collidables.add(powerUp);
        drawables.add(powerUp);
        powerUp = new PowerUp(816, 768, powerUpImg);
        collidables.add(powerUp);
        drawables.add(powerUp);
    }

    private void checkTankFire() {
        if (t1.getIsFiring()) {
            Bullet newBull = new Bullet(t1.getCenterX(), t1.getCenterY(), t1.getAngle(), t1.getTankID(), bulletImg, this);
            collidables.add(newBull);
            drawables.add(newBull);
            t1.setFiring(false);
        }
        if (t2.getIsFiring()) {
            Bullet newBull = new Bullet(t2.getCenterX(), t2.getCenterY(), t2.getAngle(), t2.getTankID(), bulletImg, this);
            collidables.add(newBull);
            drawables.add(newBull);
            t2.setFiring(false);
        }
    }

    private void updateCollidables() {
        for (int i = 0; i < collidables.size(); i++) {
            Collidable collidable = collidables.get(i);
            if (collidable.isVisible()) {
                collidable.update();
            }
            else {
                collidables.remove(i);
                drawables.remove(i);
                i--;
            }
        }
    }

    private void checkCollision() {
        for (int i = 0; i < collidables.size(); i++) {
            Collidable co1 = collidables.get(i);
            if (co1 instanceof Wall) {
                continue;
            }
            for (int j = 0; j < collidables.size(); j++) {
                if (j == i) {
                    continue;
                }
                Collidable co2 = collidables.get(j);
                if (co1.getBounds().intersects(co2.getBounds())) {
                    co1.handleCollision(co2);
                }
            }
        }
    }

    private void updateExplosion() {
        Explosion explosion;
        for (int i = 0; i < drawables.size(); i++) {
            if (drawables.get(i) instanceof Explosion) {
                explosion = (Explosion) drawables.get(i);
                if (!explosion.isVisible()) {
                    drawables.remove(i);
                }
            }
        }
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public Audio getAudio() {
        return audio;
    }

    public BufferedImage[] getSmallExpImg() {
        return smallExpImg;
    }

    public BufferedImage[] getLargeExpImg() {
        return largeExpImg;
    }

    public void addCollidable(Collidable collidable) {
        collidables.add(collidable);
    }

    public void addDrawable(Drawable drawable) {
        drawables.add(drawable);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(emptyBg, 0 , 0, null);
        worldBuffer = world.createGraphics();

        if (!gameOver) {
            drawBg();
            for (int i = drawables.size() - 1; i >= 0; i--) {
                drawables.get(i).drawImage(worldBuffer);
            }

            // split screen
            BufferedImage lefthalf = world.getSubimage(t1.getScreenX(), t1.getScreenY(), SCREEN_WIDTH/2 - 2, SCREEN_HEIGHT);
            BufferedImage righthalf = world.getSubimage(t2.getScreenX(), t2.getScreenY(), SCREEN_WIDTH/2 - 2, SCREEN_HEIGHT);
            g2.drawImage(lefthalf, 0, 0, null);
            g2.drawImage(righthalf, SCREEN_WIDTH/2 + 2, 0, null);

            // mini-map
            BufferedImage mm = world.getSubimage(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
            g2.translate(SCREEN_WIDTH/2 - ((0.1*WORLD_WIDTH)/2),SCREEN_HEIGHT - (0.1*WORLD_HEIGHT));
            g2.scale(0.1, 0.1);
            g2.drawImage(mm, 0 , 0, null);

            // p1 health bar
            g2.setColor(new Color(255, 255, 200));
            g2.setStroke(new BasicStroke(3));
            AffineTransform tx1 = new AffineTransform();
            tx1.translate(SCREEN_WIDTH/2 - 202,SCREEN_HEIGHT - 25);
            tx1.scale(1, 1);
            g2.setTransform(tx1);
            g2.drawRect(0, 0, 103, 20);

            g2.setColor(new Color(255 - (int)(t1.getHealth() * 2.55), (int)(t1.getHealth() * 2.55) - 0, 0));
            AffineTransform tx2 = new AffineTransform();
            tx2.translate(SCREEN_WIDTH/2 - 200,SCREEN_HEIGHT - 23);
            tx2.scale(1, 1);
            g2.setTransform(tx2);
            g2.fillRect(0, 0, t1.getHealth(), 17);

            // p1 lives
            AffineTransform tx3 = new AffineTransform();
            tx3.translate(50,SCREEN_HEIGHT - 25);
            g2.setTransform(tx3);
            for (int i = 0; i < t1.getLives(); i++) {
                g2.drawImage(t1Img, i * t1Img.getWidth()/2, 0, t1Img.getWidth()/2, t1Img.getHeight()/2, null);
            }

            // p2 health bar
            g2.setColor(new Color(255, 255, 200));
            AffineTransform tx4 = new AffineTransform();
            tx4.translate(SCREEN_WIDTH/2 + 102,SCREEN_HEIGHT - 25);
            tx4.scale(1, 1);
            g2.setTransform(tx4);
            g2.drawRect(0, 0, 103, 20);

            g2.setColor(new Color(255 - (int)(t2.getHealth() * 2.55), (int)(t2.getHealth() * 2.55) - 0, 0));
            AffineTransform tx5 = new AffineTransform();
            tx5.translate(SCREEN_WIDTH/2 + 104,SCREEN_HEIGHT - 23);
            tx5.scale(1, 1);
            g2.setTransform(tx5);
            g2.fillRect(0, 0, t2.getHealth(), 17);

            // p2 lives
            AffineTransform tx6 = new AffineTransform();
            tx6.translate(SCREEN_WIDTH - 125,SCREEN_HEIGHT - 25);
            g2.setTransform(tx6);
            for (int i = 0; i < t2.getLives(); i++) {
                g2.drawImage(t2Img, i * t2Img.getWidth()/2, 0, t2Img.getWidth()/2, t2Img.getHeight()/2, null);
            }
        }
        else {
            g2.drawImage(gameOverImg, SCREEN_WIDTH/2 - gameOverImg.getWidth()/2, SCREEN_HEIGHT/2 - gameOverImg.getHeight()/2,null);
            if (t1.getLives() < 0) {
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial Black", Font.BOLD, 20));
                g2.drawString("Player 2 Wins!", 425, 400);
            }
            else if (t2.getLives() < 0) {
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial Black", Font.BOLD, 20));
                g2.drawString("Player 1 Wins!", 425, 400);
            }
            audio.playGameOver();
        }

        g2.dispose();
    }

    private void drawBg() {
        int TileWidth = bgImg.getWidth(null);
        int TileHeight = bgImg.getHeight(null);

        int NumberX = WORLD_WIDTH / TileWidth;
        int NumberY = WORLD_HEIGHT / TileHeight;

        for (int i = 0; i <= NumberY; i++) {
            for (int j = 0; j <= NumberX; j++) {
                worldBuffer.drawImage(bgImg, j * TileWidth, i * TileHeight, TileWidth, TileHeight, null);
            }
        }
    }
}
