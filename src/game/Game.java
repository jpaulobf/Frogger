package game;

import util.Audio;
import util.LoadingStuffs;

import java.awt.Color;
import java.awt.Graphics2D;
import game.interfaces.Stages;
import interfaces.GameInterface;
import java.awt.image.VolatileImage;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;

/**
 * Class responsable for the game
 */
public class Game implements GameInterface {

    //the game statemachine goes here
    private StateMachine gameState          = null;

    //some support and the graphical device itself
    private Graphics2D g2d                  = null;

    //extra volatile image for hud
    private final byte HUDHeight            = 40;
    private final byte scoreHeight          = 40;

    //the game variables go here...
    private Score score                     = null;
    private Scenario scenario               = null;
    private HUD hud                         = null;
    private SidewalkSnake sidewalkSnake     = null;
    private Frog frog                       = null;
    private GameOver gameOver               = null;
    private Menu menu                       = null;
    private Options options                 = null;
    private Message message                 = null;
    private Timer timer                     = null;
    private volatile Audio theme            = null;
    private volatile Audio gameoverTheme    = null;
    private volatile long framecounter      = 0;
    private volatile boolean mute           = false;
    private volatile boolean canContinue    = true;
    private volatile boolean reseting       = false;
    private volatile boolean skipDraw       = false;

    //width and height of window for base metrics of the game (minus HUD)
    private final int wwm                   = 1344;
    private final int whm                   = 832;
    private final int completeWhm           = this.scoreHeight + this.whm + this.HUDHeight;

    private VolatileImage bufferImage       = null;
    private GraphicsEnvironment ge          = null;
    private GraphicsDevice dsd              = null;
    private Graphics2D g2dFS                = null;

    /**
     * Game constructor
     */
    public Game() {
        //create the double-buffering image
        this.ge             = GraphicsEnvironment.getLocalGraphicsEnvironment();
        this.dsd            = ge.getDefaultScreenDevice();
        this.bufferImage    = dsd.getDefaultConfiguration().createCompatibleVolatileImage(this.wwm, this.completeWhm);
        this.g2d            = (Graphics2D)bufferImage.getGraphics();
        this.g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        //////////////////////////////////////////////////////////////////////
        // ->>>  create the game elements objects
        //////////////////////////////////////////////////////////////////////
        this.gameState      = new StateMachine(this);
        this.score          = new Score(this, this.wwm, this.scoreHeight);
        this.scenario       = new Scenario(this, this.wwm, this.whm, this.scoreHeight);
        this.sidewalkSnake  = new SidewalkSnake(this, this.wwm);
        this.frog           = new Frog(this);
        this.hud            = new HUD(this, this.wwm, this.scoreHeight + this.whm, this.HUDHeight);
        this.gameOver       = new GameOver(this, this.wwm, this.completeWhm);
        this.message        = new Message(this, this.wwm, this.completeWhm);
        this.timer          = new Timer(this, this.wwm, this.scoreHeight + this.whm);
        this.menu           = new Menu(this, this.wwm, this.whm);
        this.options        = new Options(this, this.wwm, this.whm);
        this.theme          = (Audio)LoadingStuffs.getInstance().getStuff("theme");
        this.gameoverTheme  = (Audio)LoadingStuffs.getInstance().getStuff("gameover-theme");
    }
    
    /**
     * Update the game logic / receives the frametime
     * @param frametime
     */
    @Override
    public synchronized void update(long frametime) {
        //how many pixels per second I want?
        //ex.: movement at 200px/s
        //To do so, I need to divide 1_000_000_000 (1 second) by the exactly frametime to know the current FPS
        //With this number in hand, divide the pixel distance by the current fps
        //this must be your maximum movement amount in pixels
        //example:
        //double movementPerSecond = 100D;
        //double step = movementPerSecond / (double)(1_000_000_000D / (double)frametime);

        if (!this.reseting) {
            //////////////////////////////////////////////////////////////////////
            // ->>>  update the game elements
            //////////////////////////////////////////////////////////////////////
            if (this.gameState.getCurrentState() == StateMachine.STAGING) {
                this.framecounter += frametime;
                
                if (this.framecounter == frametime) { //update just one time
                    this.scenario.update(frametime);
                    this.message.update(frametime);
                    this.hud.update(frametime);
                    this.timer.update(frametime);
                } else {
                    this.message.update(frametime);
                    if (!this.message.finished()) {
                        this.message.showStageAnnouncement();
                    } else {
                        this.framecounter = 0;
                        this.message.showing(false);
                        this.message.toogleStageAnnouncement();
                        this.skipDraw();
                        this.gameState.setCurrentState(StateMachine.IN_GAME);
                    }
                }
            } else if (this.gameState.getCurrentState() == StateMachine.MENU) {
                this.framecounter += frametime;

                this.menu.update(frametime);
                if (this.framecounter == frametime) { //update just one time
                    
                } else {
                }
                
            } else if (this.gameState.getCurrentState() == StateMachine.OPTIONS) {
                this.framecounter += frametime;

                this.options.update(frametime);
                if (this.framecounter == frametime) { //update just one time
                    
                } else {
                }
                
            } else if (this.gameState.getCurrentState() == StateMachine.IN_GAME) {
                this.framecounter += frametime;
                if (this.framecounter == frametime) {
                    this.theme.playContinuously();
                }

                this.score.update(frametime);
                this.scenario.update(frametime);
                this.sidewalkSnake.update(frametime);
                this.frog.update(frametime);
                this.hud.update(frametime);
                this.timer.update(frametime);
                this.message.update(frametime);
                
                if (this.frog.getLives() == 0) { //after possible colision, check lives.
                    this.gameState.setCurrentState(StateMachine.GAME_OVER);
                    this.score.storeNewHighScore();
                    this.score.reset();
                    this.framecounter = 0;
                }
            } else if (this.gameState.getCurrentState() == StateMachine.GAME_OVER) {
                this.framecounter += frametime;
                if (this.framecounter >= 7_000_000_000L) {
                    this.framecounter = 0;
                    this.frog.resetLives();
                    this.softReset();
                    this.theme.playContinuously();
                    this.gameState.setCurrentState(StateMachine.IN_GAME);
                } else if (this.framecounter == frametime) { //run just once
                    this.theme.stop();
                    this.gameoverTheme.play();
                }
            }

            //Prevent overflow
            if (this.framecounter < 0) {
                this.framecounter = 1;
            }
        }
    }

