package jeliot.calltree;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

/**
 * @author Niko Myller
 */
public class TreeDraw extends JComponent {

    /**
     * 
     */
    private TreeBuilder builder = new TreeBuilder();
    
    /**
     * 
     */
    private Tree tree;
    
    /**
     * 
     */
    private JScrollPane jsp;

    /**
     * 
     *
     */
    public TreeDraw() {
        //build the tree
        initialize();
        jsp = new JScrollPane(TreeDraw.this);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

    }

    /**
     * 
     */
    public void initialize() {
        tree = builder.buildTree();
        repaint();
    }
    
    /**
     * 
     * @return
     */
    public JComponent getComponent() {
        return jsp;
    }

    /**
     * The paint method draws the tree. 
     * There are 2 steps: 
     * <UL>
     * <LI>The BoundingBoxCalculator determines the width of each subtree.</LI>
     * <LI>The TreeDrawer calculates the exact locations for labels and edges and draws the tree.</LI>
     * </UL>
     */
    public void paint(Graphics g) {

        g.setColor(getBackground());

        BoundingBoxCalculator calc = new BoundingBoxCalculator(getGraphics());
        calc.execute(tree);
        TreeDrawer drawer = new TreeDrawer(g);

        Dimension area = new Dimension(calc.getMaxWidth() + drawer.getXoffset() + 10,
                                       calc.getMaxHeight() + drawer.getYoffset() + 30);
        setPreferredSize(area);
        revalidate();

        drawer.execute(tree);
    }

    /**
     * 
     * @param call
     */
    public void insertMethodCall(String call) {
        builder.insertNode(call);
        repaint();
        
    }

    /**
     * 
     * @param returnValue
     */
    public void returnMethodCall(String returnValue) {
        builder.returnNode(returnValue);
        repaint();
    }
}