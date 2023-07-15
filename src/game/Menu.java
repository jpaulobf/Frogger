package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.VolatileImage;

import util.Audio;
import util.LoadingStuffs;
import java.awt.image.BufferedImage;
import java.awt.GraphicsEnvironment;

public class Menu {

    //Scenario variables
    private Game gameRef                = null;
    private Graphics2D bgd2             = null;
    private int windowWidth             = 0;
    private int windowHeight            = 0;
    
    //Buffered Image
    private VolatileImage bgBufferImage = null;

    //Images
    private BufferedImage selector      = null;
    private BufferedImage logo          = null;
    private BufferedImage labelPlayGame = null;
    private BufferedImage labelOptions  = null;
    private BufferedImage labelExit     = null;
    private BufferedImage starOff       = null;
    private BufferedImage starOn        = null;
    
    //Control variables
    private int labelPlayW              = 0;
    private int labelPlayH              = 0;
    private int labelPlayX              = 0;
    private int optionsImgW             = 0;
    private int optionsImgH             = 0;
    private int optionsImgX             = 0;
    private int exitImgW                = 0;
    private int exitImgH                = 0;
    private int exitImgX                = 0;
    private int selectorY               = 0;

    //const
    private final Color GREEN_COLOR     = new Color(51, 152, 101, 255);
    private final int LABEL_PLAY_GAME_Y = 577;
    private final int LABEL_OPTIONS_Y   = 710;
    private final int LABEL_EXIT_Y      = 782;
    private final int SELECTOR_X        = 105;
    private final int BASE_SELECTOR_Y   = 582;
    private final int SELECTOR_DIFF     = 134;
    private final int SELECTOR_DIFF_OFF = -63;
    private final int starH             = 628;

    //control
    private byte currentSelectorPos     = 0;
    private byte currentStageSelection  = 0;
    private volatile long framecounter  = 0;

    //TODO: Music & SoundFX
    private volatile Audio music        = null;
    private volatile Audio menuSelect   = null;
    

    /**
     * Constructor
     * @param g2d
     * @param windowWidth
     * @param windowHeight
     */
    public Menu(Game game, int windowWidth, int windowHeight) {
        this.gameRef        = game;
        this.windowHeight   = windowHeight;
        this.windowWidth    = windowWidth;

        //load the images
        this.starOn         = LoadingStuffs.getInstance().getImage("star-on");
        this.selector       = LoadingStuffs.getInstance().getImage("selector");
        this.logo           = LoadingStuffs.getInstance().getImage("logo");
        this.labelPlayGame  = LoadingStuffs.getInstance().getImage("label-play-game");
        this.labelOptions   = LoadingStuffs.getInstance().getImage("label-options");
        this.labelExit      = LoadingStuffs.getInstance().getImage("label-exit");
        this.starOff        = LoadingStuffs.getInstance().getImage("star-off");
        this.music          = LoadingStuffs.getInstance().getAudio("menu-music");
        this.menuSelect     = LoadingStuffs.getInstance().getAudio("menu-select");

        //create the buffered image
        this.drawInBuffer();
    }

    /**
     * This private method construct the BG just once.
     * Than, when necessary it is ploted in the backbuffer.
     */
    private void drawInBuffer() {
        if (this.bgd2 == null) {

            //create a backbuffer image for doublebuffer
            this.bgBufferImage  = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleVolatileImage(this.windowWidth, this.windowHeight);
            this.bgd2           = (Graphics2D)bgBufferImage.getGraphics();

            //paint all bg in green
            this.bgd2.setBackground(GREEN_COLOR);
            this.bgd2.clearRect(0, 0, this.windowWidth, this.windowHeight);
            
            //calc image positions
            int logoImgW = this.logo.getWidth();
            int logoImgX = ((this.windowWidth - logoImgW)/2);
            int logoImgY = 84;

            this.labelPlayW = this.labelPlayGame.getWidth();
            this.labelPlayH = this.labelPlayGame.getHeight();
            this.labelPlayX = ((this.windowWidth - labelPlayW)/2);

            this.optionsImgW = this.labelOptions.getWidth();
            this.optionsImgH = this.labelOptions.getHeight();
            this.optionsImgX = ((this.windowWidth - optionsImgW)/2);

            this.exitImgW = this.labelExit.getWidth();
            this.exitImgH = this.labelExit.getHeight();
            this.exitImgX = ((this.windowWidth - exitImgW)/2);

            //draw static logo
            this.bgd2.drawImage(this.logo, logoImgX, logoImgY, null);

            //draw static stars
            this.bgd2.drawImage(this.starOn, 441, starH, null);
            this.bgd2.drawImage(this.starOff, 487, starH, null);
            this.bgd2.drawImage(this.starOff, 534, starH, null);
            this.bgd2.drawImage(this.starOff, 580, starH, null);
            this.bgd2.drawImage(this.starOff, 627, starH, null);
            this.bgd2.drawImage(this.starOff, 673, starH, null);
            this.bgd2.drawImage(this.starOff, 720, starH, null);
            this.bgd2.drawImage(this.starOff, 767, starH, null);
            this.bgd2.drawImage(this.starOff, 817, starH, null);
            this.bgd2.drawImage(this.starOff, 862, starH, null);
        }
    }

