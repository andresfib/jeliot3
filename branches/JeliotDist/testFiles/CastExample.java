//Class:PrintMe
//Call-Method:main(new String[0])

public class PrintMe {
    public void doIt (int character) {
        System.out.println (character+character);
    }
    public static void main (String [] args) {
        new PrintMe().doIt('A');
    }
}
