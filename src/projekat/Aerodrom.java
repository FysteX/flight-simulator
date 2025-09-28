package projekat;

import java.util.ArrayList;
import java.util.List;

public class Aerodrom {
	
	private String naziv;
	private String kod;
	private int x;
	private int y;
	private boolean shown;
	
	private boolean selected;
	
	private List<Avion> avioniKojiCekaju;
	
	public Aerodrom(String naziv, String kod, int x, int y) {
		this.naziv = naziv;
		this.kod = kod; //treba provera da li je kod ispravan
		this.x = x; 
		this.y = y; // za koordinate da li su u opsegu izmedju -90 i 90
		this.selected = false;
		this.shown = true;
		this.avioniKojiCekaju = new ArrayList<>();
	}
	
	public int kolikoAvionaCeka() {
		return avioniKojiCekaju.size();
	}
	
	public Avion getAvionKojiCeka() {
		return avioniKojiCekaju.remove(0);
	}
	
	public void dodajAvionURedCekanja(Avion avion) {
		avioniKojiCekaju.add(avion);
	}
	
	public boolean isShown() {
		return shown;
	}
	
	public void show() {
		shown = true;
	}
	
	public void unshow()  throws AerodromJeSelektovanException{
		if(isSelected()) {
			throw new AerodromJeSelektovanException();
		}
		shown = false;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void select() {
		if(isShown()) {
			selected = true;
		}
	}
	
	public void deselect() {
		if(isShown()) {
			selected = false;
		}
	}
	
	public boolean uporediKod(String kod) {
		if(this.kod.equals(kod)) {
			return true;
		}
		return false;
	}
	
	public boolean uporediKoordinate(int x, int y) {
		if(this.x == x && this.y == y) {
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return kod;
	}

	public String getNaziv() {
		return naziv;
	}

	public String getKod() {
		return kod;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

}
