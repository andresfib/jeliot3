package jeliot;

/**
  * This exception is thrown when the parser discovers use of a language
  * feature that is not (yet) implemented in Jeliot.
  *
  * @author Pekka Uronen
  * @author Niko Myller
  */
public class FeatureNotImplementedException extends RuntimeException {

    /**
     * Information abou the not implemented feature.
     */
    String info;

    /**
	* The only constructor of the FeatureNoeImplementedException.
	*
	* @param	s	The info string of the exception.
	*
	*/
    public FeatureNotImplementedException(String s) {
        super(s);
        this.info = s;
    }

    /**
	* Turns the exception into the string that defines the exception
	*
	* @return	The description of the exception in HTML -form.
	*/
    public String toString() {
        return (
            "<HTML><HEAD><TITLE>XXX</TITLE></HEAD>\n" +
            "<BODY bgcolor=white>" +
            "<H1>Missing Feature in Jeliot!!!</H3>\n" +
            "<P>" +
            "Unfortunately your program cannot be compiled. " +
            "It uses a feature not implemented in Jeliot:\n" +
            "</P><BLOCKQUOTE><B>\n" +
                info +
            "</B></BLOCKQUOTE>\n" +
            "It will be implemented in near future." +
            "Promise." +

            "</BODY></HTML>"
            );
    }
}
