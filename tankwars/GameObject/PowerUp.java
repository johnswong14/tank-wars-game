package tankwars.GameObject;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class PowerUp extends GameObject {
    private long spawnTimer = System.nanoTime();
    private final long SPAWNDELAY = 5000;
    private int spawnX, spawnY;

    public PowerUp(int x, int y, BufferedImage img) {
        super(x, y, img);
        this.spawnX = x;
        this.spawnY = y;
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
        long elapsed = (System.nanoTime() - spawnTimer) / 1000000;
        if (elapsed > SPAWNDELAY) {
            // spawn bullets
            this.x = spawnX;
            this.y = spawnY;
            this.spawnTimer = System.nanoTime();
        }
    }

    @Override
    public void handleCollision(Collidable obj) {
        if (obj instanceof Tank) {
            Tank tank = (Tank) obj;
            if (tank.getHealth() < 100) {
                tank.setHealth(tank.getHealth() + 5);
                this.x = -10;
                this.y = -10;
            }
        }
    }

    @Override
    public void drawImage(Graphics2D g2d) {
        AffineTransform rotation = AffineTransform.getTranslateInstance(this.x, this.y);
        rotation.rotate(Math.toRadians(this.angle), this.img.getWidth() / 2.0, this.img.getHeight() / 2.0);
        g2d.drawImage(this.img, rotation,null);
    }
}