    /**
     * Draw the game elements
     * @param frametime
     */
    @Override
    public synchronized void draw(long frametime) {

        //this graphical device (g2d) points to backbuffer, so, we are making things behide the scenes
        //clear the stage
        this.g2d.setBackground(Color.BLACK);
        this.g2d.clearRect(0, 0, this.wwm, this.whm + this.HUDHeight);

        if (!this.reseting && !this.skipDraw) {
            //////////////////////////////////////////////////////////////////////
            // ->>>  draw the game elements
            //////////////////////////////////////////////////////////////////////
            if (this.gameState.getCurrentState() == StateMachine.STAGING) {
                this.message.draw(frametime);
            } else if (this.gameState.getCurrentState() == StateMachine.MENU) {
                this.menu.draw(frametime);
            } else if (this.gameState.getCurrentState() == StateMachine.OPTIONS) {
                this.options.draw(frametime);
            } else if (this.gameState.getCurrentState() == StateMachine.IN_GAME) {
                this.score.draw(frametime);
                this.scenario.draw(frametime);
                this.sidewalkSnake.draw(frametime);
                this.frog.draw(frametime);
                this.hud.draw(frametime);
                this.message.draw(frametime);
                this.timer.draw(frametime);
            } else if (this.gameState.getCurrentState() == StateMachine.GAME_OVER) {
                this.gameOver.draw(frametime);
            }
        } else {
            this.skipDraw = false;
        }
    }

    /**
     * Draw the game in full screen
     */
    @Override
    public void drawFullscreen(long frametime, int fullScreenXPos, int fullScreenYPos, int fullScreenWidth, int fullScreenHeight) {
        this.g2dFS.drawImage(this.bufferImage, fullScreenXPos, fullScreenYPos, fullScreenWidth, fullScreenHeight, 
                                               0, 0, this.wwm, this.completeWhm, null);
    }

    /**
     * Control the game main character movement
     * @param keyDirection
     */
    public void movement(int keyDirection) {
        if (this.gameState.getCurrentState() == StateMachine.IN_GAME) {
            this.frog.move(keyDirection);
        } else if (this.gameState.getCurrentState() == StateMachine.MENU) {
            this.menu.move(keyDirection);
        } else if (this.gameState.getCurrentState() == StateMachine.OPTIONS) {
            this.options.move(keyDirection);
        }
    }

    /**
     * Mute / unmute the game theme
     */
    @Override
    public void toogleMuteTheme() {
        if (!this.mute) {
            this.theme.pause();
        } else {
            this.theme.playContinuously();
        }
        this.mute = !this.mute;
    }

    /**
     * Decrease the Master Volume
     */
    @Override
    public void decMasterVolume() {
        this.decVolumeSFX();
        this.decVolumeTheme();
    }

    /**
     * Increase the Master Volume
     */
    @Override
    public void incMasterVolume() {
        this.incVolumeSFX();
        this.incVolumeTheme();
    }

    /**
     * Decrease only the theme
     */
    @Override
    public void decVolumeTheme() {
        this.theme.decVolume(1);
        this.gameoverTheme.decVolume(1);
    }

    /**
     * Increase the theme volume
     */
    @Override
    public void incVolumeTheme() {
        this.theme.addVolume(1);
        this.gameoverTheme.addVolume(1);
    }

    /**
     * Decrease the SFX Volume
     */
    @Override
    public void decVolumeSFX() {
        this.frog.jumpAudio.decVolume(1);
        this.frog.plunkAudio.decVolume(1);
        this.frog.squashAudio.decVolume(1);
        this.frog.dockerAudio.decVolume(1);
        this.frog.clearAudio.decVolume(1);
        this.frog.catchAudio.decVolume(1);
        this.timer.lasttime.decVolume(1);
    }

