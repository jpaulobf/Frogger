package game;

import game.interfaces.SpriteCollection;
import game.interfaces.Stages;
import util.LoadingStuffs;

import java.awt.image.BufferedImage;

/**
 * Mosquito class
 */
public class Mosquito extends SpriteImpl {

    private BufferedImage mosquitoSprite            = null;
    private volatile SpriteCollection spriteColRef  = null;
    private volatile Dockers dockers                = null;
    private final double [] positionsX              = {102, 378, 654, 930, 1206};
    private volatile boolean finished               = false;
    private volatile long framecounter              = 0;
    private volatile boolean isVisible              = false;
    private volatile byte sorted                    = -1;

    /**
     * Constructor
     */
    public Mosquito(SpriteCollection spriteCol) {
        this.spriteColRef   = spriteCol;
        this.mosquitoSprite = LoadingStuffs.getInstance().getImage("mosquito");
        this.dockers        = (Dockers)this.spriteColRef;
        this.width          = (short)this.mosquitoSprite.getWidth();
        this.height         = (byte)this.mosquitoSprite.getHeight();
        this.positionY      = (this.height/2) + 15;
    }

    @Override
    public synchronized void update(long frametime) {
        //increment framecounter
        this.framecounter += frametime;
        
        //just one per cycle
        if (this.framecounter == frametime) {
            //start the process
            this.finished = false;
            byte free = 1;

            if (!dockers.getDockersComplete() && dockers.getFreeDockersCounter() >= free) {
                //recover the taked dockers
                boolean [] isInWhichDock = dockers.getIsInDock();
                byte counter    = 0;
                byte MAX_TRIES  = 10;

                //sort one free docker
                do {
                    this.sorted = (byte)(Math.random() * 5);
                    if (++counter > MAX_TRIES) {
                        break;
                    }
                } while (isInWhichDock[sorted] || this.sorted == this.dockers.getCurrentGatorHead());

                if (sorted != -1) {
                    //after sort, set the mosquito
                    this.dockers.setCurrentMosquito(sorted);

                    //update the X & Y position
                    this.positionX = this.positionsX[sorted];
                }
            }
        } else {
            if (this.sorted != -1) {
                //set visible
                this.isVisible = true;

                //duration
                if (this.framecounter >= (Stages.MOSQUITO_CONFIG[Stages.CURRENT_STAGE[0]][1] * 1_000_000_000L)) {
                    this.setInvisible();
                    this.finished = true;
                }
            } else {
                this.finished = true;
            }
        }
    }

    @Override
    public synchronized void draw(long frametime) {
        if (this.isVisible) {
            this.spriteColRef.getG2D().drawImage(this.mosquitoSprite, (int)this.positionX, (int)this.positionY + this.scenarioOffsetY, (int)(this.positionX + this.width), (int)(this.positionY + this.height + this.scenarioOffsetY), //dest w1, h1, w2, h2
                                                                      0, 0, this.width, this.height, //source w1, h1, w2, h2
                                                                      null);
        }
    }

    /**
     * Verify if the appearence (in this cycle) has finished
     * @return
     */
    public boolean appearenceFinished() {
        return (this.finished);
    }

    /**
     * Makes the mosquito invisible
     */
    public synchronized void setInvisible() {
        this.framecounter   = 0;
        this.isVisible      = false;
        this.positionX      = -1000;
        this.sorted         = -1;
        this.dockers.setCurrentMosquito((byte)-1);
    }

    /**
     * Verify if the mosquito is in the Docker
     */
    public synchronized boolean isInTheDocker(int docker) {
        return (this.sorted == docker);
    }

    /**
     * Reset the mosquito
     */
    public void reset() {
        this.finished = true;
        this.setInvisible();
    }
}