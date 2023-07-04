package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.VolatileImage;
import util.LoadingStuffs;
import java.awt.image.BufferedImage;
import java.awt.GraphicsEnvironment;

/**
 * 
 */
public class Options {

    //Scenario variables
    private Graphics2D bgd2                 = null;
    private Game gameRef                    = null;
    private int windowWidth                 = 0;
    private int windowHeight                = 0;
    private VolatileImage bgBufferImage     = null;
    
    //Images
    private BufferedImage selector          = null;
    private BufferedImage optionsLogo       = null;
    private BufferedImage labelPlayMusic    = null;
    private BufferedImage labelMusicVolume  = null;
    private BufferedImage labelPlaySFX      = null;
    private BufferedImage labelSFXVolume    = null;
    private BufferedImage labelHowMany      = null;
    private BufferedImage labelExit         = null;

    //Control variables
    
    private final short OG_SELECTOR_X       = 40;
    private final short OG_SELECTOR_Y       = 285;
    private final short X_OFFSET            = -20;
    private short selectorX                 = 40;
    private short selectorY                 = 285;
    private byte currentSelectorPos         = 0;
    
    //const
    private final Color GREEN_COLOR         = new Color(51, 152, 101, 255);
    private final short LOGO_X = 800;
    private final short LOGO_Y = 70;

    /**
     * Constructor
     * @param g2d
     * @param windowWidth
     * @param windowHeight
     */
    public Options(Game game, int windowWidth, int windowHeight) {
        this.windowHeight       = windowHeight;
        this.windowWidth        = windowWidth;
        this.gameRef            = game;

        this.selector          = (BufferedImage)LoadingStuffs.getInstance().getStuff("selector");
        this.labelPlayMusic    = (BufferedImage)LoadingStuffs.getInstance().getStuff("label-play-music");
        this.labelMusicVolume  = (BufferedImage)LoadingStuffs.getInstance().getStuff("label-music-volume");
        this.labelPlaySFX      = (BufferedImage)LoadingStuffs.getInstance().getStuff("label-play-sfx");
        this.labelSFXVolume    = (BufferedImage)LoadingStuffs.getInstance().getStuff("label-sfx-volume");
        this.labelHowMany      = (BufferedImage)LoadingStuffs.getInstance().getStuff("label-how-many-lives");
        this.labelExit         = (BufferedImage)LoadingStuffs.getInstance().getStuff("label-exit-options");

        this.drawBuffer();
    }

    /**
     * This private method construct the BG just once.
     * Than, when necessary it is ploted in the backbuffer.
     */
    private void drawBuffer() {
        if (this.bgd2 == null) {
            
            this.optionsLogo = (BufferedImage)LoadingStuffs.getInstance().getStuff("options-logo");

            //create a backbuffer image for doublebuffer
            this.bgBufferImage  = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleVolatileImage(this.windowWidth, this.windowHeight);
            this.bgd2           = (Graphics2D)bgBufferImage.getGraphics();

            //paint all bg in black
            this.bgd2.setBackground(GREEN_COLOR);
            this.bgd2.clearRect(0, 0, this.windowWidth, this.windowHeight);
            
            //draw
            this.bgd2.drawImage(this.optionsLogo, LOGO_X, LOGO_Y, null);
        }
    }

    /**
     * Update the gameover scene and its elements
     * @param frametime
     */
    public void update(long frametime) {
        this.selectorX = OG_SELECTOR_X;
        this.selectorY = OG_SELECTOR_Y;
        if (this.currentSelectorPos == 5) {
            this.selectorX = OG_SELECTOR_X + X_OFFSET;
            this.selectorY = (short)(OG_SELECTOR_Y + 93);
        }
    }

    /**
     * 
     * @param frametime
     */
    public void draw(long frametime) {
        //clear the stage
        this.gameRef.getG2D().setBackground(GREEN_COLOR);
        this.gameRef.getG2D().clearRect(0, 0, this.windowWidth, this.windowHeight * 2);

        //After construct the bg once, copy it to the graphic device
        this.gameRef.getG2D().drawImage(this.bgBufferImage, 0, 0, null);

        //draw selector
        this.gameRef.getG2D().drawImage(this.selector, this.selectorX, this.selectorY + (this.currentSelectorPos * 90), null);

        //draw labels
        this.gameRef.getG2D().drawImage(this.labelPlayMusic, 128, 280, null);
        this.gameRef.getG2D().drawImage(this.labelMusicVolume, 128, 370, null);
        this.gameRef.getG2D().drawImage(this.labelPlaySFX, 128, 460, null);
        this.gameRef.getG2D().drawImage(this.labelSFXVolume, 128, 550, null);
        this.gameRef.getG2D().drawImage(this.labelHowMany, 128, 640, null);
        this.gameRef.getG2D().drawImage(this.labelExit, 38, 817, null);
    }

    /**
     * Control keys in options
     * @param key
     */
    public void move(int key) {
        if (key == 27) {
            this.currentSelectorPos = 0;
            this.gameRef.changeGameStateToMenu();
        } else {
            if (key == 38) {
                this.currentSelectorPos = (--this.currentSelectorPos<0)?5:this.currentSelectorPos;
            } else if (key == 40) {
                this.currentSelectorPos = (byte)(++this.currentSelectorPos%6);
            } else if (key == 10 || key == 32) {
                this.currentSelectorPos = 0;
                this.gameRef.changeGameStateToMenu();
            }
        }
    }
}