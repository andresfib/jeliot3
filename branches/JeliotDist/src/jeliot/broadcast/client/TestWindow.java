
package jeliot.broadcast.client;

/**
 *
 * @author  Carlos Menéndez
 */
public class TestWindow extends javax.swing.JFrame {
    
    /** Creates new form TestForm */
    public TestWindow() {
        initComponents();
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
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
        setVisible(true);
        pack();
    }// </editor-fold>                        

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {                                            
        cadaux = new String(jTextField1.getText());
    }                                           

    
    public String getMessage(){
        return cadaux;
    }
    
    public String cadaux = new String("Hello!");
    // Variables declaration - do not modify                     
    private javax.swing.JTextField jTextField1;
    // End of variables declaration                   
    
}
