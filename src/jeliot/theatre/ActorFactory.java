package jeliot.theatre;

import java.awt.*;
import java.lang.reflect.*;
import jeliot.lang.*;

import jeliot.ecode.*;


/**
  * @author Pekka Uronen
  * @author Niko Myller
  */
public class ActorFactory {

    private Component dummy = new Panel();
    private ImageLoader iLoad;

    private Image shadowImage;
    private Image messageImage;

    private Font valueFont;
    private Font variableFont;
    private Font stageFont;
    private Font messageFont = new Font("SansSerif", Font.BOLD, 14);
    private Font indexFont = new Font("SansSerif", Font.BOLD, 12);

    private Color messagebc = new Color(0xFFFF66);
    private Color messagefc = Color.black;

    private Color trueColor =   new Color(0x006600);
    private Color falseColor =  new Color(0x990000);

    private int valueHeight;

    private Insets variableInsets = new Insets(2, 3, 2, 2);

    private int margin = 4;

    private Color[] valColor = {
            new Color(0x99CCCC),    // boolean
            new Color(0xFFCC99),    // byte
            new Color(0xFFFF99),    // short
            new Color(0xFFFFCC),    // int
            new Color(0xFFFF99),    // long
            new Color(0x99CC99),    // char
            new Color(0xFFCCFF),    // float
            new Color(0xFFCCCC),    // double
            new Color(0xFFCC99),    // String
            new Color(0xFFCC99)     // Reference
        };

    private Color[] varColor = {
            new Color(0x66CC99),    // boolean
            new Color(0xCC9999),    // byte
            new Color(0xCCCC99),    // short
            new Color(0xFFCC99),    // int
            new Color(0xCCFF66),    // long
            new Color(0x66CC66),    // char
            new Color(0xFF99CC),    // float
            new Color(0xFF9999),    // double
            new Color(0xFF6666),    // String
            new Color(0xFFFFAA)     // Reference
        };

    private String[][] binOpImageName = {
        {"mulop.gif",      "assignop.gif"}, //multiplication
        {"divop.gif",      "assignop.gif"}, //division
        {"modop.gif",      "assignop.gif"}, //remaider
        {"plusop.gif",     "assignop.gif"}, //addition
        {"minusop.gif",    "assignop.gif"}, //subtraction
        {"lshiftop.gif",   "assignop.gif"}, //left shift
        {"rshiftop.gif",   "assignop.gif"}, //right shift
        {"urshiftop.gif",  "assignop.gif"}, //unsigned right shift
        {"lessop.gif",     "assignop.gif"}, //lesser than
        {"greatop.gif",    "assignop.gif"}, //greater than
        {"lequop.gif",     "assignop.gif"}, //lesser than or equals
        {"gequop.gif",     "assignop.gif"}, //greater than or equals
        {null,             null},           //instanceof not yet implemented
        {"equop.gif",      "assignop.gif"}, //equals
        {"nequop.gif",     "assignop.gif"}, //not equals
        {"bitandop.gif",   "assignop.gif"}, //bitwise and
        {"bitxorop.gif",   "assignop.gif"}, //bitwise xor
        {"bitorop.gif",    "assignop.gif"}, //bitwise or
        {"candop.gif",     "assignop.gif"}, //logical and
        {"corop.gif",      "assignop.gif"}, //logical or
        {"cxorop.gif",     "assignop.gif"}  //logical xor
    };

    private String[][] unaOpImageName = {
        {"plusop.gif",       "assignop.gif"}, //plus
        {"minusop.gif",      "assignop.gif"}, //minus
        {"plusplusop.gif",   null},           //postdec
        {"minusminusop.gif", null},           //predec
        {"compop.gif",       "assignop.gif"}, //complement
        {"notop.gif",        "assignop.gif"}, //not
        {"plusplusop.gif",   null},           //postinc
        {"minusminusop.gif", null}            //postdec
    };

    private static int[] typeValWidth;

    private static int[] typeWidth;

    private Color opColor = new Color(0xE0E0E0);

    public ActorFactory(ImageLoader iLoad) {
        this.iLoad = iLoad;
        this.shadowImage = iLoad.getImage("shadow.gif");
        this.messageImage = iLoad.getImage("paper.jpg");

        setValueFont(new Font("SansSerif", Font.BOLD, 14));
        setVariableFont(new Font("SansSerif", Font.BOLD, 14));
        setStageFont(new Font("SansSerif", Font.PLAIN, 14));
    }

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

    public void setStageFont(Font font) {
        this.stageFont = font;
    }

    public static int getTypeValueWidth(int n) {
        if (n >= 0 && n < typeValWidth.length) {
            return typeValWidth[n];
        }
        return 0;
    }

