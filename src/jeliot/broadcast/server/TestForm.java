
package jeliot.broadcast.server;

/**
 *
 * @author  Carlos Menéndez
 */
public class TestForm extends javax.swing.JFrame {
    
    /** Creates new form TestForm */
    public TestForm() {
        initComponents();
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(300, 300));
        setResizable(false);

        jTextField1.setColumns(10);
        jTextField1.setText("Hello, this is a test text...");
        jTextField1.setPreferredSize(new java.awt.Dimension(55, 20));
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        getContentPane().add(jTextField1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        cadaux = new String(jTextField1.getText());
    }//GEN-LAST:event_jTextField1ActionPerformed

    
    public String getMessage(){
        return cadaux;
    }
    
    public String cadaux = new String("Hello!");
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
    
}
