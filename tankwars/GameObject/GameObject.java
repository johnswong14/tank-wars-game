package tankwars.GameObject;

import java.awt.image.BufferedImage;

public abstract class GameObject implements Collidable, Drawable {
    protected int x, y;
    protected int angle;
    protected BufferedImage img;
    protected BufferedImage[] imgAnim;
    protected int imgWidth, imgHeight;
    protected boolean isVisible;

    public GameObject(int x, int y, BufferedImage img) {
        this.x = x;
        this.y = y;
        this.angle = 0;
        this.img = img;
        this.imgWidth = img.getWidth();
        this.imgHeight = img.getHeight();
        this.isVisible = true;
    }

    public GameObject(int x, int y, BufferedImage[] imgAnim) {
        this.x = x;
        this.y = y;
        this.imgAnim = imgAnim;
        this.imgWidth = imgAnim[0].getWidth();
        this.imgHeight = imgAnim[0].getHeight();
        isVisible = true;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }
}
