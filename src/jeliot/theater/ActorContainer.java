package jeliot.theater;

/**
  * <code>ActoContainer</code> interface is implemented by classes that
  * are going to contain <code>Actor</code>s as their fields and take
  * care of their painting. This means that those classes having
  * actors as their fields but not taking care of the painting of
  * the actors are not implementing this class.
  *
  * @author Pekka Uronen
  * @author Niko Myller
  */
public interface ActorContainer {
    
    /**
     * Variable that indicates if the contents of the ActorContainer are currently resized or not.
     * true -> yes,
     * false -> no.
     * This variable is not used because the compiler considers it a final variable so,
     * it doesn't allow us to modify it, so is here only as a warn or indication that would say
     * that it is good to have it in order to 'standarize' the use of his interface.
     */
    //private boolean contentResized = false;
    
    /**
     * Variable that indicates if the contents of the ActorContainer are currently relocated or not.
     * true -> yes,
     * false -> no.
     * This variable is not used because the compiler considers it a final variable so,
     * it doesn't allow us to modify it, so is here only as a warn or indication that would say
     * that it is good to have it in order to 'standarize' the use of his interface.
     */
    //private boolean contentRelocated = false;
    
    /**
     * Return true if the contained actors are resized, false if not.
     */
    public boolean isContentResized();
    
    /**
     * Return true if the contained actors are relocated, false if not.
     */
    public boolean isContentRelocated();
    
    /**
     * Resizes up, or resizes down the actors contained in the given
     * ActorContainer.
     */
    public void resizeContainedActors();
    
    /**
     * Remove the the given actor from the ActorContainer. 
     * 
     * @param actor The actor to be removed.
     */
    public void removeActor(Actor actor);
    
    /**
     * Repositions the actors contained in the current
     * one.
     */
    public void relocateContainedActors();
}
