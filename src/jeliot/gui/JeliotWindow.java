package jeliot.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jeliot.FeatureNotImplementedException;
import jeliot.Jeliot;
import jeliot.calltree.TreeDraw;
import jeliot.mcode.InterpreterError;
import jeliot.mcode.MCodeUtilities;
import jeliot.theater.AnimationEngine;
import jeliot.theater.ImageLoader;
import jeliot.theater.PanelController;
import jeliot.theater.Theater;
import jeliot.tracker.Tracker;
import jeliot.util.ResourceBundles;
import jeliot.util.UserPropertyResourceBundle;

/**
 * The main window of the Jeliot 3.
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class JeliotWindow {

    /**
     * The resource bundle for gui package
     */
    static private UserPropertyResourceBundle propertiesBundle = ResourceBundles.getGuiUserPropertyResourceBundle();

    /**
     * The resource bundle for gui package
     */
    static private ResourceBundle messageBundle = ResourceBundles.getGuiMessageResourceBundle();

    /**
     * The version information about Jeliot from name and version from the
     * resource bundle.
     */
    private String jeliotVersion = propertiesBundle.getString("name") + " " + propertiesBundle.getString("version");

    /**
     * Should the messages during the program visualization be shown as message
     * dialogs.
     */
    private boolean showMessagesInDialogs = false;

    /**
     * True if an error has occured during the execution and false if no error
     * was encountered.
     */
    private boolean errorOccured = false;

    /**
     * The about window of Jeliot 3.
     */
    private AboutWindow aw = null;

    /**
     * The help window of Jeliot 3.
     */
    private HelpWindow hw = null;

    /** Color for highlighting a tab name when its content has changed. */
    private Color highlightTabColor = new Color(Integer.decode(
            propertiesBundle.getString("color.tab.foreground.highlight")).intValue());

    /** Color for highlighting a tab name when its content has changed. */
    private Color normalTabColor = new Color(Integer.decode(
            propertiesBundle.getString("color.tab.foreground.normal")).intValue());

    /** The previous speed before the run until line is set. */
    private int previousSpeed;

    /** The frame in which all the action goes on. */
    private JFrame frame;

    /** Tabbed pane to show several visualization of the one program executions. */
    private JTabbedPane tabbedPane = new JTabbedPane();
    {
        tabbedPane.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                highlightTabTitle(false, tabbedPane.getSelectedIndex());
            }
        });
    }

    /** TreeDraw view that shows the call tree of the program execution. */
    private TreeDraw callTree;

    /** The theatre in which the programs are animated. */
    private Theater theatre;

    private JScrollPane theaterScrollPane;

    /** The animation engine that that will animate the code. */
    private AnimationEngine engine;

    /** The main program. */
    private Jeliot jeliot;

    /** The code pane where the code is shown during the animation. */
    private CodePane2 codePane;

    /** The code editor in which the users can write their code. */
    private CodeEditor2 editor;

    //private CodeEditor editor = new CodeEditor();

    /** The pane that splits the window. */
    private JSplitPane codeNest;

    /** The step button. */
    private JButton stepButton;

    /** The play button. */
    private JButton playButton;

    /** The pause button. */
    private JButton pauseButton;

    /** The rewind button. */
    private JButton rewindButton;

    /** The edit button. */
    private JButton editButton;

    /** The compile button. */
    private JButton compileButton;

    /** Slider tha controls the animation speed. */
    private JSlider speedSlider;

    /** In this text area will come the output of the user-made programs. */
    private JTextArea outputConsole;

    /**
     * Menu items that should be either enabled or disabled when the animation
     * mode is entered or exited respectively.
     */
    private Vector animationMenuItems = new Vector();

    /** This ImageLoader will load all the images. */
    private ImageLoader iLoad;

    /** This variable will control the panels. */
    private PanelController panelController;

    /** Showing the history of the program execution */
    private HistoryView hv;

    /** If a method call should be asked from the user */
    private boolean askForMethod = false;

    /** If animation is running until certain line */
    private boolean runningUntil = false;

    /**
     * This JEditorPane errorJEditorPane will show the error messages for the
     * users.
     */
    private JEditorPane errorJEditorPane = new JEditorPane();
    {
        errorJEditorPane.setEditable(false);
        errorJEditorPane.setContentType("text/html");
        errorJEditorPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createLoweredBevelBorder(), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
    }

    /**
     * Scroll pane that provides the scroll bars for the error pane's editor
     */
    private JScrollPane errorPane = new JScrollPane(errorJEditorPane);
    {
        errorPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        //errorPane.setPreferredSize(new Dimension(250, 145));
    }

    /**
     * This JPanel errorViewer will help the showing of the error messages for
     * the users.
     */
    private JPanel errorViewer = new JPanel() {

        private Image backImage;

        public void paintComponent(Graphics g) {
            Dimension d = getSize();
            int w = d.width;
            int h = d.height;
            if (backImage == null) {
                backImage = iLoad.getLogicalImage("image.panel");
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
        errorViewer.setBorder(BorderFactory.createEmptyBorder(12, 12, 5, 12));
        errorViewer.setLayout(new BorderLayout());
        errorViewer.add("Center", errorPane);
        JPanel bp = new JPanel();
        bp.setOpaque(false);

        JButton ok = new JButton(messageBundle.getString("button.ok"));
        ok.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                changeTheatrePane(tabbedPane);
                //editButton.doClick();
            }
        });
        bp.add(ok);
        errorViewer.add("South", bp);
    }

    /**
     * Action listeners for the step- button.
     */
    private ActionListener stepAction = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            Tracker.writeToFile("StepButton", System.currentTimeMillis());
            stepAnimation();
        }
    };

    /**
     * Action listeners for the play- button.
     */
    private ActionListener playAction = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            Tracker.writeToFile("PlayButton", System.currentTimeMillis());
            playAnimation();
        }
    };

    /**
     * Action listeners for the pause- button.
     */
    private ActionListener pauseAction = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            Tracker.writeToFile("PauseButton", System.currentTimeMillis());
            pauseAnimation();
        }
    };

    /**
     * Action listeners for the rewind- button.
     */
    private ActionListener rewindAction = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            Tracker.writeToFile("RewindButton", System.currentTimeMillis());
            rewindAnimation();
        }
    };

    /**
     * Action listener for the exit.
     */
    private ActionListener exit = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            WindowListener[] wl = frame.getWindowListeners();
            int n = wl.length;
            for (int i = 0; i < n; i++) {
                wl[i].windowClosing(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
            /*
             if (editor.isChanged()) {
             int n = JOptionPane.showConfirmDialog(frame, bundle
             .getString("quit.without.saving.message"), bundle
             .getString("quit.without.saving.title"),
             JOptionPane.YES_NO_OPTION);
             if (n == JOptionPane.YES_OPTION) {
             editor.saveProgram();
             }
             }

             Properties prop = System.getProperties();
             File f = new File(udir);
             prop.put("user.dir", f.toString());
             Jeliot.close();
             
             if (Jeliot.isnoSystemExit()) {
             frame.dispose();
             } else {
             //frame.dispose();
             System.exit(0);
             }
             */
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

    /**
     * The user directory.
     */
    private String udir;

    /** Control panel */
    private JComponent conPan;

    /**
     * Assigns the values of the parameters in the object values. Constructs the
     * panelController with theatre and iload.
     * 
     * @param jeliot
     *            The main program.
     * @param codePane
     *            The pane where all the code is shown while animated.
     * @param theatre
     *            The theatre where all the code is animated.
     * @param engine
     *            The engine that animates the code.
     * @param iLoad
     *            The imageloader that loads all the images.
     * @param udir
     *            The user directory
     */
    public JeliotWindow(Jeliot jeliot, CodePane2 codePane, Theater theatre, AnimationEngine engine,
            ImageLoader iLoad, String udir, TreeDraw td, HistoryView hv) {

        this.jeliot = jeliot;
        this.codePane = codePane;
        this.theatre = theatre;
        this.engine = engine;
        this.iLoad = iLoad;
        this.udir = udir;
        this.callTree = td;
        this.hv = hv;

        this.panelController = new PanelController(theatre, iLoad);
        //this.editor = new CodeEditor(this.udir);
        this.editor = new CodeEditor2(this.jeliotVersion, this.udir);
        this.frame = new JFrame(jeliotVersion);
    }

    /**
     * Initializes the JFrame frame. Sets up all the basic things for the
     * window. (Panels, Panes, Menubars) Things for debugging.
     */
    public void setUp() {
        try {
            this.theaterScrollPane = new JScrollPane(this.theatre);
            //theaterScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            //theaterScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

            this.tabbedPane.addTab(messageBundle.getString("tab.title.theater"), theaterScrollPane);
            this.tabbedPane.setMnemonicAt(0, KeyEvent.VK_T);

            if (!jeliot.isExperiment()) {
                this.tabbedPane.addTab(messageBundle.getString("tab.title.call_tree"), callTree
                        .getComponent());
                this.tabbedPane.setMnemonicAt(1, KeyEvent.VK_E);

                this.tabbedPane.addTab(messageBundle.getString("tab.title.history"), hv);
                this.tabbedPane.setMnemonicAt(2, KeyEvent.VK_Y);
            }

            this.frame.setIconImage(iLoad.getImage(propertiesBundle.getString("image.jeliot_icon")));

            frame.setJMenuBar(makeMenuBar());
            editor.setMasterFrame(frame);

            JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editor, tabbedPane);
            pane.setOneTouchExpandable(true);
            codeNest = pane;

            Dimension minimumSize = new Dimension(0, 0);
            codePane.setMinimumSize(minimumSize);
            editor.setMinimumSize(minimumSize);

            JPanel bottomPane = new JPanel(new BorderLayout());
            conPan = makeControlPanel();
            bottomPane.add("West", conPan);

            OutputConsole oc = new OutputConsole(conPan);
            this.outputConsole = oc;

            bottomPane.add("Center", oc.container);

            JPanel rootPane = new JPanel(new BorderLayout());
            rootPane.add("Center", pane);
            rootPane.add("South", bottomPane);

            frame.setContentPane(rootPane);
            
            //Maximize the window.
            
            /*
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            //frame.setSize(screenSize.width, screenSize.height - 30);
            frame.setSize(800, 600);
            Dimension frameSize = frame.getSize();
            if (frameSize.height > screenSize.height)
                frameSize.height = screenSize.height;
            if (frameSize.width > screenSize.width)
                frameSize.width = screenSize.width;

            frame.setLocation((screenSize.width - frameSize.width) / 2,
                    (screenSize.height - frameSize.height) / 2);
            */
            
            frame.addWindowListener(new WindowAdapter() {

                public void windowClosing(WindowEvent e) {
                    if (editor.isChanged()) {
                        int n = JOptionPane.showConfirmDialog(frame, messageBundle
                                .getString("quit.without.saving.message"), messageBundle
                                .getString("quit.without.saving.title"), JOptionPane.YES_NO_OPTION);
                        if (n == JOptionPane.YES_OPTION) {
                            editor.saveProgram();
                        }
                    }

                    Properties prop = System.getProperties();
                    File f = new File(udir);
                    prop.put("user.dir", f.toString());
                    Jeliot.close();

                    if (Jeliot.isnoSystemExit()) {
                        frame.dispose();
                    } else {
                        //frame.dispose();
                        System.exit(0);
                    }
                }
            });

            enterEditTrue();
            pane.setDividerLocation((jeliot.isExperiment()) ? 512 : 300);

            //TheatrePopup popup = new TheatrePopup();
            //theatre.addMouseListener(popup);
            //theatre.addMouseMotionListener(popup);

            hw = new HelpWindow(iLoad.getImage(propertiesBundle.getString("image.jeliot_icon")), udir);
            aw = new AboutWindow(iLoad.getImage(propertiesBundle.getString("image.jeliot_icon")), udir);

            frame.pack();
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.show();
            //editor.requestFocus();
            //System.out.println(theatre.getSize());
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public JFrame getFrame() {
        return frame;
    }

    /**
     * Makes and returns the menubar for the main frame.
     * Things for debugging.
     * 
     * @return The menubar for the main frame.
     */
    private JMenuBar makeMenuBar() {

        JMenuBar menuBar = new JMenuBar();

        JMenu menu, submenu;
        JMenuItem menuItem;
        JCheckBoxMenuItem cbMenuItem;
        JRadioButtonMenuItem rbMenuItem;

        //a group of JMenuItems
        JMenu programMenu = editor.makeProgramMenu();

        menuItem = new JMenuItem(messageBundle.getString("menu.program.exit"));
        menuItem.setMnemonic(KeyEvent.VK_X);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
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

        JMenu[] jm = { controlMenu, animationMenu};
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

        JMenu menu = new JMenu(messageBundle.getString("menu.help"));
        menu.setMnemonic(KeyEvent.VK_H);
        JMenuItem menuItem;

        menuItem = new JMenuItem(messageBundle.getString("menu.help.help"));
        menuItem.setMnemonic(KeyEvent.VK_H);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (hw != null) {
                    //hw.pack();
                    hw.reload();
                    hw.show();
                }
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(messageBundle.getString("menu.help.about"));
        menuItem.setMnemonic(KeyEvent.VK_B);
        //        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        //        KeyEvent.VK_M, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                if (aw != null) {
                    //aw.pack();
                    aw.reload();
                    aw.show();
                }
            }
        });
        menu.add(menuItem);

        return menu;
    }

    /**
     * Menu with the VCR commands
     */
    private JMenu makeAnimationMenu() {

        JMenu menu = new JMenu(messageBundle.getString("menu.animation"));
        menu.setMnemonic(KeyEvent.VK_A);
        JMenuItem menuItem;

        menuItem = new JMenuItem(messageBundle.getString("menu.animation.pause"));
        menuItem.setMnemonic(KeyEvent.VK_U);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                pauseButton.doClick();
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(messageBundle.getString("menu.animation.play"));
        menuItem.setMnemonic(KeyEvent.VK_P);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                playButton.doClick();
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(messageBundle.getString("menu.animation.rewind"));
        menuItem.setMnemonic(KeyEvent.VK_R);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                rewindButton.doClick();
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(messageBundle.getString("menu.animation.step"));
        menuItem.setMnemonic(KeyEvent.VK_S);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                stepButton.doClick();
            }
        });
        menu.add(menuItem);

        final JCheckBoxMenuItem cbmenuItem = new JCheckBoxMenuItem(messageBundle
                .getString("menu.animation.pause_on_message"), showMessagesInDialogs);
        cbmenuItem.setMnemonic(KeyEvent.VK_D);
        cbmenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
        cbmenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                showMessagesInDialogs = cbmenuItem.getState();
            }
        });
        menu.add(cbmenuItem);

        menuItem = new JMenuItem(messageBundle.getString("menu.animation.faster"));
        menuItem.setMnemonic(KeyEvent.VK_F);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                speedSlider.setValue(speedSlider.getValue() + 1);
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(messageBundle.getString("menu.animation.slower"));
        menuItem.setMnemonic(KeyEvent.VK_L);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                speedSlider.setValue(speedSlider.getValue() - 1);
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(messageBundle.getString("menu.animation.run_until"));
        menuItem.setMnemonic(KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                runUntil();
            }
        });
        menu.add(menuItem);

        return menu;
    }

    /**
     * Menu with the commands to enter to animate and edit.
     */
    private JMenu makeControlMenu() {

        JMenu menu = new JMenu(messageBundle.getString("menu.control"));
        menu.setMnemonic(KeyEvent.VK_C);
        JMenuItem menuItem;

        menuItem = new JMenuItem(messageBundle.getString("menu.control.edit"));
        menuItem.setMnemonic(KeyEvent.VK_E);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                editButton.doClick();
            }
        });
        menu.add(menuItem);

        animWidgets.addElement(menuItem);

        menuItem = new JMenuItem(messageBundle.getString("menu.control.compile"));
        menuItem.setMnemonic(KeyEvent.VK_M);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                compileButton.doClick();
            }
        });
        menu.add(menuItem);

        editWidgets.addElement(menuItem);

        final JCheckBoxMenuItem cbmenuItem = new JCheckBoxMenuItem(messageBundle
                .getString("menu.control.ask_for_method"), false);
        cbmenuItem.setMnemonic(KeyEvent.VK_F);
        cbmenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
        cbmenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                askForMethod = cbmenuItem.getState();
            }
        });
        menu.add(cbmenuItem);

        return menu;
    }

    /**
     * Makes the control buttons for the control panel.
     * 
     * @param label
     *            The label for the button.
     * @param iconName
     *            The icon name for the icon on the button.
     * @return The control button for control panel.
     */
    private JButton makeControlButton(String label, String iconName) {

        URL imageURL = this.getClass().getClassLoader().getResource(
                propertiesBundle.getString("directory.images") + iconName);
        if (imageURL == null) {
            imageURL = Thread.currentThread().getContextClassLoader().getResource(
                    propertiesBundle.getString("directory.images") + iconName);
        }
        ImageIcon icon = new ImageIcon(imageURL);
        //new ImageIcon(bundle.getString("directory.images")+ iconName);
        JButton b = new JButton(label, icon);
        b.setVerticalTextPosition(AbstractButton.BOTTOM);
        b.setHorizontalTextPosition(AbstractButton.CENTER);
        //  b.setBorder(BorderFactory.createEtchedBorder());
        b.setMargin(new Insets(0, 0, 0, 0));
        return b;
    }

    /**
     * Constructs the control panel. Uses makeControlButton(String, String)
     * 
     * @return The constructed control panel.
     * @see #makeControlButton(String, String)
     */
    private JPanel makeControlPanel() {

        editButton = makeControlButton(messageBundle.getString("button.edit"), propertiesBundle
                .getString("image.edit_icon"));
        compileButton = makeControlButton(messageBundle.getString("button.compile"), propertiesBundle
                .getString("image.compile_icon"));

        editButton.setMnemonic(KeyEvent.VK_E);
        compileButton.setMnemonic(KeyEvent.VK_M);

        editButton.setMargin(new Insets(0, 2, 0, 2));
        compileButton.setMargin(new Insets(0, 2, 0, 2));

        editButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Tracker.writeToFile("EditButton", System.currentTimeMillis());

                enterEdit();
            }
        });

        compileButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Tracker.writeToFile("AnimationButton", System.currentTimeMillis());

                tryToEnterAnimate();
            }
        });

        editWidgets.addElement(compileButton);
        animWidgets.addElement(editButton);

        JPanel statePane = new JPanel();
        statePane.setLayout(new GridLayout(1, 2));
        statePane.add(editButton);
        statePane.add(compileButton);

        // create animation control buttons
        // stepButton = makeControlButton("Step", "stepicon.gif");
        // playButton = makeControlButton("Play", "playicon.gif");
        // pauseButton = makeControlButton("Pause", "pauseicon.gif");
        // rewindButton = makeControlButton("Rewind", "rewindicon.gif");

        stepButton = makeControlButton(messageBundle.getString("button.step"), propertiesBundle
                .getString("image.step_icon"));
        stepButton.setMnemonic(KeyEvent.VK_S);
        playButton = makeControlButton(messageBundle.getString("button.play"), propertiesBundle
                .getString("image.play_icon"));
        playButton.setMnemonic(KeyEvent.VK_P);
        pauseButton = makeControlButton(messageBundle.getString("button.pause"), propertiesBundle
                .getString("image.pause_icon"));
        pauseButton.setMnemonic(KeyEvent.VK_U);
        rewindButton = makeControlButton(messageBundle.getString("button.rewind"), propertiesBundle
                .getString("image.rewind_icon"));
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

        speedSlider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                int volume = speedSlider.getValue();
                engine.setVolume((double) (volume * 50.0));
                Tracker.writeToFile("Slider", "" + volume, System.currentTimeMillis());
            }
        });

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

        URL imageURL = this.getClass().getClassLoader().getResource(
                propertiesBundle.getString("directory.images") + propertiesBundle.getString("image.jeliot"));
        if (imageURL == null) {
            imageURL = Thread.currentThread().getContextClassLoader().getResource(
                    propertiesBundle.getString("directory.images") + propertiesBundle.getString("image.jeliot"));
        }

        JLabel jicon = new JLabel(new ImageIcon(imageURL));
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
        JLabel label = new JLabel(messageBundle.getString("label.animation_speed1"));
        pl.setConstraints(label, c);
        p.add(label);

        c.gridy = 2;
        label = new JLabel(messageBundle.getString("label.animation_speed2"));
        pl.setConstraints(label, c);
        p.add(label);

        c.gridx = 2;
        c.gridy = 1;
        c.gridheight = 2;
        pl.setConstraints(speedSlider, c);
        p.add(speedSlider);

        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 2, 2, 10), BorderFactory.createEtchedBorder()));

        return p;
    }

    /**
     * Enables or disables the components depending on the second parameter.
     * 
     * @param enum
     *            The collection of components that are set enabled or disabled
     *            as the boolean enable is set.
     * @param enable
     *            Sets wheter the components are enabled or disabled.
     */
    private void enableWidgets(Enumeration enum, boolean enable) {

        while (enum.hasMoreElements()) {
            Component comp = (Component) enum.nextElement();
            comp.setEnabled(enable);
        }
    }

    /**
     * Changes the code pane in the codeNest. Sets inside the codeNest the new
     * code pane.
     * 
     * @param comp
     *            The component that is changes in the code pane.
     */
    private void changeCodePane(JComponent comp) {

        int loc = codeNest.getDividerLocation();
        codeNest.setLeftComponent(comp);
        codeNest.setDividerLocation(loc);
        conPan.requestFocus();
    }

    /**
     * Changes the theatre pane in the codeNest. Sets inside the codeNest the
     * new theatre pane.
     * 
     * @param comp
     *            The component that is changes in the theatre pane.
     */
    private void changeTheatrePane(JComponent comp) {

        int loc = codeNest.getDividerLocation();
        codeNest.setRightComponent(comp);
        codeNest.setDividerLocation(loc);
        conPan.requestFocus();
    }

    /**
     * This method is called when user clicks the "Edit" button.
     */
    void enterEdit() {

        enableWidgets(editWidgets.elements(), true);
        enableWidgets(animWidgets.elements(), false);

        tabbedPane.setSelectedIndex(0);
        changeTheatrePane(tabbedPane);
        unhighlightTabTitles();
        callTree.initialize();
        if (runningUntil) {
            jeliot.runUntil(0);
            runUntilFinished();
        }
        panelController.slide(false, new Runnable() {

            public void run() {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        enterEditTrue();
                    }
                });
            }
        }).start();
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
     * @return The program code from the CodeEditor -object.
     */
    public String getProgram() {
        return editor.getProgram();
    }

    /**
     *
     */
    void tryToEnterAnimate() {
        tryToEnterAnimate(null);
    }

    /**
     * Called when the user pushes the "Compile" button.
     * Gets the code from the CodeEditor2 -object.
     * Sends it to "compilation".
     */
    public void tryToEnterAnimate(String methodCall) {

        // Jeliot 3
        if (editor.isChanged() && editor.IsSaveAutomatically()) {
            editor.saveProgram();
        }

        try {
            try {

                enableWidgets(editWidgets.elements(), false);

                String programCode = editor.getProgram();

                if (methodCall == null) {
                    methodCall = findMainMethodCall(programCode);
                    if (askForMethod) {
                        methodCall = ((methodCall != null) ? methodCall : null);
                        String inputValue = JOptionPane.showInputDialog(messageBundle
                                .getString("dialog.ask_for_method"), methodCall);
                        if (inputValue != null && !inputValue.trim().equals("")) {
                            methodCall = inputValue + ";";
                        }
                    }
                }

                if (methodCall != null) {

                    //Reader r = new BufferedReader(new
                    // StringReader(programCode));
                    //jeliot.createLauncher(r);
                    //Reader s = new BufferedReader(new
                    // StringReader(methodCall));

                    jeliot.setSourceCode(programCode, methodCall);

                    changeTheatrePane(tabbedPane);
                    tabbedPane.setSelectedIndex(0);

                    panelController.slide(true, new Runnable() {

                        public void run() {
                            SwingUtilities.invokeLater(new Runnable() {

                                public void run() {
                                    enterAnimate();
                                    //Buttons are enables just after the animation not before
                                    enableWidgets(animWidgets.elements(), true);
                                    pauseButton.setEnabled(false);
                                    rewindButton.setEnabled(false);
                                }
                            });
                        }
                    }).start();

                } else {
                    errorJEditorPane.setText(messageBundle.getString("main_method_not_found.exception"));
                    changeTheatrePane(errorViewer);

                    enableWidgets(editWidgets.elements(), true);
                    enableWidgets(animWidgets.elements(), false);
                }
            } catch (FeatureNotImplementedException e) {
                showErrorMessage(e);
                return;
            }
            /*
             * catch (InterpreterException e) { showErrorMessage(e); return; }
             * catch (SemanticException e) { showErrorMessage(e); return; }
             * catch (SyntaxErrorException e) { showErrorMessage(e); return; }
             */
        } catch (Exception e) {
            editButton.doClick();
            e.printStackTrace();
        }
    }

    /*
     * public String replaceChar(String from, char c, String with) {
     * 
     * int index = from.indexOf(c); while(index != -1) { from =
     * from.substring(0,index) + with + from.substring(index+1,from.length());
     * index = from.indexOf(c); } return from; }
     */

    Pattern method1 = Pattern
            .compile("\\s+static\\s+void\\s+main\\s*\\(\\s*String[^,]*\\[\\s*\\][^,]*\\)");

    Pattern method2 = Pattern.compile("\\s+static\\s+void\\s+main\\s*\\(\\s*\\)");

    Pattern class1 = Pattern.compile("\\s+class\\s+");

    Pattern class2 = Pattern.compile("\\s");

    /**
     * Tries to find the main method declaration
     * from one of the classes.
     * 
     * @param programCode
     * @return
     */
    public String findMainMethodCall(String programCode) {

        String commentsRemoved = removeComments(programCode);
        commentsRemoved = MCodeUtilities.replace(commentsRemoved, "\n", " ");
        commentsRemoved = MCodeUtilities.replace(commentsRemoved, "\r", " ");
        commentsRemoved = MCodeUtilities.replace(commentsRemoved, "\t", " ");
        commentsRemoved = " " + commentsRemoved;

        //System.out.println(p.pattern());
        String[] method = method1.split(commentsRemoved, 2);
        //String[] method = programCode.split("\\s+static\\s+void\\s+main\\s*\\(\\s*String[^,]*\\[\\s*\\]\\s[^,]*\\)", 2);
        if (method.length > 1 && method[1].length() > 0) {
            //System.out.println(method[0]);
            //System.out.println(method[1]);
            String[] classes = class1.split(method[0]);
            String[] classNames = class2.split(classes[classes.length - 1]);
            String className = classNames[0].replace('{', ' ');
            className = className.trim();
            if (className.length() > 0) {
                //System.out.println(className + ".main(new String[0]);");
                return className + ".main(new String[0]);";
            }
        }

        method = method2.split(commentsRemoved, 2);

        if (method.length > 1 && method[1].length() > 0) {
            //System.out.println(method[0]);
            //System.out.println(method[1]);
            String[] classes = class1.split(method[0]);
            String[] classNames = class2.split(classes[classes.length - 1]);
            String className = classNames[0].replace('{', ' ');
            className = className.trim();
            if (className.length() > 0) {
                //System.out.println(className + ".main();");
                return className + ".main();";
            }
        }
        return null;

        /*
         * OLD VERSION WITHOUT REGEXPS. BUG IN HANDLING SPACES in ...main (...!
         * String mainMethod="static void main("; String classString = " class ";
         * 
         * int methodIndex = commentsRemoved.indexOf(mainMethod);
         * //System.out.println(methodIndex); while (methodIndex > -1) { int
         * parenthesisIndex = commentsRemoved.indexOf(")", methodIndex); String
         * partProgramCode = commentsRemoved.substring(0, parenthesisIndex);
         * String methodArea = commentsRemoved.substring(methodIndex,
         * parenthesisIndex);
         * 
         * if (methodArea.indexOf(",") < 0 && methodArea.indexOf("String") >= 0 &&
         * methodArea.indexOf("[]") >= 0) {
         * 
         * int classIndex = partProgramCode.lastIndexOf(classString);
         * //System.out.println(classIndex); if (classIndex > -1) {
         * partProgramCode = partProgramCode.substring(classIndex +
         * classString.length()).trim(); int classNameIndex =
         * partProgramCode.indexOf(" "); //System.out.println(classNameIndex);
         * if (classNameIndex > -1) { String mainMethodCall =
         * partProgramCode.substring(0, classNameIndex).trim() + ".main(new
         * String[0]);"; mainMethodCall = ECodeUtilities.replace(mainMethodCall,
         * "{", ""); //System.out.println(mainMethodCall); return
         * mainMethodCall; } } else { if (partProgramCode.startsWith("class ")) {
         * partProgramCode = partProgramCode.substring(classIndex + "class
         * ".length()).trim(); int classNameIndex = partProgramCode.indexOf("
         * "); //System.out.println(classNameIndex); if (classNameIndex > -1) {
         * String mainMethodCall = partProgramCode.substring(0,
         * classNameIndex).trim() + ".main(new String[0]);"; mainMethodCall =
         * ECodeUtilities.replace(mainMethodCall, "{", "");
         * //System.out.println(mainMethodCall); return mainMethodCall; } } } }
         * methodIndex = commentsRemoved.indexOf(mainMethod, methodIndex + 1);
         * //System.out.println(methodIndex); }
         * 
         * mainMethod="static void main(";
         * 
         * methodIndex = commentsRemoved.indexOf(mainMethod);
         * //System.out.println(methodIndex); while (methodIndex > -1) { int
         * parenthesisIndex = commentsRemoved.indexOf(")", methodIndex);
         * 
         * //System.out.println("" + (methodIndex + mainMethod.length()));
         * //System.out.println("" + parenthesisIndex);
         * 
         * if (commentsRemoved.substring(methodIndex + mainMethod.length(),
         * parenthesisIndex).trim().length() == 0) {
         * 
         * String partProgramCode = commentsRemoved.substring(0, methodIndex);
         * int classIndex = partProgramCode.lastIndexOf(classString);
         * //System.out.println(classIndex); if (classIndex > -1) {
         * partProgramCode = partProgramCode.substring(classIndex +
         * classString.length()).trim(); int classNameIndex =
         * partProgramCode.indexOf(" "); //System.out.println(classNameIndex);
         * if (classNameIndex > -1) { String mainMethodCall =
         * partProgramCode.substring(0, classNameIndex).trim() + ".main();";
         * mainMethodCall = ECodeUtilities.replace(mainMethodCall, "{", "");
         * //System.out.println(mainMethodCall); return mainMethodCall; } } else {
         * if (partProgramCode.startsWith("class ")) { partProgramCode =
         * partProgramCode.substring(classIndex + "class ".length()).trim(); int
         * classNameIndex = partProgramCode.indexOf(" ");
         * //System.out.println(classNameIndex); if (classNameIndex > -1) {
         * String mainMethodCall = partProgramCode.substring(0,
         * classNameIndex).trim() + ".main();"; mainMethodCall =
         * ECodeUtilities.replace(mainMethodCall, "{", "");
         * //System.out.println(mainMethodCall); return mainMethodCall; } } } }
         * methodIndex = commentsRemoved.indexOf(mainMethod, methodIndex + 1);
         * //System.out.println(methodIndex); } return null;
         */
    }

    /**
     * Removes the comments from the source code.
     * 
     * @param programCode
     *            the source code
     * @return the source code without comments
     */
    public String removeComments(String programCode) {

        String lineComment = "//";
        String beginningComment = "/*";
        String endingComment = "*/";

        int index = programCode.indexOf(beginningComment);

        while (index > -1) {
            int endIndex = programCode.indexOf(endingComment, index);
            programCode = programCode.substring(0, index)
                    + programCode.substring(endIndex, programCode.length());
            index = programCode.indexOf(beginningComment);
        }

        index = programCode.indexOf(lineComment);

        while (index > -1) {
            int endIndex = programCode.indexOf('\n', index);
            programCode = programCode.substring(0, index)
                    + programCode.substring(endIndex, programCode.length());
            index = programCode.indexOf(lineComment);
        }

        return programCode;

    }

    /**
     * Shows the given error message and sets the buttons and menuitems for
     * animation as disabled.
     * 
     * @param e
     *            the error message in String
     */
    public void showErrorMessage(String e) {
        errorOccured = true;

        pauseButton.setEnabled(false);
        editButton.setEnabled(true);
        stepButton.setEnabled(false);
        playButton.setEnabled(false);
        pauseButton.setEnabled(false);
        rewindButton.setEnabled(false);

        String[] s1 = { messageBundle.getString("menu.control.edit")};
        setEnabledMenuItems(true, s1);
        String[] s2 = { messageBundle.getString("menu.animation.step"),
                messageBundle.getString("menu.animation.play"),
                messageBundle.getString("menu.animation.rewind"),
                messageBundle.getString("menu.animation.run_until"),
                messageBundle.getString("menu.animation.pause")};
        setEnabledMenuItems(false, s2);

        errorJEditorPane.setText(e);
        changeTheatrePane(errorViewer);
    }

    /**
     * Show the error message of the exception in the theatre pane.
     * 
     * @param e
     *            The exception that is wanted to show.
     */
    public void showErrorMessage(Exception e) {
        showErrorMessage(e.toString());
    }

    /**
     * Shows the errormessage and highlights the source code.
     * 
     * @param e
     *            interpreter error that contains the error message and the
     *            highlighting information.
     * @see JeliotWindow#showErrorMessage(String)
     */
    public void showErrorMessage(InterpreterError e) {

        showErrorMessage(e.getMessage());

        Component c = codeNest.getLeftComponent();

        if (e.getHighlight() != null) {

            if (c instanceof CodeEditor2) {
                ((CodeEditor2) c).highlight(e.getHighlight());
            }

            if (c instanceof CodePane2) {
                ((CodePane2) c).highlightStatement(e.getHighlight());
            }
        }
    }

    /**
     * Sets the given menu items contained in the second parameter either
     * enabled or disabled depending the value of the first parameter
     * 
     * @param enabled
     *            if true means that the given menu items should be enabled if
     *            false the menu items should be disabled.
     * @param menuItems
     *            the menu items to be enabled or disabled.
     */
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
     * Changes the user interface when the "Compile" button is pressed. Rewinds
     * the animation.
     */
    public void enterAnimate() {
        //enableWidgets(editWidgets.elements(), false);
        //enableWidgets(animWidgets.elements(), true);
        changeCodePane(codePane);
        rewindAnimation();
    }

    /**
     * Changes the user interface when the "Step" button is pressed. Calls
     * jeliot.step() method.
     * 
     * @see jeliot.Jeliot#step()
     */
    void stepAnimation() {

        stepButton.setEnabled(false);
        playButton.setEnabled(false);
        pauseButton.setEnabled(true);
        rewindButton.setEnabled(false);
        editButton.setEnabled(false);

        String[] s1 = { messageBundle.getString("menu.animation.pause")};
        setEnabledMenuItems(true, s1);
        String[] s2 = { messageBundle.getString("menu.animation.step"),
                messageBundle.getString("menu.animation.play"),
                messageBundle.getString("menu.animation.rewind"), messageBundle.getString("menu.control.edit"),
                messageBundle.getString("menu.animation.run_until")};
        setEnabledMenuItems(false, s2);

        jeliot.step();
    }

    /**
     * Changes the user interface when the "Play" button is pressed. Calls
     * jeliot.play() method.
     * 
     * @see jeliot.Jeliot#play()
     */
    void playAnimation() {

        stepButton.setEnabled(false);
        playButton.setEnabled(false);
        pauseButton.setEnabled(true);
        rewindButton.setEnabled(false);
        editButton.setEnabled(false);

        String[] s1 = { messageBundle.getString("menu.animation.pause")};
        setEnabledMenuItems(true, s1);
        String[] s2 = { messageBundle.getString("menu.animation.step"),
                messageBundle.getString("menu.animation.play"),
                messageBundle.getString("menu.animation.rewind"), messageBundle.getString("menu.control.edit"),
                messageBundle.getString("menu.animation.run_until")};
        setEnabledMenuItems(false, s2);

        jeliot.play();
    }

    /**
     * Changes the user interface when the "Pause" button is pressed. Calls
     * jeliot.pause() method.
     * 
     * @see jeliot.Jeliot#pause()
     */
    public void pauseAnimation() {

        stepButton.setEnabled(true);
        playButton.setEnabled(true);
        pauseButton.setEnabled(false);
        rewindButton.setEnabled(true);
        editButton.setEnabled(true);

        String[] s1 = { messageBundle.getString("menu.animation.pause")};
        setEnabledMenuItems(false, s1);
        String[] s2 = { messageBundle.getString("menu.animation.step"),
                messageBundle.getString("menu.animation.play"),
                messageBundle.getString("menu.animation.rewind"), messageBundle.getString("menu.control.edit"),
                messageBundle.getString("menu.animation.run_until")};
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

        String[] s1 = { messageBundle.getString("menu.animation.pause")};
        setEnabledMenuItems(true, s1);
        String[] s2 = { messageBundle.getString("menu.animation.step"),
                messageBundle.getString("menu.animation.play"),
                messageBundle.getString("menu.animation.rewind"), messageBundle.getString("menu.control.edit"),
                messageBundle.getString("menu.animation.run_until")};
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

        String[] s1 = { messageBundle.getString("menu.control.edit")};
        setEnabledMenuItems(true, s1);
        String[] s2 = { messageBundle.getString("menu.animation.step"),
                messageBundle.getString("menu.animation.play"),
                messageBundle.getString("menu.animation.rewind"),
                messageBundle.getString("menu.animation.pause"),
                messageBundle.getString("menu.animation.run_until")};
        setEnabledMenuItems(false, s2);
    }

    /**
     * Changes the user interface when the "Rewind" button is pressed. Calls
     * methods jeliot.rewind() and theatre.repaint().
     * 
     * @see jeliot.Jeliot#rewind()
     * @see jeliot.theatre.Theatre#repaint()
     */
    void rewindAnimation() {

        unhighlightTabTitles();

        errorOccured = false;

        jeliot.compile();

        /*
         * try { Thread.sleep(25); } catch(InterruptedException e) { throw new
         * RuntimeException(e); }
         */

        if (runningUntil) {
            //TODO: Here ask if the user wants to continue with run until.
            jeliot.runUntil(0);
            runUntilFinished();
        }

        stepButton.setEnabled(true);
        playButton.setEnabled(true);
        pauseButton.setEnabled(false);
        rewindButton.setEnabled(false);

        String[] s1 = { messageBundle.getString("menu.animation.step"),
                messageBundle.getString("menu.animation.play"),
                messageBundle.getString("menu.animation.run_until")};
        setEnabledMenuItems(true, s1);
        String[] s2 = { messageBundle.getString("menu.animation.rewind"),
                messageBundle.getString("menu.animation.pause")};
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

            String[] s1 = { messageBundle.getString("menu.control.edit"),
                    messageBundle.getString("menu.animation.rewind")};
            setEnabledMenuItems(true, s1);
            String[] s2 = { messageBundle.getString("menu.animation.step"),
                    messageBundle.getString("menu.animation.play"),
                    messageBundle.getString("menu.animation.run_until"),
                    messageBundle.getString("menu.animation.pause")};
            setEnabledMenuItems(false, s2);

        } else {

            editButton.setEnabled(true);
            stepButton.setEnabled(false);
            playButton.setEnabled(false);
            pauseButton.setEnabled(false);
            rewindButton.setEnabled(false);

            String[] s1 = { messageBundle.getString("menu.control.edit")};
            setEnabledMenuItems(true, s1);
            String[] s2 = { messageBundle.getString("menu.animation.step"),
                    messageBundle.getString("menu.animation.play"),
                    messageBundle.getString("menu.animation.run_until"),
                    messageBundle.getString("menu.animation.rewind"),
                    messageBundle.getString("menu.animation.pause")};
            setEnabledMenuItems(false, s2);
        }
    }

    /**
     * Get the showMessagesInDialogs variables value
     * 
     * @return
     */
    public boolean showMessagesInDialogs() {
        return showMessagesInDialogs;
    }

    /**
     * Writes the outputted string to the output console.
     * 
     * @param str
     *            String for output.
     */
    public void output(String str) {
        //System.out.println("This is output: " + str);
        outputConsole.append(str);
    }

    /**
     * Method is used to implement the run until feature.
     */
    public void runUntil() {
        String inputValue = JOptionPane.showInputDialog(messageBundle.getString("dialog.run_until"),
                new Integer(0));
        int lineNumber = 0;

        try {
            lineNumber = Integer.parseInt(inputValue);
        } catch (Exception ex) {}

        if (lineNumber > 0) {
            jeliot.runUntil(lineNumber);
            previousSpeed = speedSlider.getValue();
            speedSlider.setValue(speedSlider.getMaximum());
            runningUntil = true;
            codePane.highlightLineNumber(lineNumber);
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    playButton.doClick();
                }
            });
        } else {
            if (runningUntil) {
                jeliot.runUntil(0);
                runUntilFinished();
            }
        }
    }

    /**
     * 
     *
     */
    public void runUntilFinished() {
        codePane.highlightLineNumber(-1);
        speedSlider.setValue(previousSpeed);
        runningUntil = false;
    }

    /**
     * Invoked when the runUntil is done.
     */
    public void runUntilDone() {
        runUntilFinished();
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                pauseButton.doClick();
            }
        });
    }

    /**
     * Sets program to the editor pane.
     * 
     * @param program The program in String
     */
    public void setProgram(String program) {
        if (editButton.isEnabled()) {
            editButton.doClick();
        }
        editor.setProgram(program);
    }

    /**
     * Set the program in the file to the editor pane.
     * 
     * @param f The program file
     */
    public void setProgram(File f) {
        editor.loadProgram(f);
    }

    /**
     * Returns the code from the CodePane.
     * 
     * @return
     */
    public CodePane2 getCodePane() {
        return codePane;
    }

    /**
     * Highlight the given tabs text if the highlight parameter is true
     * otherwise sets it to the default color of the JTabbedPane
     * 
     * @param highlight
     * @param tabNumber
     */
    public void highlightTabTitle(boolean highlight, int tabNumber) {
        try {
            if (highlight) {
                if (tabbedPane.getSelectedIndex() != tabNumber) {
                    tabbedPane.setForegroundAt(tabNumber, highlightTabColor);
                }
            } else {
                tabbedPane.setForegroundAt(tabNumber, normalTabColor);
            }
        } catch (Exception e) {}
    }

    /**
     * 
     * @param title
     * @return
     */
    public int getTabNumber(String title) {
        return tabbedPane.indexOfTab(title);
    }

    /**
     * 
     *
     */
    public void unhighlightTabTitles() {
        int n = tabbedPane.getTabCount();
        for (int i = 0; i < n; i++) {
            tabbedPane.setForegroundAt(i, normalTabColor);
        }
    }
}