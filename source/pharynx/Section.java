/*
 * Section.java
 *
 * Created on December 20, 2000, 2:43 PM
 */

package pharynx;
import java.util.*;

/** A section of the pharynx
 *
 * Section represents a finite thickness cross section of the pharynx.
 *
 * @author  leon@eatworms.swmed.edu
 * @version 0.1
 */
public class Section extends Object implements Kickable {
    private double thickness = 1.0;
    private double diameter = 0.0;      // diameter of a circle inscribed in
                                        //   maxmimally open state
    private double maxArea = 0.0;       // C-setional area when fully open
    private double maxVolume = 0.0;     // volume of fully open section
    private MotionList motion;          // a List of MotionPoints
    private int whichMotion = 0;        // index to current MotionPoint
    private double basetime = 0.0;
    private KickQueue kq = null;
    private Pharynx pharynx;            // the pharynx of which we are part
    private int sectionNumber = 0;      // where we are in it
    private Section nextA = null;       // next anterior Section (null if none)
    private Section nextP = null;       // next posterior Section
    private double xA;                  // position of anterior end
    private double xP = 0.0;            // position of posterior end
    private Set watchers;               // who to tell when our state changes
    private List tickets = new ArrayList(); // List of pending Kicks
    private double dOpening;            // rate of change of opening
    // flowA = nextP.flowA + dVolume
    private double dVolume;             // rate of change of volume
    private double flowA;               // flow into section at ant boundary
    private double flowP;               // flow out to section at post boundary

    /** Creates new Section */
    public Section(Pharynx pharynx, int sectionNumber) {
        watchers = new HashSet();
        this.pharynx = pharynx;
        this.sectionNumber = sectionNumber;
    }
    
    public Section(
        double thickness,
        double diameter,
        MotionList motion,
        Pharynx pharynx,
        int sectionNumber
    ) {
        this(pharynx, sectionNumber);
        this.thickness = thickness;
        this.diameter = diameter;
        this.motion = motion;
        computeX();
        computeVolume();
    }

    /**
     * Test whether a position is within the section
     *
     * @param x     The position to check
     * @return      true if x is within the section
     *              (within a small error tolerance)
     */
    public boolean within(double x) {
        return ((x >= xP - POSITION_TOLERANCE) &&
                (x <= xA + POSITION_TOLERANCE));
    }

    /**
     * Particle motion functions
     *
     * These six functions track the motion of a particle in the pharyngeal
     * lumen. A particle at location x0 at time t0 will move to location x1 at
     * time t1.  The where functions take t0, x0, and t1 as arguments and
     * return x1.  The when functions take t0, x0, and x1 as arguments and
     * return t1.
     *
     * The three pairs of motion functions differ in how the particle moves
     * relative to the fluid.  The Mean functions track the motion of an
     * average fluid molecule.  The Accelerated functions assume the particle
     * moves faster than the average fluid molecule by a fixed factor provided
     * as an argument.  The Center functions track motion at the center of the
     * lumen.
     *
     * These functions are valid only if the entire motion is within this
     * Section, and if the time interval is within the current motion interval
     * of the section.  They throw an illegal argument exception if an argument
     * falls outside these bounds.  They may however return results that are
     * outside bounds, as this could be useful information.
     *
     * The formulas come straight from Mathematica.  I'm hoping the java
     * compiler does CSE optimization.
     */

    /**
     * Compute mean fluid position at future time
     *
     * @param t0    initial time
     * @param x0    initial position
     * @param t1    future time
     * @return      position at time t1
     */
    public double whereMean(double t0, double x0, double t1) {
        checkTime(t0); checkX(x0); checkTime(t1);
        double o0 = currOpening(t0);
        return _whereMean(x0, t1 - t0, o0);
    }

    private double _whereMean(double x0, double t1, double o0) {
        double num = -(flowP*t1) + maxArea*o0*x0 + dOpening*maxArea*t1*xP;
        if (0 == num) return x0;
        double den = maxArea*o0 + dOpening*maxArea*t1;
        double x1 = num / den;
        /*
        if (Double.isInfinite(x1)) {
            System.out.println("Infinite Result");
        }
         */
        return x1;
    }

    /**
     * Compute time at which mean fluid reaches position
     *
     * @param t0    initial time
     * @param x0    initial position
     * @param x1    future position
     * @return      time at which position x1 reached
     */
    public double whenMean(double t0, double x0, double x1) {
        checkTime(t0); checkX(x0); checkX(x1);
        if (teleport(x0, x1)) return t0;
        if (whenCheck(x0, x1)) return Double.POSITIVE_INFINITY;
        double o0 = currOpening(t0);
        return t0 + _whenMean(x0, x1, o0);
    }

