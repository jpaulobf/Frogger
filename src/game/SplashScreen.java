package game;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import engine.Frogger;
import util.LoadingStuffs;

/*
    Project:    Modern 2D Java Game Engine
    Purpose:    Provide basics functionalities to write 2D games in Java in a more modern approach
    Author:     Mr. Joao P. B. Faria
    Date:       Octuber 2021
    WTCD:       This class, provides a selection screen, that could be hide forever, that allow the user to choose between window format (full, pseudo-full, windowed)
                and than, the syncronization method, frame cap, screen size & resolution.
*/
public class SplashScreen extends JFrame implements Runnable {

    private static final long serialVersionUID  = 1L;

    //this window properties
    private int positionX                       = 0;
    private int positionY                       = 0;
    private int windowWidth                     = 800;
    private int windowHeight                    = 650;
    private int w, h, x, y                      = 0;

    //desktop properties
    private int resolutionH                     = 0;
    private int resolutionW                     = 0;
    
    //the first 'canvas' & the backbuffer (for simple doublebuffer strategy)
    private JPanel canvas                       = null;
    private VolatileImage bufferImage           = null;
    private BufferedImage splashImage           = null;

    //some support and the graphical device itself
    private GraphicsEnvironment ge              = null;
    private GraphicsDevice dsd                  = null;
    private Graphics2D g2d                      = null;

    //this screen control logic parameter   
    private int selectedItem                    = 0;
    private int FPS                             = 0;
    private boolean fullscreen                  = false;
    
