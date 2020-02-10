package tankwars.GameObject;

import tankwars.GameWorld;
import tankwars.Audio;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Tank extends GameObject {
    private GameWorld gw;
    private Audio audio;
    private int vx;
    private int vy;
    private final int R = 2;
    private final int ROTATIONSPEED = 2;
    private boolean UpPressed;
    private boolean DownPressed;
    private boolean RightPressed;
    private boolean LeftPressed;
    private boolean ShootPressed;
    private boolean isFiring;
    private long firingTimer = System.nanoTime();
    private final long FIRINGDELAY = 500;
    private int safeX;
    private int safeY;
    private int health;
    private int lives;
    private int spawnX, spawnY, spawnAng;
    private static boolean respawn;
    private boolean respawned;
    private int tankID;

    public Tank(int x, int y, int angle, int tankID, BufferedImage img, GameWorld gw) {
        super(x, y, img);
        this.angle = angle;
        this.tankID = tankID;
        this.gw = gw;
        this.health = 100;
        this.isFiring = false;
        this.lives = 3;
        this.spawnX = x;
        this.spawnY = y;
        this.spawnAng = angle;
        this.audio = gw.getAudio();
    }

    public int getAngle() {
        return this.angle;
    }

    public int getTankID() {
        return this.tankID;
    }

    public int getCenterX() {
        return this.x + this.imgWidth/2;
    }

    public int getCenterY() {
        return this.y + this.imgHeight/2;
    }

    public boolean getIsFiring() {
        return this.isFiring;
    }

    public int getHealth() {
        return this.health;
    }

    public int getLives() {
        return this.lives;
    }

    public void setUpPressed(boolean upPressed) {
        this.UpPressed = upPressed;
    }

    public void setDownPressed(boolean downPressed) {
        this.DownPressed = downPressed;
    }

    public void setRightPressed(boolean rightPressed) {
        this.RightPressed = rightPressed;
    }

    public void setLeftPressed(boolean leftPressed) {
        this.LeftPressed = leftPressed;
    }

    public void setShootPressed(boolean shootPressed) {
        this.ShootPressed = shootPressed;
    }

    public void setFiring(boolean firing) {
        this.isFiring = firing;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void rotateLeft() {
        this.angle -= ROTATIONSPEED;
    }

    public void rotateRight() {
        this.angle += ROTATIONSPEED;
    }

    public void moveBackwards() {
        this.vx = (int) Math.round(R * Math.cos(Math.toRadians(angle)));
        this.vy = (int) Math.round(R * Math.sin(Math.toRadians(angle)));
        this.x -= vx;
        this.y -= vy;
    }

    public void moveForwards() {
        this.vx = (int) Math.round(R * Math.cos(Math.toRadians(angle)));
        this.vy = (int) Math.round(R * Math.sin(Math.toRadians(angle)));
        this.x += vx;
        this.y += vy;
    }

    public void shoot() {
        //System.out.println("Fire!");
        long elapsed = (System.nanoTime() - firingTimer) / 1000000;
        if (elapsed > FIRINGDELAY) {
            this.isFiring = true;
            this.firingTimer = System.nanoTime();
        }
    }

    public void saveCoords() {
        this.safeX = x;
        this.safeY = y;
    }

    public int getScreenX() {
        int screenX = 0;
        if (getCenterX() <= GameWorld.SCREEN_WIDTH/4) {
            screenX = 0;
        }
        if ((getCenterX() > GameWorld.SCREEN_WIDTH/4) && ((getCenterX() + GameWorld.SCREEN_WIDTH/4) <= GameWorld.WORLD_WIDTH )) {
            screenX = getCenterX() - GameWorld.SCREEN_WIDTH/4;
        }
        if ((getCenterX() + GameWorld.SCREEN_WIDTH/4) > GameWorld.WORLD_WIDTH) {
            screenX = GameWorld.WORLD_WIDTH - GameWorld.SCREEN_WIDTH/2;
        }
        return screenX;
    }

    public int getScreenY() {
        int screenY = 0;
        if (getCenterY() <= GameWorld.SCREEN_HEIGHT/2) {
            screenY = 0;
        }
        if ((getCenterY() > GameWorld.SCREEN_HEIGHT/2) && ((getCenterY() + GameWorld.SCREEN_HEIGHT/2) <= GameWorld.WORLD_HEIGHT)) {
            screenY = getCenterY() - GameWorld.SCREEN_HEIGHT/2;
        }
        if ((getCenterY() + GameWorld.SCREEN_HEIGHT/2) > GameWorld.WORLD_HEIGHT) {
            screenY = GameWorld.WORLD_HEIGHT - GameWorld.SCREEN_HEIGHT;
        }
        return screenY;
    }

    private void checkHealth() {
        //System.out.println("Tank " + this.getTankID() + " Lives: " + this.lives + " Health: " + health);
        if (this.lives > 0) {
            if (this.health == 0) {
                audio.playLargeExp();
                this.lives--;
                this.health = 100;
                respawn = true;
            }
        }
        else {
            gw.setGameOver(true);
        }
    }

    public void checkRespawn() {
        if (respawn && !this.respawned) {
            this.x = spawnX;
            this.y = spawnY;
            this.angle = spawnAng;
            this.respawned = true;
        }
        else {
            respawn = false;
            this.respawned = false;
        }
    }

    @Override
    public boolean isVisible() {
        return this.isVisible;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, imgWidth, imgHeight);
    }

    @Override
    public void update() {
        saveCoords();

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
            this.shoot();
        }

        this.checkHealth();
        this.checkRespawn();
    }

    @Override
    public void handleCollision(Collidable obj) {
        if (obj instanceof Tank || obj instanceof Wall) {
            this.x = safeX;
            this.y = safeY;
        }
    }

    @Override
    public void drawImage(Graphics2D g2d) {
        AffineTransform rotation = AffineTransform.getTranslateInstance(this.x, this.y);
        rotation.rotate(Math.toRadians(this.angle), this.img.getWidth() / 2.0, this.img.getHeight() / 2.0);
        g2d.drawImage(this.img, rotation,null);
    }

    @Override
    public String toString() {
        return "Tank " + tankID + ": x=" + x + ", y=" + y + ", angle=" + -(angle);
    }
}