    private double _whenMean(double x0, double x1, double o0) {
        double t1 =
            (maxArea*o0*(x0 - x1))/
               (flowP + dOpening*maxArea*(x1 - xP));
        return t1;
    }

    /**
     * Compute accelerated fluid position at future time
     *
     * @param f     the acceleration factor
     * @param t0    initial time
     * @param x0    initial position
     * @param t1    future time
     * @return      position at time t1
     */
    public double whereAccelerated(double f, double t0, double x0, double t1) {
        checkTime(t0); checkX(x0); checkTime(t1);
        double o0 = currOpening(t0);
        return _whereAccelerated(f, x0, t1 - t0, o0);
    }
    
    private double _whereAccelerated(double f, double x0, double t1, double o0) {
        if (0.0 == dOpening)
            return x0 - f * flowP*t1/(maxArea*o0);
        double x1 =
            (-(flowP*Power(o0 + dOpening*t1,f)) + 
                 Power(o0,f)*(flowP + dOpening*maxArea*(x0 - xP)) + 
                 dOpening*maxArea*Power(o0 + dOpening*t1,f)*xP)/
               (dOpening*maxArea*Power(o0 + dOpening*t1,f));
        return x1;
    }
    
    /**
     * Compute time at which accelerated fluid reaches position
     *
     * @param f     the acceleration factor
     * @param t0    initial time
     * @param x0    initial position
     * @param x1    future position
     * @return      time at which position x1 reached
     */
    public double whenAccelerated(double f, double t0, double x0, double x1) {
        checkTime(t0); checkX(x0); checkX(x1);
        if (teleport(x0, x1)) return t0;
        if (whenCheck(x0, x1)) return Double.POSITIVE_INFINITY;
        double o0 = currOpening(t0);
        return t0 + _whenAccelerated(f, x0, x1, o0);
    }

    private double _whenAccelerated(double f, double x0, double x1, double o0) {
        if (0.0 == dOpening)
            return _whenMean(x0, x1, o0) / f;
        double t1 =
            (-o0 + Power((Power(o0,f)*
                     (flowP + dOpening*maxArea*(x0 - xP)))/
                   (flowP + dOpening*maxArea*(x1 - xP)),1/f))/
               dOpening;
        return t1;
    }

    /**
     * Compute center fluid position at future time
     *
     * @param t0    initial time
     * @param x0    initial position
     * @param t1    future time
     * @return      position at time t1
     */
    public double whereCenter(double t0, double x0, double t1) {
        checkTime(t0); checkX(x0); checkTime(t1);
        double o0 = currOpening(t0);
        return _whereCenter(x0, t1 - t0, o0);
    }
    
    private double _whereCenter(double x0, double t1, double o0) {
        if (0.0 == dOpening) {
            double f = velocityRatio(o0);
            return _whereAccelerated(f, x0, t1, o0);
        }
        double c0 = VELOCITY_RATIO_COEFFICIENTS[0];
        double c1 = VELOCITY_RATIO_COEFFICIENTS[1];
        double c2 = VELOCITY_RATIO_COEFFICIENTS[2];
        double cse1 = (2*c1 + 2*c2*o0 + c2*dOpening*t1);
        double cse2 = (double) Math.exp((dOpening*t1*cse1)/2.);
        double x1 =
            (flowP*(Power(o0,c0) - cse2*
                     Power(o0 + dOpening*t1,c0)) + 
                 dOpening*maxArea*(Power(o0,c0)*(x0 - xP) + 
                    cse2*
                     Power(o0 + dOpening*t1,c0)*xP))/
               (dOpening*cse2*maxArea*
                 Power(o0 + dOpening*t1,c0));
        /*
        System.out.println("Section._whereCenter: x0 = " + x0 + ", t1 = " + t1 +
            ", o0 = " + o0 + ", cse1 = " + cse1 + ", cse2 = " + cse2 +
            ", x1 = " + x1);
         */
        if (Double.isInfinite(x1)) {
            System.out.println("Infinite Result");
        }
        return x1;
    }
    
