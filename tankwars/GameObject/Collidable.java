package tankwars.GameObject;

import java.awt.*;

public interface Collidable {
    boolean isVisible();
    Rectangle getBounds();
    void update();
    void handleCollision(Collidable obj);
}
