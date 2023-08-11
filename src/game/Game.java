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
    private ExitScreen exitScreen           = null;
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
        this.menu           = new Menu(this, this.wwm, this.whm);
        this.options        = new Options(this, this.wwm, this.whm);
        this.score          = new Score(this, this.wwm, this.scoreHeight);
        this.scenario       = new Scenario(this, this.wwm, this.whm, this.scoreHeight);
        this.sidewalkSnake  = new SidewalkSnake(this, this.wwm);
        this.frog           = new Frog(this);
        this.hud            = new HUD(this, this.wwm, this.scoreHeight + this.whm, this.HUDHeight);
        this.gameOver       = new GameOver(this, this.wwm, this.completeWhm);
        this.message        = new Message(this, this.wwm, this.completeWhm);
        this.timer          = new Timer(this, this.wwm, this.scoreHeight + this.whm);
        this.theme          = LoadingStuffs.getInstance().getAudio("theme");
        this.gameoverTheme  = LoadingStuffs.getInstance().getAudio("gameover-theme");
        this.exitScreen     = new ExitScreen(this, this.wwm, this.whm);
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
                
            } else if (this.gameState.getCurrentState() == StateMachine.OPTIONS) {
                this.framecounter += frametime;
                this.options.update(frametime);

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
                    this.skipDraw();
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
            } else if (this.gameState.getCurrentState() == StateMachine.EXITING) {
                this.framecounter += frametime;
                
                if (this.framecounter == frametime) {
                    this.exitScreen.firstUpdate(frametime);
                }
                
                this.exitScreen.update(frametime);
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
            } else if (this.gameState.getCurrentState() == StateMachine.IN_GAME ||
                       this.gameState.getCurrentState() == StateMachine.EXITING) {
                this.score.draw(frametime);
                this.scenario.draw(frametime);
                this.sidewalkSnake.draw(frametime);
                this.frog.draw(frametime);
                this.hud.draw(frametime);
                this.message.draw(frametime);
                this.timer.draw(frametime);
                if (this.gameState.getCurrentState() == StateMachine.EXITING) {
                    this.exitScreen.draw(frametime);
                }
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
        } else if (this.gameState.getCurrentState() == StateMachine.EXITING) {
            this.exitScreen.move(keyDirection);
        }
    }

    private void toogleMuteTheme() {
        this.toogleMuteTheme(false);
    }

    /**
     * Mute / unmute the game theme
     */
    @Override
    public void toogleMuteTheme(boolean isToMute) {
        if (!this.mute || isToMute) {
            this.theme.pause();
            this.mute = false;
        } else {
            this.theme.playContinuously();
        }
        this.mute = !this.mute;
    }

    /**
     * Set Music Volume
     * @param volume
     */
    public void setMusicVolume(byte volume) {
        LoadingStuffs.getInstance().getMusicList().stream().forEach(item -> {
            item.setVolume(volume);
        });
    }

    /**
     * Set SFX Volume
     * @param volume
     */
    public void setSFXVolume(byte volume) {
        LoadingStuffs.getInstance().getSFXList().stream().forEach(item -> {
            item.setVolume(volume);
        });
    }

    /**
     * Mute game music
     */
    public void muteMusic() {
        LoadingStuffs.getInstance().getMusicList().stream().forEach(item -> {
            item.muteVolume();
        });
    }

    /**
     * Unmute game music
     */
    public void unmuteMusic() {
        LoadingStuffs.getInstance().getMusicList().stream().forEach(item -> {
            item.unmuteVolume();
        });
    }

    /**
     * Mute SFX
     */
    public void muteSFXs() {
        LoadingStuffs.getInstance().getSFXList().stream().forEach(item -> {
            item.muteVolume();
        });
    }

    /**
     * Unmute SFX
     */
    public void unmuteSFXs() {
        LoadingStuffs.getInstance().getSFXList().stream().forEach(item -> {
            item.unmuteVolume();
        });
    }

    /**
     * Decrease the Master Volume
     */
    @Override
    public void decMasterVolume() {
        LoadingStuffs.getInstance().getMusicList().stream().forEach(item -> {
            item.decVolume(1);
        });

        LoadingStuffs.getInstance().getSFXList().stream().forEach(item -> {
            item.decVolume(1);
        });
    }

    /**
     * Increase the Master Volume
     */
    @Override
    public void incMasterVolume() {
        LoadingStuffs.getInstance().getMusicList().stream().forEach(item -> {
            item.addVolume(1);
        });

        LoadingStuffs.getInstance().getSFXList().stream().forEach(item -> {
            item.addVolume(1);
        });
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
        this.toogleMuteTheme(true);
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

    /**
     * Call the next stage
     * @param togglePause
     */
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
                if (keyCode == 45) {this.muteMusic();}
                if (keyCode == 61) {this.unmuteMusic();}
            }
        }

        if (this.gameState.getCurrentState() == StateMachine.IN_GAME) {
            //when ESC is pressed
            if (keyCode == 27) {
                this.gameState.setCurrentState(StateMachine.EXITING);
                this.skipDraw();
                this.framecounter = 0;
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

    /**
     * Return game lives
     * @return
     */
    public byte getOptionsDefinedLives() {
        return (this.options.getOptionsDefinedLives());
    }

    /**
     * Update Frogger Lives
     */
    public void updateFroggerLives() {
        this.frog.setLives(this.getOptionsDefinedLives());
    }

    @Override
    public void changeGameState(int state) {
        this.skipDraw();
        this.gameState.currentState = state;
    }

    @Override
    public void gameTerminate() {
        // this.score.reset();
        this.theme.stop();
        this.framecounter   = 0;
        this.skipDraw       = true;
    }

    @Override
    public void toMainMenu() {
        this.changeGameState(StateMachine.MENU);
    }
}