package jeliot.calltree;

import java.util.ListIterator;

/**
 * @author Niko Myller
 *
 */
public class EulerTour {

    protected Tree tree;

    public EulerTour() {}

    public void execute(Tree t) {
        this.tree = t;
        init();
        TreeNode tn = tree.getRoot();
        if (tn != null) {
            eulerTour(tn);
        }
    }

    private void eulerTour(TreeNode tn) {
        int n = tn.getChildCount();
        if (n > 0) {
            visitFirstTime(tn);
            ListIterator li = tn.getChildIterator();
            while (li.hasNext()) {
                eulerTour((TreeNode) li.next());
                if (n > 1) {
                    visitBetweenChildren(tn);
                }
            }
            visitLastTime(tn);
        } else {
            visitExternal(tn);
        }
    }

    protected void init() {}

    protected void visitExternal(TreeNode tn) {
        visitFirstTime(tn);
        visitLastTime(tn);
    }

    protected void visitFirstTime(TreeNode tn) {}
 
    protected void visitBetweenChildren(TreeNode tn) {}
    
    protected void visitLastTime(TreeNode tn) {}

}
