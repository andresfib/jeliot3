//Class:Hello
//Called-Method:(new Hello("Hello all!")).print()
/**
 * Class Hello:
 *
 * Hello-world program to demonstrate BlueJ.
 */
class Hello {
    
    /**
     * Field hello.
     */
    String hello;
    
    /**
     * 
     * @param hello that is printed with print() method.
     */
    public Hello(String hello) {
        this.hello = hello;
    }
    
    /**
     * Method that does the work
     */
    public void print() {
        System.out.println(this.hello);
    }

}