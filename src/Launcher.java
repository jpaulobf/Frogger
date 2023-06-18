import game.SplashScreen;

/**
 * Class for launching the main program
 * Author: Joao Paulo B Faria
 * Date: Oct-2022
 */
public class Launcher {

    //Define if OpenGL is enabled or not
    public static final boolean setOpenGL   = false;
    public static final boolean fullscreen  = false;

    public static void main(String[] args) {
        //enable the openGL
        if (setOpenGL) {
            System.setProperty("sun.java2d.opengl", "True");
        }
        //System.setProperty("sun.java2d.d3d", "True");
        
        //start the thread
        //--->>> FPS options (SplashScreen constructor) - 0 (unlimited) - 30/60/90/120/240
        Thread thread = new Thread(new SplashScreen(0, fullscreen), "engine");
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }
}
