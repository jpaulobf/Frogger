package engine;

import interfaces.IGame;
import game.Game;

/**
 * Game factory
 */
public class GameFactory {
    public static IGame getGameInstance() {
        return (new Game());
    }
}