    /**
     * Increase the SFX Volume
     */
    @Override
    public void incVolumeSFX() {
        this.frog.jumpAudio.addVolume(1);
        this.frog.plunkAudio.addVolume(1);
        this.frog.squashAudio.addVolume(1);
        this.frog.dockerAudio.addVolume(1);
        this.frog.clearAudio.addVolume(1);
        this.frog.catchAudio.addVolume(1);
        this.timer.lasttime.addVolume(1);
    }

    /**
     * Stop the theme position
     */
    @Override
    public void stopTheme() {
        this.theme.stop();
    }

    /**
     * Update game graphics
     * @param g2d
     */
    @Override
    public void updateGraphics2D(Graphics2D g2d) {
        this.g2dFS = g2d;
    }

    /**
     * Recupera o G2D
     * @return
     */
    @Override
    public Graphics2D getG2D() {
        return (this.g2d);
    }

    /**
     * Toogle the pause button
     */
    @Override
    public void tooglePause() {
        this.toogleMuteTheme();
        this.frog.tooglePause();
        this.sidewalkSnake.tooglePause();
        this.scenario.tooglePause();
        this.timer.tooglePause();
    }

    /**
     * Game reset
     */
    @Override
    public void softReset() {
        this.framecounter = 0;
        this.scenario.getVehicles().reset();
        this.scenario.getTrunks().reset();
        this.scenario.getTrunks().getTrunkSnake().reset();
        this.scenario.getTurtles().reset();
        this.scenario.getDockers().reset();
        this.sidewalkSnake.reset();
        this.timer.reset();
        this.frog.frogReset();
    }

    public synchronized void nextStage(boolean togglePause) {
        int next = Stages.CURRENT_STAGE[0] + 1;
        this.setCurrentStage(next, togglePause);
    }

    /** 
     * go to the next stage 
     */
    public synchronized void setCurrentStage(int currentStage, boolean togglePause) {

        //disable elements update
        this.toogleReseting();

        if (!togglePause)
        {
            //pause timer & frog
            this.timer.tooglePause();
            this.frog.tooglePause();
        }

        this.scenario.getDockers().reset();
        this.scenario.getTrunks().getTrunkSnake().reset();
        this.timer.reset();
        this.frog.frogReset();

        Stages.CURRENT_STAGE[0] = currentStage;
        this.scenario.nextStage();
        this.sidewalkSnake.nextStage();

        //unpause timer & frog
        this.timer.tooglePause();
        this.frog.tooglePause();
        
        //return to initial position & play
        this.theme.stop();
        this.toogleMuteTheme();

        //enable elements update
        this.toogleReseting();
    }

    /**
     * Game keypress
     */
    public void keyPressed(int keyCode) {
        if (this.canContinue) {
            this.canContinue = false;
            if (!this.reseting) {
                this.movement(keyCode);
                if (keyCode == 45) {this.decMasterVolume();}
                if (keyCode == 61) {this.incMasterVolume();}
            }
        }
    }

    /**
     * Game keyRelease
     */
    public void keyReleased(int keyCode) {
        this.canContinue = true;
        if (!this.reseting) {
            if (keyCode == 77) {this.toogleMuteTheme();}
            if (keyCode == 80) {this.tooglePause();}
            if (keyCode == 82) {this.softReset();}
            if (keyCode == 84) {this.nextStage(false);}
        }
    }

    /**
     * Accessor methods
     * @return
     */
    public Score getScore()                     {   return (this.score);        }
    public Scenario getScenario()               {   return (this.scenario);     }
    public HUD getHud()                         {   return this.hud;            }
    public Frog getFrog()                       {   return this.frog;           }
    public GameOver getGameOver()               {   return this.gameOver;       }
    public StateMachine getGameState()          {   return this.gameState;      }
    public Message getMessages()                {   return this.message;        }
    public Timer getTimer()                     {   return this.timer;          }
    public SidewalkSnake getSidewalkSnake()     {   return this.sidewalkSnake;  }
    public int getInternalResolutionWidth()     {   return (this.wwm);          }
    public int getInternalResolutionHeight()    {   return (this.completeWhm);  }
    public VolatileImage getBufferedImage()     {   return (this.bufferImage);  }
    public int getScenarioOffsetY()             {   return (this.scoreHeight);  }
    
    /**
     * Toogle reseting
     */
    public synchronized void toogleReseting() {
        this.reseting = !this.reseting;
    }

    /**
     * Skip 1 frame draw
     */
    private void skipDraw() {
        this.skipDraw = true;
    }

    /**
     * Exit game
     */
    public void exitGame() {
        System.exit(0);
    }

    /**
     * set game state to options
     */
    public void changeGameStateToOption() {
        this.skipDraw();
        this.gameState.setCurrentState(StateMachine.OPTIONS);
    }

    /**
     * set game state to options
     */
    public void changeGameStateToInGame(int currentStage) {
        this.skipDraw();
        this.setCurrentStage(currentStage, false);
        this.gameState.setCurrentState(StateMachine.STAGING);
    }

    /**
     * set game state to menu
     */
    public void changeGameStateToMenu() {
        this.skipDraw();
        this.gameState.setCurrentState(StateMachine.MENU);
    }
}