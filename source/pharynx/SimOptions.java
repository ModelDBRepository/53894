/*
 * SimOptions.java
 *
 * Created on December 25, 2000, 9:56 AM
 */

package pharynx;
import java.util.*;
import java.io.*;
import java.awt.Color;

/**
 *
 * @author  leon@eatworms.swmed.edu
 * @version 0.1
 */
public class SimOptions extends java.lang.Object {
    public MotionList corpusMotion;
    public MotionList aIsthmusMotion;
    public MotionList pIsthmusMotion;
    public List particleSeries;
    public double start = 0.0;          // animation start time
    public double dt = 1.0;             // time interval
    public int frame = 100;             // animation frame time
    public boolean antialias = false;   // use antialiasing when drawing
    public double snapStart = 0.0;      // start taking snapshots
    public double snapStop = 0.0;       // stop taking snapshots
    public double snapInterval = 100.0; // interval between snapshots
    public File snapFile = SnapShotSettingsPanel.EMPTY_FILE; // no snapshots
    public String snapType = "PNG";     // image file type
    public String snapExtension = "png"; // image file extension
    public double maxT = 1000.0;

    /** Creates new SimOptions */
    public SimOptions() {
        LumenDims ld = new LumenDims();
        defaultMotions();
        particleSeries = new ArrayList();
    }
    
    public SimOptions(SimOptions o) {
        corpusMotion = new MotionList(o.corpusMotion);
        aIsthmusMotion = new MotionList(o.aIsthmusMotion);
        pIsthmusMotion = new MotionList(o.pIsthmusMotion);
        particleSeries = new ArrayList();
        for(Iterator i = o.particleSeries.iterator(); i.hasNext();) {
            ParticleSeries ps = (ParticleSeries) i.next();
            particleSeries.add(new ParticleSeries(ps));
        }
        start = o.start;
        dt = o.dt;
        frame = o.frame;
        antialias = o.antialias;
        snapStart = o.snapStart;
        snapStop = o.snapStop;
        snapInterval = o.snapInterval;
        snapFile = new File(o.snapFile.getPath());
        maxT = o.maxT;
    }

    private void defaultMotions() {
        corpusMotion = new MotionList();
        corpusMotion.add(new MotionPoint(0.0, 0.0));
        corpusMotion.add(new MotionPoint(167.0, 1.0));
        corpusMotion.add(new MotionPoint(172.0, 0.0));
        corpusMotion.add(new MotionPoint(305.0, 0.0));
        corpusMotion.trimToSize();
        aIsthmusMotion = new MotionList();
        aIsthmusMotion.add(new MotionPoint(0.0, 0.0));
        aIsthmusMotion.add(new MotionPoint(67.0, 0.0));
        aIsthmusMotion.add(new MotionPoint(267.0, 1.0));
        aIsthmusMotion.add(new MotionPoint(272.0, 0.0));
        aIsthmusMotion.add(new MotionPoint(305.0, 0.0));
        aIsthmusMotion.trimToSize();
        pIsthmusMotion = new MotionList();
        pIsthmusMotion.add(new MotionPoint(0.0, 0.0));
        pIsthmusMotion.add(new MotionPoint(100.0, 0.0));
        pIsthmusMotion.add(new MotionPoint(300.0, 1.0));
        pIsthmusMotion.add(new MotionPoint(305.0, 0.0));
        pIsthmusMotion.trimToSize();
    }

    public double defaultMaxT() {
        return Math.max(Math.max(corpusMotion.maxT(), aIsthmusMotion.maxT()),
                        pIsthmusMotion.maxT());
    }

    private void connectEnd(MotionList m) {
        if (maxT > m.getmp(m.size() - 1).t()) {
            m.add(new MotionPoint(maxT, m.getmp(0).r()));
        }
        else {
            m.getmp(m.size() - 1).r(m.getmp(0).r());
        }
    }

    public void connectEnds() {
        connectEnd(corpusMotion);
        connectEnd(aIsthmusMotion);
        connectEnd(pIsthmusMotion);
    }

    public List makeMotions() {
        MotionList pi = new MotionList(pIsthmusMotion);
        if (pi.getmp(0).r() != pi.getmp(1).r())
            pi.add(0, new MotionPoint(pi.getmp(0)));
        if (pi.getmp(pi.size() - 2).r() != pi.getmp(pi.size() - 1).r())
            pi.add(new MotionPoint(pi.getmp(pi.size() - 1)));
        MotionList ai = new MotionList(aIsthmusMotion);
        if (ai.getmp(0).r() != ai.getmp(1).r())
            ai.add(0, new MotionPoint(ai.getmp(0)));
        if (ai.getmp(ai.size() - 2).r() != ai.getmp(ai.size() - 1).r())
            ai.add(new MotionPoint(ai.getmp(ai.size() - 1)));
        if (pi.size() != ai.size())
            throw new IllegalArgumentException(
                "Can't reconcile Anterior and Posterior Isthmus Motions"
            );
        List mll = new ArrayList();
        LumenDims lds = new LumenDims();
        double div = (double) (lds.isthmusA() - lds.isthmusP() - 1);
        for(int i = lds.isthmusP(); i < lds.isthmusA(); i++) {
            MotionList ml = new MotionList();
            double fac = (double) (i - lds.isthmusP()) / div;
            for(int j = 0; j < pi.size(); j++) {
                ml.add(new MotionPoint(
                    (1.0 - fac) * pi.getmp(j).t() + fac * ai.getmp(j).t(),
                    (1.0 - fac) * pi.getmp(j).r() + fac * ai.getmp(j).r()
                ));
            }
            for(int j = 1; j < ml.size(); j++) {
                if (ml.getmp(j-1).equals(ml.getmp(j))) {
                    ml.remove(j);
                    j--;
                }
            }
            mll.add(ml);
        }
        for(int i = lds.corpusP(); i < lds.corpusA(); i++) {
            mll.add(new MotionList(corpusMotion));
        }
        return(mll);
    }

    public ParticleSeries getps(int i) {
        return (ParticleSeries) particleSeries.get(i);
    }

    public interface Updater {
        public void update(SimOptions options);
    }
}
