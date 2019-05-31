/*
 * LumenDims.java
 *
 * Created on December 20, 2000, 6:22 AM
 */

package pharynx;
import java.util.*;

/**
 * An object that gives the dimensions of the C elegans pharyngeal lumen.
 *
 * @author leon@eatworms.swmed.edu
 * @version 0.1
 */
public class LumenDims {
    private double[] diameter;
    private double[] isthmusDiameter;
    private double[] corpusDiameter;
    /*
    private ArrayList motion;
    private List isthmusMotion;
    private List corpusMotion;
     */
    private int isthmusP = ISTHMUS_P;
    private int isthmusA = ISTHMUS_A;
    private int corpusP = CORPUS_P;
    private int corpusA = CORPUS_A;
    /** Creates new LumenDims object */
    public LumenDims() {
        init(0);
    }
    /** Creates new LumenDims object 
     * @param option    Currently ignored.
     */
    public LumenDims(int option) {
        init(option);
    }
    /* 
     * All the diameter access methods make a clone of the array so that user
     * can't screw up the original.  The motions are all unmodifiable lists, so
     * they can be returned directly.
     */
    /**
     * Get diameter of the pharyngeal lumen
     *
     * @return an array of lumen diameters from posterior to anterior
     */
    public double[] diameter() {
        double[] d = new double[diameter.length];
        System.arraycopy(diameter, 0, d, 0, d.length);
        return d;
    }
    /**
     * Get diameter of the isthmus lumen
     *
     * @return an array of lumen diameters from posterior to anterior
     */
    public double[] isthmusDiameter() {
        double[] d = new double[isthmusDiameter.length];
        System.arraycopy(isthmusDiameter, 0, d, 0, d.length);
        return d;
    }
    /**
     * Get diameter of the corpus lumen
     *
     * @return an array of lumen diameters from posterior to anterior
     */
    public double[] corpusDiameter() {
        double[] d = new double[corpusDiameter.length];
        System.arraycopy(corpusDiameter, 0, d, 0, d.length);
        return d;
    }
    /**
     * Get motions of the pharyngeal lumen
     *
     * @return a List of Lists of MotionPoints. Each element of the top level
     *         list corresponds to one location along the anterior/posterior
     *         axis of the pharynx. Each List of MotionPoints tells how that
     *         particular section of lumen moves. The MotionPoints are ordered
     *         by t.
     */
    // public List motion() { return new ArrayList(motion); }
    /**
     * Get motions of the isthmus lumen
     *
     * @return a List of Lists of MotionPoints. Each element of the top level
     *         list corresponds to one location along the anterior/posterior
     *         axis of the pharynx. Each List of MotionPoints tells how that
     *         particular section of lumen moves. The MotionPoints are ordered
     *         by t.
     */
    // public List isthmusMotion() { return new ArrayList(isthmusMotion); }
    /**
     * Get motions of the corpus lumen
     *
     * @return a List of Lists of MotionPoints. Each element of the top level
     *         list corresponds to one location along the anterior/posterior
     *         axis of the pharynx. Each List of MotionPoints tells how that
     *         particular section of lumen moves. The MotionPoints are ordered
     *         by t.
     */
    // public List corpusMotion() { return new ArrayList(corpusMotion); }
    /**
     * Posterior boundary of the isthmus
     *
     * The isthmus extends from subscripts isthmusP (inclusive) to isthmusA
     * (exclusive).
     */
    public int isthmusP() { return(isthmusP); }
    /**
     * Anterior boundary of the isthmus
     *
     * The isthmus extends from subscripts isthmusP (inclusive) to isthmusA
     * (exclusive).
     */
    public int isthmusA() { return(isthmusA); }
    /**
     * Posterior boundary of the corpus
     *
     * The corpus extends from subscripts corpusP (inclusive) to corpusA
     * (exclusive).
     */
    public int corpusP() { return(corpusP); }
    /**
     * Anterior boundary of the corpus
     *
     * The corpus extends from subscripts corpusP (inclusive) to corpusA
     * (exclusive).
     */
    public int corpusA() { return(corpusA); }
    public int length() { return LENGTH; }
    public double width() { return WIDTH; }
    // Internal methods
    private void init(int option) {
        diameter = new double[DIAMETER.length];
        isthmusDiameter = new double[ISTHMUS_A - ISTHMUS_P];
        corpusDiameter = new double[CORPUS_A - CORPUS_P];
        initDiameter();
        /*
        motion = new ArrayList(DIAMETER.length);
        initIsthmusMotion();
        initCorpusMotion();
        motion.trimToSize();
        corpusMotion = motion.subList(CORPUS_P, CORPUS_A);
        isthmusMotion = motion.subList(ISTHMUS_P, ISTHMUS_A);
         */
    }
    // copy DIAMETER to instance diameter
    private void initDiameter() {
        System.arraycopy(DIAMETER, 0, diameter, 0, diameter.length);
        System.arraycopy(diameter, ISTHMUS_P,
            isthmusDiameter, 0, 
            isthmusDiameter.length);
        System.arraycopy(diameter, CORPUS_P,
            corpusDiameter, 0, 
            corpusDiameter.length);
    }
    // copy CORPUS_MOTION to all of corpusMotion
    /*
    private void initCorpusMotion() {
        MotionList cm = new MotionList(CORPUS_MOTION.length);
        for(int i = 0; i < CORPUS_MOTION.length; i++) {
            cm.add(i,
                new MotionPoint(CORPUS_MOTION[i][0], CORPUS_MOTION[i][1]));
        }
        motion.addAll(CORPUS_P, Collections.nCopies(CORPUS_A - CORPUS_P, cm));
    }
    // interpolate between ISTHMUS_P_MOTION and ISTHMUS_A_MOTION
    private void initIsthmusMotion() {
        MotionList imc = new MotionList(ISTHMUS_P_MOTION.length);
        MotionList imi = new MotionList(ISTHMUS_P_MOTION.length);
        for(int i = 0; i < ISTHMUS_P_MOTION.length; i++) {
            imc.add(i, new MotionPoint(
                ISTHMUS_P_MOTION[i][0], ISTHMUS_P_MOTION[i][1]
            ));
            imi.add(i, new MotionPoint(
                (ISTHMUS_A_MOTION[i][0] - ISTHMUS_P_MOTION[i][0]) / 
                (ISTHMUS_A - ISTHMUS_P - 1),
                (ISTHMUS_A_MOTION[i][1] - ISTHMUS_P_MOTION[i][1]) / 
                (ISTHMUS_A - ISTHMUS_P - 1)
            ));
        }
        for(int j = ISTHMUS_P; j < ISTHMUS_A; j++) {
            MotionList imcc = new MotionList(imc.size());
            for(int i = 0; i < imc.size(); i++) {
                imcc.add(i, new MotionPoint(
                    imc.getmp(i).t() + (j - ISTHMUS_P) * imi.getmp(i).t(),
                    imc.getmp(i).r() + (j - ISTHMUS_P) * imi.getmp(i).r()
                ));
            }
            motion.add(j, imcc);
        }
    }
     */
    // Isthmus is subscripts [0, 207)
    private static final int ISTHMUS_P = 0;
    private static final int ISTHMUS_A = 207;
    // Corpus is subscripts [207, 820)
    private static final int CORPUS_P = 207;
    private static final int CORPUS_A = 820;
    
