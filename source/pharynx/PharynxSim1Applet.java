/*
 * PharynxSim1Applet.java
 *
 * Created on December 30, 2000, 11:02 AM
 */

package pharynx;

/**
 *
 * @author  leon
 * @version 
 */
public class PharynxSim1Applet extends javax.swing.JApplet {
    PharynxSim1 pharynxSim = null;

    /** Creates new form PharynxSim1Applet */
    public PharynxSim1Applet() {
        initComponents ();
    }

    public void init() {}

    public void start() {}

    public void stop() {}

    public void destroy() {
        pharynxSim.exit();
        pharynxSim = null;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jButton1 = new javax.swing.JButton();
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), 0));
        
        jButton1.setText("Start");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        }
        );
        
        getContentPane().add(jButton1);
        
    }//GEN-END:initComponents

  private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        pharynxSim = new PharynxSim1();
  }//GEN-LAST:event_jButton1ActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton jButton1;
  // End of variables declaration//GEN-END:variables

}
