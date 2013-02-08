//Class:teht1
//Call-Method:main(new String[0])

import java.awt.*;
import jeliot.io.*;

class Henkilo {
	
	// Luokan attribuutteina on kolme merkkijonotyyppistä String-oliota, 
	//joihin talletetaan nimi, osoite ja sähköpostiosoite
	private String nimi;  
	private String osoite;
	private String sahkopostiOsoite;
	
	
	// Muodostimessa asetetaan arvot attribuuteille, kun olio luodaan luokasta
	Henkilo (String nimi, String osoite, String sahkopostiOsoite ) {
		this.nimi=nimi;
		this.osoite=osoite;
		this.sahkopostiOsoite=sahkopostiOsoite;
			
	}
	Henkilo() {
		this.nimi = "";
		this.osoite = "";
		this.sahkopostiOsoite = "";
	}
	
	// Metodi palauttaa henkilön nimen
	public String getNimi() {
		return nimi;
	}

	// Metodi asettaa henkilön nimen
	public void setNimi(String nimi) {
		this.nimi = nimi;
	}

	// Metodi palauttaa henkilön osoitteen
	public String getOsoite() {
		return osoite;
	}
	
	// Metodilla asetetaan henkilölle uusi osoite
	public void setOsoite(String uusiOsoite) {
		this.osoite=uusiOsoite;
	} 
	
	// Metodi palauttaa henkilon osoitteen
	public String getSahkopostiOsoite () {
		return sahkopostiOsoite;
	}
	
	// Metodilla asetetaan henkilölle uusi osoite
	public void setSahkopostiOsoite(String uusiSPOsoite) {
		this.sahkopostiOsoite=uusiSPOsoite;
	}
	
	// tulostusmetodissa tulostetaan tietueen (olion) tiedot appletin 
	// piirtopinnalle annettujen koordinaattien perusteella.
	public void tulostaTiedot (Graphics g,int paikkaX, int paikkaY ) {
		g.drawString(nimi,paikkaX,paikkaY);
		g.drawString(osoite,paikkaX,paikkaY+14);
		g.drawString(sahkopostiOsoite,paikkaX,paikkaY+28);
	}

}

class Johtaja extends Henkilo {
	public int alaisten_lkm;
	public String mita_johtaa;
	
	public Johtaja() {
		super();
		alaisten_lkm = 0;
		mita_johtaa = "";
	}
	
	public void setMitaJohtaa(String mita) {
		mita_johtaa = mita;
	}
	
	public void setAlaistenMaara(int lkm) {
		alaisten_lkm = lkm;
	}
	
}

class Tyoton extends Henkilo {
	public String koulutus, ed_tyo;
	
	public Tyoton() {
		super();
		koulutus = "";
		ed_tyo = "";
	}
	
	public void setKoulutus(String koulutus) {
		this.koulutus = koulutus;
	}
	
	public void setEdellinenTyo(String edtyo) {
		ed_tyo = edtyo;
	}
	
}


class Elakelainen extends Henkilo {
	public int ika;
	public String harrastus;
	
	public Elakelainen() {
		super();
		ika = 0;
		harrastus = "";
	}
	
	public void setIka(int ika) {
		this.ika = ika;
	}
	
	public void setHarrastus(String harrastus) {
		this.harrastus = harrastus;
	}
}

public class teht1 {
	public static void main(String [] args) {
		Elakelainen e = new Elakelainen();
		e.setNimi("Pekka");
		e.setIka(85);
		Johtaja j = new Johtaja();
		j.setNimi("Juuso");
		j.setMitaJohtaa("Microsoft");
		
		
	}
}