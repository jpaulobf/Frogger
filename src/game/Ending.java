package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.VolatileImage;
import util.Audio;
import util.LoadingStuffs;
import java.awt.image.BufferedImage;
import java.awt.GraphicsEnvironment;

/**
 * Class represents the gameover scene
 */
public class Ending {
    
    //Scenario variables
    private Graphics2D bgd2             = null;
    private int windowWidth             = 0;
    private int windowHeight            = 0;
    private VolatileImage bgBufferImage = null;
    private BufferedImage ending        = null;
    private Game gameRef                = null;
    private volatile Audio endingTheme  = null;
    private boolean canExit             = false;

    /**
     * Constructor
     * @param g2d
     * @param windowWidth
     * @param windowHeight
     */
    public Ending(Game game, int windowWidth, int windowHeight) {
        this.windowHeight   = windowHeight;
        this.windowWidth    = windowWidth;
        this.gameRef        = game;
        this.endingTheme    = LoadingStuffs.getInstance().getAudio("ending-theme");
        this.drawGameOverInBuffer();
    }

    /**
     * This private method construct the BG just once.
     * Than, when necessary it is ploted in the backbuffer.
     */
    private void drawGameOverInBuffer() {
        if (this.bgd2 == null) {
            this.ending = LoadingStuffs.getInstance().getImage("ending");

            //create a backbuffer image for doublebuffer
            this.bgBufferImage  = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleVolatileImage(this.windowWidth, 2000);
            this.bgd2           = (Graphics2D)bgBufferImage.getGraphics();

            //paint all bg in black
            this.bgd2.setBackground(new Color(25, 33, 41));
            this.bgd2.clearRect(0, 0, this.windowWidth, 2000);
            
            int imgW = this.ending.getWidth();
            int imgH = this.ending.getHeight();
            int imgX = ((this.windowWidth - imgW)/2);
            int imgY = ((this.windowHeight - imgH)/2);

            this.bgd2.drawImage(this.ending, imgX, imgY, imgW + imgX, imgH + imgY, 
                                               0, 0, imgW, imgH, null);
        }
    }

    /**
     * Update the gameover scene and its elements
     * @param frametime
     */
    public void update(long frametime) {
    }

    /**
     * 
     * @param frametime
     */
    public void draw(long frametime) {
        //clear the stage
        this.gameRef.getG2D().setBackground(Color.BLACK);
        this.gameRef.getG2D().clearRect(0, 0, this.windowWidth, this.windowHeight * 2);

        //After construct the bg once, copy it to the graphic device
        this.gameRef.getG2D().drawImage(this.bgBufferImage, 0, 0, null);

        this.canExit = true;
    }

    public void playTheme() {
        this.endingTheme.playContinuously();
    }

    public void keyPressed(int keyCode) {
        if (this.canExit) {
            this.endingTheme.stop();
            this.gameRef.changeGameStateToMenu();
            this.canExit = false;
        }
    }
}