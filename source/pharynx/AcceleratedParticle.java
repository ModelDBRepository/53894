/*
 * AcceleratedParticle.java
 *
 * Created on December 28, 2000, 2:30 PM
 */

package pharynx;

/**
 * Moves at the center fluid velocity, is caught when lumen closes
 *
 * @author  leon@eatworms.swmed.edu
 * @version 0.1
 */
public class AcceleratedParticle extends Particle {
    private double factor;                  // acceleration factor

    public AcceleratedParticle(Simulator sim, Pharynx p, double diameter,
                               double factor) {
        super(sim, p, diameter);
        this.factor = factor;
    }

    public AcceleratedParticle(Simulator sim, Pharynx p, double diameter,
                               double factor, Section s, double t, double x) {
        this(sim, p, diameter, factor);
        place(s, t, x);
    }

    public double where(double t1) {
        if (caught)
            return(x);
        else
            return s.whereAccelerated(factor, t, x, t1);
    }

    protected void setKicks() {
        clearKicks();
        // System.out.print("Setting kicks.  ");
        if (caught) {
            double tRelease = s.whenReleased(diameter);
            if (isGood(tRelease)) {
                // System.out.print("Setting release kick at " + tRelease + ".  ");
                PKick k = new PKick(tRelease, this, RELEASING);
                tickets[RELEASING] = sim.add(k);
            }
        }
        else {
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
                double tExitP = s.whenAccelerated(factor, t, x, s.xP());
                if (isGood(tExitP)) {
                    // System.out.print("Setting exit P kick at " + tExitP + ".  ");
                    PKick k = new PKick(tExitP, this, EXITING_P);
                    tickets[EXITING_P] = sim.add(k);
                }
            }
            if (0 > s.flowA()) {
                double tExitA = s.whenAccelerated(factor, t, x, s.xA());
                if (isGood(tExitA)) {
                    // System.out.print("Setting exit A kick at " + tExitA + ".  ");
                    PKick k = new PKick(tExitA, this, EXITING_A);
                    tickets[EXITING_A] = sim.add(k);
                }
            }
            double tCaught = s.whenCaught(diameter);
            if (isGood(tCaught)) {
                // System.out.print("Setting caught kick at " + tCaught + ".  ");
                PKick k = new PKick(tCaught, this, CATCHING);
                tickets[CATCHING] = sim.add(k);
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
        PKick bk = (PKick) k;
        // System.out.println("Caught kick type " + bk.type + " at time " +
        //    bk.time());
        tickets[bk.type] = null;
        switch(bk.type) {
        case RELEASING:
            caught = false;
            break;
            
        case CATCHING:
            caught = true;
            double x1 = s.whereAccelerated(factor, t, x, bk.time());
            if (!s.within(x1))
                throw new CantHappenException("AcceleratedParticle caught at " + x1 +
                    ", out of section " + s.sectionNumber() + " bounds [" +
                    s.xP() + ", " + s.xA() + "]. old t = " + t +
                    ", old x = " + x + ", bk.time() = " + bk.time());
            x = x1; t = bk.time();
            break;
            
        case EXITING_A:
            if (null == s.nextA()) {
                inPharynx = false;
                clearKicks();
                s.removeWatcher(this);
                return;
            }
            place(s.nextA(), bk.time(), s.nextA().xP());
            break;
            
        case EXITING_P:
            if (null == s.nextP()) {
                inPharynx = false;
                clearKicks();
                s.removeWatcher(this);
                return;
            }
            place(s.nextP(), bk.time(), s.nextP().xA());
            break;
            
        default:
            throw new CantHappenException("unknown PKick type");
        }
        t = bk.time();
        setKicks();
        // checkKicks();
    }
}