    /**
     * Compute time at which center fluid reaches position
     *
     * @param t0    initial time
     * @param x0    initial position
     * @param x1    future position
     * @return      time at which position x1 reached
     */
    /*
     * Mathematica can't solve this one, so we solve it iteratively.
     * Calculate the center acceleration at the current opening, then use
     * whenAccelerated to figure out when it would arrive at the target if
     * that acceleration remained constant.  Then use whereCenter to figure
     * out where it actually is at that time.  Use this position and time as
     * the new (t0, x0) pair, presumably closer now to x1, and do it again
     * until the answer is close enough.
     */
    private static final int WHEN_CENTER_MAX_ITERATIONS = 10;
    private static int WHEN_CENTER_CALLS = 0;
    private static int WHEN_CENTER_ITERATIONS = 0;
    public double whenCenter(double t0, double x0, double x1) {
        checkTime(t0); checkX(x0); checkX(x1);
        if (teleport(x0, x1)) return t0;
        if (whenCheck(x0, x1)) return Double.POSITIVE_INFINITY;
        if (0.0 == dOpening) {
            // special case: treat like accelerated
            double o0 = _currOpening(t0);
            double f = velocityRatio(o0);
            return t0 + _whenAccelerated(f, x0, x1, o0);
        }
        double t00 = t0, x00 = x0;
        double t1 = 0.0;
        int i;
        for(i = 0; i < WHEN_CENTER_MAX_ITERATIONS; i++) {
            double o0 = _currOpening(t0);
            double f = velocityRatio(o0);
            t1 = _whenAccelerated(f, x0, x1, o0);
            t0 += t1;
            double truex1 = _whereCenter(x0, t1, o0);
            if (Math.abs(x1 - truex1) <= POSITION_TOLERANCE)
                break;
            x0 = truex1;
        }
        if (i >= WHEN_CENTER_MAX_ITERATIONS)
            throw new FailedToConvergeException("Section.whenCenter");
        WHEN_CENTER_CALLS++;            // keep track of how this works
        WHEN_CENTER_ITERATIONS += i + 1; // (just for the Hell of it)
        return t0;
    }
    
    /*
     * teleport returns true if the section is fully closed and not moving.
     * In this case a particle of any type moves with infinite speed, so
     * all the when methods should say that it arrives instantly at any
     * destination (hence the name).
     */
    private boolean teleport(double x0, double x1) {
        boolean stuck = 
            (motion.getmp(whichMotion).r() == 0.0) &&
            (motion.getmp(whichMotion + 1).r() == 0.0);
        if (!stuck) return false;
        return
            ((x1 <= x0) && (flowA > 0)) ||
            ((x1 >= x0) && (flowA < 0));
    }

    /*
     * whenCheck does a basic test to see whether a particle at x0 can ever
     * reach x1 under the current flow conditions.  These tests are
     * independent of the whether the particle is mean, accelerated, or
     * centered.  First, it tests whether flow at x0 is away from x1.  Then
     * it tests whether there is a surface of zero flow between x0 and x1.
     * If either of these conditions holds a particle at x0 can never reach
     * x1, and whenCheck returns true.
     */
    private boolean whenCheck(double x0, double x1) {
        double flow0 = flowA - dVolume * (xA - x0) / thickness;
        if (((flow0 >= 0) && (x1 > x0)) || ((flow0 <= 0) && (x1 < x0)))
            return true;
        double flow1 = flowA - dVolume * (xA - x1) / thickness;
        if (((flow0 <= 0) && (flow1 >= 0)) || ((flow0 >= 0) && (flow1 <= 0)))
            return true;
        return false;
    }

    /**
     * Get current diameter of this section
     *
     * The current diameter is defined as the maximum diameter of a circle
     * inscribed in the lumen.  If the current time isn't within the bounds
     * implied by the whichMotion field, throws an IllegalArgumentException.
     *
     * @param t     Current time
     */
    public double currDiameter(double t) {
        return currOpening(t) * diameter;
    }
    
    /**
     * Get current opening of this section
     *
     * The current opening varies from 0 for fully closed to 1 for fully open.
     * If the current time isn't within the bounds implied by the whichMotion
     * field, throws an IllegalArgumentException.
     *
     * @param t     Current time
     */
    public double currOpening(double t) {
        checkTime(t);
        return _currOpening(t);
    }

    public double _currOpening(double t) {
        double o = motion.getmp(whichMotion).r();
        double t0 = motion.getmp(whichMotion).t();
        o += dOpening * (t - t0 - basetime);
        return o;
    }

