package jeliot.theater;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Point;
import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import jeliot.mcode.*;
import jeliot.lang.ArrayInstance;
import jeliot.lang.ArrayUtilities;
import jeliot.lang.Instance;
import jeliot.lang.MethodFrame;
import jeliot.lang.ObjectFrame;
import jeliot.lang.Value;
import jeliot.lang.Variable;


/**
 * This class handles the centralized creation of the actors. This
 * enables the centralized appearance handling. 
 *  
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class ActorFactory {

    /**
     * The resource bundle for theater package
     */
    static private ResourceBundle bundle = ResourceBundle.getBundle(
                                      "jeliot.theater.resources.properties",
                                      Locale.getDefault());
                                      
//  DOC: document!

    /**
	 *
	 */
	private Component dummy = new Panel();
    /**
	 *
	 */
	private ImageLoader iLoad;

    /**
	 *
	 */
	private Image shadowImage;
    /**
	 *
	 */
	private Image messageImage;

    /**
	 *
	 */
	private Font valueFont;
    /**
	 *
	 */
	private Font variableFont;
    /**
	 *
	 */
	private Font stageFont;
    /**
	 *
	 */
	private Font messageFont = new Font(bundle.getString("font.message.family"),
                                        Font.BOLD,
                                        Integer.parseInt(bundle.getString("font.message.size")));
    /**
	 *
	 */
	private Font indexFont = new Font(bundle.getString("font.index.family"),
                                      Font.BOLD,
                                      Integer.parseInt(bundle.getString("font.index.size")));
    /**
	 *
	 */
	private Font SMIFont = new Font(bundle.getString("font.SMI.family"),
                                        Font.BOLD,
                                        Integer.parseInt(bundle.getString("font.SMI.size")));
    /**
	 *
	 */
	private Font OMIFont = new Font(bundle.getString("font.OMI.family"),
                                        Font.BOLD,
                                        Integer.parseInt(bundle.getString("font.OMI.size")));
    /**
	 *
	 */
	private Font ACFont = new Font(bundle.getString("font.AC.family"),
                                        Font.BOLD,
                                        Integer.parseInt(bundle.getString("font.AC.size")));
    /**
	 *
	 */
	private Font LATFont = new Font(bundle.getString("font.LAT.family"),
                                        Font.BOLD,
                                        Integer.parseInt(bundle.getString("font.LAT.size")));

    /**
	 *
	 */
	private int valueHeight;

    /**
	 *
	 */
	private Insets variableInsets = new Insets(2, 3, 2, 2);

    /**
	 *
	 */
	private int margin = Integer.parseInt(bundle.getString("actor_factory.margin"));

    /**
	 *
	 */
	private Color messagebc = new Color(Integer.decode(bundle.getString("color.message.background")).intValue());
    /**
	 *
	 */
	private Color messagefc = new Color(Integer.decode(bundle.getString("color.message.foreground")).intValue());

    /**
	 *
	 */
	private Color trueColor =   new Color(Integer.decode(bundle.getString("color.true")).intValue());
    /**
	 *
	 */
	private Color falseColor =  new Color(Integer.decode(bundle.getString("color.false")).intValue());

    /**
	 *
	 */
	private Color opColor                 = new Color(Integer.decode(bundle.getString("color.operator")).intValue());
    /**
	 *
	 */
	private Color methodStageColor        = new Color(Integer.decode(bundle.getString("color.method_stage.background")).intValue());
    /**
	 *
	 */
	private Color objectStageColor        = new Color(Integer.decode(bundle.getString("color.object_stage.background")).intValue());
    /**
	 *
	 */
	private Color SMIColor                = new Color(Integer.decode(bundle.getString("color.SMI.background")).intValue());
    /**
	 *
	 */
	private Color variableForegroundColor = new Color(Integer.decode(bundle.getString("color.variable.foreground")).intValue());
    /**
	 *
	 */
	private Color valueForegroundColor    = new Color(Integer.decode(bundle.getString("color.value.foreground")).intValue());
    /**
	 *
	 */
	private Color OMIColor                = new Color(Integer.decode(bundle.getString("color.OMI.background")).intValue());
    /**
	 *
	 */
	private Color ACColor                 = new Color(Integer.decode(bundle.getString("color.AC.background")).intValue());
    /**
	 *
	 */
	private Color bubbleColor             = new Color(Integer.decode(bundle.getString("color.bubble.background")).intValue());
    /**
	 *
	 */
	private Color LATForegroundColor      = new Color(Integer.decode(bundle.getString("color.LAT.foreground")).intValue());
    /**
	 *
	 */
	private Color LATBackgroundColor      = new Color(Integer.decode(bundle.getString("color.LAT.background")).intValue());

    /**
	 *
	 */
	private Color[] valColor = {
            new Color(Integer.decode(bundle.getString("color.value.background.boolean")).intValue()),
            new Color(Integer.decode(bundle.getString("color.value.background.byte")).intValue()),
            new Color(Integer.decode(bundle.getString("color.value.background.short")).intValue()),
            new Color(Integer.decode(bundle.getString("color.value.background.int")).intValue()),
            new Color(Integer.decode(bundle.getString("color.value.background.long")).intValue()),
            new Color(Integer.decode(bundle.getString("color.value.background.char")).intValue()),
            new Color(Integer.decode(bundle.getString("color.value.background.float")).intValue()),
            new Color(Integer.decode(bundle.getString("color.value.background.double")).intValue()),
            new Color(Integer.decode(bundle.getString("color.value.background.string")).intValue()),
            new Color(Integer.decode(bundle.getString("color.value.background.reference")).intValue())
        };

    /**
	 *
	 */
	private Color[] varColor = {
            new Color(Integer.decode(bundle.getString("color.variable.background.boolean")).intValue()),
            new Color(Integer.decode(bundle.getString("color.variable.background.byte")).intValue()),
            new Color(Integer.decode(bundle.getString("color.variable.background.short")).intValue()),
            new Color(Integer.decode(bundle.getString("color.variable.background.int")).intValue()),
            new Color(Integer.decode(bundle.getString("color.variable.background.long")).intValue()),
            new Color(Integer.decode(bundle.getString("color.variable.background.char")).intValue()),
            new Color(Integer.decode(bundle.getString("color.variable.background.float")).intValue()),
            new Color(Integer.decode(bundle.getString("color.variable.background.double")).intValue()),
            new Color(Integer.decode(bundle.getString("color.variable.background.string")).intValue()),
            new Color(Integer.decode(bundle.getString("color.variable.background.reference")).intValue())
        };

    /**
	 *
	 */
	private String[][] binOpImageName = {
        {bundle.getString("image.binary_operator.multiplication"),         bundle.getString("image.binary_operator.result.multiplication")},         //multiplication
        {bundle.getString("image.binary_operator.division"),               bundle.getString("image.binary_operator.result.division")},               //division
        {bundle.getString("image.binary_operator.remaider"),               bundle.getString("image.binary_operator.result.remaider")},               //remaider
        {bundle.getString("image.binary_operator.addition"),               bundle.getString("image.binary_operator.result.addition")},               //addition
        {bundle.getString("image.binary_operator.subtraction"),            bundle.getString("image.binary_operator.result.subtraction")},            //subtraction
        {bundle.getString("image.binary_operator.left_shift"),             bundle.getString("image.binary_operator.result.left_shift")},             //left shift
        {bundle.getString("image.binary_operator.right_shift"),            bundle.getString("image.binary_operator.result.right_shift")},            //right shift
        {bundle.getString("image.binary_operator.unsigned_right_shift"),   bundle.getString("image.binary_operator.result.unsigned_right_shift")},   //unsigned right shift
        {bundle.getString("image.binary_operator.lesser_than"),            bundle.getString("image.binary_operator.result.lesser_than")},            //lesser than
        {bundle.getString("image.binary_operator.greater_than"),           bundle.getString("image.binary_operator.result.greater_than")},           //greater than
        {bundle.getString("image.binary_operator.lesser_than_or_equals"), bundle.getString("image.binary_operator.result.lesser_than_or_equals")},  //lesser than or equals
        {bundle.getString("image.binary_operator.greater_than_or_equals"), bundle.getString("image.binary_operator.result.greater_than_or_equals")}, //greater than or equals
        {bundle.getString("image.binary_operator.instanceof"),             bundle.getString("image.binary_operator.result.instanceof")},             //instanceof not yet implemented
        {bundle.getString("image.binary_operator.equals"),                 bundle.getString("image.binary_operator.result.equals")},                 //equals
        {bundle.getString("image.binary_operator.not_equals"),             bundle.getString("image.binary_operator.result.not_equals")},             //not equals
        {bundle.getString("image.binary_operator.bitwise_and"),            bundle.getString("image.binary_operator.result.bitwise_and")},            //bitwise and
        {bundle.getString("image.binary_operator.bitwise_xor"),            bundle.getString("image.binary_operator.result.bitwise_xor")},            //bitwise xor
        {bundle.getString("image.binary_operator.bitwise_or"),             bundle.getString("image.binary_operator.result.bitwise_or")},             //bitwise or
        {bundle.getString("image.binary_operator.logical_and"),            bundle.getString("image.binary_operator.result.logical_and")},            //logical and
        {bundle.getString("image.binary_operator.logical_or"),             bundle.getString("image.binary_operator.result.logical_or")},             //logical or
        {bundle.getString("image.binary_operator.logical_xor"),            bundle.getString("image.binary_operator.result.logical_xor")}             //logical xor
    };

    /**
	 *
	 */
	private String[][] unaOpImageName = {
        {bundle.getString("image.unary_operator.plus"),       bundle.getString("image.unary_operator.result.plus")},       //plus
        {bundle.getString("image.unary_operator.minus"),      bundle.getString("image.unary_operator.result.minus")},      //minus
        {bundle.getString("image.unary_operator.postdec"),    bundle.getString("image.unary_operator.result.postdec")},    //postdec
        {bundle.getString("image.unary_operator.predec"),     bundle.getString("image.unary_operator.result.predec")},     //predec
        {bundle.getString("image.unary_operator.complement"), bundle.getString("image.unary_operator.result.complement")}, //complement
        {bundle.getString("image.unary_operator.not"),        bundle.getString("image.unary_operator.result.not")},        //not
        {bundle.getString("image.unary_operator.postinc"),    bundle.getString("image.unary_operator.result.postinc")},    //postinc
        {bundle.getString("image.unary_operator.postdec"),    bundle.getString("image.unary_operator.result.postdec")}     //postdec
    };

    /**
	 *
	 */
	private static int[] typeValWidth;

    /**
	 *
	 */
	private static int[] typeWidth;

    /**
	 * @param iLoad
	 */
	public ActorFactory(ImageLoader iLoad) {
        this.iLoad = iLoad;
        this.shadowImage = iLoad.getImage(bundle.getString("image.shadow"));
        Actor.setShadowImage(this.shadowImage);
        this.messageImage = iLoad.getImage(bundle.getString("image.message.background"));

        setValueFont(new Font(bundle.getString("font.value.family"),
                              Font.BOLD,
                              Integer.parseInt(bundle.getString("font.value.size"))));
        setVariableFont(new Font(bundle.getString("font.variable.family"),
                                 Font.BOLD,
                                 Integer.parseInt(bundle.getString("font.variable.size"))));
        setStageFont(new Font(bundle.getString("font.stage.family"),
                              Font.PLAIN,
                              Integer.parseInt(bundle.getString("font.stage.size"))));
    }

    /**
	 * @param font
	 */
	public void setValueFont(Font font) {
        FontMetrics fm = dummy.getFontMetrics(font);

        valueHeight = fm.getHeight() + margin;
        int m = 4;
        typeValWidth = new int[] {
            m + Math.max(fm.stringWidth("true"), fm.stringWidth("false")),
            m + fm.stringWidth("888"),
            m + fm.stringWidth("88888"),
            m + fm.stringWidth("8888888"),
            m + fm.stringWidth("888888888"),
            m + fm.stringWidth("xm"),
            m + fm.stringWidth("0.00E10"),
            m + fm.stringWidth("0.0000E10"),
            m + fm.stringWidth("Normal string")
        };
    }

    /**
	 * @param font
	 */
	public void setVariableFont(Font font) {
        this.variableFont = font;

        FontMetrics fm = dummy.getFontMetrics(font);

        typeWidth = new int[] {
            fm.stringWidth("boolean"),
            fm.stringWidth("byte"),
            fm.stringWidth("short"),
            fm.stringWidth("int"),
            fm.stringWidth("long"),
            fm.stringWidth("char"),
            fm.stringWidth("float"),
            fm.stringWidth("double"),
            fm.stringWidth("String")
        };
    }

    /**
	 * @param font
	 */
	public void setStageFont(Font font) {
        this.stageFont = font;
    }

    /**
	 * @param n
	 * @return
	 */
	public static int getTypeValueWidth(int n) {
        if (n >= 0 && n < typeValWidth.length) {
            return typeValWidth[n];
        }
        return 0;
    }

    /**
	 * @param n
	 * @return
	 */
	public static int getTypeWidth(int n) {
        if (n >= 0 && n < typeWidth.length) {
            return typeWidth[n];
        }
        return 0;
    }

    /**
	 * @return
	 */
	public static int getMaxTypeWidth() {
        int max = 0;
        if (typeWidth != null) {
            int n = typeWidth.length;
            for (int i = 0; i < n; i++) {
                if (typeWidth[i] > max) {
                    max = typeWidth[i];
                }
            }
            return max;
        }
        return 0;
    }

    /**
	 * @return
	 */
	public static int getMaxMethodStageWidth() {
        if (typeValWidth != null) {
            return getMaxTypeWidth() + typeValWidth[8] + 20;
        }
        return 0;
    }

    /**
	 * @return
	 */
	public static int getMaxObjectStageWidth() {
        return getMaxTypeWidth() + typeValWidth[8] + 20;
    }

    /**
	 * @param m
	 * @return
	 */
	public MethodStage produceMethodStage(MethodFrame m) {
        MethodStage stage = new MethodStage(m.getMethodName());
        stage.setFont(stageFont);
        stage.calculateSize(getMaxMethodStageWidth(),
                            valueHeight + 8 +
                            variableInsets.top +
                            variableInsets.bottom);
        stage.setBackground(methodStageColor);
        stage.setShadow(6);
        return stage;
    }

    /**
	 * @param v
	 * @return
	 */
	public VariableActor produceVariableActor(Variable v) {

        String type = v.getType();
        VariableActor actor = null;
        int typeInfo = MCodeUtilities.resolveType(type);

        if (typeInfo != MCodeUtilities.REFERENCE) {

            actor = new VariableActor();

            ValueActor vact = null;

            ImageValueActor valueActor = new ImageValueActor(
                                         iLoad.getImage(bundle.getString("image.mystery")));
            valueActor.calculateSize();
            vact = valueActor;

            int dotIndex = type.lastIndexOf(".");
            String resolvedType = type;
            if (dotIndex > -1) {
                resolvedType = resolvedType.substring(dotIndex+1);
            }
            actor.setName(resolvedType + " " + v.getName());
            actor.setFont(variableFont);
            actor.setForeground(variableForegroundColor);
            actor.setInsets(variableInsets);
            actor.setValueDimension(typeValWidth[typeInfo], valueHeight);
            actor.setBackground(varColor[typeInfo]);
            actor.setValueColor(valColor[typeInfo]);
            actor.calculateSize();
            actor.reserve(vact);
            actor.bind();

            return actor;

        } else if (typeInfo == MCodeUtilities.REFERENCE) {

            ReferenceVariableActor refAct =
                    new ReferenceVariableActor();

            if (MCodeUtilities.isArray(type)) {
                String ct =
                       MCodeUtilities.resolveComponentType(type);

                if (MCodeUtilities.isPrimitive(ct)) {
                    int ti = MCodeUtilities.resolveType(ct);
                    refAct.setBackground(varColor[ti]);
                } else {
                    //This is not implemented properly
                    refAct.setBackground(varColor[typeInfo]);
                }

                String resolvedType = MCodeUtilities.changeComponentTypeToPrintableForm(ct);
                int dotIndex = resolvedType.lastIndexOf(".");
                if (dotIndex > -1) {
                    resolvedType = resolvedType.substring(dotIndex+1);
                }

                int dims = MCodeUtilities.getNumberOfDimensions(type);
                String arrayString = "";
                for (int i = 0; i < dims; i++) {
                    arrayString += "[ ]";
                }

                refAct.setName(resolvedType + arrayString + " " + v.getName());

            } else {

                String resolvedType = type;
                int dotIndex = resolvedType.lastIndexOf(".");
                if (dotIndex > -1) {
                    resolvedType = resolvedType.substring(dotIndex+1);
                }
                refAct.setName(resolvedType + " " + v.getName());
                refAct.setBackground(varColor[typeInfo]);
            }

            refAct.setForeground(variableForegroundColor);
            refAct.setInsets(variableInsets);
            refAct.setFont(variableFont);
            refAct.setValueDimension(6 + 6, valueHeight);
            refAct.calculateSize();
            
            ReferenceActor ra = new ReferenceActor();
            ra.setBackground(refAct.getBackground());
            ra.calculateSize();
            refAct.setValue(ra);
            actor = refAct;
        }

        return actor;
    }


    /**
	 * @param v
	 * @return
	 */
	public VariableActor produceObjectVariableActor(Variable v) {

        String type = v.getType();
        VariableActor actor = null;
        int typeInfo = MCodeUtilities.resolveType(type);

        if (typeInfo != MCodeUtilities.REFERENCE) {

            actor = new VariableActor();

            ValueActor vact = produceValueActor(new Value(MCodeUtilities.getDefaultValue(type),
                                                type));

            int dotIndex = type.lastIndexOf(".");
            String resolvedType = type;
            if (dotIndex > -1) {
                resolvedType = resolvedType.substring(dotIndex+1);
            }
            actor.setName(resolvedType + " " + v.getName());
            actor.setFont(variableFont);
            actor.setForeground(variableForegroundColor);
            actor.setInsets(variableInsets);
            actor.setValueDimension(typeValWidth[typeInfo], valueHeight);
            actor.setBackground(varColor[typeInfo]);
            actor.setValueColor(valColor[typeInfo]);
            actor.calculateSize();
            actor.reserve(vact);
            actor.bind();

            return actor;

        } else if (typeInfo == MCodeUtilities.REFERENCE) {

            ReferenceVariableActor refAct =
                    new ReferenceVariableActor();

            if (MCodeUtilities.isArray(type)) {
                String ct =
                       MCodeUtilities.resolveComponentType(type);

                if (MCodeUtilities.isPrimitive(ct)) {
                    int ti = MCodeUtilities.resolveType(ct);
                    refAct.setBackground(varColor[ti]);
                } else {
                    //This is not implemented properly
                    refAct.setBackground(varColor[typeInfo]);
                }

                String resolvedType = MCodeUtilities.changeComponentTypeToPrintableForm(ct);
                int dotIndex = resolvedType.lastIndexOf(".");
                if (dotIndex > -1) {
                    resolvedType = resolvedType.substring(dotIndex+1);
                }

                int dims = MCodeUtilities.getNumberOfDimensions(type);
                String arrayString = "";
                for (int i = 0; i < dims; i++) {
                    arrayString += "[ ]";
                }

                refAct.setName(resolvedType + arrayString + " " + v.getName());

            } else {

                String resolvedType = type;
                int dotIndex = resolvedType.lastIndexOf(".");
                if (dotIndex > -1) {
                    resolvedType = resolvedType.substring(dotIndex+1);
                }
                refAct.setName(resolvedType + " " + v.getName());
                refAct.setBackground(varColor[typeInfo]);
            }

            refAct.setForeground(variableForegroundColor);
            refAct.setInsets(variableInsets);
            refAct.setFont(variableFont);
            refAct.setValueDimension(6 + 6, valueHeight);
            refAct.calculateSize();
            ReferenceActor ra = new ReferenceActor();
            ra.setBackground(refAct.getBackground());
            ra.calculateSize();
            refAct.setValue(ra);
            actor = refAct;
        }

        return actor;
    }


    /**
	 * @param val
	 * @return
	 */
	public ValueActor produceValueActor(Value val) {

        String type = val.getType();
        int typeInfo = MCodeUtilities.resolveType(type);

        //System.out.println(type);
        if (MCodeUtilities.isPrimitive(type)) {

            ValueActor actor = new ValueActor();

            actor.setForeground(valueForegroundColor);

            if (typeInfo == MCodeUtilities.BOOLEAN) {
                boolean b = Boolean.getBoolean(val.getValue());
                Color tcol = b ? trueColor : falseColor;
                actor.setForeground(tcol);
            }

            actor.setBackground(valColor[typeInfo]);
            String label = val.getValue();

//          String label = valObj instanceof Exception ?
//                                    "ERROR"          :
//                                    valObj.toString();
            if (typeInfo == MCodeUtilities.DOUBLE) {

                if (label.indexOf('E') == -1) {
                    int dot = label.indexOf('.');

                    if (dot > -1 && dot < label.length() -5) {
                        label = label.substring(0, dot + 5);
                    }
                    //typeValWidth[type.getIndex()];
                }
            }

            actor.setLabel(label);
            //actor.setActor(val.getActor());
            actor.calculateSize();

            return actor;

        } else {
            ReferenceActor actor = null;
            if (val instanceof jeliot.lang.Reference) {
                actor = produceReferenceActor((jeliot.lang.Reference) val);
            } else if (val.getActor() instanceof jeliot.theater.ReferenceActor) {
                actor = produceReferenceActor((ReferenceActor) val.getActor());
            } else {
                actor = new ReferenceActor();
                actor.setBackground(valColor[typeInfo]);
                actor.calculateSize();
            }
            actor.setForeground(valueForegroundColor);

            return actor;
        }

    }

    /**
	 * @param rf
	 * @return
	 */
	public ReferenceActor produceReferenceActor(jeliot.lang.Reference rf) {
        Instance inst = rf.getInstance();
        ReferenceActor actor = null;
        int typeInfo = MCodeUtilities.resolveType("null");

        if (inst != null) {
            typeInfo = MCodeUtilities.resolveType(inst.getType());
            actor = new ReferenceActor(inst.getActor());
        } else if (rf.getActor() instanceof ReferenceActor) {
            ReferenceActor rfa = (ReferenceActor) rf.getActor();
            typeInfo = MCodeUtilities.resolveType(rf.getType());
            actor = new ReferenceActor(rfa.getInstanceActor());
        } else {
            actor = new ReferenceActor();
        }

        actor.setBackground(valColor[typeInfo]);
        actor.calculateSize();
        actor.setForeground(valueForegroundColor);

        return actor;
    }

    /**
	 * @param cloneActor
	 * @return
	 */
	public ReferenceActor produceReferenceActor(ReferenceActor cloneActor) {

        ReferenceActor actor = new ReferenceActor(cloneActor.getInstanceActor());
        actor.setBackground(cloneActor.getBackground());
        Point p = cloneActor.getLocation();
        actor.setLocation(new Point(p.x, p.y));
        actor.setParent(cloneActor.getParent());
        actor.calculateSize();
        actor.setForeground(valueForegroundColor);

        return actor;
    }

    /**
	 * @param cloneActor
	 * @return
	 */
	public ValueActor produceValueActor(ValueActor cloneActor) {

        ValueActor actor = new ValueActor();
        actor.setForeground(cloneActor.getForeground());
        actor.setBackground(cloneActor.getBackground());
        actor.setLabel(cloneActor.getLabel());
        actor.calculateSize();
        Point p = cloneActor.getLocation();
        actor.setLocation(new Point(p.x, p.y));
        actor.setParent(cloneActor.getParent());
        return actor;
    }

    /**
	 * @param op
	 * @return
	 */
	public OperatorActor produceBinOpActor(int op) {
        Image image = iLoad.getImage(binOpImageName[op][0]);
        return produceOperatorActor(image);
    }

    /**
	 * @param op
	 * @return
	 */
	public OperatorActor produceBinOpResActor(int op) {
        Image image = iLoad.getImage(binOpImageName[op][1]);
        return produceOperatorActor(image);
    }

    /**
	 * @return
	 */
	public OperatorActor produceEllipsis() {
        Image image = iLoad.getImage(bundle.getString("image.dots"));
        OperatorActor actor = produceOperatorActor(image);
        return actor;
    }

    /**
	 * @param op
	 * @return
	 */
	public OperatorActor produceUnaOpActor(int op) {
        Image image = iLoad.getImage(unaOpImageName[op][0]);
        return produceOperatorActor(image);
    }

    /**
	 * @param op
	 * @return
	 */
	public OperatorActor produceUnaOpResActor(int op) {
        Image image = iLoad.getImage(unaOpImageName[op][1]);
        return produceOperatorActor(image);
    }

    /**
	 * @param image
	 * @return
	 */
	public OperatorActor produceOperatorActor(Image image) {
        OperatorActor actor = new OperatorActor(image, iLoad.darken(image));
        actor.calculateSize();
        int hh = valueHeight - actor.getHeight();

        if (hh > 0) {
            actor.setInsets(new Insets(hh/2, 0, (hh+1)/2, 0));
            actor.setSize(actor.getWidth(), valueHeight);
        }
        return actor;
    }

    /**
     * Static Method Invocation Actor.
	 * @param name
	 * @param paramCount
	 * @return
	 */
    public SMIActor produceSMIActor(String name, int paramCount) {
        SMIActor actor = new SMIActor(name, paramCount);
        actor.setFont(SMIFont);
        actor.setBackground(SMIColor);
        actor.setInsets(new Insets(6, 6, 6, 6));
        actor.calculateSize();
        return actor;
    }

    /**
     * Object Method Invocation Actor
	 * @param name
	 * @param paramCount
	 * @return
	 */
    public OMIActor produceOMIActor(String name, int paramCount) {
        OMIActor actor = new OMIActor(name, paramCount);
        actor.setFont(OMIFont);
        actor.setBackground(OMIColor);
        actor.setInsets(new Insets(6, 6, 6, 6));
        actor.calculateSize();
        return actor;
    }

    /**
     * Array Creator Actor
	 * @param name
	 * @param paramCount
	 * @return
	 */
    public ACActor produceACActor(String name, int paramCount) {
        ACActor actor = new ACActor(name, paramCount);
        actor.setFont(ACFont);
        actor.setBackground(ACColor);
        actor.setInsets(new Insets(6, 6, 6, 6));
        actor.calculateSize();
        return actor;
    }

