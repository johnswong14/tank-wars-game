package tankwars;

import javax.sound.sampled.*;
import java.io.File;

public class Audio {
    private String inGame, gameOver, smallExp, largeExp;
    private Clip inGameSound, gameOverSound, smallExpSound, largeExpSound;
    FloatControl gainControl;

    public Audio() {
        inGame = "Resources/audio/battletanx.wav";
        gameOver = "Resources/audio/gameover.wav";
        smallExp = "Resources/audio/Explosion_small.wav";
        largeExp = "Resources/audio/Explosion_large.wav";

        inGameSound = createSound(inGame);
        gainControl = (FloatControl) inGameSound.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(-10.0f);
        inGameSound.loop(5);
    }

    private Clip createSound(String fileName) {
        try{
            File soundFile = new File(fileName);
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
            AudioFormat format = ais.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(ais);
            return clip;
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private synchronized void playSound(Clip clip){
        clip.start();
    }

    public synchronized void playGameOver(){
        gameOverSound = createSound(gameOver);
        playSound(gameOverSound);
    }

    public synchronized void playSmallExp(){
        smallExpSound = createSound(smallExp);
        playSound(smallExpSound);
    }

    public synchronized void playLargeExp(){
        largeExpSound = createSound(largeExp);
        playSound(largeExpSound);
    }
}
