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
	
	public Time getVreme() {
		return vreme;
	}
	
	public boolean daLiJeSimulacijaZapoceta() {
		return simulacijaZapoceta;
	}
	
	public void zapocniSimulaciju() {
		vreme = new Time(0, 0);
		simulacijaZapoceta = true;
		cnt10 = 9;
	}
	
	
	//ako je vreme pocetka nekog leta, onda kreira avion i zapocinje njegov let
	private void azurirajVremena() {
		for(Avion avion : aplikacija.getPodaci().getListaAviona()) {
			synchronized(avion) {
				if(avion.daLiJePoleteo()) {
					avion.getVreme().uvecajVreme(2);
					avion.notify();	
				}
			}
		}
		for(Let let: aplikacija.getPodaci().getListaLetova()) {
			if(vreme.equals(let.getVremePoletanja())) {
				Avion avion = new Avion(aplikacija, let);
				synchronized(aplikacija.getPodaci()) {
					aplikacija.getPodaci().dodajAvion(avion);
				}
				let.getPocetniAerodrom().dodajAvionURedCekanja(avion);
			}
		}
		vreme.uvecajVreme(2);
	}
	
	public synchronized void pause() {
		paused = true;
	}
	
	public synchronized void unpause() {
		paused = false;
		this.notify();
	}
	
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
			while(!this.isInterrupted()) {
				Thread.sleep(100);
				cnt10++;
				aplikacija.getScena().toggleRedFrame();
				if(simulacijaZapoceta) {
					azurirajVremena();
				}
				if(cnt10 == 10) {
					zapocniLetove();
					synchronized(this) {
						if(!paused) {
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
	
	public synchronized void restartujShutDownTajmer() {
		shutDownTime = 60;
	}
	
	public synchronized int getShutDownTime() {
		return shutDownTime;
	}

}
