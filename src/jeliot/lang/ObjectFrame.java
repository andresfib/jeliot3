package jeliot.lang;

import java.util.*;
import jeliot.theatre.*;
import jeliot.FeatureNotImplementedException;

/**
  *
  * @author Niko Myller
  *
  * created 26.6.2003
  */
public class ObjectFrame extends Instance {

    private ObjectStage stage;

    private Hashtable vars;
    private int vcount = 0;

    private String name;

    public ObjectFrame(String hashCode, String type, int vcount) {
        super(hashCode, type);
        //Possible to change it to be something else
        this.name = type;
        this.vcount = vcount;
        vars = new Hashtable();
    }

    public Variable declareVariable(Variable var) {
        vars.put(var.getName(), var);
        return var;
    }

    public Variable getVariable(String name) {
        Variable var = (Variable) vars.get(name);
        if (var != null) {
            return var;
        } else {
            //throw new RuntimeException("No Variable " + name);
            return null;
        }
    }

    public String getObjectName() {
        return name;
    }

    public int getVarCount() {
        return vcount;
    }

    public ObjectStage getObjectStage() {
        return stage;
    }

    public void setObjectStage(ObjectStage stage) {
        this.stage = stage;
        setActor(stage);
    }
}
