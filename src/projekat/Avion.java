package projekat;

public class Avion extends Thread {

	private static int COUNTER = 0;
	
	private Aplikacija aplikacija;
	
	private int id;
	//koordinate krajnjeg aerodroma
	private int destX;
	private int destY;
	//koordinate pocetnog aerodroma
	private int startX;
	private int startY;
	
	private double x;
	private double y;
	
	private double xPomeraj;
	private double yPomeraj;
	
	private Time vreme = null;
	private Let let;
	
	private boolean poleteo = false;
	
	public boolean daLiJePoleteo() {
		return poleteo;
	}
	
	public Avion(Aplikacija aplikacija, Let let) {
		this.id = Avion.COUNTER++;
		this.let = let;
		this.x = let.getPocetniAerodrom().getX()*7;
		this.y = let.getPocetniAerodrom().getY()*7;
		this.startX = let.getPocetniAerodrom().getX()*7;
		this.startY = let.getPocetniAerodrom().getY()*7;
		this.destX = let.getKrajnjiAerodrom().getX()*7;
		this.destY = let.getKrajnjiAerodrom().getY()*7;
		this.aplikacija = aplikacija;
		this.xPomeraj = (double)(destX - startX) / (let.getMinutiTrajanjaLeta() / 2.0);
		this.yPomeraj = (double)(destY - startY) / (let.getMinutiTrajanjaLeta() / 2.0);
	}
	
	public void setVremePoletanja(Time vreme) {
		this.vreme = vreme;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public Time getVreme() {
		return vreme;
	}
	
	public Let getLet() {
		return let;
	}
	
	@Override
	public void run() {
		poleteo = true;
		int cnt = 0;
		try {
			while(cnt < let.getMinutiTrajanjaLeta() / 2.0) {
				synchronized (this) {
					x += xPomeraj;
					y += yPomeraj;
					wait();
				}
				cnt++;
			}
		} catch(InterruptedException e) {
			
		}
		synchronized(aplikacija.getPodaci()) {
			aplikacija.getPodaci().izbaciAvion(this);	
		}
	}
	
	@Override
	public String toString() {
		return "id:" + id + " x:" + x + " y:" + y + " vreme:" + vreme;
	}
}
