package jeliot.calltree;

import java.util.ListIterator;

/**
 * @author Niko Myller
 */
public class Tree {
    
    TreeNode root;
    
    public Tree() { }
    
    public TreeNode getRoot() {
        return root;
    }
    
    public void setRoot(TreeNode r) {
        this.root = r;
    }
    
    public void addChild(TreeNode tn, TreeNode child) {
        tn.addChild(child);
    }

    public void addChild(TreeNode tn, int in, TreeNode child) {
        tn.addChild(in, child);
    }
    
    public ListIterator getChildIterator(TreeNode tn) {
        return tn.getChildIterator();
    }
    
    public TreeNode getChild(TreeNode tn, int from) {
        return tn.getChild(from);
    }

    public void removeChild(TreeNode tn, TreeNode child) {
        tn.removeChild(child);
    }

    public void removeChild(TreeNode tn, int in) {
        tn.removeChildAt(in);
    }
    
    public int getSize() {
        if (root != null) {
            return root.getSize();
        } else {
            return 0;
        }
    }
    
    public int depth() {
        if (root != null) {
            return root.getDepth();
        } else {
            return 0;
        }
    }
}
