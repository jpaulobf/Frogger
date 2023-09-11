package util;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;

/**
 * This class is responsible for load the game stuffs
 */
public class LoadingStuffs {
    
    //private instance of loader
    private static LoadingStuffs instance   = null;
    private int chargeStatus                = 0;

    //Stuffs Map
    private Map<String, BufferedImage> images   = new HashMap<>();
    private Map<String, Audio> musics           = new HashMap<>();
    private Map<String, Audio> sfxs             = new HashMap<>();

    /**
     * Constructor... load the game stuffs...
     */
    private LoadingStuffs() {
        //load the tiles and sprites
        try {
            BufferedImage image = null;
            
            image = ImageIO.read(new File("images\\animals2.png"));
            images.put("animalTiles", image);

            image = ImageIO.read(new File("images\\froggerdead.png"));
            images.put("froggerDeadTiles", image);

            image = ImageIO.read(new File("images\\gameover.png"));
            images.put("gameover", image);

            image = ImageIO.read(new File("images\\sidewalk.png"));
            images.put("sidewalk", image);

            image = ImageIO.read(new File("images\\splash.png"));
            images.put("splashImage", image);

            image = ImageIO.read(new File("images\\vehicules.png"));
            images.put("vehiclesTile", image);

            image = ImageIO.read(new File("images\\grass.png"));
            images.put("grass", image);

            image = ImageIO.read(new File("images\\subgrass.png"));
            images.put("subgrass", image);

            image = ImageIO.read(new File("images\\trunks.png"));
            images.put("trunksTiles", image);

            image = ImageIO.read(new File("images\\pixel.png"));
            images.put("pixel", image);

            image = ImageIO.read(new File("images\\turtles.png"));
            images.put("turtles", image);

            image = ImageIO.read(new File("images\\live.png"));
            images.put("live", image);

            image = ImageIO.read(new File("images\\clear.png"));
            images.put("stage-clear", image);

            image = ImageIO.read(new File("images\\time.png"));
            images.put("time-tile", image);

            image = ImageIO.read(new File("images\\stage1.png"));
            images.put("stage-1", image);

            image = ImageIO.read(new File("images\\go.png"));
            images.put("go!", image);

            image = ImageIO.read(new File("images\\mosquito.png"));
            images.put("mosquito", image);

            image = ImageIO.read(new File("images\\gatorhead.png"));
            images.put("gator-head", image);

            image = ImageIO.read(new File("images\\1up.png"));
            images.put("oneupTile", image);

            image = ImageIO.read(new File("images\\hiscore.png"));
            images.put("hiscoreTile", image);

            image = ImageIO.read(new File("images\\number0.png"));
            images.put("number-0", image);

            image = ImageIO.read(new File("images\\number1.png"));
            images.put("number-1", image);

            image = ImageIO.read(new File("images\\number2.png"));
            images.put("number-2", image);

            image = ImageIO.read(new File("images\\number3.png"));
            images.put("number-3", image);

            image = ImageIO.read(new File("images\\number4.png"));
            images.put("number-4", image);

            image = ImageIO.read(new File("images\\number5.png"));
            images.put("number-5", image);

            image = ImageIO.read(new File("images\\number6.png"));
            images.put("number-6", image);
            
            image = ImageIO.read(new File("images\\number7.png"));
            images.put("number-7", image);

            image = ImageIO.read(new File("images\\number8.png"));
            images.put("number-8", image);

            image = ImageIO.read(new File("images\\number9.png"));
            images.put("number-9", image);

            image = ImageIO.read(new File("images\\snakestiles.png"));
            images.put("snake-tile", image);

            image = ImageIO.read(new File("images\\gator.png"));
            images.put("gator", image);

            image = ImageIO.read(new File("images\\selector.png"));
            images.put("selector", image);

            image = ImageIO.read(new File("images\\logo.png"));
            images.put("logo", image);

            image = ImageIO.read(new File("images\\lb_playgame.png"));
            images.put("label-play-game", image);

            image = ImageIO.read(new File("images\\lb_options.png"));
            images.put("label-options", image);

            image = ImageIO.read(new File("images\\lb_exit.png"));
            images.put("label-exit", image);

            image = ImageIO.read(new File("images\\star_on.png"));
            images.put("star-on", image);

            image = ImageIO.read(new File("images\\star_off.png"));
            images.put("star-off", image);

            image = ImageIO.read(new File("images\\options_logo.png"));
            images.put("options-logo", image);

            image = ImageIO.read(new File("images\\label_game_music.png"));
            images.put("label-play-music", image);

            image = ImageIO.read(new File("images\\label_music_volume.png"));
            images.put("label-music-volume", image);

            image = ImageIO.read(new File("images\\label_game_sfx.png"));
            images.put("label-play-sfx", image);

            image = ImageIO.read(new File("images\\label_sfx_volume.png"));
            images.put("label-sfx-volume", image);

            image = ImageIO.read(new File("images\\label_how_many_lives.png"));
            images.put("label-how-many-lives", image);

            image = ImageIO.read(new File("images\\label_exit_options.png"));
            images.put("label-exit-options", image);

            image = ImageIO.read(new File("images\\slide_off_1.png"));
            images.put("slide-off-0", image);

            image = ImageIO.read(new File("images\\slide_off_2.png"));
            images.put("slide-off-1", image);

            image = ImageIO.read(new File("images\\slide_off_3.png"));
            images.put("slide-off-2", image);

            image = ImageIO.read(new File("images\\slide_off_4.png"));
            images.put("slide-off-3", image);

            image = ImageIO.read(new File("images\\slide_off_5.png"));
            images.put("slide-off-4", image);

            image = ImageIO.read(new File("images\\slide_off_6.png"));
            images.put("slide-off-5", image);

            image = ImageIO.read(new File("images\\slide_on_1.png"));
            images.put("slide-on-0", image);

            image = ImageIO.read(new File("images\\slide_on_2.png"));
            images.put("slide-on-1", image);

            image = ImageIO.read(new File("images\\slide_on_3.png"));
            images.put("slide-on-2", image);

            image = ImageIO.read(new File("images\\slide_on_4.png"));
            images.put("slide-on-3", image);

            image = ImageIO.read(new File("images\\slide_on_5.png"));
            images.put("slide-on-4", image);

            image = ImageIO.read(new File("images\\slide_on_6.png"));
            images.put("slide-on-5", image);

            image = ImageIO.read(new File("images\\toggle_on.png"));
            images.put("toggle-on", image);

            image = ImageIO.read(new File("images\\toggle_off.png"));
            images.put("toggle-off", image);

            image = ImageIO.read(new File("images\\live_1.png"));
            images.put("live-1", image);

            image = ImageIO.read(new File("images\\live_2.png"));
            images.put("live-2", image);

            image = ImageIO.read(new File("images\\live_3.png"));
            images.put("live-3", image);

            image = ImageIO.read(new File("images\\live_4.png"));
            images.put("live-4", image);

            image = ImageIO.read(new File("images\\live_5.png"));
            images.put("live-5", image);

            image = ImageIO.read(new File("images\\live_6.png"));
            images.put("live-6", image);

            image = ImageIO.read(new File("images\\live_7.png"));
            images.put("live-7", image);

            image = ImageIO.read(new File("images\\live_8.png"));
            images.put("live-8", image);

            image = ImageIO.read(new File("images\\live_9.png"));
            images.put("live-9", image);

            image = ImageIO.read(new File("images\\really.png"));
            images.put("really", image);

            image = ImageIO.read(new File("images\\yes.png"));
            images.put("lb-yes", image);

            image = ImageIO.read(new File("images\\no.png"));
            images.put("lb-no", image);

            image = ImageIO.read(new File("images\\ending.png"));
            images.put("ending", image);

            Logger.INFO("read all images...", this);

            Audio audio = new Audio("audio\\jump.wav", 0);
            if (audio != null && audio.isReady()) {
                sfxs.put("jumpAudio", audio);
            }

            audio = new Audio("audio\\plunk.wav", 0);
            if (audio != null && audio.isReady()) {
                sfxs.put("plunkAudio", audio);
            }

            audio = new Audio("audio\\squash.wav", 0);
            if (audio != null && audio.isReady()) {
                sfxs.put("squashAudio", audio);
            }

            audio = new Audio("audio\\theme2.wav", 0);
            if (audio != null && audio.isReady()) {
                musics.put("theme", audio);
            }

            audio = new Audio("audio\\docker.wav", 0);
            if (audio != null && audio.isReady()) {
                musics.put("docker", audio);
            }

            audio = new Audio("audio\\clear.wav", 0);
            if (audio != null && audio.isReady()) {
                musics.put("clearAudio", audio);
            }

            audio = new Audio("audio\\gameover.wav", 0);
            if (audio != null && audio.isReady()) {
                musics.put("gameover-theme", audio);
            }

            audio = new Audio("audio\\ending.wav", 0);
            if (audio != null && audio.isReady()) {
                musics.put("ending-theme", audio);
            }

            audio = new Audio("audio\\lasttime.wav", 0);
            if (audio != null && audio.isReady()) {
                sfxs.put("lasttime", audio);
            }

            audio = new Audio("audio\\beepstart.wav", 0);
            if (audio != null && audio.isReady()) {
                musics.put("beepstart", audio);
            }

            audio = new Audio("audio\\catch.wav", 0);
            if (audio != null && audio.isReady()) {
                sfxs.put("catchAudio", audio);
            }

            audio = new Audio("audio\\menu.wav", 0);
            if (audio != null && audio.isReady()) {
                musics.put("menu-music", audio);
            }

            audio = new Audio("audio\\menuitem.wav", 0);
            if (audio != null && audio.isReady()) {
                sfxs.put("menu-item", audio);
            }

            audio = new Audio("audio\\select.wav", 0);
            if (audio != null && audio.isReady()) {
                sfxs.put("menu-select", audio);
            }

            audio = new Audio("audio\\exiting.wav", 0);
            if (audio != null && audio.isReady()) {
                sfxs.put("exiting", audio);
            }

            audio = new Audio("audio\\opening.wav", 0);
            if (audio != null && audio.isReady()) {
                sfxs.put("opening", audio);
            }

            audio = new Audio("audio\\closing.wav", 0);
            if (audio != null && audio.isReady()) {
                sfxs.put("closing", audio);
            }

            Logger.INFO("read all audio...", this);
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a transluced volatile image
     * @param image
     * @return
     */
    protected VolatileImage createVImage(BufferedImage image) { 
        VolatileImage vImage = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleVolatileImage(image.getWidth(), image.getHeight(), Transparency.BITMASK);
        Graphics2D bgd2 = (Graphics2D)vImage.getGraphics();
        bgd2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OUT));
        bgd2.setColor(new java.awt.Color(255,255,255,0));
        bgd2.fillRect(0, 0, image.getWidth(), image.getHeight());
        bgd2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        bgd2.drawImage(image, 0, 0, vImage.getWidth(), vImage.getHeight(), //dest w1, h1, w2, h2
                              0, 0, image.getWidth(), image.getHeight(), //source w1, h1, w2, h2
                              null);
        return (vImage);
    }

    public BufferedImage getImage(String objectName) {
        return (this.images.get(objectName));
    }

    public Audio getAudio(String objectName) {
        if (this.musics.get(objectName) != null) {
            return (this.musics.get(objectName));
        } else {
            return (this.sfxs.get(objectName));
        }
    }

    public Collection<Audio> getSFXList() {
        return (this.sfxs.values());
    }

    public Collection<Audio> getMusicList() {
        return (this.musics.values());
    }

    /**
     * Recover the singleton instance  
     * @return
     */
    public static LoadingStuffs getInstance() {
        if (instance == null) {
            instance = new LoadingStuffs();
        }
        return instance;
    }

    /**
     * Returns the charge counter status (0 ... 100%)
     * @return
     */
    public int getChargeStatus() {
        return (this.chargeStatus);
    }
}