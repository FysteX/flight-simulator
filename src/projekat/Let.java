package projekat;

public class Let {
	Aerodrom pocetniAerodrom;
	Aerodrom krajnjiAerodrom;
	Time vremePoletanja;
	int minutiTrajanjaLeta;
	
	public Let(Aerodrom pocetni, Aerodrom krajnji, int satPoletanja, int minutPoletanja, int minutiTrajanjaLeta) {
		this.pocetniAerodrom = pocetni;
		this.krajnjiAerodrom = krajnji;
		this.vremePoletanja =  new Time(satPoletanja, minutPoletanja);
		this.minutiTrajanjaLeta = minutiTrajanjaLeta;
	}
	
	@Override
	public String toString() {
		return pocetniAerodrom.toString() + " " + krajnjiAerodrom + " " + vremePoletanja.getSat() + " " + 
				vremePoletanja.getMinut() + " " + minutiTrajanjaLeta;
	}

	public Aerodrom getPocetniAerodrom() {
		return pocetniAerodrom;
	}

	public Aerodrom getKrajnjiAerodrom() {
		return krajnjiAerodrom;
	}

	public Time getVremePoletanja() {
		return vremePoletanja;
	}

	public int getMinutiTrajanjaLeta() {
		return minutiTrajanjaLeta;
	}
}
