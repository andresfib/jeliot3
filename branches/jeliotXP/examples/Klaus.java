
/** Diese Klasse kann einen nicht-negativen
* ganzzahligen Wert verwalten
* @author Klaus Bovermann
* @version 1.0
*/
public class Zaehler {
    private int saldo;

/** Der wert des Zaehlers ist initial NULL
*/
    public Zaehler(int saldo) {
        this.saldo = saldo;
    }

    public Zaehler() {
        this(1000);
    }

/** Zeigt den aktuellen Stand des verwalteten Wertes an
* @return aktueller Wert
*/
    public int getSaldo(){
            return saldo;
        }
/** Setzt den Wert auf den initialen wert NULLzurueck
*/
    public void resetSaldo(){
            this.saldo = 0;
    }

/**Der verwaltete Wert wird um den uebegebenen Wert vergroessert
* Ist der uebergebene Wert negativ, hat diese Methode keine Wirkung!
* @param Zahl, um den der Wert vergroessert werden soll
*/
    public void addWert(int wert) {
        if (wert < 0) {
        }
        else {
            this.saldo = this.saldo + wert;
        }
    }
}
public class start
{
    public static void main (String[] args)
    {
        Zaehler z = new Zaehler();
/*        Automat a = new Automat ();
        a.reset ();
        a.einzahlen (100);
        a.reset ();*/
    }
}
/** Der Automat hat intern drei Geldschaechte (1 Euro, 2 Euro, 50 Cent)
* und einen Zaehler fuer die bereits eingeworfenen Muenzen.
* @author Klaus Bovermann
* @version 1.0
*/
public class Automat {
    private Zaehler haben;
    private GeldKasten meinGeldkasten;

    public Automat() {
        this.meinGeldkasten = new GeldKasten ();
        this.haben     = new Zaehler ();
    }

/** der Automat wird in den initialen Zustand zurueckgesetzt.
* Falls Haben vorhanden ist, wird dieses ausgezahlt.
*/
    public void reset() {
        if (this.haben.getSaldo() > 0) {
            this.auszahlen (this.haben.getSaldo());
            this.haben.resetSaldo();
        }
    }

/** Der angegebene Wert wird (falls machbar!!) mit korrekten Muenzen
* zurueckgezahlt.
* Falls zu wenig Muenzen zum Auszahlen vorhanden sind: PECH!!!
* Falls ein negativer Wert zurueckzuzahlen ist, passiert NIX!
* @param Wert des Geldes, das zurueckzuzahlen ist.
*/
    private void auszahlen (int wieviel) {
        int zuZahlen = wieviel;
        /* erstmal die Zweier nutzen! */
        while (zuZahlen >= 200 && this.meinGeldkasten.hatZweier()) {
            this.meinGeldkasten.auszahlen(200); //klappt immer!!!
            zuZahlen = zuZahlen - 200;
            System.out.println ("Zweier zurueck!");
        }
        /* jetzt die Einer nutzen! */
        while (zuZahlen >= 100 && this.meinGeldkasten.hatEiner()) {
            this.meinGeldkasten.auszahlen(100); //klappt immer!!!
            zuZahlen = zuZahlen - 100;
            System.out.println ("Einer zurueck!");
        }
        /* jetzt die 50-Cent nutzen! */
        while (zuZahlen >= 50 && this.meinGeldkasten.hatFuffziger()) {
            this.meinGeldkasten.auszahlen(50); //klappt immer!!!
            zuZahlen = zuZahlen - 50;
            System.out.println ("Fuenfziger zurueck!");
        }
        if (zuZahlen > 0) {
            System.out.println ("Pech; noch zu zahlen: " + zuZahlen);
        }
    }

/** Korrekte Muenzen werden im Haben-Register registriert
* und in den korrekten Geldschacht geworfen!
* Falls anschliessend genug Geld im Haben-Register ist,
* wird das Restgeld zurueckgezahlt.
* @param Wert der Muenze
*/
    public void einzahlen (int muenzwert) {
        boolean geklappt = this.meinGeldkasten.einzahlen(muenzwert);
        if (geklappt) {
            this.haben.addWert(muenzwert);
        }
        else {
            System.out.println ("Falscher Muenzwert: " + muenzwert);
        }
        if (this.haben.getSaldo() >= 450) {
            System.out.println ("Hier ist die Fahrkarte!");
            int back = this.haben.getSaldo() - 450;
            this.auszahlen(back);
            this.haben.resetSaldo();
        }
    }
}
public class GeldKasten {
    private Geldschacht einer;
    private Geldschacht fuffziger;
    private Geldschacht zweier;

    public GeldKasten() {
        this.einer     = new Geldschacht (100);
        this.zweier    = new Geldschacht (200);
        this.fuffziger = new Geldschacht (50);
    }

    public boolean einzahlen (int muenzwert) {
        switch (muenzwert){
        case 50 : {
            this.fuffziger.increase();
            return true;
            }
        case 100 : {
            this.einer.increase();
            return true;
            }
        case 200 : {
            this.zweier.increase();
            return true;
            }
        default : {
            return false;
            }
        }
    }

    public boolean auszahlen (int muenzwert) {
        switch (muenzwert){
        case 50 : {
            if (this.hatFuffziger()) {
                this.fuffziger.decrease();
                return true;
                }
            else{
                return false;
                }
            }
        case 100 : {
            if (this.hatEiner()) {
                this.einer.decrease();
                return true;
                }
            else {
                return false;
                }
            }
        case 200 : {
            if (this.hatZweier()) {
                this.zweier.decrease();
                return true;
                }
            else {
                return false;
                }
            }
        default : {
            return false;
            }
        }
    }

    public boolean hatEiner() {
        return (this.einer.getAnzahlMuenzen () > 0);
    }

    public boolean hatZweier() {
        return (this.zweier.getAnzahlMuenzen () > 0);
    }

    public boolean hatFuffziger() {
        return (this.fuffziger.getAnzahlMuenzen () > 0);
    }
}
/** Eine Klasse zum Verwalten eines Geldschachtes.
* In einem Geldschacht koennen von einer Sorte Muenzen
* beliebig viele Stuecke gelagert werden.
* Die werte werden grundsaetzlich in Cent gemessen!
* @author Klaus Bovermann
* @version 1.0
*
*/

public class Geldschacht {
    private int muenzwert;
    private int anzahlMuenzen;

/** Der Konstruktor verlangt die Angabe, welcher Muenztyp
* hier gesammelt wird.
* @param Wert jeder hier zu sammelnden Muenze
*/
    public Geldschacht(int muenzwert) {
        this.muenzwert     = muenzwert;
        this.anzahlMuenzen = 0;
    }



/** @return Wert einer jeden Muenze in diesem Schacht
*/
    public int getMuenzwert(){
            return muenzwert;
        }

/** @return Anzahl der hier gesammelten Muenzen
*/
    public int getAnzahlMuenzen(){
            return anzahlMuenzen;
        }
/** Eine Muenze wird hinzugefuegt.
*/
    public void increase() {
        this.anzahlMuenzen++;
    }

/** Falls nicht leer, wird eine Muenze entfernt
* @return true, falls erfolgreich, sonst false
*/
    public boolean decrease() {
        if (this.anzahlMuenzen <= 0) {
            return false;
        }
        else {
            this.anzahlMuenzen--;
            return true;
        }
    }
}