    /**
     * Find out when a particle will be caught
     *
     * @param d     The diameter of the particle
     * @return      The time at which the particle will be caught, or
     *              POSITIVE_INFINITY if the section is opening or will not
     *              be caught in the current motion interval
     */
    public double whenCaught(double d) {
        if (dOpening >= 0.0)
            return Double.POSITIVE_INFINITY;
        double o = d / diameter;
        double t0 = motion.getmp(whichMotion).t();
        double o0 = motion.getmp(whichMotion).r();
        double o1 = motion.getmp(whichMotion + 1).r();
        if ((o1 > o) || (o0 < o))
            return Double.POSITIVE_INFINITY;
        return t0 + basetime + (o - o0) / dOpening;
    }

    /**
     * Find out when a particle will be released
     *
     * @param d     The diameter of the particle
     * @return      The time at which the particle will be released, or
     *              POSITIVE_INFINITY if the section is closing or will not
     *              be released in the current motion interval
     */
    public double whenReleased(double d) {
        if (dOpening <= 0.0)
            return Double.POSITIVE_INFINITY;
        double o = d / diameter;
        double t0 = motion.getmp(whichMotion).t();
        double o0 = motion.getmp(whichMotion).r();
        double o1 = motion.getmp(whichMotion + 1).r();
        if ((o1 < o) || (o0 > o))
            return Double.POSITIVE_INFINITY;
        return t0 + basetime + (o - o0) / dOpening;
    }

    private void checkTime(double t) {
        if (
            (basetime + motion.getmp(whichMotion).t() > t) ||
            (basetime + motion.getmp(whichMotion + 1).t() < t)
        )
            throw new IllegalArgumentException(
                "Time " + t + " out of bounds [" +
                motion.getmp(whichMotion).t() +
                ", " + motion.getmp(whichMotion + 1).t() + "]"
            );
    }

    private void checkX(double x) {
        if ((xP > x) || (xA < x))
            throw new IllegalArgumentException(
                "Position " + x + " out of bounds [" + xP + ", " + xA + "]"
            );
    }

    private boolean computingX = false;
    private synchronized void computeX() {
        if (computingX)
            throw new CantHappenException("Cyclically linked Sections");
        computingX = true;
        xP = (null == nextP) ? 0.0 : nextP.xA();
        xA = xP + thickness;
        // Following could lead to infinite recursion if someone screws up and
        // links Sections cyclically
        if (null != nextA) nextA.computeX();
        computingX = false;
    }

    private void computeVolume() {
        maxArea = (double) (0.75 * SQRT3 * diameter * diameter);
        maxVolume = maxArea * thickness;
        computeFlows();
    }

    private void computeFlows() {
        if (
            (null != motion) &&
            (whichMotion >= 0) &&
            (whichMotion < motion.size() - 1)
        ) {
            MotionPoint s = motion.getmp(whichMotion);
            MotionPoint e = motion.getmp(whichMotion + 1);
            dOpening = (e.r() - s.r()) / (e.t() - s.t());
            dVolume = dOpening * maxVolume;
            updateFlowA();
        }
    }

    private void computeFlows(double t) {
        if (
            (null != motion) &&
            (whichMotion >= 0) &&
            (whichMotion < motion.size() - 1)
        ) {
            MotionPoint s = motion.getmp(whichMotion);
            MotionPoint e = motion.getmp(whichMotion + 1);
            dOpening = (e.r() - s.r()) / (e.t() - s.t());
            dVolume = dOpening * maxVolume;
            _updateFlowA(t);
        }
    }

    /** update flowA */
    private void updateFlowA() {
        flowP = (null == nextP) ? 0.0 : nextP.flowA();
        flowA = flowP + dVolume;
        if (null != nextA) pharynx.requestUpdate(sectionNumber);
        tellWatchers();
    }

    /*
     * This private internal version of updateFlowA does the update, but
     * doesn't tell the watchers.  This is so that if the update is a
     * consequence of motion or geometry changes the watchers can be warned
     * by the caller before those changes take place.
     */
    private void _updateFlowA(double t) {
        flowP = (null == nextP) ? 0.0 : nextP.flowA();
        flowA = flowP + dVolume;
        if (null != nextA) pharynx.requestUpdate(sectionNumber, t);
    }

    /** update flowA
     *
     * @param t     Time after which update applies
     */
    protected void updateFlowA(double t) {
        warnWatchers(t);
        _updateFlowA(t);
        tellWatchers(t);
    }

    /**
     * Add a watcher
     *
     * @param w     The watcher
     */
    public void addWatcher(Watcher w) {
        watchers.add(w);
    }

