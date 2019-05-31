/*
 * Watcher.java
 *
 * Created on December 21, 2000, 11:53 AM
 */

package pharynx;
import java.util.*;

/**
 * Be informed of a change in the watched object's state
 *
 * @author  leon@eatworms.swmed.edu
 * @version 0.1
 */
public interface Watcher {

    /**
     * Called just before a change takes place
     *
     * @param s     The Object whose state is changing
     * @param t     Time at which change will occur
     */
    public void imminent(Object o, double t);

    /**
     * Called just after a change takes place
     *
     * The two-argument form of consummated is called when a change occurs
     * during simulation (i.e., when the Object receives a Kick), and will
     * always be preceded by a call to imminent.
     *
     * @param s     The Object whose state changed
     * @param t     Time at which change occurred
     */
    public void consummated(Object o, double t);
    
    /**
     * Called just after a change takes place
     *
     * The one-argument form of consummated is called when some external call
     * changes the Object's state.
     *
     * @param s     The Object whose state changed
     */
    public void consummated(Object o);
    
}

