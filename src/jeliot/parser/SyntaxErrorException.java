package jeliot.parser;

import jeliot.theatre.*;

/**
  * An exception caused by a syntax error in the program being parsed.
  *
  * @author Pekka Uronen
  *
  * created 23.9.1999
  */
public class SyntaxErrorException extends Exception {

    String info;
    int left, right;

    public SyntaxErrorException(String s, int left, int right) {
        super(s);
        this.info = s;
        this.left = left;
        this.right = right;
    }

    public String toString() {
        return (
            "<HTML><HEAD><TITLE>XXX</TITLE></HEAD>\n" +
            "<BODY bgcolor=white>" +
            "<H1>Syntax Error</H3>\n" +
            "<P>" +
            "Unfortunately your program cannot be compiled. " +
            "There is a syntax error somewhere in it:\n" +
            "</P><BLOCKQUOTE><B>\n" +
                info +
            "</B></BLOCKQUOTE>\n" +
            "The problem is most probably on the highlighted line, " +
            "but it might be also somewhere else." +

            "</BODY></HTML>"
            );
    }

    public int getLeftPos() {
        return left;
    }

    public int getRightPos() {
        return right;
    }

    public Highlight getHighlight() {
        return new Highlight(0,0,0,0);
    }
}
