/*
 * Pharynx.java
 *
 * Created on December 21, 2000, 8:06 AM
 */

package pharynx;
import java.util.*;

/**
 *
 * @author  leon@eatworms.swmed.edu
 * @version 0.1
 */
public class Pharynx extends java.lang.Object implements Kickable {
    private ArrayList sections;
    private int length;
    private int width;
    private boolean repeat = false;
    private double basetime = 0.0;
    private double duration = 0.0;
    private Simulator sim = null;

    /*
     * If firstUpdate >= 0, an update is pending on sections starting with
     * firstUpdate.
     */
    private int firstUpdate = -1;

    /** Creates new Pharynx */
    public Pharynx(List m) {
        this(m, new Simulator());
    }
    
    public Pharynx(List m, Simulator sim) {
        this(m, sim, false);
    }

    public Pharynx(List m, boolean repeat) {
        this(m, new Simulator(), repeat);
    }

    public Pharynx(List m, Simulator sim, boolean repeat) {
        this.sim = sim;
        this.repeat = repeat;
        LumenDims lds = new LumenDims();
        double[] diams = lds.diameter();
        length = diams.length;
        width = 0;
        // List m = lds.motion();
        sections = new ArrayList();
        int sn = 0;
        Section last = null;
        for(int i = 0; i < length; i++) {
            if (diams[i] > width)
                width = (int) Math.ceil(diams[i]);
            MotionList ml = (MotionList) m.get(i);
            Section s = new Section(1, diams[i], ml, this, sn);
            if (
                (null != last) &&
                (last.diameter() == s.diameter()) &&
                last.motion().equals(s.motion())
            ) {
                // merge identical sections (makes simulation faster)
                last.thickness(last.thickness() + s.thickness());
            }
            else {
                sections.add(s);
                last = s;
                sn++;
                double t = ml.getmp(ml.size() - 1).t();
                if (duration < t) duration = t;
            }
        }
        sections.trimToSize();
        for(int i = 1; i < sections.size(); i++) {
            Section s = (Section) sections.get(i);
            MotionList ml = s.motion();
            double t = ml.getmp(ml.size() - 1).t();
            if (t < duration)
                ml.add(new MotionPoint(duration, ml.getmp(ml.size() - 1).r()));
        }
        Section curr = null;
        Section next = (Section) sections.get(0);
        next.nextP(null);
        for(int i = 1; i < sections.size(); i++) {
            curr = next;
            next = (Section) sections.get(i);
            curr.nextA(next);
            next.nextP(curr);
        }
        next.nextA(null);
        firstUpdate = 0;
        doUpdate(0.0F);
    }
    
    /** Run the simulation */
    public void simulate() {
        for(int i = sections.size() - 1; i >= 0; i--) {
            Section s = (Section) sections.get(i);
            s.start(sim, basetime);
        }
        if (repeat) sim.add(new Kick(duration, this));
        sim.run();
    }

    public void halt() {
        sim.halt();
    }
    
    public void kick(Kick k) {
        if (!repeat) return;
        basetime = k.time();
        k.time(basetime + duration);
        sim.add(k);
        for(int i = sections.size() - 1; i >= 0; i--) {
            Section s = (Section) sections.get(i);
            s.start(sim, basetime);
        }
    }

    /**
     * Request an update of sections anterior to a certain point
     *
     * Something has occurred to change the flow through the anterior boundary
     * of section s. requestUpdate is called (typically by the Section) to
     * tell its containing Pharynx that the anterior Section needs to update
     * its flow (which will have the same effect on the next anterior section,
     * etc). requestUpdate doesn't actually cause the updates to be performed.
     * The Pharynx saves them until things have settled -- this is more
     * efficient.
     *
     * @param s     The section whose flowA has changed
     */
    protected void requestUpdate(int s) {
        if ((firstUpdate < 0) || (s < firstUpdate)) firstUpdate = s;
    }

    /**
     * Request an update of sections anterior to a certain point after Time t
     *
     * In this version of requestUpdate the time at which the flow change
     * occurred is specified. Pharynx waits until all Kicks at that time have
     * completed before carrying out the update.
     *
     * This optimization has been turned off: turned out to be far too much
     * trouble to make sure updates were always completed before someone else
     * needed them to be, and doesn't gain that much time to boot.
     *
     * @param s     The section whose flowA has changed
     * @param t     Time after which update required
     */
    private boolean updating = false;
    protected /* synchronized */  void requestUpdate(int s, double t) {
        requestUpdate(s);
        if (updating) return;
        /*
        if (
            (null != sim) &&                // no simulation in progress...
            !sim.isEmpty()
        ) {
            Kick k = (Kick) sim.peek();
            if (                            // if next kickee a Section...
                (k.kickee() instanceof Section) &&
                (k instanceof Section.SKick)
            ) {
                Section.SKick sk = (Section.SKick) k;
                if (                        // and it's a motion change...
                    (sk.type() == Section.MOTION_CHANGE) &&
                    (sk.time() <= t)        // at same time as this one...
                ) return;                   // postpone update
            }
        }
         */
        updating = true;                    // all tests passed: do it
        doUpdate(t);
        updating = false;
    }

    /** Carry out pending Section flow updates */
    protected /* synchronized */ void doUpdate(double t) {
        while(firstUpdate >= 0) {
            int sn = firstUpdate;
            firstUpdate = -1;
            if (sn >= sections.size()) break;
            ((Section) sections.get(sn+1)).updateFlowA(t);
        }
    }

    public Simulator sim() { return sim; }
    public List sections() { return sections; }
    public int length() { return length; }
    public int width() { return width; }

}
