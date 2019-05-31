/*
 * FluidParticle.java
 *
 * Created on December 28, 2000, 7:33 AM
 */

package pharynx;


/**
 * Moves at the mean fluid velocity, can't be caught
 *
 * Diameter irrelevant except for display purposes
 *
 * @author  leon@eatworms.swmed.edu
 * @version 0.1
 */
public class FluidParticle extends Particle {
    public FluidParticle(Simulator sim, Pharynx p, double diameter) {
        super(sim, p, diameter);
    }

    public FluidParticle(Simulator sim, Pharynx p, double diameter, Section s,
                         double t, double x
    ) {
        this(sim, p, diameter);
        place(s, t, x);
    }

    public boolean caught() { return false; }

    public double where(double t1) {
        return s.whereMean(t, x, t1);
    }

    protected void setKicks() {
        clearKicks();
        // System.out.print("Setting kicks.  ");
        if (0 < s.flowP()) {
            if (
                (null != s.nextP()) &&
                (s.flowP() != s.nextP().flowA())
            ) {
                    throw new CantHappenException(
                        "Inconsistent flow in adjacent sections"
                    );
                    /*
                    System.out.println("Inconsistent flow in adjacent sections");
                    s.updateFlowA(t);
                    return;
                     */
            }
            double tExitP = s.whenMean(t, x, s.xP());
            if (isGood(tExitP)) {
                // System.out.print("Setting exit P kick at " + tExitP + ".  ");
                PKick k = new PKick(tExitP, this, EXITING_P);
                tickets[EXITING_P] = sim.add(k);
            }
        }
        if (0 > s.flowA()) {
            double tExitA = s.whenMean(t, x, s.xA());
            if (isGood(tExitA)) {
                // System.out.print("Setting exit A kick at " + tExitA + ".  ");
                PKick k = new PKick(tExitA, this, EXITING_A);
                tickets[EXITING_A] = sim.add(k);
            }
        }
        // checkKicks();
    }

    /**
     * receive a Kick from the Kick handler
     *
     * @param k     the Kick
     */
    public void kick(Kick k) {
        PKick fk = (PKick) k;
        // System.out.println("Caught kick type " + fk.type + " at time " +
        //    fk.time());
        tickets[fk.type] = null;
        switch(fk.type) {
        case EXITING_A:
            if (null == s.nextA()) {
                inPharynx = false;
                clearKicks();
                s.removeWatcher(this);
                return;
            }
            place(s.nextA(), fk.time(), s.nextA().xP());
            break;

        case EXITING_P:
            if (null == s.nextP()) {
                inPharynx = false;
                clearKicks();
                s.removeWatcher(this);
                return;
            }
            place(s.nextP(), fk.time(), s.nextP().xA());
            break;
            
        default:
            throw new CantHappenException("unknown PKick type");
        }
        t = fk.time();
        setKicks();
        // checkKicks();
    }
}
