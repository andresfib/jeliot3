package jeliot.lang;


/**
  * @author Pekka Uronen
  *
  * created         2.10.1999
  */
public class Reference extends Value {

    /**
	 *
	 */
	private Instance instance;
	/**
	 *
	 */
	private boolean referenced = false;
    
    /**
	 * 
	 */
	public Reference() {
        super("null", "null");
    }

    /**
	 * @param instance
	 */
	public Reference(Instance instance) {
        super(instance.getHashCode(), instance.getType());
        this.instance = instance;
        //this.instance.reference();
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() {
	    if (referenced && instance != null) {
	        instance.dereference();
        }
    }

    /**
	 * 
	 */
	public void makeReference() {
	    if (instance != null) {
		    instance.reference();
		    referenced = true;
	    }
    }
    
    /**
	 * 
	 */
	public void unmakeReference() {
	 	if (instance != null) {
		 	instance.dereference();
		 	referenced = false;
	 	}   
    }
    
    /**
	 * @param inst
	 */
	public void setInstance(Instance inst) {
        this.instance = inst;
    }

    /**
	 * @return
	 */
	public Instance getInstance() {
        return this.instance;
    }
    
    /**
	 * @param value
	 */
	public void setReferenced(boolean value) {
		referenced = value;
	}
	    
    /* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
	    Object obj = super.clone();
        //instance.reference();
        if (obj != null) {
	        ((Reference)obj).setReferenced(false);
        }
        return obj;
    }
}
