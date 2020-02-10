package tankwars.GameObject;

import tankwars.GameWorld;
import tankwars.Audio;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Bullet extends GameObject {
    private GameWorld gw;
    private Audio audio;
    private final int SPEED = 5;
    private final int DAMAGE = 10;
    private int vx;
    private int vy;
    private int bulletID;

    public Bullet(int x, int y, int angle, int bulletID, BufferedImage img, GameWorld gw) {
        super(x, y, img);
        this.angle = angle;
        this.bulletID = bulletID;
        this.gw = gw;
        this.audio = gw.getAudio();
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
        this.vx = (int) Math.round(SPEED * Math.cos(Math.toRadians(angle)));
        this.vy = (int) Math.round(SPEED * Math.sin(Math.toRadians(angle)));
        this.x += vx;
        this.y += vy;
    }

    @Override
    public void handleCollision(Collidable obj) {

        //handle case of bullet hitting wall
        if (obj instanceof Wall) {
            Wall wall = (Wall) obj;
            // breakable wall
            if (wall.getType() == 2) {
                wall.setVisible(false);
                audio.playLargeExp();
                gw.addDrawable(new Explosion(this.x - 16, this.y, gw.getLargeExpImg()));
            }
            this.isVisible = false;
            audio.playSmallExp();
            gw.addDrawable(new Explosion(this.x - 16, this.y, gw.getSmallExpImg()));
        }

        //handle case of bullet hitting tank
        if (obj instanceof Tank) {
            Tank tank = (Tank) obj;
            // no friendly (self) fire
            if (this.bulletID != tank.getTankID()) {
                // add explosion
                tank.setHealth(tank.getHealth() - DAMAGE);
                this.isVisible = false;
                if (tank.getHealth() == 0) {
                    audio.playLargeExp();
                    gw.addDrawable(new Explosion(this.x - 16, this.y, gw.getLargeExpImg()));
                }
                else {
                    audio.playSmallExp();
                    gw.addDrawable(new Explosion(this.x - 16, this.y, gw.getSmallExpImg()));
                }
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
