/*
 * SimThread.java
 *
 * Created on December 22, 2000, 6:58 AM
 */

package pharynx;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;

/**
 *
 * @author  leon
 * @version 
 */
public class SimThread extends java.lang.Thread implements Kickable {
    private Cubbyhole c;
    private Pharynx p;
    private Collection particles;
    private SimOptions options;
    private double dt;                   // time step size
    private double start;
    private PictureData pData;
    private Snapper snapper = new Snapper();

    /** Creates new SimThread, setting time step
     *
     * @param dt    the time step
     */
    public SimThread(Cubbyhole c, SimOptions options) {
        this.options = options;
        this.c = c;
        this.dt = options.dt;
        this.start = options.start;
    }

    public void run() {
        this.currentThread().setName("Simulation");
        this.currentThread().setPriority(Thread.MIN_PRIORITY);
        p = new Pharynx(options.makeMotions(), true);
        pData = new PictureData();
        particles = new ArrayList();
        getConstantDims();
        addParticles();
        p.sim().add(new SKick(start, this, ANIMATE));
        addSnapshots();
        p.simulate();
        c.halt();
    }

    private void addParticles() {
        for(Iterator i = options.particleSeries.iterator(); i.hasNext();) {
            ParticleSeries ps = (ParticleSeries) i.next();
            for(int j = 0; j < ps.n; j++) {
                double t = ps.startTime + j * ps.interval;
                SKick sk = new SKick(t, this, INSERT_PARTICLE);
                sk.ps = ps;
                p.sim().add(sk);
            }
        }
    }

    private void addSnapshots() {
        if (0 == options.snapFile.getPath().compareTo(""))
            return;
        p.sim().add(new SKick(options.snapStart, this, SNAPSHOT));
    }
    
    public void halt() {
        p.halt();
    }

    /** Internal Kick with extra info */
    class SKick extends Kick {
        private int type;
        private ParticleSeries ps;
        
        public SKick(double t, Kickable k, int type) {
            super(t, k);
            this.type = type;
        }
    }
    
    private void retrievePictureData(double time) {
        pData.t = time;
        getCurrDiameter(time);
        pData.things = new ArrayList();
        for(Iterator i = particles.iterator(); i.hasNext();) {
            Particle pp = (Particle) i.next();
            if (!pp.inPharynx()) {
                i.remove();
                continue;
            }
            PictureData.Thing pt = pData.new Thing();
            pt.x = pp.where(time);
            pt.color = pp.color();
            pt.shape(pp.shape());
            pData.things.add(pt);
        }
    }

    /**
     * receive a Kick from the Kick handler
     *
     * Get numbers to be animated, send them off, and ask for another kick
     *
     * @param k     the Kick
     */
    public void kick(Kick k) {
        SKick sk = (SKick) k;
        switch(sk.type) {
        case ANIMATE:
            if (p.sim().isEmpty()) return;
            send(k.time());
            k.time(k.time() + dt);
            p.sim().add(k);
            break;

        case SNAPSHOT:
            if (p.sim().isEmpty()) return;
            snap(k.time());
            double next = k.time() + options.snapInterval;
            if (
                (next > k.time()) &&
                (next >= options.snapStart) &&
                (next <= options.snapStop)
            ) {
                k.time(next);
                p.sim().add(k);
            }
            break;

        case INSERT_PARTICLE:
            Particle pp = null;
            switch(sk.ps.type) {
            case ParticleSeries.BACTERIUM:
                pp = new Bacterium(p.sim(), p, sk.ps.getDiameter());
                break;

            case ParticleSeries.ACCELERATED:
                pp = new AcceleratedParticle(p.sim(), p, sk.ps.getDiameter(),
                                             sk.ps.acceleration);
                break;

            case ParticleSeries.FLUID:
                pp = new FluidParticle(p.sim(), p, sk.ps.getDiameter());
                break;

            default:
                throw new CantHappenException("Unknown particle type");
            }
            if (null != pp) {
                pp.color(sk.ps.color);
                pp.shape(sk.ps.shape);
                particles.add(pp);
                pp.place(sk.time(), p.length() - sk.ps.startX);
                pp.start();
            }
            break;

        default:
            throw new CantHappenException("Unknown SKick type");
        }
    }

    /** send PictureData to the animator */
    private void send(double time) {
        retrievePictureData(time);
        c.put(new PictureData(pData));
    }

    /** send PictureData to the animator */
    private void snap(double time) {
        retrievePictureData(time);
        snapper.write(time, pData, options);
    }

    private void getConstantDims() {
        java.util.List sections = p.sections();
        pData.length = p.length();
        pData.width = p.width();
        pData.maxDiameter = new double[sections.size()];
        pData.thickness = new double[sections.size()];
        for(int i = 0; i < sections.size(); i++) {
            Section s = (Section) sections.get(i);
            pData.maxDiameter[i] = s.diameter();
            pData.thickness[i] = s.thickness();
        }
    }
    
    private void getCurrDiameter(double t) {
        java.util.List sections = p.sections();
        pData.currDiameter = new double[sections.size()];
        for(int i = 0; i < sections.size(); i++) {
            Section s = (Section) sections.get(i);
            pData.currDiameter[i] = s.currDiameter(t);
        }
    }

    public void dt(double dt) { this.dt = dt; }
    public double dt() { return(dt); }
    public Pharynx pharynx() { return p; }

    private static final int ANIMATE= 0;
    private static final int INSERT_PARTICLE = 1;
    private static final int SNAPSHOT = 2;
    
}
