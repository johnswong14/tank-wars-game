package tankwars.GameObject;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Wall extends GameObject {
    private int type;

    public Wall(int x, int y, int type, BufferedImage img) {
        super(x, y, img);
        this.type = type;
    }

    public int getType() {
        return this.type;
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
    }

    @Override
    public void handleCollision(Collidable obj) {

    }

    @Override
    public void drawImage(Graphics2D g2d) {
        AffineTransform rotation = AffineTransform.getTranslateInstance(this.x, this.y);
        rotation.rotate(Math.toRadians(this.angle), this.img.getWidth() / 2.0, this.img.getHeight() / 2.0);
        g2d.drawImage(this.img, rotation,null);
    }
}
