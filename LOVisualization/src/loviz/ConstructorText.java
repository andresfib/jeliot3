// Copyright 2010 by Mordechai (Moti) Ben-Ari. See VN.java. */
package loviz;

public class ConstructorText {
  public static String introText =
  "Concept: Constructors can be overloaded like other methods.\n\n" +
  "A method overloaded when there is more than one method with the same name.\n\n" + 
  "The parameter signature is used to decide which method to call.\n\n" +
  "For constructors, overloading is usually done when some of the fields of an " +
  "object can be initialized with default values, although we want to " +
  "retain the possibility of explicitly supplying all the initial values.\n\n" +
  "In such cases, it is convenient to invoke one constructor from within " +
  "another in order to avoid duplicating code.\n\n" +
  "Invoking the method this within one constructor calls another " +
  "constructor with the appropriate parameter signature.\n\n" +
  "Example: The website charges a uniform price per second for all songs,\n" +
  "except for special offers.\n"+
  "We define two constructors, one that specifies a price for special offers\n"+
  "and another that uses a default price for ordinary songs.";
  
  public static String step1 =
   "The value of the static constant DEFAULT_PRICE is set as soon as the " +
   "class is loaded and is displayed in the Constant area.";

  public static String step2 =
    "The variable song1 is allocated and contains the null value.";

  public static String step3 = 
    "Memory is allocated for the four fields of the object "+
    "and default values are assigned to the fields.";

  public static String step4 = 
    "The constructor is called with two actual parameters\n" +
    "The call is resolved so that it is the second constructor that is executed.";

  public static String step5 = 
   "The two parameters, together with the default price, are immediately " +
   "used to call the first constructor that has three parameters.\n" +
   "The method name this means: call a constructor from this class.\n" +
   "This constructor initializes the first three fields from the parameters," +
   "and the value of the fourth field is computed by calling the method computePrice.";

  public static String step6 = 
   "The constructor returns a reference to the object, " +
   "which is stored in the variable song1.";
   
   public static String[] steps =
     new String[]{step1, step2, step3, step4, step5, step6};
     public static int[] number = {5, 3, 6, 1, 2, 15};

}
