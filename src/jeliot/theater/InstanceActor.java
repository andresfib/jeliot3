package jeliot.theater;

import java.util.Enumeration;
import java.util.Vector;

/**
 * 
 * @author Pekka Uronen
 * @author Niko Myller 
 */
public class InstanceActor extends Actor implements ActorContainer {

    /**
	 *
	 */
	private Vector references = new Vector();

    /**
	 * @param ref
	 */
	public void addReference(ReferenceActor ref) {
        references.addElement(ref);
    }

    /**
	 * @param ref
	 */
	public void removeReference(ReferenceActor ref) {
        references.removeElement(ref);
    }

    /**
	 * @return
	 */
	public int getNumberOfReferences() {
        return references.size();
    }

    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#setLocation(int, int)
	 */
	public void setLocation(int x, int y) {
        super.setLocation(x, y);
        Enumeration enum = references.elements();
        while (enum.hasMoreElements()) {
            ReferenceActor actor = (ReferenceActor)enum.nextElement();
            actor.calculateBends();
        }
    }

    /* (non-Javadoc)
	 * @see jeliot.theater.ActorContainer#removeActor(jeliot.theater.Actor)
	 */
	public void removeActor(Actor actor) { }


}
