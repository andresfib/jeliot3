package jeliot.lang;

import java.util.*;
import jeliot.theatre.*;
import jeliot.FeatureNotImplementedException;

/**
  * MethodFrame is an instance of a method under execution.
  * A method frame is created runtime each time a method is called.
  *
  * @author Pekka Uronen
  * @author Niko Myller
  */
public class MethodFrame {

    private MethodStage stage;
    //private int depth;

    private Stack vars;
    private int vcount = 0;

    private String name;

    public MethodFrame(String name) {
        this.name = name;
        vars = new Stack();
    }

    //public PMethod getMethod() {
        //return method;
    //}

    public Variable declareVariable(Variable var) {
        vcount++;
        vars.push(var);
        return var;
    }

    public Variable getVariable(String name) {
        int size = vars.size();
        for (int i = 0; i < size; ++i) {
            if (vars.elementAt(i) != null) {
                if (((Variable) vars.elementAt(i)).getName().equals(name)) {
                    return (Variable) vars.elementAt(i);
                }
            }
        }
        //throw new RuntimeException("No Variable " + name);
        return null;
    }

    public void openScope() {
        vars.push(null);
        stage.openScope();
    }

    public void closeScope() {

        while (vars.pop() != null);
        stage.closeScope();
    }

    public String getMethodName() {
        return name;
    }

    public int getVarCount() {
        return vcount;
    }

    public MethodStage getMethodStage() {
        return stage;
    }

    public void setMethodStage(MethodStage stage) {
        this.stage = stage;
    }
}