    public static int getTypeWidth(int n) {
        if (n >= 0 && n < typeWidth.length) {
            return typeWidth[n];
        }
        return 0;
    }

    public static int getMaxTypeWidth() {
        int max = 0;
        int n = typeWidth.length;
        for (int i = 0; i < n; i++) {
            if (typeWidth[i] > max) {
                max = typeWidth[i];
            }
        }
        return max;
    }

    public static int getMaxMethodStageWidth() {
        return getMaxTypeWidth() + typeValWidth[8] + 20;
    }

    public static int getMaxObjectStageWidth() {
        return getMaxTypeWidth() + typeValWidth[8] + 20;
    }

    public MethodStage produceMethodStage(MethodFrame m) {
        MethodStage stage = new MethodStage(m.getMethodName());
        stage.setFont(stageFont);
        stage.calculateSize(getMaxMethodStageWidth(),
                            valueHeight + 8 +
                            variableInsets.top +
                            variableInsets.bottom);
        stage.setBackground(new Color(0xFFCCCC));
        stage.setShadow(6);
        stage.setShadowImage(shadowImage);
        return stage;
    }

    public VariableActor produceVariableActor(Variable v) {

        String type = v.getType();
        VariableActor actor = null;
        int typeInfo = ECodeUtilities.resolveType(type);

        if (typeInfo != ECodeUtilities.REFERENCE) {

            actor = new VariableActor();

            ValueActor vact = null;

            ImageValueActor valueActor = new ImageValueActor(
                                         iLoad.getImage("mystery.gif"));
            valueActor.calculateSize();
            vact = valueActor;

            int dotIndex = type.lastIndexOf(".");
            String resolvedType = type;
            if (dotIndex > -1) {
                resolvedType = resolvedType.substring(dotIndex+1);
            }
            actor.setName(resolvedType + " " + v.getName());
            actor.setFont(variableFont);
            actor.setForeground(Color.black);
            actor.setInsets(variableInsets);
            actor.setValueDimension(typeValWidth[typeInfo], valueHeight);
            actor.setBackground(varColor[typeInfo]);
            actor.setValueColor(valColor[typeInfo]);
            actor.calculateSize();
            actor.setShadowImage(shadowImage);
            actor.reserve(vact);
            actor.bind();

            return actor;

        } else if (typeInfo == ECodeUtilities.REFERENCE) {

            ReferenceVariableActor refAct =
                    new ReferenceVariableActor();

            if (ECodeUtilities.isArray(type)) {
                String ct =
                       ECodeUtilities.resolveComponentType(type);

                if (ECodeUtilities.isPrimitive(ct)) {
                    int ti = ECodeUtilities.resolveType(ct);
                    refAct.setBackground(varColor[ti]);
                } else {
                    //This is not implemented properly
                    refAct.setBackground(varColor[typeInfo]);
                }

                String resolvedType = ECodeUtilities.changeComponentTypeToPrintableForm(ct);
                int dotIndex = resolvedType.lastIndexOf(".");
                if (dotIndex > -1) {
                    resolvedType = resolvedType.substring(dotIndex+1);
                }

                int dims = ECodeUtilities.getNumberOfDimensions(type);
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

            refAct.setForeground(Color.black);
            refAct.setInsets(variableInsets);
            refAct.setFont(variableFont);
            refAct.setValueDimension(6 + 6, valueHeight);
            refAct.calculateSize();
            refAct.setShadowImage(shadowImage);
            ReferenceActor ra = new ReferenceActor();
            ra.setBackground(refAct.getBackground());
            ra.setShadowImage(shadowImage);
            ra.calculateSize();
            refAct.setValue(ra);
            actor = refAct;
        }

        return actor;
    }


    public VariableActor produceObjectVariableActor(Variable v) {

        String type = v.getType();
        VariableActor actor = null;
        int typeInfo = ECodeUtilities.resolveType(type);

        if (typeInfo != ECodeUtilities.REFERENCE) {

            actor = new VariableActor();

            ValueActor vact = produceValueActor(new Value(ECodeUtilities.getDefaultValue(type),
                                                type));

            int dotIndex = type.lastIndexOf(".");
            String resolvedType = type;
            if (dotIndex > -1) {
                resolvedType = resolvedType.substring(dotIndex+1);
            }
            actor.setName(resolvedType + " " + v.getName());
            actor.setFont(variableFont);
            actor.setForeground(Color.black);
            actor.setInsets(variableInsets);
            actor.setValueDimension(typeValWidth[typeInfo], valueHeight);
            actor.setBackground(varColor[typeInfo]);
            actor.setValueColor(valColor[typeInfo]);
            actor.calculateSize();
            actor.setShadowImage(shadowImage);
            actor.reserve(vact);
            actor.bind();

            return actor;

        } else if (typeInfo == ECodeUtilities.REFERENCE) {

            ReferenceVariableActor refAct =
                    new ReferenceVariableActor();

            if (ECodeUtilities.isArray(type)) {
                String ct =
                       ECodeUtilities.resolveComponentType(type);

                if (ECodeUtilities.isPrimitive(ct)) {
                    int ti = ECodeUtilities.resolveType(ct);
                    refAct.setBackground(varColor[ti]);
                } else {
                    //This is not implemented properly
                    refAct.setBackground(varColor[typeInfo]);
                }

                String resolvedType = ECodeUtilities.changeComponentTypeToPrintableForm(ct);
                int dotIndex = resolvedType.lastIndexOf(".");
                if (dotIndex > -1) {
                    resolvedType = resolvedType.substring(dotIndex+1);
                }

                int dims = ECodeUtilities.getNumberOfDimensions(type);
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

            refAct.setForeground(Color.black);
            refAct.setInsets(variableInsets);
            refAct.setFont(variableFont);
            refAct.setValueDimension(6 + 6, valueHeight);
            refAct.calculateSize();
            refAct.setShadowImage(shadowImage);
            ReferenceActor ra = new ReferenceActor();
            ra.setBackground(refAct.getBackground());
            ra.setShadowImage(shadowImage);
            ra.calculateSize();
            refAct.setValue(ra);
            actor = refAct;
        }

        return actor;
    }


    public ValueActor produceValueActor(Value val) {

        String type = val.getType();
        int typeInfo = ECodeUtilities.resolveType(type);

        //System.out.println(type);
        if (ECodeUtilities.isPrimitive(type)) {

            ValueActor actor = new ValueActor();

            if (typeInfo == ECodeUtilities.BOOLEAN) {
                boolean b = Boolean.getBoolean(val.getValue());
                Color tcol = b ? trueColor : falseColor;
                actor.setForeground(tcol);
            }

            actor.setBackground(valColor[typeInfo]);
            String label = val.getValue();

//          String label = valObj instanceof Exception ?
//                                    "ERROR"          :
//                                    valObj.toString();
            if (typeInfo == ECodeUtilities.DOUBLE) {

                if (label.indexOf('E') == -1) {
                    int dot = label.indexOf('.');

                    if (dot > -1 && dot < label.length() -5) {
                        label = label.substring(0, dot + 5);
                    }
                    //typeValWidth[type.getIndex()];
                }
            }

            actor.setLabel(label);
            actor.setShadowImage(shadowImage);
            //actor.setActor(val.getActor());
            actor.calculateSize();

            return actor;

        } else {
            ReferenceActor actor = null;
            if (val instanceof jeliot.lang.Reference) {
                actor = produceReferenceActor((jeliot.lang.Reference) val);
            } else if (val.getActor() instanceof jeliot.theatre.ReferenceActor) {
                actor = produceReferenceActor((ReferenceActor) val.getActor());
            } else {
                actor = new ReferenceActor();
                actor.setBackground(valColor[typeInfo]);
                actor.setShadowImage(shadowImage);
                actor.calculateSize();
            }
            actor.setForeground(Color.black);

            return actor;
        }

    }

    public ReferenceActor produceReferenceActor(jeliot.lang.Reference rf) {
        Instance inst = rf.getInstance();
        ReferenceActor actor = null;
        int typeInfo = ECodeUtilities.resolveType("null");

        if (inst != null) {
            typeInfo = ECodeUtilities.resolveType(inst.getType());
            actor = new ReferenceActor(inst.getActor());
        } else if (rf.getActor() instanceof ReferenceActor) {
            ReferenceActor rfa = (ReferenceActor) rf.getActor();
            typeInfo = ECodeUtilities.resolveType(rf.getType());
            actor = new ReferenceActor(rfa.getInstanceActor());
        } else {
            actor = new ReferenceActor();
        }

        actor.setBackground(valColor[typeInfo]);
        actor.setShadowImage(shadowImage);
        actor.calculateSize();
        actor.setForeground(Color.black);

        return actor;
    }

    public ReferenceActor produceReferenceActor(ReferenceActor cloneActor) {

        ReferenceActor actor = new ReferenceActor(cloneActor.getInstanceActor());
        actor.setBackground(cloneActor.getBackground());
        actor.setShadowImage(shadowImage);
        Point p = cloneActor.getLocation();
        actor.setLocation(new Point(p.x, p.y));
        actor.setParent(cloneActor.getParent());
        actor.calculateSize();
        actor.setForeground(Color.black);

        return actor;
    }

    public ValueActor produceValueActor(ValueActor cloneActor) {

        ValueActor actor = new ValueActor();
        actor.setForeground(cloneActor.getForeground());
        actor.setBackground(cloneActor.getBackground());
        actor.setLabel(cloneActor.getLabel());
        actor.setShadowImage(shadowImage);
        actor.calculateSize();
        Point p = cloneActor.getLocation();
        actor.setLocation(new Point(p.x, p.y));
        actor.setParent(cloneActor.getParent());
        return actor;
    }

    public OperatorActor produceBinOpActor(int op) {
        Image image = iLoad.getImage(binOpImageName[op][0]);
        return produceOperatorActor(image);
    }

    public OperatorActor produceBinOpResActor(int op) {
        Image image = iLoad.getImage(binOpImageName[op][1]);
        return produceOperatorActor(image);
    }

    public OperatorActor produceEllipsis() {
        Image image = iLoad.getImage("dots.gif");
        OperatorActor actor = produceOperatorActor(image);
        return actor;
    }

    public OperatorActor produceUnaOpActor(int op) {
        Image image = iLoad.getImage(unaOpImageName[op][0]);
        return produceOperatorActor(image);
    }

    public OperatorActor produceUnaOpResActor(int op) {
        Image image = iLoad.getImage(unaOpImageName[op][1]);
        return produceOperatorActor(image);
    }

    public OperatorActor produceOperatorActor(Image image) {
        OperatorActor actor = new OperatorActor(image, iLoad.darken(image));
        actor.calculateSize();
        int hh = valueHeight - actor.getHeight();

        if (hh > 0) {
            actor.setInsets(new Insets(hh/2, 0, (hh+1)/2, 0));
            actor.setSize(actor.getWidth(), valueHeight);
        }

        actor.setShadowImage(shadowImage);
        return actor;
    }

    //Added for Jeliot 3
    public SMIActor produceSMIActor(String name, int paramCount) {
        SMIActor actor = new SMIActor(name, paramCount);
        actor.setFont(messageFont);
        actor.setBackground(new Color(0xFFEAEA));
        actor.setInsets(new Insets(6, 6, 6, 6));
        actor.calculateSize();
        actor.setShadowImage(shadowImage);
        return actor;
    }

    public OMIActor produceOMIActor(String name, int paramCount) {
        OMIActor actor = new OMIActor(name, paramCount);
        actor.setFont(messageFont);
        actor.setBackground(new Color(0xFFEAEA));
        actor.setInsets(new Insets(6, 6, 6, 6));
        actor.calculateSize();
        actor.setShadowImage(shadowImage);
        return actor;
    }

    //Added for Jeliot 3
    public ACActor produceACActor(String name, int paramCount) {
        ACActor actor = new ACActor(name, paramCount);
        actor.setFont(messageFont);
        actor.setBackground(new Color(0xFFEAEA));
        actor.setInsets(new Insets(6, 6, 6, 6));
        actor.calculateSize();
        actor.setShadowImage(shadowImage);
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

    public BubbleActor produceBubble(Actor actor) {
        BubbleActor ba = new BubbleActor(actor);
        ba.setBackground(new Color(0xFFFFCC));
        ba.setInsets(new Insets(8, 8, 8, 8));
        return ba;
    }


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
        ma.setShadowImage(shadowImage);
        return ma;
    }

    public ConstantBox produceConstantBox() {
        ConstantBox cbox = new ConstantBox(
                iLoad.getImage("constants.gif"));
        cbox.calculateSize();
        return cbox;
    }

/*    //Added for Jeliot 3
    public ConstantBox produceInputBox() {
        ConstantBox cbox = new ConstantBox(
                iLoad.getImage("input.gif"));
        cbox.calculateSize();
        return cbox;
    }
*/
    public AnimatingActor produceHand() {
        AnimatingActor hand = new AnimatingActor(produceImage("Hand"));
        hand.calculateSize();
        return hand;
    }

    public Image produceImage(String iname) {
        return iLoad.getLogicalImage(iname);
    }

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
        int typeInfo = ECodeUtilities.resolveType(ctype);

        if (ECodeUtilities.isPrimitive(ctype)) {
            aactor.setFont(indexFont);
            aactor.setBackground(varColor[typeInfo]);
            aactor.setValueColor(valColor[typeInfo]);
            aactor.calculateSize(typeValWidth[typeInfo], valueHeight);
        } else {
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

    ObjectStage produceObjectStage(ObjectFrame m) {
        ObjectStage stage = new ObjectStage("Object of the class " + m.getObjectName(), m.getVarCount());
        stage.setFont(stageFont);

        //The width of the object stage is not correct but we have not found any better.
        stage.calculateSize(getMaxObjectStageWidth(),
                            valueHeight + 8 +
                            variableInsets.top +
                            variableInsets.bottom);
        stage.setBackground(new Color(0xFFFFAA));
        stage.setShadow(6);
        stage.setShadowImage(shadowImage);
        return stage;
    }
}