    /**
     * Update the gameover scene and its elements
     * @param frametime
     */
    public void update(long frametime) {

        this.framecounter += frametime;
        if (this.framecounter == frametime) {
            this.music.playContinuously();
        }
        
        this.selectorY = BASE_SELECTOR_Y + (this.currentSelectorPos * SELECTOR_DIFF) + ((this.currentSelectorPos == 2)?SELECTOR_DIFF_OFF:0);
    }

    /**
     * Foward the keys
     * @param frametime
     */
    public void move(int key) {
        if (key == 40) {
            this.currentSelectorPos = (byte)(++this.currentSelectorPos%3);
            this.menuSelect.play();
        } else if (key == 38) {
            this.currentSelectorPos = (--this.currentSelectorPos<0)?2:this.currentSelectorPos;
            this.menuSelect.play();
        }

        if ((key == 10 || key == 32)) {
            if (this.currentSelectorPos == 2) {
                this.gameRef.exitGame();
            } else if (this.currentSelectorPos == 1) {
                this.gameRef.changeGameStateToOption();
            } else if (this.currentSelectorPos == 0) {
                this.music.stop();
                this.gameRef.changeGameStateToInGame(this.currentStageSelection + 1);
            }
        }

        if (this.currentSelectorPos == 0) {
            if (key == 39) {
                this.currentStageSelection = (byte)(++this.currentStageSelection%10);
            } else if (key == 37) {
                this.currentStageSelection = (--this.currentStageSelection<0)?9:this.currentStageSelection;
            }
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

        //draw dynamic images
        //draw selector
        this.gameRef.getG2D().drawImage(this.selector, SELECTOR_X, this.selectorY, null);

        //draw labels
        this.gameRef.getG2D().drawImage(this.labelPlayGame, 
                                        this.labelPlayX, 
                                        LABEL_PLAY_GAME_Y, 
                                        this.labelPlayW + this.labelPlayX, 
                                        this.labelPlayH + LABEL_PLAY_GAME_Y, 
                                        0, 0, 
                                        this.labelPlayW, 
                                        this.labelPlayH, 
                                        null);

        this.gameRef.getG2D().drawImage(this.labelOptions, 
                                        this.optionsImgX, 
                                        this.LABEL_OPTIONS_Y, 
                                        this.optionsImgW + this.optionsImgX, 
                                        this.optionsImgH + this.LABEL_OPTIONS_Y, 
                                        0, 
                                        0, 
                                        this.optionsImgW, 
                                        this.optionsImgH, 
                                        null);

        this.gameRef.getG2D().drawImage(this.labelExit, 
                                        this.exitImgX, 
                                        LABEL_EXIT_Y, 
                                        this.exitImgW + this.exitImgX, 
                                        this.exitImgH + LABEL_EXIT_Y, 
                                        0, 0, 
                                        this.exitImgW, 
                                        this.exitImgH, 
                                        null);

        switch (this.currentStageSelection)
        {
            case 9:
                this.gameRef.getG2D().drawImage(this.starOn, 862, starH, null);
            case 8:
                this.gameRef.getG2D().drawImage(this.starOn, 817, starH, null);
            case 7:
                this.gameRef.getG2D().drawImage(this.starOn, 767, starH, null);
            case 6:
                this.gameRef.getG2D().drawImage(this.starOn, 720, starH, null);
            case 5:
                this.gameRef.getG2D().drawImage(this.starOn, 673, starH, null);
            case 4:
                this.gameRef.getG2D().drawImage(this.starOn, 627, starH, null);
            case 3:
                this.gameRef.getG2D().drawImage(this.starOn, 580, starH, null);
            case 2:
                this.gameRef.getG2D().drawImage(this.starOn, 534, starH, null);
            case 1:
                this.gameRef.getG2D().drawImage(this.starOn, 487, starH, null);
            case 0:
                this.gameRef.getG2D().drawImage(this.starOn, 441, starH, null);
        }
    }
}