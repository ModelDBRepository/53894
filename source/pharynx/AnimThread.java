/*
 * AnimThread.java
 *
 * Created on December 22, 2000, 11:38 AM
 */

package pharynx;
import java.util.*;
import javax.swing.*;

/**
 *
 * @author  leon
 * @version 
 */
public class AnimThread extends java.lang.Thread {
    private static final int SLEEP_TIME = 10;

    private Cubbyhole c;
    private PharynxAnimPanel panel;
    private SimOptions options;
    private int dt;                     // time between frames in ms

    /** Creates new AnimThread */
    public AnimThread(Cubbyhole c, PharynxAnimPanel panel,
                      SimOptions options) {
        this.c = c;
        this.panel = panel;
        this.options = options;
        this.dt = options.frame;
    }
    
    public void run() {
        this.currentThread().setName("Animation");
        TimerTask displayPharynx = new TimerTask() {
            public void run() {
                PictureData pd = (PictureData) c.get();
                if (!c.isRunning())
                    cancel();
                else {
                    panel.display(options, pd);
                    try {               // give other threads a chance
                        sleep(SLEEP_TIME);
                    } catch(InterruptedException e) {}
                }
            }
        };
        java.util.Timer timer = new java.util.Timer();
        timer.schedule(displayPharynx, 0, dt);
    }
    
}