    /**
     * Remove a watcher
     *
     * @param w     The watcher
     */
    public void removeWatcher(Watcher w) {
        watchers.remove(w);
    }

    /** Inform watchers of changed flow state */
    private void tellWatchers() {
        for(Iterator i = watchers.iterator(); i.hasNext();) {
            ((Watcher) i.next()).consummated(this);
        }
    }

    /** Warn watchers that flow state about to change
     *
     * @param t     current time
     */
    private void warnWatchers(double t) {
        for(Iterator i = watchers.iterator(); i.hasNext();) {
            ((Watcher) i.next()).imminent(this, t);
        }
    }

    /** Inform watchers of changed flow state
     *
     * @param t     current time
     */
    private void tellWatchers(double t) {
        for(Iterator i = watchers.iterator(); i.hasNext();) {
            ((Watcher) i.next()).consummated(this, t);
        }
    }

    /** Internal Kick with extra info */
    class SKick extends Kick {
        private int type;
        private int newWhichMotion;
        
        public SKick(double t, Kickable k, int type) {
            super(t, k);
            this.type = type;
        }

        int type() { return type; }
    }

    /** Start this section moving for simulation
     *
     * Queue kicks so that this section will become active when Simulator
     * starts.
     *
     * @param kq    The KickQueue used by the simulator
     */
    public void start(KickQueue kq, double basetime) {
        for(Iterator i = tickets.iterator(); i.hasNext();) {
            this.kq.remove((KickQueue.Ticket) i.next());
        }
        tickets.clear();
        this.kq = kq;
        this.basetime = basetime;
        whichMotion = 0;
        for(int i = 0; i < motion.size() - 1; i++) {
            double t = motion.getmp(i).t() ;
            SKick k = new SKick(t + basetime, this, MOTION_CHANGE);
            k.newWhichMotion = i;
            tickets.add(kq.add(k));
        }
    }

    /**
     * receive a Kick from the Kick handler
     *
     * @param k     the Kick
     */
    public void kick(Kick k) {
        SKick sk = (SKick) k;
        switch(sk.type) {
        case MOTION_CHANGE:
            warnWatchers(sk.time());
            whichMotion = sk.newWhichMotion;
            computeFlows(sk.time());
            tellWatchers(sk.time());
            break;

        default:
            throw new CantHappenException("Section: Unknown Kick type");
        }
    }
    
    /** find ratio of center velocity to mean velocity
     *
     * This function uses a quadratic approximation.
     *
     * @param o     ranges from 0 for closed to 1 for fully open
     * @return      The ratio of the flow velocity at the center to the mean
     *              flow velocity.
     */
    public static double velocityRatio(double o) {
        return
            VELOCITY_RATIO_COEFFICIENTS[0] +
            o * (VELOCITY_RATIO_COEFFICIENTS[1] +
                 o * VELOCITY_RATIO_COEFFICIENTS[2]);
    }
    private static final double[] VELOCITY_RATIO_COEFFICIENTS = {
        3.558738728F, -2.366025717F, 1.00969086F
    };

    public double thickness() { return thickness; }
    void thickness(double t) { thickness = t; computeX(); computeVolume(); }
    public double diameter() { return diameter; }
    void diameter(double d) { diameter = d; computeVolume(); }
    public MotionList motion() { return motion; }
    void motion(MotionList m) { motion = m; computeFlows(); }
    public Pharynx pharynx() { return pharynx; }
    void pharynx(Pharynx p) { pharynx = p; }
    public int sectionNumber() { return sectionNumber; }
    void sectionNumber(int s) { sectionNumber = s; }
    public Section nextA() { return nextA; }
    void nextA(Section s) { nextA = s; updateFlowA(); }
    public Section nextP() { return nextP; }
    void nextP(Section s) { nextP = s; computeX(); updateFlowA(); }
    public double flowA() { return flowA; }
    public double flowP() { return flowP; }
    public double dVolume() { return dVolume; }
    public double xA() { return xA; }
    public double xP() { return xP; }

    private static final double SQRT3 = Math.sqrt(3.0);
    
    /*
     * This is just a convenience function that makes the Mathematica
     * notation Power(a, b) work.  Also takes care of type conversions.
     */
    private static final double Power(double a, double b) {
        return (double) Math.pow(a, b);
    }

    private static final double POSITION_TOLERANCE = 0.00001;

    /* Kick types */
    static final int MOTION_CHANGE = 0;
}
