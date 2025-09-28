package projekat;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Podaci {

	private List<Aerodrom> listaAerodroma;
	private List<Let> listaLetova;
	private List<Avion> listaAviona;
	
	private String fajlAerodromaPutanja;
	private String fajlLetovaPutanja;
	
	private Aplikacija aplikacija;
	
	private Aerodrom selectedAerodrom;
	
	public Podaci(Aplikacija aplikacija) {
		this.aplikacija = aplikacija;
		this.selectedAerodrom = null;
		
		listaAerodroma = new ArrayList<>();
		listaLetova = new ArrayList<>();
		listaAviona = new ArrayList<>();
		
		fajlAerodromaPutanja = "src/projekat/Aerodromi.csv";
		fajlLetovaPutanja = "src/projekat/Letovi.csv";
	}
	
	public List<Aerodrom> getListaAerodroma() {
		return this.listaAerodroma;	
	}
	
	public List<Avion> getListaAviona() {
		return this.listaAviona;	
	}
	
	public List<Let> getListaLetova() {
		return this.listaLetova;
	}
	
	//vraca true ako je aerodrom na poziciji (x, y) u suprotnom false
	private boolean isAerodromAtLocation(Aerodrom aerodrom, int x, int y) {
		if(x <= aerodrom.getX()*Scena.SCALE + Scena.SCALE && x >= aerodrom.getX()*Scena.SCALE &&
				y >= aerodrom.getY()*Scena.SCALE && y <= aerodrom.getY()*Scena.SCALE + Scena.SCALE) {
			return true;
		}
		return false;
	}
	
	//ova funkcija proverava da li je na kliknutoj poziciji (x, y) aerodrom i ukoliko jeste vrsi odredjenu selekciju i deselekciju
	public void selectAerodrom(int x, int y) {
		
		for(Aerodrom aerodrom: listaAerodroma) {
			if(isAerodromAtLocation(aerodrom, x, y) && aerodrom.isShown()) {
				if(aerodrom.isSelected()) {
					aerodrom.deselect();
					selectedAerodrom = null;
					aplikacija.getTimer().unpause();
				}else {
					if(selectedAerodrom != null) {
						selectedAerodrom.deselect();
					}
					aerodrom.select();
					selectedAerodrom = aerodrom;
					aplikacija.getScena().setRedFrame();
					aplikacija.getTimer().pause();
				}
			} 
			aplikacija.getScena().repaint();
		}	
	}
	
	
	public void upisiPodatkeUFajlove() throws FileNotFoundException {
		PrintWriter writer = null;
		
		try {
			writer = new PrintWriter(fajlAerodromaPutanja);
			
			for(Aerodrom aerodrom : listaAerodroma) {
				writer.write(aerodrom.getNaziv() + ",");
				writer.write(aerodrom.getKod() + ",");
				writer.write(Integer.toString(aerodrom.getX()) + ",");
				writer.write(Integer.toString(aerodrom.getY()) + "\n");
			}
			writer.close();
			
			writer = new PrintWriter(fajlLetovaPutanja);
			for(Let let : listaLetova) {
				writer.write(let.getPocetniAerodrom().getKod() + ",");
				writer.write(let.getKrajnjiAerodrom().getKod() + ",");
				writer.write(Integer.toString(let.getVremePoletanja().getSat()) + ",");
				writer.write(Integer.toString(let.getVremePoletanja().getMinut()) + ",");
				writer.write(Integer.toString(let.getMinutiTrajanjaLeta()) + "\n");
			}
			writer.close();
			
		} catch(FileNotFoundException e) {
			throw new FileNotFoundException();
		}
		finally {
			writer.close();
		}
		
	}
	
	public void procitajAerodromeIzFajla() throws PogresnoUnetKodException, LosBrojKolonaException,
	KoordinateVanOpsegaException, AerodromSaIstimKodomPostoji,  AerodromSaIstimKoordinatamaPostoji, FileNotFoundException, CitanjeIzFajlaException 
	{
		BufferedReader reader = null;
		String line = "";
		
		try {
			reader = new BufferedReader(new FileReader(fajlAerodromaPutanja));
			
			while((line = reader.readLine()) != null) {
				
				String[] strings = line.split(",");
				
				if(strings.length != 4) {
					throw new LosBrojKolonaException();
				}
				
				dodajAerodrom(strings[0], strings[1], strings[2], strings[3]);
			}
		} catch(IOException  e) {
			if(e instanceof FileNotFoundException) {
				throw new FileNotFoundException();
			} else {
				throw new CitanjeIzFajlaException();
			}
			
		} finally {
			try {
				if(reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				System.out.println("Greska, reader ne pokazuje na fajl");
			}
		}
	}

	public void procitajLetoveIzFajla() throws PogresnoUnetKodException, LoseUnetoVremeException, 
	UnetiIstiAerodromiException, PocetniAerodromNePostojiException, KrajnjiAerodromNePostojiException, 
	LosBrojKolonaException, FileNotFoundException, CitanjeIzFajlaException 
	{
		BufferedReader reader = null;
		String line = "";
		
		try {
			reader = new BufferedReader(new FileReader(fajlLetovaPutanja));
			
			while((line = reader.readLine()) != null) {
				
				String[] strings = line.split(",");
				
				if(strings.length != 5) {
					throw new LosBrojKolonaException();
				}
				
				dodajLet(strings[0], strings[1], strings[2], strings[3], strings[4]);
			}
		} catch(IOException  e) {
			if(e instanceof FileNotFoundException) {
				throw new FileNotFoundException();
			} else {
				throw new CitanjeIzFajlaException();
			}
			
		} finally {
			try {
				if(reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				System.out.println("Greska, reader ne pokazuje na fajl");
			}
		}
	}
	
	public void dodajAerodrom(String naziv, String kod, String xStr, String yStr ) 
			throws PogresnoUnetKodException, KoordinateVanOpsegaException, AerodromSaIstimKodomPostoji, AerodromSaIstimKoordinatamaPostoji
	{
		if(kod.length() != 3) {
			throw new PogresnoUnetKodException();
		}
		for(int i = 0 ; i < 3 ; i++) {
			if(kod.charAt(i) < 'A' ||kod.charAt(i) > 'Z') {
				throw new PogresnoUnetKodException();
			}
		}
		int x = Integer.parseInt(xStr);
		int y = Integer.parseInt(yStr);
		if(x < -90 || x > 90 || y < -90 || y > 90) {
			throw new KoordinateVanOpsegaException();
		}
		for(Aerodrom aerodrom: listaAerodroma) {
			if(aerodrom.uporediKod(kod)) {
				throw new AerodromSaIstimKodomPostoji();
			}
			if(aerodrom.uporediKoordinate(x, y)) {
				throw new AerodromSaIstimKoordinatamaPostoji();
			}
		}
		Aerodrom aerodrom = new Aerodrom(naziv, kod, x, y);
		listaAerodroma.add(aerodrom);
		aplikacija.dodajUListuAerodromaGUI(aerodrom);
		aplikacija.getScena().repaint();
	}
	
	public void dodajLet(String kodPocetnogAerodroma, String kodKrajnjegAerodroma, String satPoletanjaStr,
			String minutPoletanjaStr, String trajanjeLetaStr) 
					throws PogresnoUnetKodException, LoseUnetoVremeException, UnetiIstiAerodromiException,
					PocetniAerodromNePostojiException, 	KrajnjiAerodromNePostojiException
	
	{	
		if(kodPocetnogAerodroma.length() != 3 || kodKrajnjegAerodroma.length() != 3) {
			throw new PogresnoUnetKodException();
		}
		for(int i = 0 ; i < 3 ; i++) {
			if(kodPocetnogAerodroma.charAt(i) < 'A' || kodPocetnogAerodroma.charAt(i) > 'Z' 
			|| kodKrajnjegAerodroma.charAt(i) < 'A' || kodKrajnjegAerodroma.charAt(i) > 'Z') {
				throw new PogresnoUnetKodException();
			}
		}
		int satPoletanja = Integer.parseInt(satPoletanjaStr);
		int minutPoletanja = Integer.parseInt(minutPoletanjaStr);
		int trajanjeLeta = Integer.parseInt(trajanjeLetaStr);
		
		if(satPoletanja < 0 || satPoletanja > 23 || minutPoletanja < 0 || minutPoletanja > 59 
				|| trajanjeLeta < 0) {
			throw new LoseUnetoVremeException();
		}
		if(kodPocetnogAerodroma.equals(kodKrajnjegAerodroma)) {
			throw new UnetiIstiAerodromiException();
		}
		Aerodrom pocetniAerodrom = null;
		Aerodrom krajnjiAerodrom = null;
		for(int i = 0 ; i < listaAerodroma.size() ; i++) {
			if(listaAerodroma.get(i).uporediKod(kodPocetnogAerodroma)) {
				pocetniAerodrom = listaAerodroma.get(i);
			} else if (listaAerodroma.get(i).uporediKod(kodKrajnjegAerodroma)) {
				krajnjiAerodrom = listaAerodroma.get(i);
			}
		}
		if(pocetniAerodrom == null) {
			throw new PocetniAerodromNePostojiException();
		}
		if(krajnjiAerodrom == null) {
			throw new KrajnjiAerodromNePostojiException();
		}
		Let let = new Let(pocetniAerodrom, krajnjiAerodrom, satPoletanja, minutPoletanja, trajanjeLeta);
		
		listaLetova.add(let);
		System.out.println(this);
	}
	
	public void dodajAvion(Avion avion) {
		synchronized (this) {
			listaAviona.add(avion);
		}
	}
	
	public void izbaciAvion(Avion avion) {
		synchronized(listaAviona) {
			listaAviona.remove(avion);	
		}
	}
	
	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		
		string.append("Aerodromi:\n");
		for(Aerodrom aerodrom: listaAerodroma) {
			string.append(aerodrom + "\n");
		}
		
		string.append("\n");
		string.append("Letovi:\n");
		for(Let let: listaLetova) {
			string.append(let + "\n");
		}
		
		string.append("\n");
		string.append("Avioni:\n");
		for(Avion avion: listaAviona) {
			string.append(avion + "\n");
		}	
		
		return new String(string);
	}
}
