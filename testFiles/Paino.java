//Class:Paino
//Call-Method:main(new String[] {"170","65"})

public class Paino
{
      private float paino; // henkil�n paino
      private float pituus;  // henkil�n pituus

      // alustetaan henkilon tiedot
      public Paino(float kg, float cm)
      {
      paino = kg;
      pituus = cm;
      }

      // palautetaan painoindeksi
      public float painoIndeksi()
      {
      float ind = paino / (pituus * pituus * 0.0001F);
          return ind;
      }

      // ilmoitetaan onko ali-, yli vai normaalipainoinen
      public void vertailuTieto()
      {
       float indeksi = this.painoIndeksi();
       if (indeksi < 20) 
           System.out.println("Olet alipainoinen");
       else if (indeksi < 25)
           System.out.println("Olet normaalipainoinen");
       else if (indeksi > 25)
           System.out.println("Olet ylipainoinen");
      }

      // testaamiseen k�ytetty p��ohjelma
      public static void main(String[] args)
      {
      if (args.length != 2) {
         System.out.println("Ajettava: java Paino 170 65");
         System.out.println("Sinulla on " +
         args.length + " parametria");
          }
          else {
         int pituus = Integer.parseInt(args[0]); // 1. parametri
         int paino = Integer.parseInt(args[1]);  // 2. parametri

         Paino hen = new Paino(paino, pituus);

         System.out.println("Painoindeksisi on " + hen.painoIndeksi());
         hen.vertailuTieto();
      }
      }
}
