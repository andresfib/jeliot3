package jeliot.gui;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import jeliot.util.ResourceBundles;

/**
 * @author Niko Myller
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class UndoRedo {
    
    static private ResourceBundle messageBundle = ResourceBundles
    .getGuiMessageResourceBundle();
    
    UndoAction undoAction = new UndoAction();
    RedoAction redoAction = new RedoAction();
    UndoManager undo = new UndoManager();
    MyUndoableEditListener myundoable = new MyUndoableEditListener();
    
    class UndoAction extends AbstractAction {
        public UndoAction() {
            super(messageBundle.getString("menu.edit.undo"));
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                undo.undo();
            } catch (CannotUndoException ex) {
                System.out.println("Unable to undo: " + ex);
                ex.printStackTrace();
            }
            updateUndoState();
            redoAction.updateRedoState();
        }

        protected void updateUndoState() {
            if (undo.canUndo()) {
                setEnabled(true);
                putValue(Action.NAME, undo.getUndoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, messageBundle.getString("menu.edit.undo"));
            }
        }
    }

    class RedoAction extends AbstractAction {
        public RedoAction() {
            super(messageBundle.getString("menu.edit.redo"));
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                undo.redo();
            } catch (CannotRedoException ex) {
                System.out.println("Unable to redo: " + ex);
                ex.printStackTrace();
            }
            updateRedoState();
            undoAction.updateUndoState();
        }

        protected void updateRedoState() {
            if (undo.canRedo()) {
                setEnabled(true);
                putValue(Action.NAME, undo.getRedoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, messageBundle.getString("menu.edit.redo"));
            }
        }
    }

    protected class MyUndoableEditListener
                    implements UndoableEditListener {
        public void undoableEditHappened(UndoableEditEvent e) {
            //Remember the edit and update the menus.
            undo.addEdit(e.getEdit());
            undoAction.updateUndoState();
            redoAction.updateRedoState();
        }
    }

}

