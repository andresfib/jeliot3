package jeliot.gui;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import jeliot.theatre.*;
//import jeliot.parser.*;
import jeliot.*;
import jeliot.ecode.*;

import koala.dynamicjava.interpreter.*;

/**
 * This is the main window of the Jeliot 3.
 *
 * @author Pekka Uronen
 */
public class JeliotWindow {

    private String jeliotVersion = "Jeliot 3.1";

    private boolean showMessagesInDialogs = false;
    private boolean errorOccured = false;

    private AboutWindow aw = null;
    private HelpWindow hw = null;

    private int previousSpeed;

    /** The frame in which all the action goes on. */
    private JFrame frame;

    /** The theatre in which the programs are animated. */
    private Theatre theatre;

    /** The animation engine that that will animate the code. */
    private AnimationEngine engine;

    /** The main program. */
    private Jeliot jeliot;

    /** The code pane where the code is shown during the animation. */
    private CodePane codePane;

    /** The code editor in which the users can write their code. */
    private CodeEditor editor = new CodeEditor();

    /** The pane that splits the window. */
    private JSplitPane codeNest;

    /** The step button.*/
    private JButton stepButton;

    /** The play button.*/
    private JButton playButton;

    /** The pause button.*/
    private JButton pauseButton;

    /** The rewind button.*/
    private JButton rewindButton;

    /** The edit button.*/
    private JButton editButton;

    /** The compile button.*/
    private JButton compileButton;

    /** Slider tha controls the animation speed. */
    private JSlider speedSlider;

    /** In this text area will come the output of the user-made programs. */
    private JTextArea outputConsole;

    private Vector animationMenuItems = new Vector();

    /** This ImageLoader will load all the images. */
    private ImageLoader iLoad;

    /** This variable will control the panels. */
    private PanelController panelController;

    /** This JEditorPane errorJEditorPane will show the error messages for the users. */
    private JEditorPane errorJEditorPane = new JEditorPane(); {
        errorJEditorPane.setEditable(false);
        errorJEditorPane.setContentType("text/html");
        errorJEditorPane.setBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLoweredBevelBorder(),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
    }

    private JScrollPane errorPane = new JScrollPane(errorJEditorPane); {
        errorPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        //errorPane.setPreferredSize(new Dimension(250, 145));
    }



    /** This JPanel errorViewer will help the showing of the error messages for the users. */
    private JPanel errorViewer = new JPanel() {
        private Image backImage;

        public void paintComponent(Graphics g) {
            Dimension d = getSize();
            int w = d.width;
            int h = d.height;
            if (backImage == null) {
                backImage = iLoad.getLogicalImage("Panel");
            }
            int biw = backImage.getWidth(this);
            int bih = backImage.getHeight(this);

            if (biw >= 1 || bih >= 1) {
                for (int x = 0; x < w; x += biw) {
                    for (int y = 0; y < h; y += bih) {
                        g.drawImage(backImage, x, y, this);
                    }
                }
            }
        }
    };

