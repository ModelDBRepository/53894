/*
 * Kick.java
 *
 * Created on December 20, 2000, 11:34 AM
 */

package pharynx;
import java.util.*;

/**
 * A simulation event
 *
 * This should be called Event, except that word is used too much by the JFC.
 * Although a bare Kick is possible, most users will want to subclass it to
 * include information about how to respond to the kick.
 *
 * @author  leon@eatworms.swmed.edu
 * @version 0.1
 */
public class Kick extends Object {
    private double t;                   // time at which the Kick occurs
    private Kickable k;                 // who gets the kick

    /** Creates new Kick */
    public Kick() {
    }
    /** Creates new Kick 
     *
     * @param t     Time at which the kick occurs
     * @param k     Who gets the kick.
     */
    public Kick(double t, Kickable k) {
        this.t = t;
        this.k = k;
    }
    /** Get time at which kick occurs */
    public double time() { return t; }
    public void time(double t) { this.t = t; }

    /** Get the recipient of the kick */
    public Kickable kickee() { return k; }
}
