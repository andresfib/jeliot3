package jeliot.mcode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class ConceptVectors{
 
    private Map conceptVectors;
    private Map complexities;
    
    ConceptVectors(){
        conceptVectors = new HashMap();
        complexities = new HashMap();
    }
    /**
     * 
     * @param concept
     */
    public void addConcept(int concept, int conceptComplexity) {
        Iterator itConcepts = this.conceptVectors.entrySet().iterator();
        Iterator itComplexities = this.complexities.entrySet().iterator();
        while(itConcepts.hasNext() && itComplexities.hasNext()){
            Map.Entry e = (Map.Entry) itConcepts.next();
            Vector concepts = (Vector) e.getValue();
            if (concepts != null) {
                concepts.add(new Integer(concept));
            }
            e = (Map.Entry) itComplexities.next();
            Integer expressionComplexity = (Integer) e.getValue();
            if (expressionComplexity != null) {
                expressionComplexity = new Integer(expressionComplexity.intValue() 
                        + conceptComplexity);
            } else {
                expressionComplexity = new Integer(conceptComplexity);
            }
                
            e.setValue(expressionComplexity);
        }
    }
    
    public void newVector(long expressionId){
        conceptVectors.put(new Long(expressionId), new Vector());
        complexities.put(new Long(expressionId), new Integer(0));
    }
    /**
     * 
     * @param concept
     */
    public boolean hasConcept(int concept) {
        boolean result = false;
        for (Iterator i = this.conceptVectors.entrySet().iterator(); i
                .hasNext();) {
            Map.Entry e = (Map.Entry) i.next();
            if (e.getValue() != null) {
                result = result || ((Vector) e.getValue()).contains(new Integer(concept));
            }
        }
        return result;
    }
    
   
    public Integer[] removeConceptVector(long expressionId) {
        
        this.complexities.remove(new Long(expressionId));
        Vector v = (Vector) this.conceptVectors.remove(new Long(expressionId));
        if (v != null) {
            return (Integer[]) v.toArray(new Integer[0]);
        }
        return new Integer[0];
    }
   
    public int complexity(long expressionId){
        return ((Integer) complexities.get(new Long(expressionId))).intValue();
    }
}

