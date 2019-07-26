package tankwars.GameObject;

import tankwars.GameWorld;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Explosion extends GameObject {

    public Explosion(int x, int y, BufferedImage[] imgAnim) {
        super(x, y, imgAnim);
    }

    private BufferedImage getImage() {
        int counter = -1;
        if (counter < imgAnim.length) {
            counter++;
            this.isVisible = true;
        }
        else {
            this.isVisible = false;
        }
        return imgAnim[counter];
    }

    @Override
    public boolean isVisible() {
        return this.isVisible;
    }

    @Override
    public Rectangle getBounds() {
        return null;
    }

    @Override
    public void update() {

    }

    @Override
    public void handleCollision(Collidable obj) {

    }

    @Override
    public void drawImage(Graphics2D g2d) {
        if (isVisible) {
            g2d.drawImage(getImage(), x, y, null);
            this.isVisible = false;
        }
    }
}
