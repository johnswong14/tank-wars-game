package tankwars;

import tankwars.GameObject.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Controller implements KeyListener {

    private Tank tank;
    private final int upKey;
    private final int downKey;
    private final int leftKey;
    private final int rightKey;
    private final int shootKey;

    public Controller(Tank tank, int upKey, int downKey, int leftKey, int rightKey, int shootKey) {
        this.tank = tank;
        this.upKey = upKey;
        this.downKey = downKey;
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.shootKey = shootKey;
    }

    @Override
    public void keyTyped(KeyEvent ke) {

    }

    @Override
    public void keyPressed(KeyEvent ke) {
        int keyPressed = ke.getKeyCode();
        if (keyPressed == upKey) {
            this.tank.setUpPressed(true);
        }
        if (keyPressed == downKey) {
            this.tank.setDownPressed(true);
        }
        if (keyPressed == leftKey) {
            this.tank.setLeftPressed(true);
        }
        if (keyPressed == rightKey) {
            this.tank.setRightPressed(true);
        }
        if (keyPressed == shootKey) {
            this.tank.setShootPressed(true);
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        int keyReleased = ke.getKeyCode();
        if (keyReleased  == upKey) {
            this.tank.setUpPressed(false);
        }
        if (keyReleased == downKey) {
            this.tank.setDownPressed(false);
        }
        if (keyReleased  == leftKey) {
            this.tank.setLeftPressed(false);
        }
        if (keyReleased  == rightKey) {
            this.tank.setRightPressed(false);
        }
        if (keyReleased  == shootKey) {
            this.tank.setShootPressed(false);
        }
    }
}