    /*
        WTMD: some responsabilites here:
            1) load some parameters from config file (if exists)
            2) center the window in the screen
            3) add a keylistener
            4) initialize the canvas and retrieve the graphical device objects
    */
    public SplashScreen(int FPS, boolean fullscreen) {

        //////////////////////////////////////////////////////////////////////
        // ->>>  for the window
        //////////////////////////////////////////////////////////////////////
        LoadingStuffs.getInstance();
        this.fullscreen = fullscreen;

        //load or provide the default configuration file
        new ConfigurationFile().verifyTheConfigurationFile();

        //set some properties for this window
        Dimension basic = new Dimension(this.windowWidth, this.windowHeight);
        this.setPreferredSize(basic);
        this.setMinimumSize(basic);
        this.setUndecorated(true);

        //default operation on close (exit in this case)
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        //recover the desktop resolution
        Dimension size = Toolkit.getDefaultToolkit(). getScreenSize();

        //and save this values
        this.resolutionH = (int)size.getHeight();
        this.resolutionW = (int)size.getWidth();

        //center the current window regards the desktop resolution
        this.positionX = (int)((size.getWidth() / 2) - (this.windowWidth / 2));
        this.positionY = (int)((size.getHeight() / 2) - (this.windowHeight / 2));
        this.setLocation(this.positionX, this.positionY);
        
        //add a keylistener
        this.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 39) {if (selectedItem < 2) { selectedItem++;paint(g2d);}}
                if (e.getKeyCode() == 37) {if (selectedItem > 0) {selectedItem--;paint(g2d);}}
                if (e.getKeyCode() == 27) {setVisible(false);System.exit(0);}
            }
        });

        //create the backbuffer from the size of screen resolution to avoid any resize process penalty
        this.ge             = GraphicsEnvironment.getLocalGraphicsEnvironment();
        this.dsd            = ge.getDefaultScreenDevice();
        this.bufferImage    = dsd.getDefaultConfiguration().createCompatibleVolatileImage(this.resolutionW, this.resolutionH);
        this.g2d            = (Graphics2D)bufferImage.getGraphics();
        
        //Get the already loaded image from loader
        this.splashImage    = LoadingStuffs.getInstance().getImage("splashImage");

        //////////////////////////////////////////////////////////////////////
        // ->>>  now, for the canvas
        //////////////////////////////////////////////////////////////////////
        this.w = this.splashImage.getWidth();
        this.h = this.splashImage.getHeight();
        this.x = (this.windowWidth - this.w) / 2;
        this.y = (this.windowHeight - this.h) / 2;
        this.FPS = FPS;

        //initialize the canvas
        this.canvas = new JPanel(null);
        this.canvas.setSize(windowWidth, windowHeight);
        this.canvas.setBackground(Color.BLACK);
        this.setVisible(true);
        this.canvas.setOpaque(true);
        
        //final parameters for the window
        this.add(canvas);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.requestFocus();
    }

    /*
        WTMD: Override the paint method, transfering the rendering control to draw.
    */
    @Override
    public void paint(Graphics g) {
        this.draw();
    }

    /*
        WTMD: This method draw the current screen, some steps described here:
            1) Clear the stage
            2) Print the main label
            3) Print the selection buttons
            4) Print the exit label
     */
    public void draw() {

        //update the window size variables if the user resize it.
        this.windowHeight = this.getHeight();
        this.windowWidth  = this.getWidth();

        if (this.g2d != null) {
            
            //clear the stage
            this.g2d.setBackground(new Color(9, 26, 52));
            this.g2d.clearRect(0, 0, this.resolutionW, this.resolutionH);

            //draw the splash image
            this.g2d.drawImage(this.splashImage, x, y, w + x, h + y, //dest w1, h1, w2, h2
                                                 0, 0, w, h, //source w1, h1, w2, h2
                                                 null);

            //At least, copy the backbuffer to the canvas screen
            this.canvas.getGraphics().drawImage(this.bufferImage, 0, 0, this);
        }
    }

    @Override
    public void run() {
        try {
            Thread.sleep(2_000);
            this.setVisible(false);
            //start the thread
            Thread thread = new Thread(new Frogger(this.FPS, this.fullscreen), "engine");
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.start();
        } catch (Exception e) {}
    }

    /*
        This subclass is still under development...
    */
    @SuppressWarnings("unused")
    private class ConfigurationFile {

        //comentários para o arquivo
        private final String commentWindowMode  = "#define the window mode (windowed or fullscreen)";
        private final String commentWindowSize  = "#define the window preferred size";
        private final String comentResolution   = "#define the window resolution (when fullscreen)";
        private final String comentVsync        = "#define vsync-mode (true or false)";
        private final String commentFrameCap    = "#frame-cap (0 = unlimited, 30, 60, 90, 120)";
        private final String [] param           = {"window-mode", "window-size-w", "window-size-h", "resolution-w", "resolution-h", "enable-vsync", "frame-cap"};

        //parâmetros do arquivo
        private String windowMode               = "windowed";
        private int windowSizeW                 = 0;
        private int windowSizeH                 = 0;
        private int resolutionW                 = 0;
        private int resolutionH                 = 0;
        private boolean enableVsync             = false;
        private int frameCap                    = 0;
        private int defaultResolutionW          = 0;
        private int defaultResolutionH          = 0;        

        //Verifica se o arquivo existe
        private boolean fileExists              = false;

        /* Lê o arquivo de configurações */
        public void verifyTheConfigurationFile() {
            
            //Indica o local do arquivo de configuração
            File configfile = new File("files\\config.ini");
            fileExists = configfile.exists();

            //Recupera a resolução padrão do monitor
            Dimension size = Toolkit.getDefaultToolkit(). getScreenSize();
            this.defaultResolutionW = (int)size.getWidth();
            this.defaultResolutionH = (int)size.getHeight();

            //Se existir, lê o arquivo
            if (fileExists) {
                Properties props = new Properties();
                try (FileInputStream fis = new FileInputStream(configfile)) {
                    props.load(fis);

                    // Leitura simplificada usando Properties
                    // O segundo parametro é o valor default caso a chave não exista ou falhe
                    this.windowMode = props.getProperty("window-mode", "windowed");
                    
                    this.windowSizeW = parseIntSafe(props.getProperty("window-size-w"), 800);
                    this.windowSizeH = parseIntSafe(props.getProperty("window-size-h"), 600);
                    
                    this.resolutionW = parseIntSafe(props.getProperty("resolution-w"), defaultResolutionW);
                    this.resolutionH = parseIntSafe(props.getProperty("resolution-h"), defaultResolutionH);
                    
                    this.enableVsync = Boolean.parseBoolean(props.getProperty("enable-vsync", "false"));
                    this.frameCap    = parseIntSafe(props.getProperty("frame-cap"), 60);

                } catch (Exception e) {
                    System.err.println("Erro ao abrir o arquivo...");
                }
            } else {
                this.createConfigFile();
            }
        }

        // Método auxiliar para evitar NumberFormatException quebrar a carga
        private int parseIntSafe(String value, int defaultValue) {
            try {
                return Integer.parseInt(value != null ? value.trim() : "");
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }

        /* Cria o arquivo de configuração */
        private void createConfigFile() {
            //Indica o local do arquivo de configuração
            File configfile = new File("files\\config.ini");
            try {
                configfile.createNewFile();    
            } catch (Exception e) {
                System.err.println("Impossível criar o arquivo de configuração...");
            }
            
            if (configfile.canWrite()) {
                String defaultFileConfig = "";
                BufferedWriter writer = null;
                
                try {
                    writer = new BufferedWriter(new FileWriter(configfile, true));
                    writer.append(this.commentWindowMode);
                    writer.append("\n");
                    writer.append(param[0] + ":windowed");
                    writer.append("\n\n");
                    writer.append(commentWindowSize);
                    writer.append("\n");
                    writer.append(param[1] + ":800");
                    writer.append("\n");
                    writer.append(param[2] + ":600");
                    writer.append("\n\n");
                    writer.append(comentResolution);
                    writer.append("\n");
                    writer.append(param[3] + ":" + defaultResolutionW);
                    writer.append("\n");
                    writer.append(param[4] + ":" + defaultResolutionH);
                    writer.append("\n\n");  
                    writer.append(comentVsync);
                    writer.append("\n");
                    writer.append(param[5] + ":false");
                    writer.append("\n\n");
                    writer.append(commentFrameCap);
                    writer.append("\n");
                    writer.append(param[6] + ":60");
                    writer.append("\n\n");

                    writer.append(defaultFileConfig);
                } catch (IOException e) {
                    System.err.println("Impossível preencher o arquivo de configuração...");
                } finally {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        System.err.println("Impossível fechar o BufferWriter...");
                        e.printStackTrace();
                    }
                }
            }
        }

        public String getWindowMode()   {   return windowMode;      }
        public int getWindowSizeW()     {   return windowSizeW;     }
        public int getWindowSizeH()     {   return windowSizeH;     }
        public int getResolutionW()     {   return resolutionW;     }
        public int getResolutionH()     {   return resolutionH;     }
        public boolean isEnableVsync()  {   return enableVsync;     }
        public int getFrameCap()        {   return frameCap;        }
    }
}