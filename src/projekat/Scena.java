package projekat;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Polygon;

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
	
	private void drawTriangle(Graphics g, int x, int y) {
		int[] xpoints = {x,  x + SCALE/2, x + SCALE};
		int[] ypoints = {y + SCALE, y, y + SCALE};
		g.fillPolygon(xpoints, ypoints, 3);
	}
	
	@Override
	public void paint(Graphics g) {
		synchronized(aplikacija.getPodaci()) {
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
			for(Avion avion: aplikacija.getPodaci().getListaAviona()) {
				synchronized (avion) {
					if(avion.daLiJePoleteo()) {
						g.setColor(Color.blue);
						g.fillOval((int)avion.getX(), (int)avion.getY(), SCALE, SCALE);
					}
				}
			}
		}
		synchronized(aplikacija.getTimer()) {
			if(aplikacija.getTimer().daLiJeSimulacijaZapoceta()) {
				g.setColor(Color.red);
				g.drawString(aplikacija.getTimer().getVremeSimulacije().toString(), SCALE, SCALE + 5);
			}
		}
	}

}
