package jeliot.mcode;

//Not used in Jeliot 3 right now.

public class Command {

    private int expressionReference = 0;
    private int type = 0;

    protected Command() { }

    public Command(int t, int er) {
        this.type = t;
        this.expressionReference = er;
    }

    public void setExpressionReference(int er) {
        this.expressionReference = er;
    }

    public void setType(int t) {
        this.type = t;
    }

    public int getExpressionReference() {
        return this.expressionReference;
    }

    public int getType() {
        return this.type;
    }

}