//     public SMIActor produceSMIActor(MethodPointer fmp, int n) {
//         ReferenceType type = fmp.getDeclaringClass();
//         String name = type.getSimpleName() + "." + fmp.getName();
//         SMIActor actor = new SMIActor(name, n);
//         actor.setFont(messageFont);
//         actor.setBackground(new Color(0xFFEAEA));
//         actor.setInsets(new Insets(6, 6, 6, 6));
//         actor.calculateSize();
//         return actor;
//     }

//     public SMIActor produceSMIActor(DomesticMethodPointer dmp, int n) {
//         ReferenceType type = dmp.getDeclaringClass();
//         String name = type.getSimpleName() + "." + dmp.getName();
//         SMIActor actor = new SMIActor(name, n);
//         actor.setFont(messageFont);
//         actor.setBackground(new Color(0xFFEAEA));
//         actor.setInsets(new Insets(6, 6, 6, 6));
//         actor.calculateSize();
//         return actor;
//     }

    /**
     * @param actor
     * @return
     */
    public BubbleActor produceBubble(Actor actor) {
        BubbleActor ba = new BubbleActor(actor);
        ba.setBackground(bubbleColor);
        ba.setInsets(new Insets(8, 8, 8, 8));
        return ba;
    }


    /**
	 * @param text
	 * @return
	 */
	MessageActor produceMessageActor(String[] text) {
        MessageActor ma = new MessageActor();
        ma.setBackground(messageImage);
        ma.setText(text);
        ma.setBackground(messagebc);
        ma.setForeground(messagefc);
        ma.setFont(messageFont);
        ma.setShadow(6);
        if (text != null) {
            ma.calculateSize();
        }
        return ma;
    }

    /**
	 * @return
	 */
	public ConstantBox produceConstantBox() {
        ConstantBox cbox = new ConstantBox(
                iLoad.getImage(bundle.getString("image.constant_box")));
        cbox.calculateSize();
        return cbox;
    }

    /**
	 * @return
	 */
	public AnimatingActor produceHand() {
        AnimatingActor hand = new AnimatingActor(produceImage("image.hand1"));
        hand.calculateSize();
        return hand;
    }

    /**
	 * @param iname
	 * @return
	 */
	public Image produceImage(String iname) {
        return iLoad.getImage(bundle.getString(iname));
    }

    /**
	 * @param array
	 * @return
	 */
	public ArrayActor produceArrayActor(ArrayInstance array) {

        int[] dims = array.getDimensions();

        Object valueActors = Array.newInstance(
                                (new ValueActor()).getClass(),
                                dims);

        int n = dims.length;
        int[] index = new int[n];

        for (int i = 0; i < n; i++) {
            index[i] = 0;
        }

        int k = 0;
        do {
            for (int i = 0; i < dims[n-1]; i++) {
                index[n-1] = i;
                Value value = array.getVariableAt(index).getValue();
                ValueActor va = produceValueActor(value);
                value.setActor(va);
                ArrayUtilities.setObjectAt(valueActors, index, va);

                //Testing
                //System.out.println(""+k);
                //k++;
            }
        } while (ArrayUtilities.nextIndex(index, dims));

        ArrayActor aactor = new ArrayActor(valueActors, dims);

        String ctype = array.getComponentType();
        int typeInfo = MCodeUtilities.resolveType(ctype);

        if (MCodeUtilities.isPrimitive(ctype)) {
            aactor.setFont(indexFont);
            aactor.setBackground(varColor[typeInfo]);
            aactor.setValueColor(valColor[typeInfo]);
            aactor.calculateSize(typeValWidth[typeInfo], valueHeight);
        } else {
            //TODO: handle the reference type as a component type. 
            //If array's component type is reference type
            //is not implemented.
            return null;
        }

        for (int i = 0; i < n; i++) {
            index[i] = 0;
        }

        do {
            for (int i = 0; i < dims[n-1]; i++) {
                index[n-1] = i;
                VariableActor va = aactor.getVariableActor(index);
                va.setValueDimension(typeValWidth[typeInfo], valueHeight);
                array.getVariableAt(index).setActor(va);
            }
        } while (ArrayUtilities.nextIndex(index, dims));

        aactor.setShadow(6);
        return aactor;
    }

    /**
	 *
	 */
	private MessageFormat objectStageTitle = new MessageFormat(bundle.getString("title.object_stage"));

    /**
	 * @param m
	 * @return
	 */
	public ObjectStage produceObjectStage(ObjectFrame m) {
        ObjectStage stage = new ObjectStage(objectStageTitle.format(new String[] { m.getObjectName() }), m.getVarCount());
        stage.setFont(stageFont);

        //The width of the object stage is not correct but we have not found any better.
        stage.calculateSize(getMaxObjectStageWidth(),
                            valueHeight + 8 +
                            variableInsets.top +
                            variableInsets.bottom);
        stage.setBackground(objectStageColor);
        stage.setShadow(6);
        return stage;
    }

    /**
	 * @return
	 */
	public LinesAndText produceLinesAndText() {
        LinesAndText lat = new LinesAndText();
        lat.setBackground(LATBackgroundColor);
        lat.setForeground(LATForegroundColor);
        lat.setFont(LATFont);
        return lat;
    }
}
