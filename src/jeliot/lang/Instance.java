package jeliot.lang;

import jeliot.theatre.InstanceActor;

/**
  * @author Pekka Uronen
  * @modified Niko Myller
  * created         2.10.1999
  * modified        7.5.2003
  */
public class Instance {

    private String type;
    private InstanceActor actor;

    private String hashCode;

    private int references = 0;

    protected Instance(String hashCode) {
        this.hashCode = hashCode;
    }

    protected Instance(String hashCode, String type) {
        this.hashCode = hashCode;
        this.type = type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setActor(InstanceActor actor) {
        this.actor = actor;
    }

    public InstanceActor getActor() {
        return actor;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public String getHashCode() {
        return hashCode;
    }

    public void reference() {
        references++;
    }

    public void dereference() {
        references--;
    }

    public int getNumberOfReferences() {
        return references;
    }
}


