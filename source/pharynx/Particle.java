/*
 * Particle.java
 *
 * Created on December 23, 2000, 10:18 AM
 */

package pharynx;
import java.util.*;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.*;

/**
 *
 * @author  leon
 * @version
 */
public abstract class Particle extends java.lang.Object
implements Watcher, Kickable {
    protected Simulator sim = null;
    protected Pharynx p = null;
    protected Section s = null;
    protected double t = 0;
    protected double x = 0;
    protected double diameter;
    protected boolean caught;
    protected boolean running = false;
    protected boolean inPharynx = false;
    protected Shape shape = new Ellipse2D.Double(-5, -5, 10, 10);
    protected Color color = new Color(200, 200, 0);
    
    public Particle(Simulator sim, Pharynx p, double diameter) {
        this.sim = sim;
        this.p = p;
        this.diameter = diameter;
        tickets = new KickQueue.Ticket[KICK_TYPES];
        for(int i = 0; i < KICK_TYPES; i++) tickets[i] = null;
    }
    
    public Particle(Simulator sim, Pharynx p, double diameter, Section s,
                    double t, double x) {
        this(sim, p, diameter);
        place(s, t, x);
    }
    
    public void place(Section s, double t, double x) {
        if (!s.within(x)) {
            debugPrint("Position " + x + " is not within the Section");
            throw new IllegalArgumentException("Position " + x +
                " is not within the Section");
        }
        this.t = t;
        this.x = Math.max(s.xP(), Math.min(s.xA(), x));
        if (this.s != s) {
            if (null != this.s) this.s.removeWatcher(this);
            this.s = s;
            s.addWatcher(this);
        }
        inPharynx = true;
        caught = (s.currDiameter(t) < diameter);
    }
    
    public void place(double t, double x) {
        Section s = null;
        for(Iterator i = p.sections().iterator(); i.hasNext();) {
            Section ss = (Section) i.next();
            if (ss.within(x)) {
                s = ss;
                break;
            }
        }
        if (null == s)
            throw new IllegalArgumentException(x + " is not within the pharynx");
        place(s, t, x);
    }
    
    public void start() {
        running = true;
        setKicks();
        // checkKicks();
    }
    
    /**
     * Called just after a change takes place
     *
     * The one-argument form of consummated is called when some external call
     * changes the Object's state.
     *
     * @param o     The Object whose state changed
     */
    public void consummated(Object o) {
        // checkKicks();
        if (o != s)
            throw new CantHappenException("Message from wrong section");
        place(s, t, x);
    }
    
    /**
     * Called just before a change takes place
     *
     * @param o     The Object whose state is changing
     * @param t     Time at which change will occur
     */
    public void imminent(Object o, double t1) {
        // checkKicks();
        if (o != s)
            throw new CantHappenException("Message from wrong section");
        clearKicks();
        double x1 = x;
        if (!caught()) x1 = where(t1);
        double oldx = x;
        double oldt = t;
        // try {
        place(s, t1, x1);
        /*
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("oldx = " + oldx +
                                               ", oldt = " + oldt +
                                               ", t1 = " + t1 +
                                               ", x1 = " + x1 +
                                               ", caught = " + caught +
                                               ", section number = " + s.sectionNumber());
        }
         */
    }
    
    /**
     * Called just after a change takes place
     *
     * The two-argument form of consummated is called when a change occurs
     * during simulation (i.e., when the Object receives a Kick), and will
     * always be preceded by a call to imminent.
     *
     * @param o     The Object whose state changed
     * @param t     Time at which change occurred
     */
    public void consummated(Object o, double t) {
        if (o != s)
            throw new CantHappenException("Message from wrong section");
        if (t != this.t)
            throw new CantHappenException("Time changed unexpectedly");
        setKicks();
        // checkKicks();
    }
    
    protected KickQueue.Ticket[] tickets;
    protected static final int CATCHING = 0;
    protected static final int RELEASING= 1;
    protected static final int EXITING_A = 2;
    protected static final int EXITING_P = 3;
    protected static final int KICK_TYPES = 4;

    protected class PKick extends Kick {
        int type;

        PKick(double t, Kickable k, int type) {
            super(t, k);
            this.type = type;
        }
    }

    protected void clearKicks() {
        for(int i = 0; i < KICK_TYPES; i++) {
            if (null != tickets[i])
                sim.remove(tickets[i]);
            tickets[i] = null;
        }
    }

    protected void checkKickTime(double t, double kt) {
        if (kt <= t)
            System.out.println("Setting Kick at time " + kt +
                " before or at current time " + t);
        /*
            throw new CantHappenException("Setting Kick at time " + kt +
                " before or at current time " + t);
         */
    }

    protected boolean checkKicks() {
        for(int i = 0; i < tickets.length; i++) {
            if (null != tickets[i]) return true;
        }
        System.out.println("Particle: no Kicks set");
        return false;
    }

    public abstract double where(double t);
    protected abstract void setKicks();
    public abstract void kick(Kick k);
    
    public Section section() { return s; }
    public double diameter() { return diameter; }
    public boolean inPharynx() { return inPharynx; }
    public Shape shape() { return shape; }
    public void shape(Shape s) { shape = s; }
    public Color color() { return color; }
    public void color(Color c) { color = c; }
    public boolean caught() { return caught; }
    
    public void debugPrint(String s) {
        System.out.println(s);
    }

    protected boolean isGood(double t) {
        return !Double.isInfinite(t) && !Double.isNaN(t) && (t >= this.t);
    }
    
}
