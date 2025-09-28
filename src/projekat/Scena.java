package projekat;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;

public class Scena extends Canvas {

	public static final int SCALE = 7;
	
	private Aplikacija aplikacija;
	private boolean isRedFramePaintable;
	
	public synchronized void setRedFrame() {
		isRedFramePaintable = true;
	}
	
	public synchronized void toggleRedFrame() {
		isRedFramePaintable = !isRedFramePaintable;
	}
	
	public Scena(Aplikacija aplikacija) {
		this.aplikacija = aplikacija;
		
		this.setBackground(Color.yellow);
		this.setBounds(0, 0, 630, 630);
	}
	
	@Override
	public void paint(Graphics g) {
		
		for(Aerodrom aerodrom: aplikacija.getPodaci().getListaAerodroma()) {	
			if(aerodrom.isShown()) {
				g.setColor(Color.gray);
				g.fillRect(aerodrom.getX()*SCALE, aerodrom.getY()*SCALE, SCALE, SCALE);
				g.drawString(aerodrom.getKod().toString(), aerodrom.getX()*SCALE + SCALE, aerodrom.getY()*SCALE + SCALE);
				//scena je skalirana sa 7 pa se moraju skalirati i nacrtane figure
				if(aerodrom.isSelected() && isRedFramePaintable) {
					g.setColor(Color.red);
					g.drawRect(aerodrom.getX()*SCALE, aerodrom.getY()*SCALE, SCALE, SCALE);
				}
			}	
		}
		synchronized(aplikacija.getPodaci()) {
			for(Avion avion: aplikacija.getPodaci().getListaAviona()) {
				synchronized (avion) {
					if(avion.daLiJePoleteo()) {
						g.setColor(Color.blue);
						g.fillOval((int)avion.getX(), (int)avion.getY(), SCALE, SCALE);
					}
				}
			}
			if(aplikacija.getTimer().daLiJeSimulacijaZapoceta()) {
				g.setColor(Color.red);
				g.drawString(aplikacija.getTimer().getVreme().toString(), SCALE, SCALE + 5);
			}
		}
	}

}
