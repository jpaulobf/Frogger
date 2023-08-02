package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.VolatileImage;

import util.Audio;
import util.LoadingStuffs;
import java.awt.image.BufferedImage;
import java.awt.GraphicsEnvironment;

/**
 * Class representing the Option screen
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
    private BufferedImage [] slideOff       = new BufferedImage[6];
    private BufferedImage [] slideOn        = new BufferedImage[6];
    private BufferedImage [] live           = new BufferedImage[9];
    private BufferedImage toggleOn          = null;
    private BufferedImage toggleOff         = null;

    private BufferedImage toggleMusicBt     = null;
    private BufferedImage toggleSFXBt       = null;

    private short slideMusicXInit           = 1111;
    private short slideMusicYInit           = 374;
    private short slideSFXXInit             = 1111;
    private short slideSFXYInit             = 554;
    private short slideXOffset              = 31;
    private short slideYOffset              = 4;

    private short [] slideMusicX            = {(short)(slideMusicXInit+(5*slideXOffset)), (short)(slideMusicXInit+(4*slideXOffset)), (short)(slideMusicXInit+(3*slideXOffset)), (short)(slideMusicXInit+(2*slideXOffset)), (short)(slideMusicXInit+(1*slideXOffset)), slideMusicXInit};
    private short [] slideMusicY            = {(short)(slideMusicYInit+(5*slideYOffset)), (short)(slideMusicYInit+(4*slideYOffset)), (short)(slideMusicYInit+(3*slideYOffset)), (short)(slideMusicYInit+(2*slideYOffset)), (short)(slideMusicYInit+(1*slideYOffset)), slideMusicYInit};
    private short [] slideSFXX              = {(short)(slideSFXXInit+(5*slideXOffset)), (short)(slideSFXXInit+(4*slideXOffset)), (short)(slideSFXXInit+(3*slideXOffset)), (short)(slideSFXXInit+(2*slideXOffset)), (short)(slideSFXXInit+(1*slideXOffset)), slideSFXXInit};
    private short [] slideSFXY              = {(short)(slideSFXYInit+(5*slideYOffset)), (short)(slideSFXYInit+(4*slideYOffset)), (short)(slideSFXYInit+(3*slideYOffset)), (short)(slideSFXYInit+(2*slideYOffset)), (short)(slideSFXYInit+(1*slideYOffset)), slideSFXYInit};

    //Control variables
    private final short OG_SELECTOR_X       = 40;
    private final short OG_SELECTOR_Y       = 285;
    private final short X_OFFSET            = -20;
    private short selectorX                 = 40;
    private short selectorY                 = 285;
    private byte currentSelectorPos         = 0;
    private boolean toggleMusic             = true;
    private boolean toggleSFX               = true;
    private byte musicVolume                = 5;
    private byte oldMusicVolume             = 5;
    private byte sfxVolume                  = 5;
    private byte oldSfxVolume               = 5;
    private byte lives                      = 5;

    //const
    private final Color GREEN_COLOR         = new Color(51, 152, 101, 255);
    private final short LOGO_X = 800;
    private final short LOGO_Y = 70;

    //music & sfx
    private volatile Audio menuSelect       = null;
    private volatile Audio menuItem         = null;
    private volatile Audio exiting          = null;

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

        this.selector           = LoadingStuffs.getInstance().getImage("selector");
        this.labelPlayMusic     = LoadingStuffs.getInstance().getImage("label-play-music");
        this.labelMusicVolume   = LoadingStuffs.getInstance().getImage("label-music-volume");
        this.labelPlaySFX       = LoadingStuffs.getInstance().getImage("label-play-sfx");
        this.labelSFXVolume     = LoadingStuffs.getInstance().getImage("label-sfx-volume");
        this.labelHowMany       = LoadingStuffs.getInstance().getImage("label-how-many-lives");
        this.labelExit          = LoadingStuffs.getInstance().getImage("label-exit-options");
        this.toggleOn           = LoadingStuffs.getInstance().getImage("toggle-on");
        this.toggleOff          = LoadingStuffs.getInstance().getImage("toggle-off");
        this.menuSelect         = LoadingStuffs.getInstance().getAudio("menu-select");
        this.menuItem           = LoadingStuffs.getInstance().getAudio("menu-item");
        this.exiting            = LoadingStuffs.getInstance().getAudio("exiting");

        //load images from slide off
        for (int i = 0; i < slideOff.length; i++) {
            this.slideOff[i] = LoadingStuffs.getInstance().getImage("slide-off-" + i);    
        } 

        //load images from slide on
        for (int i = 0; i < slideOn.length; i++) {
            this.slideOn[i] = LoadingStuffs.getInstance().getImage("slide-on-" + i);    
        }

        for (int i = 0; i < live.length; i++) {
            this.live[i] = LoadingStuffs.getInstance().getImage("live-" + (i + 1));    
        }

        //draw the static part
        this.drawBuffer();
    }

    /**
     * This private method construct the BG just once.
     * Than, when necessary it is ploted in the backbuffer.
     */
    private void drawBuffer() {
        if (this.bgd2 == null) {
            
            this.optionsLogo = LoadingStuffs.getInstance().getImage("options-logo");

            //create a backbuffer image for doublebuffer
            this.bgBufferImage  = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                                     .getDefaultScreenDevice()
                                                     .getDefaultConfiguration()
                                                     .createCompatibleVolatileImage(this.windowWidth, this.windowHeight);
            this.bgd2 = (Graphics2D)bgBufferImage.getGraphics();

            //paint all bg in black
            this.bgd2.setBackground(GREEN_COLOR);
            this.bgd2.clearRect(0, 0, this.windowWidth, this.windowHeight);
            
            //draw
            this.bgd2.drawImage(this.optionsLogo, LOGO_X, LOGO_Y, null);

            //draw slider
            for (byte i = 0; i < slideOff.length; i++) {
                this.bgd2.drawImage(this.slideOff[i], slideMusicX[i], slideMusicY[i], null);
            }

            //draw slider
            for (byte i = 0; i < slideOff.length; i++) {
                this.bgd2.drawImage(this.slideOff[i], slideSFXX[i], slideSFXY[i], null);
            }
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

        //control toggle buttom image to display
        this.toggleMusicBt  = (this.toggleMusic)?this.toggleOn:this.toggleOff;
        this.toggleSFXBt    = (this.toggleSFX)?this.toggleOn:this.toggleOff;
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

        //draw active slider
        for (byte i = 0; i <= this.musicVolume; i++) {
            this.gameRef.getG2D().drawImage(this.slideOn[i], slideMusicX[i], slideMusicY[i], null);
        }

        //draw active slider
        for (byte i = 0; i <= this.sfxVolume; i++) {
            this.gameRef.getG2D().drawImage(this.slideOn[i], slideSFXX[i], slideSFXY[i], null);
        }

        //draw toggle music
        this.gameRef.getG2D().drawImage(this.toggleMusicBt, 1111, 280, null);

        //draw toggle sfx
        this.gameRef.getG2D().drawImage(this.toggleSFXBt, 1111, 459, null);

        //draw lives
        this.gameRef.getG2D().drawImage(this.live[(lives-1)], 1111, 641, null);
    }

    /**
     * Control keys in options
     * @param key
     */
    public void move(int key) {
        if (key == 27) {
            this.exiting.play();
            this.currentSelectorPos = 0;
            this.gameRef.changeGameStateToMenu();
        } else {
            if (key == 38) {
                if (this.musicVolume == -1 && this.currentSelectorPos == 2) {
                    this.currentSelectorPos = 0;
                } else if (this.sfxVolume == -1 && this.currentSelectorPos == 4) {
                    this.currentSelectorPos = 2;
                } else {
                    this.currentSelectorPos = (--this.currentSelectorPos<0)?5:this.currentSelectorPos;
                }
                this.menuSelect.play();
            } else if (key == 40) {
                if (this.musicVolume == -1 && this.currentSelectorPos == 0) {
                    this.currentSelectorPos = 2;
                } else if (this.sfxVolume == -1 && this.currentSelectorPos == 2) {
                    this.currentSelectorPos = 4;
                } else {
                    this.currentSelectorPos = (byte)(++this.currentSelectorPos%6);
                }
                this.menuSelect.play();
            } else if (this.currentSelectorPos == 5 && (key == 10 || key == 32)) {
                this.exiting.play();
                this.currentSelectorPos = 0;
                this.gameRef.changeGameStateToMenu();
            }

            if (this.currentSelectorPos == 0) {
                if (key == 39 || key == 37) {
                    this.toggleMusic = !this.toggleMusic;
                    if (!this.toggleMusic) {
                        this.oldMusicVolume = this.musicVolume;
                        this.musicVolume = -1;
                    } else {
                        this.musicVolume = this.oldMusicVolume;
                    }
                    this.menuItem.play();
                }
            } else if (this.currentSelectorPos == 2) {
                if (key == 39 || key == 37) {
                    this.toggleSFX = !this.toggleSFX;
                    if (!this.toggleSFX) {
                        this.oldSfxVolume = this.sfxVolume;
                        this.sfxVolume = -1;
                    } else {
                        this.sfxVolume = this.oldSfxVolume;
                    }
                    this.menuItem.play();
                }
            } else if (this.currentSelectorPos == 1) {
                //left
                if (key == 37) {
                    this.musicVolume = (byte)(++this.musicVolume%6);
                } else if (key == 39) { //right
                    this.musicVolume = (--this.musicVolume<0)?5:this.musicVolume;
                }
            } else if (this.currentSelectorPos == 3) {
                //left
                if (key == 37) {
                    this.sfxVolume = (byte)(++this.sfxVolume%6);
                } else if (key == 39) { //right
                    this.sfxVolume = (--this.sfxVolume<0)?5:this.sfxVolume;
                }
            } else if (this.currentSelectorPos == 4) {
                //left
                if (key == 37) {
                    this.lives = (--this.lives<1)?9:this.lives;
                    this.menuItem.play();
                } else if (key == 39) { //right
                    this.lives = (byte)(++this.lives%10);
                    if (this.lives == 0) ++this.lives;
                    this.menuItem.play();
                }
                this.gameRef.updateFroggerLives();
            }
        }
    }

    /**
     * Return frogger lives
     * @return
     */
    public byte getOptionsDefinedLives() {
        return (this.lives);
    }
}