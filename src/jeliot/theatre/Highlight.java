package jeliot.theatre;

public class Highlight {

    private int beginLine, beginColumn, endLine, endColumn;

    protected Highlight() { }

    public Highlight(int bl, int bc, int el, int ec) {
        this.beginLine = bl;
        this.beginColumn = bc;
        this.endLine = el;
        this.endColumn = ec;
    }

    public int getBeginLine() {
        return this.beginLine;
    }

    public int getBeginColumn() {
        return this.beginColumn;
    }

    public int getEndLine() {
        return this.endLine;
    }

    public int getEndColumn() {
        return this.endColumn;
    }

}