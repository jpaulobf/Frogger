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
            
            int imgW = this.logo.getWidth();
            int imgH = this.logo.getHeight();
            int imgX = ((this.windowWidth - imgW)/2);
            int imgY = 84;

            int logoImgW = this.labelPlayGame.getWidth();
            int logoImgH = this.labelPlayGame.getHeight();
            int logoImgX = ((this.windowWidth - logoImgW)/2);
            int logoImgY = 577;

            int optionsImgW = this.labelOptions.getWidth();
            int optionsImgH = this.labelOptions.getHeight();
            int optionsImgX = ((this.windowWidth - optionsImgW)/2);
            int optionsImgY = 710;

            int exitImgW = this.labelExit.getWidth();
            int exitImgH = this.labelExit.getHeight();
            int exitImgX = ((this.windowWidth - exitImgW)/2);
            int exitImgY = 782;


            this.bgd2.drawImage(this.logo, imgX, imgY, imgW + imgX, imgH + imgY, 
                                               0, 0, imgW, imgH, null);

            this.bgd2.drawImage(this.labelPlayGame, logoImgX, logoImgY, logoImgW + logoImgX, logoImgH + logoImgY, 
                                                0, 0, logoImgW, logoImgH, null);

            this.bgd2.drawImage(this.labelOptions, optionsImgX, optionsImgY, optionsImgW + optionsImgX, optionsImgH + optionsImgY, 
                                                0, 0, optionsImgW, optionsImgH, null);

            this.bgd2.drawImage(this.labelExit, exitImgX, exitImgY, exitImgW + exitImgX, exitImgH + exitImgY, 
                                                0, 0, exitImgW, exitImgH, null);
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

        //todo... os demais itens...
    }
}