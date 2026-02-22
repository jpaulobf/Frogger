import game.SplashScreen;

/**
 * Class for launching the main program
 * Author: Joao Paulo B Faria
 * Date: Oct-2022
 */
public class Launcher {

    //Define if OpenGL is enabled or not
    public static final boolean setOpenGL   = false;
    public static final boolean setD3D      = true;
    public static final boolean fullscreen  = false;

    public static void main(String[] args) {
        //Configurações de aceleração gráfica
        if (setOpenGL) {
            System.setProperty("sun.java2d.opengl", "true");
            System.setProperty("sun.java2d.d3d", "false");
        }
        
        if (setD3D) {
            // Força o uso do pipeline Direct3D (geralmente padrão no Windows)
            System.setProperty("sun.java2d.d3d", "true");
            System.setProperty("sun.java2d.opengl", "false");
        }
        
        //start the thread
        //--->>> FPS options (SplashScreen constructor) - 0 (unlimited) - 30/60/90/120/240
        Thread thread = new Thread(new SplashScreen(60, fullscreen), "engine");
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }
}
