package projekat;

import java.util.ArrayList;
import java.util.List;

public class Timer extends Thread {

	private boolean simulacijaZapoceta;
	
	private int shutDownTime = 60;
	private boolean shutDownTimePaused = false;
	
	private int cnt10 = 0;
	private int cnt2 = 0;
	private boolean simulacijaPauzirana = false;
	
	private Aplikacija aplikacija;
	
	private Time vremeSimulacije;
	
	public Timer(Aplikacija aplikacija) {
		this.aplikacija = aplikacija;
		simulacijaZapoceta = false;
	}
	
	public synchronized Time getVremeSimulacije() {
		return vremeSimulacije;
	}
	
	public synchronized boolean daLiJeSimulacijaZapoceta() {
		return simulacijaZapoceta;
	}
	
	public synchronized void inkrementirajVremeSimulacije() {
		vremeSimulacije.inkrementirajVreme();
	}
	
	public synchronized void pokreniSimulaciju() {
		if(!simulacijaZapoceta ) {
			vremeSimulacije = new Time(0, 0);
			simulacijaZapoceta = true;
			cnt10 = 9;
		}
		else if (simulacijaPauzirana){
			this.notify();
			simulacijaPauzirana = false;
		}
	}
	
	public synchronized void pauzirajSimulaciju() {
		simulacijaPauzirana = true;
	}
	
	public synchronized void restartujSimulaciju() {
		vremeSimulacije.setSat(0);
		vremeSimulacije.setMinut(0);
		simulacijaZapoceta = false;
		simulacijaPauzirana = false;
		shutDownTimePaused = false;
		cnt2 = 1;
	}
	
	//vrsi notify aviona kako bi on azurirao svoju poziciju
	private void azurirajVremenaAviona() {
		synchronized(aplikacija.getPodaci()) {
			for(Avion avion : aplikacija.getPodaci().getListaAviona()) {
				synchronized(avion) {
					if(avion.daLiJePoleteo()) {
						avion.getVreme().uvecajVreme(2);//moze i bez ovoga jer na svake 0.2 sec signaliziran avion sam azurira svoje koordinate
						avion.notify();	
					}
				}
			}
		}
	}
	
	//ako je vreme da neki avion poleti, kreira avion sa vremenom poletanja i dodaje ga u listu cekanja pocetnog aerodroma
	private void kreirajAvioneNaOsnovuLetova() {
		synchronized(aplikacija.getPodaci()) {
			for(Let let: aplikacija.getPodaci().getListaLetova()) {
				if(vremeSimulacije.compare(let.getVremePoletanja()) == 0) {
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
					avion.setVremePoletanja(vremeSimulacije.clone());
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
				//ako je simulacija zapoceta, azuriraju se vremena aviona i vreme simulacije
				synchronized(this) {
					if(simulacijaZapoceta && !simulacijaPauzirana) {
						cnt2++;
						if(cnt2 == 2) {
							azurirajVremenaAviona();
							cnt2 = 0;	
						}
						kreirajAvioneNaOsnovuLetova();// ovo mora pre inkrementiranja vremena kako bi poleteli letovi u 00:00
						inkrementirajVremeSimulacije();
					}
				}
				if(cnt10 == 10) {
					zapocniLetove();
					synchronized(this) {
						if(!shutDownTimePaused && !simulacijaZapoceta) {
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
		shutDownTimePaused = true;
	}
	
	public synchronized void unpauseShutDownTimer() {
		shutDownTimePaused = false;
		this.notify();
	}
	
	public synchronized void restartujShutDownTajmer() {
		shutDownTime = 60;
	}
	
	public synchronized int getShutDownTime() {
		return shutDownTime;
	}

}
