/*
 * Snapper.java
 *
 * Created on January 1, 2002, 1:40 PM
 */

package pharynx;

import java.io.*;
import java.text.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.media.jai.*;
import com.sun.media.jai.codec.*;

/**
 * Snapper takes snapshots of the state of the simulation.  These can be drawn
 * in a widget or written to an image file.
 *
 * @author  leon
 * @version 
 */
public class Snapper {
    private Component c = null;
    private Image basePic = null;
    private BufferedImage filePic = null;
    private static final int imageType = BufferedImage.TYPE_4BYTE_ABGR;
    
    private static LumenDims ld = new LumenDims();
    private static NumberFormat dnf;    // time format in display
    private static NumberFormat fnf;    // time format in filename
    {
        dnf = NumberFormat.getInstance();
        dnf.setMaximumFractionDigits(1);
        fnf = NumberFormat.getInstance();
        fnf.setMaximumFractionDigits(1);
        fnf.setMinimumIntegerDigits(4);
        fnf.setGroupingUsed(false);
    }

    public Snapper() {}

    public Snapper(Component c) {
        this();
        this.c = c;
    }

    public void write(double t, PictureData pd, SimOptions options)
        throws java.lang.NoClassDefFoundError {
        int pw = ld.length() + 20;
        int ph = (int) Math.ceil(ld.width()) + 20;
        if (null == filePic)
            filePic = new BufferedImage(pw, ph, imageType);
        Graphics g = filePic.getGraphics();
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.black);
        g2.fillRect(0, 0, pw, ph);
        paint(g, pw, ph, pd, options);
        String ts = fnf.format(t);
        String fname = options.snapFile.getPath() +
                       ts + "." + options.snapExtension;
        JAI.create("filestore", filePic, fname, options.snapType, null);
        String txtname = options.snapFile.getPath() + ts + ".txt";
        writetxt(txtname, t, pd, options);
    }
    
    private void writetxt(String txtname, double t, PictureData pd,
                          SimOptions options) {
        PrintWriter fw;
        try {
            fw = new PrintWriter(new FileOutputStream(txtname));
        }
        catch(FileNotFoundException e) {
            throw new CantCreateFileException("Can't create file " + txtname);
        }
        fw.println("time: " + t);
        for(int i = 0; i < pd.things.size(); i++) {
            PictureData.Thing ti = (PictureData.Thing) pd.things.get(i);
            fw.println("particle at: " + (ld.length() - ti.x));
        }
        fw.close();
    }
    
    public void paint(
        Graphics g,
        int pw, int ph,
        PictureData pd,
        SimOptions options
    ) {
        if (null == pd) return;
        Graphics2D g2 = (Graphics2D) g;
        Object aa = options.antialias ?
                        RenderingHints.VALUE_ANTIALIAS_ON :
                        RenderingHints.VALUE_ANTIALIAS_OFF;
        initBasePic(pw, ph, pd, options);
        g2.drawImage(basePic, 0, 0, null);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, aa);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, 
                            RenderingHints.VALUE_RENDER_SPEED);
        AffineTransform tx = g2.getTransform();
        g2.translate((pw + pd.length)/2, ph/2);
        Color openColor = Color.blue;
        g2.setColor(openColor);
        double x = 0.0;
        for(int i = 0; i < pd.thickness.length; i++) {
            double h = pd.currDiameter[i];
            Shape r = new Rectangle2D.Double(x - pd.thickness[i],
                                             -h/2,
                                             pd.thickness[i],
                                             h);
            g2.fill(r);
            x -= pd.thickness[i];
        }
        AffineTransform orig = g2.getTransform();
        Collections.sort(pd.things, new Comparator() {
            public int compare(Object o1, Object o2) {
                PictureData.Thing t1 = (PictureData.Thing) o1;
                PictureData.Thing t2 = (PictureData.Thing) o2;
                return (t1.x - t1.bbx() < t2.x - t2.bbx()) ? -1 :
                    ((t1.x -t1.bbx() == t2.x - t2.bbx()) ? 0 : 1);
            }
            
            public boolean equals(Object o1, Object o2) {
                PictureData.Thing t1 = (PictureData.Thing) o1;
                PictureData.Thing t2 = (PictureData.Thing) o2;
                return (t1.x - t1.bbx() == t2.x - t2.bbx());
            }
        });
        int j;
        for(int i = 0; i < pd.things.size(); i = j) {
            PictureData.Thing ti = (PictureData.Thing) pd.things.get(i);
            double totalH = ti.bbh();
            for(j = i + 1; j < pd.things.size(); j++) {
                PictureData.Thing tj = (PictureData.Thing) pd.things.get(j);
                if (tj.bbx() + tj.bbw() - tj.x <= ti.bbx() - ti.x) break;
                totalH += tj.bbh();
            }
            double currentH = -totalH / 2;
            for(int k = i; k < j; k++) {
                PictureData.Thing tk = (PictureData.Thing) pd.things.get(k);
                g2.translate(-tk.x, currentH - tk.bby());
                g2.setColor(tk.color);
                g2.fill(tk.shape());
                g2.setTransform(orig);
                currentH += tk.bbh();
            }
        }
        g2.setTransform(tx);
        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD));
        g2.drawString(dnf.format(pd.t),
                      (pw - pd.length) / 2, (ph + pd.width) / 2);
    }

    private void initBasePic(int pw, int ph,
                             PictureData pd,
                             SimOptions options
    ) {
        if (null != basePic) return;
        if (null != c)
            basePic = c.createImage(pw, ph);
        else
            basePic = new BufferedImage(pw, ph, imageType);
        Graphics gb = basePic.getGraphics();
        Graphics2D gb2 = (Graphics2D) gb;
        Object aa = options.antialias ?
                        RenderingHints.VALUE_ANTIALIAS_ON :
                        RenderingHints.VALUE_ANTIALIAS_OFF;
        gb2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, aa);
        if (null != c) {
            gb2.setColor(Color.black);
            gb2.fillRect(0, 0, pw, ph);
        }
        gb2.setColor(new Color(0, 0, 0, 0)); //transparent black
        gb2.fillRect(0, 0, pw, ph);
        gb2.translate((pw + pd.length)/2, ph/2);
        Color maxColor = Color.white;
        double x = 0.0;
        gb2.setColor(maxColor);
        for(int i = 0; i < pd.thickness.length; i++) {
            double h = pd.maxDiameter[i];
            Shape r = new Rectangle2D.Double(x - pd.thickness[i],
                                             -h/2,
                                             pd.thickness[i],
                                             h);
            gb2.fill(r);
            x -= pd.thickness[i];
        }
    }

}
