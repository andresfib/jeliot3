package jeliot.theatre;

import java.awt.*;
import jeliot.lang.*;

import jeliot.ecode.*;

//import jeliot.parser.*;

/**
  * @author Pekka Uronen
  *
  * created         9.8.1999
  * modified        12.12.2002 by Niko Myller
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
            new Color(0xFFCC99),    // String needed
            new Color(0xFFCC99)     // Reference needed?
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
            new Color(0xFF6666),    // String needed
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
        FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);

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
            m + fm.stringWidth("Normal string.")
        };
    }

    public void setVariableFont(Font font) {
        this.variableFont = font;
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

    Stage produceStage(MethodFrame m) {
         //the getVarCount method is changed to a constant for a while
         //because it is not set properly in our situation.
         //Actually it is not easy to know how many variables there are.
        Stage stage = new Stage(m.getMethodName(), 6);// m.getVarCount());
        stage.setFont(stageFont);
        stage.calculateSize(typeValWidth[8] + 60,
                            valueHeight + 8 +
                            variableInsets.top +
                            variableInsets.bottom);
        stage.setBackground(new Color(0xFFCCCC));
        stage.setShadow(6);
        stage.setShadowImage(shadowImage);
        return stage;
    }

    VariableActor produceVariableActor(Variable v) {

        String type = v.getType();
        VariableActor actor = null;
        int typeInfo = ECodeUtilities.resolveType(type);

        if (typeInfo != ECodeUtilities.REFERENCE) {

            actor = new VariableActor();

            ImageValueActor vact = new ImageValueActor(
                     iLoad.getImage("mystery.gif"));
            vact.calculateSize();

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

            refAct.setName(v.getName());
            refAct.setFont(variableFont);
            refAct.setForeground(Color.black);
            refAct.setInsets(variableInsets);
            refAct.setBackground(varColor[typeInfo]);
            refAct.setValueDimension(6, valueHeight);
            if (ECodeUtilities.isArray(type)) {
                //Here is a problem!!!

//                 String ct = v.getComponentType();
//                 if (ECodeUtilities.isPrimitive(ct)) {
//                     int ti = ECodeUtilities.resolveType(ct);
//                     refAct.setBackground(varColor[ti]);
//                 }
            }
            refAct.calculateSize();
            refAct.setShadowImage(shadowImage);

            actor = refAct;
        }

        return actor;
    }

    public ValueActor produceValueActor(Value val) {

        ValueActor actor = new ValueActor();
        String type = val.getType();
        int typeInfo = ECodeUtilities.resolveType(type);

        System.out.println(type);

        if (typeInfo == ECodeUtilities.BOOLEAN) {
            boolean b = Boolean.getBoolean(val.getValue());
            Color tcol = b ? trueColor : falseColor;
            actor.setForeground(tcol);
        }

        if (ECodeUtilities.isPrimitive(type)) {
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
        }

        return null;

    }

    public ValueActor produceValueActor(ValueActor cloneActor) {

        ValueActor actor = new ValueActor();
        actor.setForeground(cloneActor.getForeground());
        actor.setBackground(cloneActor.getBackground());
        actor.setLabel(cloneActor.getLabel());
        actor.setShadowImage(shadowImage);
        actor.calculateSize();

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

    public Actor produceEllipsis() {
        Image image = iLoad.getImage("dots.gif");
        Actor actor = produceOperatorActor(image);
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

    //Added for Jeliot 3
    public ConstantBox produceInputBox() {
        ConstantBox cbox = new ConstantBox(
                iLoad.getImage("input.gif"));
        cbox.calculateSize();
        return cbox;
    }

    public AnimatingActor produceHand() {
        AnimatingActor hand = new AnimatingActor(produceImage("Hand"));
        hand.calculateSize();
        return hand;
    }

    public Image produceImage(String iname) {
        return iLoad.getLogicalImage(iname);
    }
/*
    public ArrayActor produceArrayActor(ArrayInstance array) {
        int n = array.length();
        ValueActor[] valueActors = new ValueActor[n];
        for (int i = 0; i < n; ++i) {
            Value value = array.getVariableAt(i).getValue();
            valueActors[i] = produceValueActor(value);
            value.setActor(valueActors[i]);
        }
        ArrayActor aactor = new ArrayActor(valueActors);
        Type ctype = ((ArrayType)array.getType()).getComponentType();
        if (ctype instanceof PrimitiveType) {
            int index = ((PrimitiveType)ctype).getIndex();
            aactor.setFont(indexFont);
            aactor.setBackground(varColor[index]);
            aactor.setValueColor(valColor[index]);
            aactor.calculateSize(typeValWidth[index], valueHeight);
        }
        else return null;

        for (int i = 0; i < n; ++i) {
            array.getVariableAt(i).setActor(
                    aactor.getVariableActor(i));
        }
        aactor.setShadow(6);
        return aactor;
    }
*/
}

