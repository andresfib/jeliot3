package jeliot.theater;

/**
 * Public interface that mus be implemented for all those elements that
 * we want to be resizable. The first one to implement it will be the class
 * Actor so, all its children will be, in first instance, resizable. Anyway, is
 * a way to unify the idea of which ones will be resizable or which ones
 * will not.
 * 
 * @author viktor
 */
public interface Resizable {

    /**
     * Variable that indicates if the actor is currently resized or not.
     * true -> yes,
     * false -> no.
     * This variable is not used because the compiler considers it a final variable so,
     * it doesn't allow us to modify it, so is here only as a warn or indication that would say
     * that it is good to have it in order to 'standarize' the use of his interface.
     */
    //protected boolean resized;
    
    /**
     * Variable final (constant) that indicates the coeficient of resizing imposed to all those
     * actors that implements the interface.
     */
    public int RESIZE_SCALE = 2;
    
    /**
     * Method that returns the value of the variable resized(if it is used)
     */
    public boolean isResized();
    
    /**
     * Method used to resize, in a proportion of RESIZE_SCALE, the implementing element.
     */
    public void resize();
    
    /**
     * Method that returns the value of RESIZE_SCALE.
     */
    public int getResizeScale();
    
}
