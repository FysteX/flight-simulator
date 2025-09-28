package projekat;

import java.util.ArrayList;
import java.util.List;

public class Timer extends Thread {

	private boolean simulacijaZapoceta;
	
	private int shutDownTime = 60;
	private boolean paused = false;
	
	int cnt10 = 0;
	
	private Aplikacija aplikacija;
	
	private Time vreme;
	
	public Timer(Aplikacija aplikacija) {
		this.aplikacija = aplikacija;
		simulacijaZapoceta = false;
	}
	
	public synchronized Time getVreme() {
		return vreme;
	}
	
	public synchronized boolean daLiJeSimulacijaZapoceta() {
		return simulacijaZapoceta;
	}
	
	public synchronized void zapocniSimulaciju() {
		vreme = new Time(0, 0);
		simulacijaZapoceta = true;
		cnt10 = 9;
	}
	
	
	//ako je vreme pocetka nekog leta, onda kreira avion i dodaje ga u listu cekanja njegovog pocetnog aerodroma kako bi kasnije u nekom 
	//desetominutnom intervalu poleteo i takodje azu
	private void azurirajVremena() {
		synchronized(aplikacija.getPodaci()) {
			for(Avion avion : aplikacija.getPodaci().getListaAviona()) {
				synchronized(avion) {
					if(avion.daLiJePoleteo()) {
						avion.getVreme().uvecajVreme(2);
						avion.notify();	
					}
				}
			}
			for(Let let: aplikacija.getPodaci().getListaLetova()) {
				//posto se ova funkcija zove na svake 0.2 milisec pa trenutno vreme moze da bude vece od vreme poletanja nekog aviona koristi se compare
				if(vreme.compare(let.getVremePoletanja()) >= 0) {
					Avion avion = new Avion(aplikacija, let);
					synchronized(aplikacija.getPodaci()) {
						aplikacija.getPodaci().dodajAvion(avion);
					}
					let.getPocetniAerodrom().dodajAvionURedCekanja(avion);
				}
			}
		}
	}
	//ukoliko postoji neki let na aerodromu u redu letova koji cekaju, pusta prvog koji je dosao na red
	private void zapocniLetove() {
		synchronized (aplikacija.getPodaci()) {
			for(Aerodrom aerodrom: aplikacija.getPodaci().getListaAerodroma()) {
				if(aerodrom.kolikoAvionaCeka() > 0) {
					Avion avion = aerodrom.getAvionKojiCeka();
					avion.setVremePoletanja(vreme.clone());
					avion.start();
				}
			}
		}
	}
	
	@Override
	public void run() {
		try {
			int cnt2 = 1;
			while(!this.isInterrupted()) {
				Thread.sleep(100);
				cnt10++;
				aplikacija.getScena().toggleRedFrame();
				//ako je simulacija zapoceta, azuriraju se vremena aviona i vreme simulacije
				if(simulacijaZapoceta) {
					cnt2++;
					if(cnt2 == 2) {
						azurirajVremena();
						cnt2 = 0;	
					}
					synchronized(this) {
						vreme.uvecajVreme(1);
					}
				}
				if(cnt10 == 10) {
					zapocniLetove();
					synchronized(this) {
						if(!paused && !simulacijaZapoceta) {
							shutDownTime--;
						}
						if(shutDownTime == 5) {
							aplikacija.vremeIsticePrikaz();
						}
						if(shutDownTime == 0) {
							aplikacija.gasiAplikaciju();
						}
					}
					cnt10 = 0;
					System.out.println(shutDownTime);
				}
				aplikacija.getScena().repaint();
			}
		} catch (InterruptedException e) {}
	}
	
	public synchronized void pauseShutDownTimer() {
		paused = true;
	}
	
	public synchronized void unpauseShutDownTimer() {
		paused = false;
		this.notify();
	}
	
	public synchronized void restartujShutDownTajmer() {
		shutDownTime = 60;
	}
	
	public synchronized int getShutDownTime() {
		return shutDownTime;
	}

}
