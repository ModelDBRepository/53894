/*
 * Simulator.java
 *
 * Created on December 20, 2000, 1:36 PM
 */

package pharynx;
import java.util.*;

/** Simulator
 *
 * To run a simulation, 
 *
 * @author  leon@eatworms.swmed.edu
 * @version 0.1
 */
public class Simulator extends KickQueue {
    private double t;                      // current time
    private boolean halted = false;
    
    /** Creates new Simulator */
    public Simulator() {
    }
    
    /** Creates new Simulator and starts it running
     *
     * @param k     The initial Kick to load in the KickQueue
     */
    public Simulator(Kick k) {
        super(k);
        run();
    }
    
    /** Creates new Simulator and starts it running
     *
     * @param klist The initial list of Kicks to load in the  KickQueue
     */
    public Simulator(List klist) {
        super(klist);
        run();
    }
    
    /** Retrieve the current time */
    public double time() {
        return t;
    }
    /** halt the simulation
     *
     * This would typically be called by a kickee during the course of the
     * simulation.
     */
    public synchronized void halt() {
        halted = true;
    }
    /* halt or restart the simulation
     *
     * @param h     true to halt, false to clear a previous halt
     */
    public synchronized void halt(boolean h) {
        halted = h;
    }
    /* find out whether the simulation was halted */
    public synchronized boolean isHalted() {
        return halted;
    }
    /** Simulate up through the next Kick 
     *
     * @return true if something was done, false if the queue was empty or if
     *         the simulation was halted.
     */
    public boolean runOnce() {
        if (halted || isEmpty()) return false;
        Kick k = first();
        t = k.time();
        k.kickee().kick(k);
        return true;
    }
    /** Run until time t 
     *
     * @return true if we stopped because of the time constraint, false if the
     *         queue ran out or the simulation was halted before we passed
     *         time t.
     */
    public boolean run(double t) {
        while(runOnce()) {
            if (this.t > t) return true;
        }
        return false;
    }

    /** Run the Simulation until there are no Kicks left */
    public void run() {
        while(runOnce()) {}
    }
}