    {
        errorViewer.setBorder(
                BorderFactory.createEmptyBorder(12, 12, 5, 12));
        errorViewer.setLayout(new BorderLayout());
        errorViewer.add("Center", errorPane);
        JPanel bp = new JPanel();
        bp.setOpaque(false);

        JButton ok = new JButton("OK");
        ok.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    changeTheatrePane(theatre);
                    //editButton.setEnabled(true);
                }
           }
        );
        bp.add(ok);
        errorViewer.add("South", bp);
    }

    /**
     * Action listeners for the step- button.
     */
    private ActionListener stepAction =
        new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stepAnimation();
            }
        };

    /**
     * Action listeners for the play- button.
     */
    private ActionListener playAction =
        new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playAnimation();
            }
        };

    /**
     * Action listeners for the pause- button.
     */
    private ActionListener pauseAction =
        new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pauseAnimation();
            }
        };

    /**
     * Action listeners for the rewind- button.
     */
    private ActionListener rewindAction =
        new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rewindAnimation();
            }
        };

    private ActionListener exit = new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (editor.isChanged()) {
                int n = JOptionPane.showConfirmDialog(frame,
                            "You are quiting the program\n" +
                            "without saving the edited file.\n" +
                            "Do you want to save your file?",
                            "Quiting without saving",
                            JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    editor.saveProgram();
                }
            }
            System.exit(0);
        }
    };

    /**
     * Helping to enable and disable the components.
     */
    private Vector editWidgets = new Vector();

    /**
     * Helping to enable and disable the components.
     */
    private Vector animWidgets = new Vector();

    private String udir;

    /**
     * Assigns the values of the parameters in the object values.
     * Constructs the panelController with theatre and iload.
     *
     * @param   jeliot  The main program.
     * @param   codePane    The pane where all the code is shown while animated.
     * @param   theatre The theatre where all the code is animated.
     * @param   engine  The engine that animates the code.
     * @param   iLoad       The imageloader that loads all the images.
     * @param   udir    The user directory
     */
    public JeliotWindow(
            Jeliot jeliot,
            CodePane codePane,
            Theatre theatre,
            AnimationEngine engine,
            ImageLoader iLoad ,
            String udir) {

        this.jeliot = jeliot;
        this.codePane = codePane;
        this.theatre = theatre;
        this.engine = engine;
        this.iLoad = iLoad;
        this.udir = udir;

        this.panelController = new PanelController(theatre, iLoad);
    }


    /**
     * Initializes the JFrame frame.
     * Sets up all the basic things for the window. (Panels, Panes, Menubars)
     * Things for debugging.
     */
    public void setUp() {

        frame = new JFrame(jeliotVersion);
        frame.setIconImage(iLoad.getLogicalImage("Jeliot-icon"));

        frame.setJMenuBar(makeMenuBar());
        editor.setMasterFrame(frame);

        JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            codePane, theatre);
        pane.setOneTouchExpandable(true);
        codeNest = pane;

        Dimension minimumSize = new Dimension(0, 0);
        codePane.setMinimumSize(minimumSize);
        editor.setMinimumSize(minimumSize);

        JPanel bottomPane = new JPanel(new BorderLayout());
        final JComponent conPan = makeControlPanel();
        bottomPane.add("West", conPan);

        OutputConsole oc = new OutputConsole(conPan);
        this.outputConsole = oc;

        bottomPane.add("Center", oc.container);

        JPanel rootPane = new JPanel(new BorderLayout());
        rootPane.add("Center", pane);
        rootPane.add("South", bottomPane);

        frame.setContentPane(rootPane);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        /* frame.setSize(screenSize.width, screenSize.height - 30); // To maximize the window*/
        frame.setSize(800, 600);

        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height)
            frameSize.height = screenSize.height;
        if (frameSize.width > screenSize.width)
            frameSize.width = screenSize.width;

        frame.setLocation((screenSize.width - frameSize.width) / 2,
                           (screenSize.height - frameSize.height) / 2);


        frame.addWindowListener(
            new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            }
        );

        enterEditTrue();
        pane.setDividerLocation(300);

        //TheatrePopup popup = new TheatrePopup();
        //theatre.addMouseListener(popup);
        //theatre.addMouseMotionListener(popup);

        hw = new HelpWindow(iLoad.getLogicalImage("Jeliot-icon"), udir);
        aw = new AboutWindow(iLoad.getLogicalImage("Jeliot-icon"), udir);

        frame.show();
    }


    /**
     * Makes and returns the menubar for the main frame.
     * Things for debugging.
     *
     * @return  The menubar for the main frame.
     */
    private JMenuBar makeMenuBar() {

        JMenuBar menuBar = new JMenuBar();

        JMenu menu, submenu;
        JMenuItem menuItem;
        JCheckBoxMenuItem cbMenuItem;
        JRadioButtonMenuItem rbMenuItem;

        //a group of JMenuItems
        JMenu programMenu = editor.makeProgramMenu();

        menuItem = new JMenuItem("Exit");
        menuItem.setMnemonic(KeyEvent.VK_X);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(exit);
        programMenu.add(menuItem);

        menuBar.add(programMenu);
        editWidgets.addElement(programMenu);

        JMenu editMenu = editor.makeEditMenu();
        menuBar.add(editMenu);
        editWidgets.addElement(editMenu);

        JMenu controlMenu = makeControlMenu();
        menuBar.add(controlMenu);

        JMenu animationMenu = makeAnimationMenu();
        menuBar.add(animationMenu);
        animWidgets.addElement(animationMenu);

        JMenu helpMenu = makeHelpMenu();
        menuBar.add(helpMenu);

        JMenu[] jm = {controlMenu, animationMenu};
        addInAnimationMenuItems(jm);

        return menuBar;
    }

    /**
     * Adds the given JMenu's JMenuItems into the Vector animationMenuItems.
     */
    public void addInAnimationMenuItems(JMenu[] jm) {
        for (int i = 0; i < jm.length; i++) {
            int length = jm[i].getItemCount();
            for (int j = 0; j < length; j++) {
                JMenuItem jmi = jm[i].getItem(j);
                if (jmi != null) {
                    animationMenuItems.add(jmi);
                }
            }
        }
    }

    /**
     * Menu with the commands to enter to animate and edit.
     */
    private JMenu makeHelpMenu() {

        JMenu menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);
        JMenuItem menuItem;

        menuItem = new JMenuItem("Help");
        menuItem.setMnemonic(KeyEvent.VK_H);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_F1, 0));
        menuItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (hw != null) {
                        //hw.pack();
                        hw.show();
                    }
                }
            }
        );
        menu.add(menuItem);

        menuItem = new JMenuItem("About Jeliot");
        menuItem.setMnemonic(KeyEvent.VK_B);
        //        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        //        KeyEvent.VK_M, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(
                new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    if (aw != null) {
                        //aw.pack();
                        aw.show();
                    }
                }
            }
        );
        menu.add(menuItem);

        return menu;
    }
    /**
     * Menu with the VCR commands
     */
    private JMenu makeAnimationMenu() {

        JMenu menu = new JMenu("Animation");
        menu.setMnemonic(KeyEvent.VK_A);
        JMenuItem menuItem;

        menuItem = new JMenuItem("Pause");
        menuItem.setMnemonic(KeyEvent.VK_U);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_U, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    pauseButton.doClick();
                }
            }
        );
        menu.add(menuItem);

        menuItem = new JMenuItem("Play");
        menuItem.setMnemonic(KeyEvent.VK_P);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    playButton.doClick();
                }
            }
        );
        menu.add(menuItem);

        menuItem = new JMenuItem("Rewind");
        menuItem.setMnemonic(KeyEvent.VK_R);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_R, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    rewindButton.doClick();
                }
            }
        );
        menu.add(menuItem);

        menuItem = new JMenuItem("Step");
        menuItem.setMnemonic(KeyEvent.VK_S);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_SPACE,0));
        menuItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    stepButton.doClick();
                }
            }
        );
        menu.add(menuItem);

        final JCheckBoxMenuItem cbmenuItem = new JCheckBoxMenuItem("Pause on message",
                                         showMessagesInDialogs);
        cbmenuItem.setMnemonic(KeyEvent.VK_D);
        cbmenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_D, ActionEvent.CTRL_MASK));
        cbmenuItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showMessagesInDialogs = cbmenuItem.getState();
                }
            }
        );
        menu.add(cbmenuItem);

        menuItem = new JMenuItem("Faster");
        menuItem.setMnemonic(KeyEvent.VK_F);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    speedSlider.setValue(speedSlider.getValue()+1);
                }
            }
        );
        menu.add(menuItem);

        menuItem = new JMenuItem("Slower");
        menuItem.setMnemonic(KeyEvent.VK_L);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_MINUS, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    speedSlider.setValue(speedSlider.getValue()-1);
                }
            }
        );
        menu.add(menuItem);

        menuItem = new JMenuItem("Run until...");
        menuItem.setMnemonic(KeyEvent.VK_L);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_R, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    runUntil();
                }
            }
        );
        menu.add(menuItem);

        return menu;
    }


    /**
     * Menu with the commands to enter to animate and edit.
     */
    private JMenu makeControlMenu() {

        JMenu menu = new JMenu("Control");
        menu.setMnemonic(KeyEvent.VK_C);
        JMenuItem menuItem;

        menuItem = new JMenuItem("Edit");
        menuItem.setMnemonic(KeyEvent.VK_E);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    editButton.doClick();
                }
            }
        );
        menu.add(menuItem);

        animWidgets.addElement(menuItem);

        menuItem = new JMenuItem("Compile");
        menuItem.setMnemonic(KeyEvent.VK_M);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_M, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    compileButton.doClick();
                }
            }
        );
        menu.add(menuItem);

        editWidgets.addElement(menuItem);

        return menu;
    }


    /**
     * Makes the control buttons for the control panel.
     *
     * @param   label   The label for the button.
     * @param   iconName    The icon name for the icon on the button.
     * @return  The control button for control panel.
     */
    private JButton makeControlButton(String label, String iconName) {

        ImageIcon icon = new ImageIcon("images/"+iconName);
        JButton b = new JButton(label, icon);
        b.setVerticalTextPosition(AbstractButton.BOTTOM);
        b.setHorizontalTextPosition(AbstractButton.CENTER);
    //  b.setBorder(BorderFactory.createEtchedBorder());
        b.setMargin(new Insets(0,0,0,0));
        return b;
    }

    /**
     * Constructs the control panel.
     * Uses makeControlButton(String, String)
     *
     * @return  The constructed control panel.
     * @see #makeControlButton(String, String)
     */
    private JPanel makeControlPanel() {

        editButton = makeControlButton("Edit", "editicon.gif");
        compileButton = makeControlButton("Compile", "compileicon.gif");

        editButton.setMnemonic(KeyEvent.VK_E);
        compileButton.setMnemonic(KeyEvent.VK_M);

        editButton.setMargin(new Insets(0, 2, 0, 2));
        compileButton.setMargin(new Insets(0, 2, 0, 2));

        editButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    enterEdit();
                }
            }
        );

        compileButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    tryToEnterAnimate();
                }
            }
        );

        editWidgets.addElement(compileButton);
        animWidgets.addElement(editButton);


        JPanel statePane = new JPanel();
        statePane.setLayout(new GridLayout(1, 2));
        statePane.add(editButton);
        statePane.add(compileButton);

        // create animation control buttons
        stepButton = makeControlButton("Step", "stepicon.gif");
        playButton = makeControlButton("Play", "playicon.gif");
        pauseButton = makeControlButton("Pause", "pauseicon.gif");
        rewindButton = makeControlButton("Rewind", "rewindicon.gif");

        stepButton = makeControlButton("Step", "stepicon.gif");
        stepButton.setMnemonic(KeyEvent.VK_S);
        playButton = makeControlButton("Play", "playicon.gif");
        playButton.setMnemonic(KeyEvent.VK_P);
        pauseButton = makeControlButton("Pause", "pauseicon.gif");
        pauseButton.setMnemonic(KeyEvent.VK_U);
        rewindButton = makeControlButton("Rewind", "rewindicon.gif");
        rewindButton.setMnemonic(KeyEvent.VK_R);

        stepButton.addActionListener(stepAction);
        playButton.addActionListener(playAction);
        pauseButton.addActionListener(pauseAction);
        rewindButton.addActionListener(rewindAction);

        animWidgets.addElement(stepButton);
        animWidgets.addElement(playButton);
        animWidgets.addElement(pauseButton);
        animWidgets.addElement(rewindButton);

        // create animation speed control slider
        speedSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, 10);
        speedSlider.setMajorTickSpacing(10);
        speedSlider.setMinorTickSpacing(5);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(false);

        speedSlider.addChangeListener(
            new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    int volume = speedSlider.getValue();
                    engine.setVolume((double)(volume * 50.0));
                }
            }
        );

        animWidgets.addElement(speedSlider);

        JPanel bp = new JPanel();
        bp.setLayout(new GridLayout(1, 4));
        bp.add(stepButton);
        bp.add(playButton);
        bp.add(pauseButton);
        bp.add(rewindButton);

        JPanel p = new JPanel();
        GridBagLayout pl = new GridBagLayout();
        p.setLayout(pl);


        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 4, 8);
        pl.setConstraints(statePane, c);
        p.add(statePane);

        JLabel jicon = new JLabel(new ImageIcon("images/jeliot.gif"));
        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 2;
        c.insets = new Insets(0, 0, 0, 0);
        pl.setConstraints(jicon, c);
        p.add(jicon);

        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 0, 4, 0);
        pl.setConstraints(bp, c);
        p.add(bp);

        c.gridy = 1;
        c.gridwidth = 1;
        c.insets = new Insets(0, 0, 0, 0);
        c.anchor = GridBagConstraints.WEST;
        JLabel label = new JLabel("Animation");
        pl.setConstraints(label, c);
        p.add(label);

        c.gridy = 2;
        label = new JLabel("speed");
        pl.setConstraints(label, c);
        p.add(label);

        c.gridx = 2;
        c.gridy = 1;
        c.gridheight = 2;
        pl.setConstraints(speedSlider, c);
        p.add(speedSlider);

        p.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(5, 2, 2, 10),
                    BorderFactory.createEtchedBorder()));

        return p;
    }


    /**
     * Enables or disables the components depending on the second parameter.
     *
     * @param   enum    The collection of components that are set enabled or disabled as the boolean enable is set.
     * @param   enable  Sets wheter the components are enabled or disabled.
     */
    private void enableWidgets(Enumeration enum, boolean enable) {

        while (enum.hasMoreElements()) {
            Component comp = (Component)enum.nextElement();
            comp.setEnabled(enable);
        }
    }

    /**
     * Changes the code pane in the codeNest.
     * Sets inside the codeNest the new code pane.
     *
     * @param   comp    The component that is changes in the code pane.
     */
    private void changeCodePane(JComponent comp) {

        int loc = codeNest.getDividerLocation();
        codeNest.setLeftComponent(comp);
        codeNest.setDividerLocation(loc);
    }


    /**
     * Changes the theatre pane in the codeNest.
     * Sets inside the codeNest the new theatre pane.
     *
     * @param   comp    The component that is changes in the theatre pane.
     */
    private void changeTheatrePane(JComponent comp) {

        int loc = codeNest.getDividerLocation();
        codeNest.setRightComponent(comp);
        codeNest.setDividerLocation(loc);
    }

    /**
     * This method is called when user clicks the "Edit" button.
     */
    void enterEdit() {

        changeTheatrePane(theatre);

        panelController.slide(false,
            new Runnable() {
                public void run() {
                    SwingUtilities.invokeLater(
                        new Runnable() {
                            public void run() {
                                enterEditTrue();
                            }
                        }
                    );
                }
            }
        ).start();
    }


    /**
     * Makes the user interface changes when user clicks the "Edit" button.
     */
    public void enterEditTrue() {
        changeCodePane(editor);
        enableWidgets(editWidgets.elements(), true);
        enableWidgets(animWidgets.elements(), false);
    }


    /**
     * Returns the program code from the CodeEditor -object.
     *
     * @return  The program code from the CodeEditor -object.
     */
    public String getProgram() {
        return editor.getProgram();
    }

    /**
     * Called when the user pushes the "Compile" button.
     * Gets the code from the CodeEditor -object.
     * Sends it to "compilation".
     */
    void tryToEnterAnimate() {

        // Jeliot 3
        if (editor.isChanged()) {
            if (editor.getCurrentFile() != null) {
                editor.writeProgram(editor.getCurrentFile());
            } else {
                editor.saveProgram();
            }
            editor.setChanged(false);
        }

        try {
            try {

                String programCode = editor.getProgram();

                String methodCall = null;
                methodCall = findMainMethodCall(programCode);

                if (methodCall != null) {

                    //Reader r = new BufferedReader(new StringReader(programCode));
                    //jeliot.createLauncher(r);
                    //Reader s = new BufferedReader(new StringReader(methodCall));

                    jeliot.compile(programCode, methodCall);

                    changeTheatrePane(theatre);

                    panelController.slide(true,
                        new Runnable() {
                            public void run() {
                                SwingUtilities.invokeLater(
                                    new Runnable() {
                                        public void run() {
                                            enterAnimate();
                                        }
                                    }
                                );
                            }
                        }
                    ).start();

                } else {

                    showErrorMessage("<H2>No main method found</H2>"+
                    "<P>There was no method main found from any of the classes"+
                    "and thus the program cannot be run. Add a method main to one of the classes (the main class).</P>" +
                    "<P>For example like this:</P>" +
                    "<CODE>public class MyClass {<BR>&nbsp;&nbsp;&nbsp;public static void main(String[] args) {" +
                    "<BR>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;//Your Algorithm" +
                    "<BR>&nbsp;&nbsp;&nbsp;}<BR>}</CODE>");
                    editButton.setEnabled(false);
                }
            }

            catch (FeatureNotImplementedException e) {
                showErrorMessage(e);
                return;
            }
/*            catch (InterpreterException e) {
                showErrorMessage(e);
                return;
            }
            catch (SemanticException e) {
                showErrorMessage(e);
                return;
            }
            catch (SyntaxErrorException e) {
                showErrorMessage(e);
                return;
            }
*/
        }
        catch (Exception e) {
            editButton.doClick();
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }
/*
    public String replaceChar(String from, char c, String with) {

        int index = from.indexOf(c);
        while(index != -1) {
            from = from.substring(0,index) +
            with +
            from.substring(index+1,from.length());
            index = from.indexOf(c);
        }
        return from;
    }
*/

    /**
     * Tries to find the main method call from one of the classes.
     */
    public String findMainMethodCall(String programCode) {

        String commentsRemoved = removeComments(programCode);
        commentsRemoved = ECodeUtilities.replace(commentsRemoved, "\n", " ");
        commentsRemoved = ECodeUtilities.replace(commentsRemoved, "\r", " ");
        commentsRemoved = ECodeUtilities.replace(commentsRemoved, "\t", " ");

        String mainMethod="static void main(";
        String classString = " class ";

        int methodIndex = commentsRemoved.indexOf(mainMethod);
        //System.out.println(methodIndex);
        while (methodIndex > -1) {
            int parenthesisIndex = commentsRemoved.indexOf(")", methodIndex);
            String partProgramCode = commentsRemoved.substring(0, parenthesisIndex);
            String methodArea = commentsRemoved.substring(methodIndex, parenthesisIndex);

            if (methodArea.indexOf(",") < 0 &&
                methodArea.indexOf("String") >= 0 &&
                methodArea.indexOf("[]") >= 0) {

                int classIndex = partProgramCode.lastIndexOf(classString);
                //System.out.println(classIndex);
                if (classIndex > -1) {
                    partProgramCode = partProgramCode.substring(classIndex + classString.length()).trim();
                    int classNameIndex = partProgramCode.indexOf(" ");
                    //System.out.println(classNameIndex);
                    if (classNameIndex > -1) {
                        String mainMethodCall = partProgramCode.substring(0, classNameIndex).trim() + ".main(new String[0]);";
                        mainMethodCall = ECodeUtilities.replace(mainMethodCall, "{", "");
                        //System.out.println(mainMethodCall);
                        return mainMethodCall;
                    }
                } else {
                    if (partProgramCode.startsWith("class ")) {
                        partProgramCode = partProgramCode.substring(classIndex + "class ".length()).trim();
                        int classNameIndex = partProgramCode.indexOf(" ");
                        //System.out.println(classNameIndex);
                        if (classNameIndex > -1) {
                            String mainMethodCall = partProgramCode.substring(0, classNameIndex).trim() + ".main(new String[0]);";
                            mainMethodCall = ECodeUtilities.replace(mainMethodCall, "{", "");
                            //System.out.println(mainMethodCall);
                            return mainMethodCall;
                        }
                    }
                }
            }
            methodIndex =  commentsRemoved.indexOf(mainMethod, methodIndex + 1);
            //System.out.println(methodIndex);
        }

        mainMethod="static void main(";

        methodIndex = commentsRemoved.indexOf(mainMethod);
        //System.out.println(methodIndex);
        while (methodIndex > -1) {
            int parenthesisIndex = commentsRemoved.indexOf(")", methodIndex);

            //System.out.println("" + (methodIndex + mainMethod.length()));
            //System.out.println("" + parenthesisIndex);

            if (commentsRemoved.substring(methodIndex +
                                          mainMethod.length(),
                                          parenthesisIndex).trim().length() == 0) {

                String partProgramCode = commentsRemoved.substring(0, methodIndex);
                int classIndex = partProgramCode.lastIndexOf(classString);
                //System.out.println(classIndex);
                if (classIndex > -1) {
                    partProgramCode = partProgramCode.substring(classIndex + classString.length()).trim();
                    int classNameIndex = partProgramCode.indexOf(" ");
                    //System.out.println(classNameIndex);
                    if (classNameIndex > -1) {
                        String mainMethodCall = partProgramCode.substring(0, classNameIndex).trim() + ".main();";
                        mainMethodCall = ECodeUtilities.replace(mainMethodCall, "{", "");
                        //System.out.println(mainMethodCall);
                        return mainMethodCall;
                    }
                } else {
                    if (partProgramCode.startsWith("class ")) {
                        partProgramCode = partProgramCode.substring(classIndex + "class ".length()).trim();
                        int classNameIndex = partProgramCode.indexOf(" ");
                        //System.out.println(classNameIndex);
                        if (classNameIndex > -1) {
                            String mainMethodCall = partProgramCode.substring(0, classNameIndex).trim() + ".main();";
                            mainMethodCall = ECodeUtilities.replace(mainMethodCall, "{", "");
                            //System.out.println(mainMethodCall);
                            return mainMethodCall;
                        }

                    }
                }
            }
            methodIndex = commentsRemoved.indexOf(mainMethod, methodIndex + 1);
            //System.out.println(methodIndex);
        }
        return null;
    }

    public String removeComments(String programCode) {

        String lineComment = "//";
        String beginningComment = "/*";
        String endingComment = "*/";

        int index = programCode.indexOf(beginningComment);

        while (index > -1) {
            int endIndex = programCode.indexOf(endingComment, index);
            programCode = programCode.substring(0, index) +
            programCode.substring(endIndex, programCode.length());
            index = programCode.indexOf(beginningComment);
        }

        index = programCode.indexOf(lineComment);

        while (index > -1) {
            int endIndex = programCode.indexOf('\n', index);
            programCode = programCode.substring(0, index) +
            programCode.substring(endIndex, programCode.length());
            index = programCode.indexOf(lineComment);
        }

        return programCode;

    }

    public void showErrorMessage(String e) {
        errorJEditorPane.setText(e);
        changeTheatrePane(errorViewer);
    }


    /**
     * Show the error message of the exception in the theatre pane.
     *
     * @param   e   The exception that is wanted to show.
     */
    public void showErrorMessage(Exception e) {
        showErrorMessage(e.toString());
    }


    public void showErrorMessage(InterpreterError e) {

        pauseButton.setEnabled(false);
        errorOccured = true;
        showErrorMessage(e.getMessage());

        editButton.setEnabled(true);
        stepButton.setEnabled(false);
        playButton.setEnabled(false);
        pauseButton.setEnabled(false);
        rewindButton.setEnabled(true);

        String[] s1 = {"Edit","Rewind"};
        setEnabledMenuItems(true, s1);
        String[] s2 = { "Step","Play","Pause" };
        setEnabledMenuItems(false, s2);

        Component c = codeNest.getLeftComponent();

        if (e.getHighlight() != null) {

            if (c instanceof CodeEditor) {
                ((CodeEditor)c).highlight(e.getHighlight());
            }

            if (c instanceof CodePane) {
                ((CodePane)c).highlightStatement(e.getHighlight());
            }
        }
    }

    public void setEnabledMenuItems(boolean enabled, String[] menuItems) {
        for (int i = 0; i < animationMenuItems.size(); i++) {
            JMenuItem jmi = (JMenuItem) animationMenuItems.elementAt(i);
            if (jmi != null) {
                for (int j = 0; j < menuItems.length; j++) {
                    if (menuItems[j].equals(jmi.getText())) {
                        jmi.setEnabled(enabled);
                    }
                }
            }
        }
    }

    /**
     * Changes the user interface when the "Compile" button is pressed.
     * Rewinds the animation.
     */
    public void enterAnimate() {

        changeCodePane(codePane);
        enableWidgets(editWidgets.elements(), false);
        enableWidgets(animWidgets.elements(), true);

        rewindAnimation();
    }


    /**
     * Changes the user interface when the "Step" button is pressed.
     * Calls jeliot.step() method.
     *
     * @see jeliot.Jeliot#step()
     */
    void stepAnimation() {

        stepButton.setEnabled(false);
        playButton.setEnabled(false);
        pauseButton.setEnabled(true);
        rewindButton.setEnabled(false);
        editButton.setEnabled(false);

        String[] s1 = {"Pause"};
        setEnabledMenuItems(true, s1);
        String[] s2 = { "Step","Play","Rewind","Edit","Run until..."};
        setEnabledMenuItems(false, s2);

        jeliot.step();
    }

    /**
     * Changes the user interface when the "Play" button is pressed.
     * Calls jeliot.play() method.
     *
     * @see jeliot.Jeliot#play()
     */
    void playAnimation() {

        stepButton.setEnabled(false);
        playButton.setEnabled(false);
        pauseButton.setEnabled(true);
        rewindButton.setEnabled(false);
        editButton.setEnabled(false);

        String[] s1 = {"Pause"};
        setEnabledMenuItems(true, s1);
        String[] s2 = { "Step","Play","Rewind","Edit","Run until..."};
        setEnabledMenuItems(false, s2);

        jeliot.play();
    }


    /**
     * Changes the user interface when the "Pause" button is pressed.
     * Calls jeliot.pause() method.
     *
     * @see jeliot.Jeliot#pause()
     */
    public void pauseAnimation() {

        stepButton.setEnabled(true);
        playButton.setEnabled(true);
        pauseButton.setEnabled(false);
        rewindButton.setEnabled(true);
        editButton.setEnabled(true);

        String[] s1 = {"Pause"};
        setEnabledMenuItems(false, s1);
        String[] s2 = { "Step","Play","Rewind","Edit","Run until..."};
        setEnabledMenuItems(true, s2);

        jeliot.pause();
    }


    /**
     * Changes the user interface when the "Resume" button is pressed.
     */
    public void resumeAnimation() {

        stepButton.setEnabled(false);
        playButton.setEnabled(false);
        pauseButton.setEnabled(true);
        rewindButton.setEnabled(false);
        editButton.setEnabled(false);

        String[] s1 = { "Pause" };
        setEnabledMenuItems(true, s1);
        String[] s2 = { "Step","Play","Rewind","Edit","Run until..."};
        setEnabledMenuItems(false, s2);
    }

    /**
     * Changes the user interface when the animation is freezed.
     */
    public void freezeAnimation() {

        stepButton.setEnabled(false);
        playButton.setEnabled(false);
        pauseButton.setEnabled(false);
        rewindButton.setEnabled(false);
        editButton.setEnabled(true);

        String[] s1 = { "Edit" };
        setEnabledMenuItems(true, s1);
        String[] s2 = { "Step","Play","Rewind","Pause", "Run until..."};
        setEnabledMenuItems(false, s2);
    }

    /**
     * Changes the user interface when the "Rewind" button is pressed.
     * Calls jeliot.rewind() method.
     * Calls theatre.repaint() method
     *
     * @see jeliot.Jeliot#rewind()
     * @see jeliot.theatre.Theatre#repaint()
     */
    void rewindAnimation() {

        errorOccured = false;

        jeliot.recompile();

/*
        try {
            Thread.sleep(25);
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }
*/

        stepButton.setEnabled(true);
        playButton.setEnabled(true);
        pauseButton.setEnabled(false);
        rewindButton.setEnabled(false);

        String[] s1 = { "Step","Play", "Run until..." };
        setEnabledMenuItems(true, s1);
        String[] s2 = { "Rewind","Pause"};
        setEnabledMenuItems(false, s2);

        jeliot.rewind();
        theatre.repaint();
    }

    /**
     * Changes the user interface when the animation is finished.
     */
    public void animationFinished() {
        if (!errorOccured) {

            stepButton.setEnabled(false);
            playButton.setEnabled(false);
            pauseButton.setEnabled(false);
            rewindButton.setEnabled(true);
            editButton.setEnabled(true);

            String[] s1 = { "Edit","Rewind" };
            setEnabledMenuItems(true, s1);
            String[] s2 = { "Step","Play","Pause","Run until..."};
            setEnabledMenuItems(false, s2);

        } else {

            editButton.setEnabled(true);
            stepButton.setEnabled(false);
            playButton.setEnabled(false);
            pauseButton.setEnabled(false);
            rewindButton.setEnabled(true);

            String[] s1 = {"Edit","Rewind"};
            setEnabledMenuItems(true, s1);
            String[] s2 = { "Step","Play","Pause","Run until..."};
            setEnabledMenuItems(false, s2);
        }
    }

    public boolean showMessagesInDialogs() {
        return showMessagesInDialogs;
    }

    /**
     * Writes the outputted string to the output console.
     *
     * @param   str String for output.
     */
    public void output(String str) {
        //System.out.println("This is output: " + str);
        outputConsole.append(str);
    }

    public void runUntil() {
        String inputValue = JOptionPane.showInputDialog("Run until the line", new Integer(0));
        int lineNumber = 0;

        try {
            lineNumber = Integer.parseInt(inputValue);
        } catch (Exception ex) {}

        if (lineNumber > 0) {
            jeliot.runUntil(lineNumber);
            previousSpeed = speedSlider.getValue();
            speedSlider.setValue(speedSlider.getMaximum());
            SwingUtilities.invokeLater(new Runnable() {
                                           public void run() {
                                               playButton.doClick();
                                           }
                                       });

        }
    }

    public void runUntilDone() {
        speedSlider.setValue(previousSpeed);
        SwingUtilities.invokeLater(new Runnable() {
                                       public void run() {
                                           pauseButton.doClick();
                                       }
                                   });
    }

}
