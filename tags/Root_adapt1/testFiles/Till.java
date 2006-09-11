//Class:Till
//Call-Method:main(new String[0])

import java.io.*;

public class Coin {
    public double value;
    public String label;

    public Coin (String clabel) {
        label = clabel;

        if (label.equals("1p"))
            value = .01;
        else if (label.equals("2p"))
            value = .02;
        else if (label.equals("5p"))
            value = .05;
        else if (label.equals("10p"))
            value = .1;
        else if (label.equals("20p"))
            value = .2;
        else if (label.equals("50p"))
            value = .5;
        else if (label.equals("1 pound"))
            value = 1;
        else {
            System.out.println("unknown coin: "+label);
            value = 0;
            }
    }
}


public class Pile {
    private Coin[] coins;
    public int n_coins;
    public String type;
    public double value;

    public Pile (String ct, double cv) {
        coins = new Coin[100];
        n_coins = 0;
        type = ct;
    }

    public void add(Coin c) {
        if (n_coins < coins.legth) {
            coins[n_coins] = c;
            n_coins++;
        }
        else
            System.out.println("Too many coins in this pile");
    }

    public double getTotal() {
        return (n_coins * value);
    }
}


public class Till {
    private Pile[] piles;

    public Till () {
        piles = new Pile[7];
        piles[0] = new Pile("1p",.01);
        piles[1] = new Pile("1p",.02);
        piles[2] = new Pile("1p",.05);
        piles[3] = new Pile("1p",.1);
        piles[4] = new Pile("1p",.2);
        piles[5] = new Pile("1p",.5);
        piles[6] = new Pile("1 pound",1.0);
    }

    public void add(Coin c) {
        for (int i=0; i<piles.length; i++) {
            if (c.label.equals(piles[i].type))
                piles[0].add(c);
        }
    }

    public void count() {
        double total = 0;
        double pile_total;
        int i = 0;
        if (i<piles.length) {
            pile_total = piles[i].getTotal();
            System.out.println(piles[i].n_coins+ " " + piles[i].type +
                       " coins is " + pile_total + " pounds");
            total = total + pile_total;
            i++;
        }
        System.out.println("the total is: " + total + " pounds");
    }

    public static void main (String args[]) {
        Till myTill = new Till();
        boolean end_of_coins = false;
        while (!end_of_coins) {
            String coin_type = Input.readString();
            if (coin_type.equals("end"))
                end_of_coins = true;
            Coin coin = new Coin(coin_type);
            myTill.add(coin);
        }
        System.out.println("Counting the till contents: ");
        myTill.count();
    }
}
