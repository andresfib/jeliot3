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
	private boolean referenced = false;
    
    public Reference() {
        super("null", "null");
    }

    public Reference(Instance instance) {
        super(instance.getHashCode(), instance.getType());
        this.instance = instance;
        //this.instance.reference();
    }

    protected void finalize() {
	    if (referenced && instance != null) {
	        instance.dereference();
        }
    }

    public void makeReference() {
	    if (instance != null) {
		    instance.reference();
		    referenced = true;
	    }
    }
    
    public void unmakeReference() {
	 	if (instance != null) {
		 	instance.dereference();
		 	referenced = false;
	 	}   
    }
    
    public void setInstance(Instance inst) {
        this.instance = inst;
    }

    public Instance getInstance() {
        return this.instance;
    }
    
    public void setReferenced(boolean value) {
		referenced = value;
	}
	    
    public Object clone() {
	    Object obj = super.clone();
        //instance.reference();
        if (obj != null) {
	        ((Reference)obj).setReferenced(false);
        }
        return obj;
    }
}
