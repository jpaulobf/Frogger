package interfaces;

import java.awt.Graphics2D;
import java.awt.image.VolatileImage;

/**
 * All games need to implement the IGame interface
 */
public interface GameInterface {
    /**
     * Game update
     * @param frametime
     */
    public void update(long frametime);

    /**
     * Game draw
     * @param frametime
     */
    public void draw(long frametime);

    /**
     * Draw the game in fullscreen
     * @param frametime
     * @param fullScreenXPos
     * @param fullScreenYPos
     * @param fullScreenWidth
     * @param fullScreenHeight
     */
    public void drawFullscreen(long frametime, int fullScreenXPos, int fullScreenYPos, int fullScreenWidth, int fullScreenHeight);

    /**
     * Recover the G2D from the buffer
     * @return
     */
    public Graphics2D getG2D();

    /**
     * Recover the bufferedImage
     * @return
     */
    public VolatileImage getBufferedImage();

    /**
     * Update Graphics for FullScreen
     * @param g2d
     */
    public void updateGraphics2D(Graphics2D g2d);

    /**
     * Get internal resolution - W
     * @return internal width resolution
     */
    public int getInternalResolutionWidth();

    /**
     * Get internal resolution - H
     * @return internal height resolution
     */
    public int getInternalResolutionHeight();

    /**
     * Return the scenario offsetY
     * @return
     */
    public int getScenarioOffsetY();
    
    /**
     * Mute the music
     */
    public void toogleMuteTheme(boolean isToMute);

    /**
     * Decrease the Master Volume
     */
    public void decMasterVolume();

    /**
     * Increase the Master Volume
     */
    public void incMasterVolume();

    /**
     * Stop the music
     */
    public void stopTheme();

    /**
     * Pause the game
     */
    public void tooglePause();

    /**
     * Soft reset
     */
    public void softReset();

    /**
     * Key pressed
     * @param keyCode
     */
    public void keyPressed(int keyCode);

    /**
     * Key released
     * @param keyCode
     */
    public void keyReleased(int keyCode);


    /**
     * Mute game music
     */
    public void muteMusic();

    /**
     * Unmute game music
     */
    public void unmuteMusic();

    /**
     * Mute SFX
     */
    public void muteSFXs();

    /**
     * Unmute SFX
     */
    public void unmuteSFXs();

    /**
     * Set Music Volume
     * @param volume
     */
    public void setMusicVolume(byte volume);

    /**
     * Set SFX Volume
     * @param volume
     */
    public void setSFXVolume(byte volume);

    /**
     * Change the game state
     * @param inGame
     */
    public void changeGameState(int inGame);

    /**
     * Change the game state
     * @param inGame
     */
    public void changeGameState(int state, boolean resetFrameCounter);

    /**
     * Terminate the game
     */
    public void gameTerminate();

    /**
     * Set the game back to main menu
     */    
    public void toMainMenu();

    /**
     * Set the game to ending state
     */
    public void endGame();

    /**
     * Cancel exiting and back to running game
     * @param ignoreNextEsc
     */
    public void backToGame(boolean ignoreNextEsc);

    /**
     * Exit the game
     */
    public void exitGame();

    /**
     * Change the gamestate to options menu
     */
    public void changeGameStateToOption();

    /**
     * Change the gamestate to ending
     */
    public void changeGameStateToEnding();

    /**
     * Change the gamestate to ingame
     */
    public void changeGameStateToInGame(int whichStage);
}