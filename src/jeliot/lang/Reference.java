package jeliot.lang;

import java.lang.reflect.*;
import java.util.*;
import jeliot.FeatureNotImplementedException;

/**
  * @author Pekka Uronen
  *
  * created         2.10.1999
  */
public class Reference extends Value {

    private Instance instance;

    public Reference() {
        super("null", "null");
    }

    public Reference(Instance instance) {
        super(instance.getHashCode(), instance.getType());
        this.instance = instance;
        this.instance.reference();
    }

    protected void finalize() {
        instance.dereference();
    }

    public void setInstance(Instance inst) {
        this.instance = inst;
    }

    public Instance getInstance() {
        return this.instance;
    }
}


