/*
 * Created on 11.1.2006
 */
package test.jeliot;

import jeliot.util.SourceCodeUtilities;

public class SourceCodeTests {

    public SourceCodeTests() {
        super();
    }
    
    public static void main(String[] args) {
        System.out.println(SourceCodeUtilities.removeCommentsAndStrings("/*\n*\nsdfasdf\\dfsasf\nadfsasf*/va\n/* // */in // t�m� ei tuu\n//*********> Algorithm starts here\n// Define the interval [a,b] in which you expect\n// a root\n t�m� ja /* // */ t�m� pit�isi /*   */ j��d� /*\n t�m� ei tuu\n // */\n"));
    }

}
