package jeliot.parser;

/**
  * An exception caused by a semantic error in the program being parsed.
  *
  * @author Pekka Uronen
  *
  * created 5.8.1999
  * last modified -
  */
public class SemanticException extends RuntimeException {

    protected String info;

    public SemanticException() {
        super();
    }

    public SemanticException(String s) {
        super(s);
        this.info = s;
    }

    public String toString() {
        if (info == null) {
            info = super.toString();
        }
        
        return (
            "<HTML><HEAD><TITLE>XXX</TITLE></HEAD>\n" +
            "<BODY bgcolor=white>" +
            "<H1>Terrible Error!!!</H3>\n" +
            "<P>" +
            "Unfortunately your program cannot be compiled. " +
            "There is an error in it:\n" +
            "</P><BLOCKQUOTE><B>\n" +
                info +
            "</B></BLOCKQUOTE>\n" +
            "Try to fix it first, or read " +
            "<A href='info'>more info</A>." +

            "</BODY></HTML>"
            );
    }
            
}
