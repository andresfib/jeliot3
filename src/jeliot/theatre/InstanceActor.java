package jeliot.theatre;

import java.awt.*;
import java.util.*;

/**
  * @author Pekka Uronen
  *
  * created         10.10.1999
  */
public class InstanceActor extends Actor implements ActorContainer {

    private Vector references = new Vector();

    public void addReference(ReferenceActor ref) {
        references.addElement(ref);
    }

    public void removeReference(ReferenceActor ref) {
        references.removeElement(ref);
    }

    public int getNumberOfReferences() {
        return references.size();
    }

    public void setLocation(int x, int y) {
        super.setLocation(x, y);
        Enumeration enum = references.elements();
        while (enum.hasMoreElements()) {
            ReferenceActor actor = (ReferenceActor)enum.nextElement();
            actor.calculateBends();
        }
    }

    public void removeActor(Actor actor) { }


}
