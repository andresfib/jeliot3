package jeliot.theater;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;




/**
 *
 * @author  viktor
 */
public class ActorInfo extends JDialog {
                      
    private JButton jButton1;
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private JEditorPane jEditorPane1;
    private String text;
    
    public ActorInfo(String text) {
        this.text = new String(text);
        initComponents();
        setVisible(true);
    }
                              
    private void initComponents() {

        jPanel1 = new JPanel();
        jButton1 = new JButton();
        jScrollPane1 = new JScrollPane();
        jEditorPane1 = new JEditorPane();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Actor Information");
        setLocation(400,200);
        setBackground(new Color(204, 204, 255));
        setPreferredSize(new Dimension(400, 400));
        jPanel1.setLayout(new GridBagLayout());
        jPanel1.setBackground(new Color(204, 204, 204));
        jButton1.setText("Accept");
        jButton1.setEnabled(true);
        jButton1.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });

        jPanel1.add(jButton1, new GridBagConstraints());

        getContentPane().add(jPanel1, BorderLayout.SOUTH);

        jEditorPane1.setBackground(new Color(204, 204, 204));
        jEditorPane1.setEditable(false);
        jEditorPane1.setText(text);
        jEditorPane1.setLocale(Locale.getDefault());
        jScrollPane1.setViewportView(jEditorPane1);
        getContentPane().add(jScrollPane1, BorderLayout.CENTER);

        pack();
    }                        

    private void jButton1MouseClicked(MouseEvent evt) {
        this.dispose();
    }                    
}