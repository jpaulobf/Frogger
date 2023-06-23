package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.VolatileImage;
import util.LoadingStuffs;
import java.awt.image.BufferedImage;
import java.awt.GraphicsEnvironment;

public class Menu {

    //Scenario variables
    private Graphics2D bgd2             = null;
    private int windowWidth             = 0;
    private int windowHeight            = 0;
    private Game gameRef                = null;
    private BufferedImage selector      = null;
    private BufferedImage logo          = null;
    private VolatileImage bgBufferImage = null;
    private BufferedImage labelPlayGame = null;
    private BufferedImage labelOptions  = null;
    private BufferedImage labelExit     = null;
    private final Color greenColor      = new Color(51, 152, 101, 255);
    private int labelPlayW              = 0;
    private int labelPlayH              = 0;
    private int labelPlayX              = 0;
    private final int labelPlayY        = 577;
    private int optionsImgW             = 0;
    private int optionsImgH             = 0;
    private int optionsImgX             = 0;
    private final int optionsImgY       = 710;
    private int exitImgW                = 0;
    private int exitImgH                = 0;
    private int exitImgX                = 0;
    private final int exitImgY          = 782;

    /**
     * Constructor
     * @param g2d
     * @param windowWidth
     * @param windowHeight
     */
    public Menu(Game game, int windowWidth, int windowHeight) {
        this.windowHeight   = windowHeight;
        this.windowWidth    = windowWidth;
        this.gameRef        = game;
        this.drawInBuffer();
    }

    /**
     * This private method construct the BG just once.
     * Than, when necessary it is ploted in the backbuffer.
     */
    private void drawInBuffer() {
        if (this.bgd2 == null) {
            
            this.selector       = (BufferedImage)LoadingStuffs.getInstance().getStuff("selector");
            this.logo           = (BufferedImage)LoadingStuffs.getInstance().getStuff("logo");
            this.labelPlayGame  = (BufferedImage)LoadingStuffs.getInstance().getStuff("label-play-game");
            this.labelOptions   = (BufferedImage)LoadingStuffs.getInstance().getStuff("label-options");
            this.labelExit      = (BufferedImage)LoadingStuffs.getInstance().getStuff("label-exit");

            //create a backbuffer image for doublebuffer
            this.bgBufferImage  = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleVolatileImage(this.windowWidth, this.windowHeight);
            this.bgd2           = (Graphics2D)bgBufferImage.getGraphics();

            //paint all bg in black
            this.bgd2.setBackground(greenColor);
            this.bgd2.clearRect(0, 0, this.windowWidth, this.windowHeight);
            
            int logoImgW = this.logo.getWidth();
            int logoImgH = this.logo.getHeight();
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

            this.bgd2.drawImage(this.logo, logoImgX, logoImgY, logoImgW + logoImgX, logoImgH + logoImgY, 
                                               0, 0, logoImgW, logoImgH, null);
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
        this.gameRef.getG2D().setBackground(greenColor);
        this.gameRef.getG2D().clearRect(0, 0, this.windowWidth, this.windowHeight * 2);

        //After construct the bg once, copy it to the graphic device
        this.gameRef.getG2D().drawImage(this.bgBufferImage, 0, 0, null);
        this.gameRef.getG2D().drawImage(this.selector, 105, 582, null);

        this.gameRef.getG2D().drawImage(this.labelPlayGame, labelPlayX, labelPlayY, labelPlayW + labelPlayX, labelPlayH + labelPlayY, 
                                                0, 0, labelPlayW, labelPlayH, null);

        this.gameRef.getG2D().drawImage(this.labelOptions, 
                                        this.optionsImgX, 
                                        this.optionsImgY, 
                                        this.optionsImgW + this.optionsImgX, 
                                        this.optionsImgH + this.optionsImgY, 
                                        0, 
                                        0, 
                                        this.optionsImgW, 
                                        this.optionsImgH, 
                                        null);

        this.gameRef.getG2D().drawImage(this.labelExit, exitImgX, exitImgY, exitImgW + exitImgX, exitImgH + exitImgY, 
                                            0, 0, exitImgW, exitImgH, null);
    }
}