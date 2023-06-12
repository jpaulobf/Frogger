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
            
            this.selector   = (BufferedImage)LoadingStuffs.getInstance().getStuff("selector");
            this.logo       = (BufferedImage)LoadingStuffs.getInstance().getStuff("logo");

            
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

            this.bgd2.drawImage(this.logo, imgX, imgY, imgW + imgX, imgH + imgY, 
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
        this.gameRef.getG2D().setBackground(greenColor);
        this.gameRef.getG2D().clearRect(0, 0, this.windowWidth, this.windowHeight * 2);

        //After construct the bg once, copy it to the graphic device
        this.gameRef.getG2D().drawImage(this.bgBufferImage, 0, 0, null);


        this.gameRef.getG2D().drawImage(this.selector, 105, 582, null);

        //todo... os demais itens...
    }
}