    private static final int LENGTH = 820;
    private static final double L_TO_W_SCALE = 2.189;
    private static final double WIDTH = L_TO_W_SCALE * 39;
    // The motions of each part of the pharynx are given as a series of (t, r)
    // pairs. t is in milliseconds. r varies from 0 = closed lumen, to
    // 1 = fully open.
    /*
    private static final double[][] CORPUS_MOTION = {
        {0, 0},
        {167, 1},
        {172, 0},
        {315, 0}
    };
    private static final double[][] ISTHMUS_P_MOTION = {
        {0, 0},
        {100, 0},
        {300, 1},
        {305, 0},
        {315, 0}
    };
    private static final double[][] ISTHMUS_A_MOTION = {
        {0, 0},
        {67, 0},
        {267, 1},
        {272, 0},
        {315, 0}
    };
     */
    /*
     * Diameter of the pharyngeal lumen. Units are arbitrary, but are the same
     * as the subscripts.
     */
    private static final double[] DIAMETER = {
	L_TO_W_SCALE * 1,
	L_TO_W_SCALE * 1,
	L_TO_W_SCALE * 1,
	L_TO_W_SCALE * 1,
	L_TO_W_SCALE * 1,
	L_TO_W_SCALE * 1,
	L_TO_W_SCALE * 1,
	L_TO_W_SCALE * 1,
	L_TO_W_SCALE * 2,
	L_TO_W_SCALE * 2,
	L_TO_W_SCALE * 2,
	L_TO_W_SCALE * 2,
	L_TO_W_SCALE * 2,
	L_TO_W_SCALE * 2,
	L_TO_W_SCALE * 2,
	L_TO_W_SCALE * 3,
	L_TO_W_SCALE * 3,
	L_TO_W_SCALE * 3,
	L_TO_W_SCALE * 3,
	L_TO_W_SCALE * 3,
	L_TO_W_SCALE * 3,
	L_TO_W_SCALE * 3,
	L_TO_W_SCALE * 3,
	L_TO_W_SCALE * 4,
	L_TO_W_SCALE * 4,
	L_TO_W_SCALE * 4,
	L_TO_W_SCALE * 4,
	L_TO_W_SCALE * 4,
	L_TO_W_SCALE * 4,
	L_TO_W_SCALE * 4,
	L_TO_W_SCALE * 5,
	L_TO_W_SCALE * 5,
	L_TO_W_SCALE * 5,
	L_TO_W_SCALE * 5,
	L_TO_W_SCALE * 5,
	L_TO_W_SCALE * 5,
	L_TO_W_SCALE * 5,
	L_TO_W_SCALE * 5,
	L_TO_W_SCALE * 6,
	L_TO_W_SCALE * 6,
	L_TO_W_SCALE * 6,
	L_TO_W_SCALE * 6,
	L_TO_W_SCALE * 6,
	L_TO_W_SCALE * 6,
	L_TO_W_SCALE * 6,
	L_TO_W_SCALE * 7,
	L_TO_W_SCALE * 7,
	L_TO_W_SCALE * 7,
	L_TO_W_SCALE * 7,
	L_TO_W_SCALE * 7,
	L_TO_W_SCALE * 7,
	L_TO_W_SCALE * 7,
	L_TO_W_SCALE * 7,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 8,
	L_TO_W_SCALE * 10,
	L_TO_W_SCALE * 11,
	L_TO_W_SCALE * 12,
	L_TO_W_SCALE * 13,
	L_TO_W_SCALE * 14,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 16,
	L_TO_W_SCALE * 17,
	L_TO_W_SCALE * 19,
	L_TO_W_SCALE * 20,
	L_TO_W_SCALE * 20,
	L_TO_W_SCALE * 21,
	L_TO_W_SCALE * 22,
	L_TO_W_SCALE * 23,
	L_TO_W_SCALE * 23,
	L_TO_W_SCALE * 24,
	L_TO_W_SCALE * 25,
	L_TO_W_SCALE * 25,
	L_TO_W_SCALE * 26,
	L_TO_W_SCALE * 27,
	L_TO_W_SCALE * 27,
	L_TO_W_SCALE * 28,
	L_TO_W_SCALE * 29,
	L_TO_W_SCALE * 29,
	L_TO_W_SCALE * 30,
	L_TO_W_SCALE * 30,
	L_TO_W_SCALE * 31,
	L_TO_W_SCALE * 31,
	L_TO_W_SCALE * 31,
	L_TO_W_SCALE * 32,
	L_TO_W_SCALE * 32,
	L_TO_W_SCALE * 33,
	L_TO_W_SCALE * 33,
	L_TO_W_SCALE * 34,
	L_TO_W_SCALE * 34,
	L_TO_W_SCALE * 35,
	L_TO_W_SCALE * 35,
	L_TO_W_SCALE * 35,
	L_TO_W_SCALE * 35,
	L_TO_W_SCALE * 36,
	L_TO_W_SCALE * 36,
	L_TO_W_SCALE * 36,
	L_TO_W_SCALE * 36,
	L_TO_W_SCALE * 37,
	L_TO_W_SCALE * 37,
	L_TO_W_SCALE * 37,
	L_TO_W_SCALE * 37,
	L_TO_W_SCALE * 37,
	L_TO_W_SCALE * 38,
	L_TO_W_SCALE * 38,
	L_TO_W_SCALE * 38,
	L_TO_W_SCALE * 38,
	L_TO_W_SCALE * 38,
	L_TO_W_SCALE * 38,
	L_TO_W_SCALE * 38,
	L_TO_W_SCALE * 38,
	L_TO_W_SCALE * 39,
	L_TO_W_SCALE * 39,
	L_TO_W_SCALE * 39,
	L_TO_W_SCALE * 39,
	L_TO_W_SCALE * 39,
	L_TO_W_SCALE * 39,
	L_TO_W_SCALE * 39,
	L_TO_W_SCALE * 39,
	L_TO_W_SCALE * 39,
	L_TO_W_SCALE * 39,
	L_TO_W_SCALE * 39,
	L_TO_W_SCALE * 39,
	L_TO_W_SCALE * 39,
	L_TO_W_SCALE * 38,
	L_TO_W_SCALE * 38,
	L_TO_W_SCALE * 38,
	L_TO_W_SCALE * 38,
	L_TO_W_SCALE * 38,
	L_TO_W_SCALE * 38,
	L_TO_W_SCALE * 38,
	L_TO_W_SCALE * 37,
	L_TO_W_SCALE * 37,
	L_TO_W_SCALE * 37,
	L_TO_W_SCALE * 37,
	L_TO_W_SCALE * 37,
	L_TO_W_SCALE * 37,
	L_TO_W_SCALE * 36,
	L_TO_W_SCALE * 36,
	L_TO_W_SCALE * 36,
	L_TO_W_SCALE * 35,
	L_TO_W_SCALE * 35,
	L_TO_W_SCALE * 35,
	L_TO_W_SCALE * 34,
	L_TO_W_SCALE * 34,
	L_TO_W_SCALE * 33,
	L_TO_W_SCALE * 33,
	L_TO_W_SCALE * 33,
	L_TO_W_SCALE * 32,
	L_TO_W_SCALE * 32,
	L_TO_W_SCALE * 32,
	L_TO_W_SCALE * 31,
	L_TO_W_SCALE * 31,
	L_TO_W_SCALE * 30,
	L_TO_W_SCALE * 29,
	L_TO_W_SCALE * 29,
	L_TO_W_SCALE * 28,
	L_TO_W_SCALE * 28,
	L_TO_W_SCALE * 27,
	L_TO_W_SCALE * 26,
	L_TO_W_SCALE * 26,
	L_TO_W_SCALE * 25,
	L_TO_W_SCALE * 25,
	L_TO_W_SCALE * 24,
	L_TO_W_SCALE * 23,
	L_TO_W_SCALE * 23,
	L_TO_W_SCALE * 22,
	L_TO_W_SCALE * 21,
	L_TO_W_SCALE * 20,
	L_TO_W_SCALE * 19,
	L_TO_W_SCALE * 18,
	L_TO_W_SCALE * 17,
	L_TO_W_SCALE * 17,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15,
	L_TO_W_SCALE * 15
    };
}
